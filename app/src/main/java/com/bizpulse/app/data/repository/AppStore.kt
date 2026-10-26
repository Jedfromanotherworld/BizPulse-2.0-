package com.bizpulse.app.data.repository
import com.bizpulse.app.data.db.entity.*
import com.bizpulse.app.data.db.relation.TransactionWithItems
import com.bizpulse.app.ui.session.SessionManager
import javax.inject.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
data class AppData(
    val users: List<UserEntity> = emptyList(), val categories: List<CategoryEntity> = emptyList(),
    val products: List<ProductEntity> = emptyList(), val transactions: List<TransactionWithItems> = emptyList(),
    val logs: List<AuditLogEntity> = emptyList(), val current: UserEntity? = null,
    val target: Double = 7500.0, val ready: Boolean = false, val error: String? = null, val batches:List<BatchEntity> = emptyList(), val shifts:List<ShiftEntity> = emptyList(), val suppliers:List<SupplierEntity> = emptyList()
)
@Singleton class AppStore @Inject constructor(users: UserRepository, products: ProductRepository, categories: CategoryRepository, transactions: TransactionRepository, audit: AuditRepository, session: SessionManager, db:com.bizpulse.app.data.db.BizPulseDatabase) {
    private val scope=CoroutineScope(SupervisorJob()+Dispatchers.IO)
    private val records=combine(users.users,products.products,categories.categories,transactions.transactions,audit.logs) { u,p,c,t,a -> AppData(u,c,p,t,a) }
    private val extended=combine(records,db.operations().batches(),db.operations().shifts(),db.operations().suppliers()){d,b,s,p->d.copy(batches=b,shifts=s,suppliers=p)}
    val data: StateFlow<AppData> = combine(extended,session.userId,session.target) { d,id,target -> d.copy(current=d.users.find { it.id==id },target=target,ready=true) }
        .catch { emit(AppData(ready=true,error=it.message ?: "Database could not be opened")) }
        .stateIn(scope,SharingStarted.Eagerly,AppData())
}
