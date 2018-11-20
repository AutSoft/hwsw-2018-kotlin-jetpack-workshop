package hu.autsoft.hwswmobile18.jobs.arch

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import hu.autsoft.hwswmobile18.jobs.injector
import javax.inject.Inject

abstract class InjectedFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

}
