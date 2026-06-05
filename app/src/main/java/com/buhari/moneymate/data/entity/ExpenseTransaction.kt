package com.buhari.moneymate.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "isExpense")
    val isExpense: Boolean,
    @ColumnInfo(name = "icon")
    val icon: Int,
    @ColumnInfo(name = "description")
    val description: String? = null
)
