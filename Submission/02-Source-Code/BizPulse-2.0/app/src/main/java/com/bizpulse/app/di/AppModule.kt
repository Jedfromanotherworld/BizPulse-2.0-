package com.bizpulse.app.di
import android.content.Context
import com.bizpulse.app.data.db.BizPulseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module @InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton fun database(@ApplicationContext context: Context): BizPulseDatabase = BizPulseDatabase.getInstance(context)
}
