package com.bizpulse.app.data.db.entity
import androidx.room.*
@Entity(tableName="suppliers")
data class SupplierEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val name:String,val address:String,val ipAddress:String="",val latitude:Double,val longitude:Double)
