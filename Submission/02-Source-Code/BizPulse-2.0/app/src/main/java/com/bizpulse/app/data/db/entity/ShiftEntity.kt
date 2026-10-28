package com.bizpulse.app.data.db.entity
import androidx.room.*
@Entity(tableName="shifts",indices=[Index("cashierId")])
data class ShiftEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val cashierId:Long,val cashierName:String,val timeIn:Long,val timeOut:Long?=null)
