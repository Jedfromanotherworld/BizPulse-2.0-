package com.bizpulse.app.ui.session
import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.*
import kotlinx.coroutines.flow.*
private val Context.sessionStore by preferencesDataStore("bizpulse_session")
@Singleton class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val key = longPreferencesKey("user_id")
    private val targetKey = doublePreferencesKey("daily_target")
    val userId: Flow<Long?> = context.sessionStore.data.map { it[key] }
    val target: Flow<Double> = context.sessionStore.data.map { it[targetKey] ?: 7500.0 }
    suspend fun login(id: Long) { context.sessionStore.edit { it[key] = id } }
    suspend fun logout() { context.sessionStore.edit { it.remove(key) } }
    suspend fun setTarget(amount: Double) { context.sessionStore.edit { it[targetKey] = amount } }
}
