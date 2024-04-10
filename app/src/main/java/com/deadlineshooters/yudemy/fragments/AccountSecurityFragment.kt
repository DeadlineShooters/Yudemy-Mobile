package com.deadlineshooters.yudemy.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.databinding.FragmentAccountSecurityBinding
import com.deadlineshooters.yudemy.repositories.AuthenticationRepository

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountSecurityFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountSecurityFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentAccountSecurityBinding

    private lateinit var done: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAccountSecurityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        done = view.findViewById(R.id.doneAccSecurity)
        done.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.etEmail.setText((activity as BaseActivity).getCurrentUserEmail())

        binding.btnChangePw.setOnClickListener {
            Log.d("AccountSecurityFragment", "${binding.passwordInputRetype.text} - ${binding.passwordInputNew.text}")
            if (binding.passwordInputRetype.text.toString() != binding.passwordInputNew.text.toString()) {
                binding.passwordInputRetype.error = "Passwords do not match"
                return@setOnClickListener
            }
            (activity as BaseActivity).showProgressDialog("Changing password...")
            AuthenticationRepository().checkPassword(binding.passwordInputCurr.text.toString()) { success ->
                if (success == true) {
                    AuthenticationRepository().changePassword(binding.passwordInputNew.text.toString()) { success ->
                        if (success == true) {
                            Toast.makeText(
                                context,
                                "Password changed successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.passwordInputCurr.text?.clear()
                            binding.passwordInputNew.text?.clear()
                            binding.passwordInputRetype.text?.clear()
                            binding.passwordInputCurr.clearFocus()
                            binding.passwordInputNew.clearFocus()
                            binding.passwordInputRetype.clearFocus()

                            (activity as BaseActivity).hideKeyboard(view)
                        } else {
                            Toast.makeText(context, "Failed to change password", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
                else {
                    Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            (activity as BaseActivity).hideProgressDialog()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountSecurityFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountSecurityFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}