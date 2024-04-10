package com.deadlineshooters.yudemy.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.SignInActivity
import com.deadlineshooters.yudemy.repositories.AuthenticationRepository
import com.deadlineshooters.yudemy.repositories.UserRepository

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CloseAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CloseAccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var closeAccBtn: Button
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
        return inflater.inflate(R.layout.fragment_close_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeAccBtn = view.findViewById(R.id.closeAccBtn)
        val warningText = view.findViewById<TextView>(R.id.tvWarningCloseAcc)
        done = view.findViewById(R.id.doneCloseAcc)

        val mSpannableString = SpannableString(context?.resources?.getString(R.string.close_account_warning))
        val warningSpan = TextAppearanceSpan(context, R.style.WarningCloseAccText)
        mSpannableString.setSpan(warningSpan, 0, 8, 0)
        warningText.text = mSpannableString

        done.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        closeAccBtn.setOnClickListener {
            // TODO: handle close account
            createConfirmCloseAccountDialog()
        }
    }

    private fun createConfirmCloseAccountDialog() {
        val builder = AlertDialog.Builder(context)

        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_close_account_confirm, null)
        builder.setView(dialogView)

        val dialog = builder.create()

        dialogView.findViewById<Button>(R.id.cancelCloseAccConfirmBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.closeAccConfirmBtn).setOnClickListener {
            val edittextPassword = dialogView.findViewById<TextView>(R.id.etConfirmPwCloseAcc)
            if(edittextPassword.text.isEmpty()){
                edittextPassword.error = "Please enter your password"
                return@setOnClickListener
            }
            val password = edittextPassword.text.toString()
            AuthenticationRepository().checkPassword(password) { isCorrect ->
                if (isCorrect == true) {
                    AuthenticationRepository().closeAccount { isComplete, userId ->
                        if (isComplete) {
                            UserRepository().deleteUser(userId!!)
                            dialog.dismiss()
                            Toast.makeText(context, "Account closed successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(context, SignInActivity::class.java))
                        }
                        else
                            Toast.makeText(context, "Failed to close account", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(context, "Incorrect password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CloseAccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CloseAccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}