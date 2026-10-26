package com.bizpulse.app.data.repository
import androidx.room.withTransaction
import com.bizpulse.app.data.db.BizPulseDatabase
import com.bizpulse.app.data.db.entity.CategoryEntity
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
class CategoryRepository @Inject constructor(private val db: BizPulseDatabase, private val users: UserRepository, private val audit: AuditRepository) {
    val categories = db.categories().getAll().flowOn(Dispatchers.IO)
    suspend fun save(c: CategoryEntity) = withContext(Dispatchers.IO) {
        val actor=users.requireOwner()
        require(c.name.isNotBlank()) { "Category name is required" }
        db.withTransaction {
            require(db.categories().snapshot().none { it.id!=c.id && it.name.equals(c.name,true) }) { "Category already exists" }
            if(c.id==0L) db.categories().insert(c) else db.categories().update(c)
            audit.log(actor.name,"Category Saved",c.name,"update")
        }
    }
    suspend fun delete(c: CategoryEntity) = withContext(Dispatchers.IO) {
        val actor=users.requireOwner()
        db.withTransaction {
            require(db.products().snapshot().none { it.categoryId==c.id }) { "Move or delete products in this category first" }
            require(db.lines().snapshot().none { it.categoryId==c.id }) { "This category is used by historical receipts" }
            db.categories().delete(c); audit.log(actor.name,"Category Deleted",c.name,"delete")
        }
    }
}
