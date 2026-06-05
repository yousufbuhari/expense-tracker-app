package com.refresh.expensetracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE (:category IS NULL OR category = :category) AND (:startDate IS NULL OR date >= :startDate) AND (:endDate IS NULL OR date <= :endDate) ORDER BY date DESC")
    fun getFilteredTransactions(category: String?, startDate: Long?, endDate: Long?): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 0")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 1")
    fun getTotalExpense(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 0 AND date >= :startDate AND date <= :endDate")
    fun getIncomeInRange(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 1 AND date >= :startDate AND date <= :endDate")
    fun getExpenseInRange(startDate: Long, endDate: Long): Flow<Double?>
}
