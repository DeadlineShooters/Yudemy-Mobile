package com.deadlineshooters.yudemy.repositories

import android.util.Log
import com.deadlineshooters.yudemy.models.Transaction
import com.deadlineshooters.yudemy.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TransactionRepository {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()
    private val transactionsCollection = mFireStore.collection(Constants.TRANSACTIONS)

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

    fun getTransactions(receiverId: String, callback: (List<Transaction>) -> Unit) {
        transactionsCollection
            .whereEqualTo("receiverId", receiverId)
            .get()
            .addOnSuccessListener { documents ->
                val transactions = documents.map { it.toObject(Transaction::class.java) }
                callback(transactions)
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
            }
    }

    fun getTransactionsForCourse(receiverId: String, courseId: String, callback: (List<Transaction>) -> Unit) {
        transactionsCollection
            .whereEqualTo("receiverId", receiverId)
            .whereEqualTo("courseId", courseId)
            .get()
            .addOnSuccessListener { documents ->
                val transactions = documents.map { it.toObject(Transaction::class.java) }
                callback(transactions)
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
            }
    }

}
