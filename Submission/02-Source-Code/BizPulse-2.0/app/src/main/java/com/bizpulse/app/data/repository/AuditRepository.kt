package com.bizpulse.app.data.repository
import com.bizpulse.app.data.db.BizPulseDatabase
import com.bizpulse.app.data.db.entity.AuditLogEntity
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
class AuditRepository @Inject constructor(private val db: BizPulseDatabase) {
    val logs = db.audit().getAll().flowOn(Dispatchers.IO)
    fun search(term: String) = db.audit().search(term).flowOn(Dispatchers.IO)
    suspend fun log(user: String, action: String, details: String, type: String) = withContext(Dispatchers.IO) {
        db.audit().insert(AuditLogEntity(userName=user, action=action, details=details, type=type))
    }
}
