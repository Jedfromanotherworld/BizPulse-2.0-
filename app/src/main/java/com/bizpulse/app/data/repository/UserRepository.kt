package com.bizpulse.app.data.repository
import androidx.room.withTransaction
import com.bizpulse.app.data.db.BizPulseDatabase
import com.bizpulse.app.data.db.entity.UserEntity
import com.bizpulse.app.ui.session.SessionManager
import com.bizpulse.app.util.*
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
class UserRepository @Inject constructor(private val db: BizPulseDatabase, private val session: SessionManager, private val audit: AuditRepository) {
    val users = db.users().getAll().flowOn(Dispatchers.IO)
    suspend fun actor(): UserEntity = withContext(Dispatchers.IO) {
        session.userId.first()?.let { db.users().getById(it) } ?: error("Please log in again")
    }
    suspend fun requireOwner(): UserEntity = actor().also { require(it.role == "owner") { "Restricted Access: Business Owner only" } }
    suspend fun login(username: String, password: String, role: String) = withContext(Dispatchers.IO) {
        val u = db.users().getByUsername(username.trim().lowercase())
        require(u != null && PasswordHasher.verify(password,u.passwordHash) && u.role == role) { "Invalid username, password, or role" }
        session.login(u.id)
        audit.log(u.name,"Login","Signed in as ${u.role}","auth")
    }
    suspend fun logout() = withContext(Dispatchers.IO) {
        val u = runCatching { actor() }.getOrNull()
        try { if(u != null) audit.log(u.name,"Logout","Session ended","auth") } finally { session.logout() }
    }
    suspend fun save(id: Long, name: String, username: String, password: String, role: String, registration: Boolean = false) = withContext(Dispatchers.IO) {
        val actor = if(registration) null else requireOwner()
        require(name.trim().isNotEmpty()) { "Full name is required" }
        val login = username.trim().lowercase()
        require(Validation.username(login)) { "Username: 3–30 letters, digits or underscores" }
        require(Validation.role(role)) { "Choose a valid role" }
        db.withTransaction {
            val old = if(id == 0L) null else db.users().getById(id) ?: error("User no longer exists")
            require(old==null || old.role==role || db.operations().active(id)==null){"End the active shift before changing roles"}
            val duplicate = db.users().getByUsername(login)
            require(duplicate == null || duplicate.id == id) { "Username already exists" }
            if(old == null || password.isNotEmpty()) require(Validation.password(password)) { "Password must contain at least 6 characters" }
            if(old?.role == "owner" && role != "owner") {
                require(db.users().snapshot().count { it.role == "owner" } > 1) { "Keep at least one Business Owner" }
                require(actor?.id != id) { "You cannot change your own role" }
            }
            val row = UserEntity(id,name.trim(),login,if(password.isEmpty()) old!!.passwordHash else PasswordHasher.hash(password),role,old?.createdAt ?: System.currentTimeMillis())
            if(old == null) db.users().insert(row) else db.users().update(row)
            audit.log(actor?.name ?: name,if(old==null) "User Created" else "User Updated",login,if(old==null) "create" else "update")
        }
    }
    suspend fun delete(user: UserEntity) = withContext(Dispatchers.IO) {
        val actor = requireOwner()
        db.withTransaction {
            val current=db.users().getById(user.id) ?: error("User no longer exists")
            require(actor.id != current.id) { "You cannot delete your own account" }
            require(current.role != "owner" || db.users().snapshot().count { it.role=="owner" } > 1) { "Keep at least one Business Owner" }
            require(db.operations().active(current.id)==null){"End the active shift before deleting this account"}
            db.users().delete(current)
            audit.log(actor.name,"User Deleted",current.username,"delete")
        }
    }
}
