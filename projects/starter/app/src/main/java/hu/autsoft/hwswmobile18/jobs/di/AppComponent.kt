package hu.autsoft.hwswmobile18.jobs.di

import dagger.Component
import hu.autsoft.hwswmobile18.jobs.arch.InjectedFragment
import hu.autsoft.hwswmobile18.jobs.data.network.NetworkModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApplicationModule::class,
    ViewModelModule::class,
    NetworkModule::class
])
interface AppComponent {

    fun inject(injectedFragment: InjectedFragment)

}
