package com.bizpulse.app
import android.app.Application
import dagger.hilt.android.HiltAndroidApp
@HiltAndroidApp class BizPulseApplication : Application() {
 override fun onCreate(){super.onCreate();com.bizpulse.app.util.AiDailyWorker.schedule(this)}
}
