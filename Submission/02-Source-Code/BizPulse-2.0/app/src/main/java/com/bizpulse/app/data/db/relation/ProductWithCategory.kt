package com.bizpulse.app.data.db.relation
import androidx.room.*
import com.bizpulse.app.data.db.entity.*
data class ProductWithCategory(
    @Embedded val product: ProductEntity,
    @Relation(parentColumn = "categoryId", entityColumn = "id") val category: CategoryEntity?
)
