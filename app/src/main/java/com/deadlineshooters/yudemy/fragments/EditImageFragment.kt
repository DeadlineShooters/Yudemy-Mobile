package com.deadlineshooters.yudemy.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
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
import com.deadlineshooters.yudemy.helpers.CloudinaryHelper
import com.deadlineshooters.yudemy.helpers.ImageViewHelper
import com.deadlineshooters.yudemy.models.Image
import com.deadlineshooters.yudemy.viewmodels.InstructorViewModel
import com.deadlineshooters.yudemy.viewmodels.UserViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var cancelEditImageBtn: TextView
    private lateinit var editedImage: ImageView
    private lateinit var saveInstructorImageBtn: Button
    private val dumpImage = "https://t4.ftcdn.net/jpg/00/97/58/97/360_F_97589769_t45CqXyzjz0KXwoBZT9PRaWGHRk5hQqQ.jpg"
    private lateinit var startForImagePickerResult: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var userViewModel: UserViewModel
    private lateinit var instructorViewModel: InstructorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        instructorViewModel = ViewModelProvider(this)[InstructorViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.getCurUser()
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
            replaceFragment(AccountFragment.newInstance(true, BaseActivity().getCurrentUserID()))
        }

        userViewModel.userData.observe(viewLifecycleOwner, Observer {user ->
            if(user.instructor != null){
                ImageViewHelper().setImageViewFromUrl(Image(user.instructor!!.image.secure_url, user.instructor!!.image.public_id), editedImage)
            }
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
                val filepath = saveBitmapToFile(bitmap, requireContext())

                if (filepath != null) {
                    CloudinaryHelper().uploadToCloudinary(filepath) { image ->
                        if (image != null) {
                            val imageUrl = image.secure_url
                            val publicId = image.public_id
                            instructorViewModel.updateInstructorImage(BaseActivity().getCurrentUserID(),publicId, imageUrl)
                            Toast.makeText(context, "Image updated successfully", Toast.LENGTH_SHORT).show()
                            replaceFragment(AccountFragment.newInstance(true, BaseActivity().getCurrentUserID()))
                        } else {
                            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } ?: run {
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayoutInstructor, fragment)
        fragmentTransaction.commit()
    }

    private fun saveBitmapToFile(bitmap: Bitmap, context: Context): String? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "JPEG_$timeStamp.jpg"
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(directory, fileName)

        return try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
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
        fun newInstance(param1: String, param2: String) =
            EditImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}