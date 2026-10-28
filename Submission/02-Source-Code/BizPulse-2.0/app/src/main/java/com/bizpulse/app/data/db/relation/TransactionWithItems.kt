package com.bizpulse.app.data.db.relation
import androidx.room.*
import com.bizpulse.app.data.db.entity.*
data class TransactionWithItems(
    @Embedded val transaction: TransactionEntity,
    @Relation(parentColumn = "id", entityColumn = "transactionId") val lineItems: List<LineItemEntity>
)
