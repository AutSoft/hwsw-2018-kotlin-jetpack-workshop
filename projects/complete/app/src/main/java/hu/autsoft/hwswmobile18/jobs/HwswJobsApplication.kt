package hu.autsoft.hwswmobile18.jobs

import android.app.Application
import hu.autsoft.hwswmobile18.jobs.di.AppComponent
import hu.autsoft.hwswmobile18.jobs.di.ApplicationModule
import hu.autsoft.hwswmobile18.jobs.di.DaggerAppComponent
import timber.log.Timber


lateinit var injector: AppComponent

class HwswJobsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        injector = DaggerAppComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }

}
