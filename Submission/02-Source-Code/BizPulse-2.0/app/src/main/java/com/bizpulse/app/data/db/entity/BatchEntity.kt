package com.bizpulse.app.data.db.entity
import androidx.room.*
@Entity(tableName="batches",indices=[Index("productId")])
data class BatchEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val productId:Long,val supplierName:String,val dateReceived:Long,val quantityReceived:Int,val costPerUnit:Double,val quantityRemaining:Int,val expiryDate:String?=null)
