package com.bizpulse.app.data.db.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.bizpulse.app.data.db.entity.*
@Dao interface OperationsDao {
 @Query("SELECT * FROM batches ORDER BY dateReceived,id") fun batches():Flow<List<BatchEntity>>
 @Query("SELECT * FROM batches ORDER BY dateReceived,id") suspend fun batchSnapshot():List<BatchEntity>
 @Query("SELECT * FROM batches WHERE productId=:id AND quantityRemaining>0 ORDER BY dateReceived,id") suspend fun available(id:Long):List<BatchEntity>
 @Insert suspend fun insertBatch(b:BatchEntity):Long
 @Query("UPDATE batches SET quantityRemaining=quantityRemaining-:n WHERE id=:id AND quantityRemaining>=:n") suspend fun deduct(id:Long,n:Int):Int
 @Query("UPDATE batches SET quantityRemaining=quantityRemaining+:n WHERE id=:id") suspend fun restore(id:Long,n:Int):Int
 @Query("SELECT * FROM shifts ORDER BY timeIn DESC") fun shifts():Flow<List<ShiftEntity>>
 @Query("SELECT * FROM shifts ORDER BY timeIn DESC") suspend fun shiftSnapshot():List<ShiftEntity>
 @Query("SELECT * FROM shifts WHERE cashierId=:id AND timeOut IS NULL LIMIT 1") suspend fun active(id:Long):ShiftEntity?
 @Query("SELECT * FROM shifts WHERE id=:id") suspend fun shift(id:Long):ShiftEntity?
 @Insert suspend fun insertShift(s:ShiftEntity):Long
 @Query("UPDATE shifts SET timeOut=:end WHERE id=:id AND timeOut IS NULL") suspend fun endShift(id:Long,end:Long):Int
 @Query("SELECT * FROM suppliers ORDER BY name") fun suppliers():Flow<List<SupplierEntity>>
 @Query("SELECT * FROM suppliers ORDER BY id") suspend fun supplierSnapshot():List<SupplierEntity>
 @Insert suspend fun insertSupplier(s:SupplierEntity):Long
 @Update suspend fun updateSupplier(s:SupplierEntity)
 @Delete suspend fun deleteSupplier(s:SupplierEntity)
 @Query("DELETE FROM batches") suspend fun clearBatches()
 @Query("DELETE FROM shifts") suspend fun clearShifts()
 @Query("DELETE FROM suppliers") suspend fun clearSuppliers()
}
