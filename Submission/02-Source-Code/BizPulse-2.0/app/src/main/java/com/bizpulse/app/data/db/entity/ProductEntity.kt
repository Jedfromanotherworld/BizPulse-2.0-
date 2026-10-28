package com.bizpulse.app.data.db.entity
import androidx.room.*
@Entity(tableName = "products", foreignKeys = [ForeignKey(entity = CategoryEntity::class, parentColumns = ["id"], childColumns = ["categoryId"], onDelete = ForeignKey.RESTRICT)],
    indices = [Index(value = ["barcode"], unique = true), Index("categoryId")])
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, val barcode: String, val price: Double, val cost: Double,
    val stock: Int, val minAlert: Int, val categoryId: Long,
    val createdAt: Long = System.currentTimeMillis(), val imageUrl:String?=null
)
