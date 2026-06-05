package com.buhari.moneymate.data.repository

import com.buhari.moneymate.data.entity.Transaction
import com.buhari.moneymate.data.dao.TransactionDao
import kotlinx.coroutines.flow.Flow

/**
 * Repository class that abstracts access to the Transaction data source.
 */
class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    
    fun getMonthlyIncome(startDate: Long, endDate: Long): Flow<Double?> = transactionDao.getMonthlyIncome(startDate, endDate)

    fun getMonthlyExpense(startDate: Long, endDate: Long): Flow<Double?> = transactionDao.getMonthlyExpense(startDate, endDate)

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun getTransactionById(id: Int): Transaction? {
        return transactionDao.getTransactionById(id)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }
}
