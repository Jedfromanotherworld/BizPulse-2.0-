package com.bizpulse.app.data.db.entity
import androidx.room.*
@Entity(tableName = "line_items", foreignKeys = [ForeignKey(entity = TransactionEntity::class, parentColumns = ["id"], childColumns = ["transactionId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("transactionId")])
data class LineItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: Long, val productId: Long, val productName: String,
    val quantity: Int, val price: Double, val cost: Double, val categoryId: Long, val batchId:Long?=null, val batchReceivedAt:Long?=null
)
