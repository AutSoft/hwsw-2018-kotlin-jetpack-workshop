package hu.autsoft.hwswmobile18.jobs.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val applicationContext: Application) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return applicationContext
    }

}
