package com.deadlineshooters.yudemy.helpers

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import com.deadlineshooters.yudemy.R


class DialogHelper {
    companion object {
        private var mProgressDialog: Dialog? = null

        fun showProgressDialog(context: Context, text: String) {
            mProgressDialog = Dialog(context)
            mProgressDialog?.setCancelable(false)
            mProgressDialog?.setCanceledOnTouchOutside(false)

            /*Set the screen content from a layout resource.
            The resource will be inflated, adding all top-level views to the screen.*/
            mProgressDialog?.setContentView(R.layout.dialog_progress)

            val tvProgressText = mProgressDialog?.findViewById<TextView>(R.id.tv_progress_text)
            tvProgressText?.text = text

            //Start the dialog and display it on screen.
            mProgressDialog?.show()
        }

        fun hideProgressDialog() {
            mProgressDialog?.dismiss()
            mProgressDialog = null
        }

        fun showKeyboard(mEtSearch: EditText, context: Context) {
            mEtSearch.requestFocus()
            mEtSearch.isFocusableInTouchMode = true
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(mEtSearch, 0)
        }

    }
}
