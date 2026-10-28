package com.bizpulse.app.util
import android.content.Context
import android.app.NotificationChannel
import android.app.NotificationManager
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.bizpulse.app.data.db.BizPulseDatabase
import java.time.*
import java.util.concurrent.TimeUnit
class AiDailyWorker(context:Context,params:WorkerParameters):CoroutineWorker(context,params) {
 override suspend fun doWork():Result {
  return try {
   val db=BizPulseDatabase.getInstance(applicationContext)
   val date=LocalDate.now().minusDays(1).toString()
   val sales=db.transactions().snapshot().filter{it.date==date}
   val text=LocalAiService(applicationContext).generateDailySummary("Date $date; revenue PHP ${rounded(sales.sumOf{it.total})}; transactions ${sales.size}; items ${sales.sumOf{it.items}}.")
   applicationContext.getSharedPreferences("insights",0).edit().putString("daily",text).putLong("generated",System.currentTimeMillis()).apply()
   val manager=applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
   manager.createNotificationChannel(NotificationChannel("daily","Daily store summary",NotificationManager.IMPORTANCE_DEFAULT))
   if(android.os.Build.VERSION.SDK_INT<33 || ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED)manager.notify(200,NotificationCompat.Builder(applicationContext,"daily").setSmallIcon(com.bizpulse.app.R.drawable.ic_launcher_foreground).setContentTitle("BizPulse daily summary ready").setContentText("Open Analytics as the owner to review it.").setVisibility(NotificationCompat.VISIBILITY_PRIVATE).build())
   Result.success()
  }catch(e:kotlinx.coroutines.CancellationException){throw e}catch(e:Exception){Result.failure()}finally{schedule(applicationContext,true)}
 }
 companion object {
  fun schedule(context:Context,append:Boolean=false){
   val now=ZonedDateTime.now();var next=now.toLocalDate().atTime(8,0).atZone(now.zone);if(!next.isAfter(now))next=next.plusDays(1)
   val request=OneTimeWorkRequestBuilder<AiDailyWorker>().setInitialDelay(Duration.between(now,next).toMillis(),TimeUnit.MILLISECONDS).setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build()).build()
   WorkManager.getInstance(context).enqueueUniqueWork("daily-ai",if(append)ExistingWorkPolicy.APPEND_OR_REPLACE else ExistingWorkPolicy.KEEP,request)
  }
 }
}
