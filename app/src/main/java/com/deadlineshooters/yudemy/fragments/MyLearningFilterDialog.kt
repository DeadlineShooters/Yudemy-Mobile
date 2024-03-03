package com.deadlineshooters.yudemy.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.helpers.MyLearningFilterAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class MyLearningFilterDialog: BottomSheetDialogFragment() {
    var rvFilters: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_my_learning_filter, container, false)

        rvFilters = view.findViewById(R.id.rvFilters)

        val adapter = MyLearningFilterAdapter(resources.getStringArray(R.array.my_learning_filter))
        rvFilters!!.adapter = adapter
        rvFilters!!.layoutManager = LinearLayoutManager(activity)
        val itemDecoration: ItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        rvFilters!!.addItemDecoration(itemDecoration)
        adapter.onItemClick = { filter ->
            // TODO: handle filter
            Log.i("Filter option click", filter)
        }

        view.findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dismiss()
        }
        return view
    }

    companion object {
        const val TAG = "MyLearningFilterDialog"
    }
}