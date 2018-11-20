package hu.autsoft.hwswmobile18.jobs.di

import dagger.Component
import hu.autsoft.hwswmobile18.jobs.arch.InjectedFragment
import hu.autsoft.hwswmobile18.jobs.data.disk.DiskModule
import hu.autsoft.hwswmobile18.jobs.data.network.NetworkModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ViewModelModule::class,
    ApplicationModule::class,
    NetworkModule::class,
    DiskModule::class
])
interface AppComponent {

    fun inject(injectedFragment: InjectedFragment)

}
