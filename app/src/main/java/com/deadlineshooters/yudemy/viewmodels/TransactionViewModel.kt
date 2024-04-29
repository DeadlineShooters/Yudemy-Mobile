package com.deadlineshooters.yudemy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.deadlineshooters.yudemy.models.Transaction
import com.deadlineshooters.yudemy.repositories.TransactionRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionViewModel : ViewModel() {
    private val transactionRepository = TransactionRepository()

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions


    private val _groupedTransactions = MutableLiveData<Map<String, Int>>()
    val groupedTransactions: LiveData<Map<String, Int>> = _groupedTransactions
    fun refreshTransactions(receiverId: String) {
        transactionRepository.getTransactions(receiverId) { transactions ->
            _transactions.value = transactions
        }
    }

    fun getTransactionsGroupedByDate(receiverId: String) {
        transactionRepository.getTransactions(receiverId) { transactions ->
            val sdf =  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val groupedTransactions = transactions.groupBy {
                sdf.parse(it.date)?.time ?: 0L
            }.mapValues {
                it.value.sumOf { transaction -> transaction.amount }
            }
            // Convert the Long timestamps back to String dates
            val stringDateTransactions = groupedTransactions.mapKeys {
                sdf.format(Date(it.key))
            }

            _groupedTransactions.value = stringDateTransactions
        }
    }

    fun getTransactionsForCourseGroupedByDate(receiverId: String, courseId: String) {
        transactionRepository.getTransactionsForCourse(receiverId, courseId) { transactions ->
            val sdf =  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val groupedTransactions = transactions.groupBy {
              sdf.parse(it.date)?.time ?: 0L
            }.mapValues {
                it.value.sumOf { transaction -> transaction.amount }
            }
            // Convert the Long timestamps back to String dates
            val stringDateTransactions = groupedTransactions.mapKeys {
                sdf.format(Date(it.key))
            }

            _groupedTransactions.value = stringDateTransactions
        }

    }


}