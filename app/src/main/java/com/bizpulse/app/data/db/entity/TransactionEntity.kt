package com.bizpulse.app.data.db.entity
import androidx.room.*
@Entity(tableName = "transactions", indices = [Index("date"), Index("cashierId")])
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, val time: String, val total: Double, val items: Int,
    val payment: String, val cashierName: String, val cashierId: Long,
    val cost: Double, val profit: Double, val createdAt: Long = System.currentTimeMillis(),
    val manual: Boolean = false, val tendered: Double = 0.0, val shiftId:Long?=null
)
