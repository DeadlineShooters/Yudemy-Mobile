package com.deadlineshooters.yudemy.fragments

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.activities.CourseLearningActivity
import com.deadlineshooters.yudemy.helpers.CloudinaryHelper
import com.deadlineshooters.yudemy.helpers.ImageViewHelper
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.viewmodels.InstructorViewModel
import com.deadlineshooters.yudemy.viewmodels.UserViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditImageFragment : Fragment() {
    // TODO: Rename and change types of parameters
//    private var isInstructor: Boolean? = null

//    private var param2: String? = null
    private lateinit var cancelEditImageBtn: TextView
    private lateinit var editedImage: ImageView
    private lateinit var saveInstructorImageBtn: Button
    private lateinit var startForImagePickerResult: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var userViewModel: UserViewModel
    private lateinit var instructorViewModel: InstructorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            isInstructor = it.getBoolean(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
        instructorViewModel = ViewModelProvider(this)[InstructorViewModel::class.java]
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancelEditImageBtn = view.findViewById(R.id.cancelEditImageBtn)
        editedImage = view.findViewById(R.id.editedImage)
        saveInstructorImageBtn = view.findViewById(R.id.saveInstructorImageBtn)

        cancelEditImageBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        userViewModel.userData.observe(viewLifecycleOwner, Observer {user ->
            ImageViewHelper().setImageViewFromUrl(Image(user.image.secure_url, user.image.public_id), editedImage)
        })

        startForImagePickerResult = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            editedImage.setImageURI(uri)
        }

        editedImage.setOnClickListener{
            startForImagePickerResult.launch(PickVisualMediaRequest())
        }

        saveInstructorImageBtn.setOnClickListener {
            editedImage.drawable?.let { drawable ->
                val bitmap = (drawable as BitmapDrawable).bitmap
                val filepath = BaseActivity().saveBitmapToFile(bitmap, requireContext())
                if (filepath != null) {
                    (activity as BaseActivity).showProgressDialog("")
                    CloudinaryHelper().uploadToCloudinary(filepath) { image ->
                        if (image != null) {
                            userViewModel.updateUserImage(BaseActivity().getCurrentUserID(), image)
                            Toast.makeText(context, "Image updated successfully", Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.popBackStack()
                            userViewModel.getCurUser()
                            (activity as BaseActivity).hideProgressDialog()
                        } else {
                            (activity as BaseActivity).hideProgressDialog()
                            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } ?: run {
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Boolean) =
            EditImageFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}