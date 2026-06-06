package com.buhari.moneymate.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["uuid"], unique = true)]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "uuid")
    val uuid: String = UUID.randomUUID().toString(),
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
