package com.bizpulse.app.data.db.dao
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.bizpulse.app.data.db.entity.ProductEntity
@Dao interface ProductDao {
    @Insert suspend fun insert(row: ProductEntity): Long
    @Update suspend fun update(row: ProductEntity)
    @Delete suspend fun delete(row: ProductEntity)
    @Query("SELECT * FROM products ORDER BY id") fun getAll(): Flow<List<ProductEntity>>
    @Query("SELECT * FROM products ORDER BY id") suspend fun snapshot(): List<ProductEntity>
    @Query("SELECT * FROM products WHERE id = :id") suspend fun getById(id: Long): ProductEntity?
    @Query("DELETE FROM products") suspend fun clear()
    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1") suspend fun getByBarcode(barcode: String): ProductEntity?
    @Query("SELECT * FROM products WHERE stock <= minAlert ORDER BY stock") fun getLowStock(): Flow<List<ProductEntity>>
    @Query("SELECT * FROM products WHERE name LIKE '%' || :term || '%' OR barcode LIKE '%' || :term || '%'") fun search(term: String): Flow<List<ProductEntity>>
    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :id AND stock >= :quantity") suspend fun deduct(id: Long, quantity: Int): Int
    @Query("UPDATE products SET stock = stock + :quantity WHERE id = :id") suspend fun restore(id: Long, quantity: Int)
}
