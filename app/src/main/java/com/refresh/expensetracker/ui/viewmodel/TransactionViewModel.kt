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
import java.util.Calendar

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

    // Trends calculation
    val weeklyTrend: Flow<Double> = calculateExpenseTrend(Calendar.WEEK_OF_YEAR)
    val monthlyTrend: Flow<Double> = calculateExpenseTrend(Calendar.MONTH)

    private fun calculateExpenseTrend(period: Int): Flow<Double> {
        val currentStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (period == Calendar.WEEK_OF_YEAR) {
                set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            } else {
                set(Calendar.DAY_OF_MONTH, 1)
            }
        }.timeInMillis

        val previousStart = Calendar.getInstance().apply {
            timeInMillis = currentStart
            add(period, -1)
        }.timeInMillis
        
        val previousEnd = currentStart - 1

        val currentExpenses = repository.getExpenseInRange(currentStart, System.currentTimeMillis())
        val previousExpenses = repository.getExpenseInRange(previousStart, previousEnd)

        return combine(currentExpenses, previousExpenses) { current, previous ->
            val cur = current ?: 0.0
            val prev = previous ?: 0.0
            if (prev == 0.0) 0.0 else ((cur - prev) / prev) * 100
        }
    }

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
