package com.refresh.expensetracker.data.repository

import com.refresh.expensetracker.data.Transaction
import com.refresh.expensetracker.data.TransactionDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that abstracts access to the Transaction data source.
 */
class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    
    fun getTotalIncome(): Flow<Double?> = transactionDao.getTotalIncome()
    
    fun getTotalExpense(): Flow<Double?> = transactionDao.getTotalExpense()

    fun getFilteredTransactions(
        category: String?, 
        startDate: Long?, 
        endDate: Long?
    ): Flow<List<Transaction>> {
        return transactionDao.getFilteredTransactions(category, startDate, endDate)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }
}
