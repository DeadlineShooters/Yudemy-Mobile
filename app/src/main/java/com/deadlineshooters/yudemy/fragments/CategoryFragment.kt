package com.deadlineshooters.yudemy.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.deadlineshooters.yudemy.models.Category
import com.deadlineshooters.yudemy.viewmodels.CategoryViewModel

// Define a data class to hold the category name and ID

class CategoryFragment(private val currentCategoryIndex: Int) : DialogFragment() {
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var adapter: ArrayAdapter<Category>

    interface DialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, selectedCategory: Category, selectedCategoryIndex: Int)
    }

    var listener: DialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        categoryViewModel =
            ViewModelProvider(this@CategoryFragment).get(CategoryViewModel::class.java)
        categoryViewModel.refreshCategories()

        adapter = object : ArrayAdapter<Category>(
            requireContext(),
            android.R.layout.simple_list_item_single_choice
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView
                textView.text = getItem(position)!!.name
                return view
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Choose Category")
            .setSingleChoiceItems(adapter, currentCategoryIndex) { _, which ->
                val selectedCategory = adapter.getItem(which)
                listener?.onDialogPositiveClick(this, selectedCategory ?: Category("", ""), which)
            }
            .setPositiveButton("OK", null)

        categoryViewModel.categoryList.observe(this, Observer { categories ->
            Log.d(this.javaClass.simpleName, categories[0].toString())
            val categoryItems = categories.map { Category(it._id, it.name) }
            adapter.clear()
            adapter.addAll(categoryItems)

//            val currentIndex = categoryItems.indexOfFirst { it._id == categoryID }

        })

        return builder.create()
    }


}
