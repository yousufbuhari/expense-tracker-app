package com.refresh.expensetracker.ui.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.refresh.expensetracker.data.AppDatabase
import com.refresh.expensetracker.data.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).transactionDao()
    
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions().onEach { 
        Log.d("TransactionViewModel", "Retrieved ${it.size} transactions from DB")
    }
    val totalIncome: Flow<Double?> = dao.getTotalIncome()
    val totalExpense: Flow<Double?> = dao.getTotalExpense()

    fun getFilteredTransactions(category: String?, startDate: Long?, endDate: Long?): Flow<List<Transaction>> {
        return dao.getFilteredTransactions(category, startDate, endDate)
    }

    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            Log.d("TransactionViewModel", "Adding transaction: $transaction")
            dao.insertTransaction(transaction)
        }
    }

    fun editTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            dao.deleteTransaction(transaction)
        }
    }
}
