package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TransactionRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val transactionsCollection = mFireStore.collection("transactions")

    fun addTransaction(transaction: Transaction, callback: (Boolean) -> Unit) {
        transactionsCollection
            .add(transaction)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "add failed with ", exception)
                callback(false)
            }
    }

}
