package com.refresh.expensetracker.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.refresh.expensetracker.data.AppDatabase
import com.refresh.expensetracker.data.Transaction
import com.refresh.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for managing Transaction data and UI state.
 * Implements MVVM by acting as a bridge between the Repository and the UI.
 */
class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    
    // Initialize Repository (In a real app, use Dependency Injection like Hilt)
    private val repository: TransactionRepository by lazy {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        TransactionRepository(dao)
    }

    // StateFlow for better UI state management in Compose
    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val allTransactions: StateFlow<List<Transaction>> = _allTransactions.asStateFlow()

    val totalIncome: Flow<Double?> = repository.getTotalIncome()
    val totalExpense: Flow<Double?> = repository.getTotalExpense()

    init {
        // Collect from repository and update state
        viewModelScope.launch {
            repository.allTransactions
                .onEach { Log.d("TransactionViewModel", "Retrieved ${it.size} transactions") }
                .collect { _allTransactions.value = it }
        }
    }

    /**
     * Business logic for filtering transactions
     */
    fun getFilteredTransactions(
        category: String? = null, 
        startDate: Long? = null, 
        endDate: Long? = null
    ): Flow<List<Transaction>> {
        return repository.getFilteredTransactions(category, startDate, endDate)
    }

    /**
     * Data mutation operations wrapped in viewModelScope
     */
    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            Log.d("TransactionViewModel", "Adding: ${transaction.title}")
            repository.insertTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            Log.d("TransactionViewModel", "Deleting: ${transaction.title}")
            repository.deleteTransaction(transaction)
        }
    }
}
