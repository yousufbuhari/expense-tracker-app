package com.buhari.moneymate.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.buhari.moneymate.data.local.AppDatabase
import com.buhari.moneymate.data.entity.Transaction
import com.buhari.moneymate.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import com.buhari.moneymate.data.repository.UserProfileRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * ViewModel for managing Transaction data and UI state.
 * Implements MVVM by acting as a bridge between the Repository and the UI.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    
    // Initialize Repository (In a real app, use Dependency Injection like Hilt)
    private val repository: TransactionRepository by lazy {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        TransactionRepository(dao)
    }

    private val userProfileRepository: UserProfileRepository by lazy {
        val dao = AppDatabase.getDatabase(application).userProfileDao()
        UserProfileRepository(dao)
    }

    private val calendar = Calendar.getInstance()
    
    private val monthStart = calendar.apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private val monthEnd = calendar.apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    // StateFlow for better UI state management in Compose
    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val allTransactions: StateFlow<List<Transaction>> = _allTransactions.asStateFlow()

    private val _currentCurrency = userProfileRepository.userProfile
        .map { it?.currency ?: "INR" }
        .distinctUntilChanged()

    val totalIncome: Flow<Double?> = _currentCurrency.flatMapLatest { currency ->
        repository.getMonthlyIncome(monthStart, monthEnd, currency)
    }

    val totalExpense: Flow<Double?> = _currentCurrency.flatMapLatest { currency ->
        repository.getMonthlyExpense(monthStart, monthEnd, currency)
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
     * Data mutation operations wrapped in viewModelScope
     */
    fun insertTransaction(transaction: Transaction) {
        viewModelScope.launch {
            Log.d("TransactionViewModel", "Adding: ${transaction.title}")
            repository.insertTransaction(transaction)
        }
    }

    suspend fun getTransactionById(id: Int): Transaction? {
        return repository.getTransactionById(id)
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            Log.d("TransactionViewModel", "Deleting: ${transaction.title}")
            repository.deleteTransaction(transaction)
        }
    }
}
