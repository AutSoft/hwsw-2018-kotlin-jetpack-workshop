package hu.autsoft.hwswmobile18.jobs.arch

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.CallSuper
import android.view.View


abstract class BaseFragment<VS : Any, VM : BaseViewModel<VS>> : InjectedFragment() {

    protected lateinit var viewModel: VM

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = provideViewModel()

        viewModel.state.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { render(it) }
        })
        viewModel.events.observe(viewLifecycleOwner, Observer { event ->
            event?.let { onEvent(it) }
        })
    }

    abstract fun provideViewModel(): VM

    abstract fun render(viewState: VS)

    open fun onEvent(event: OneShotEvent) {}

}

inline fun <F : BaseFragment<VS, VM>, VS, reified VM : BaseViewModel<VS>> F.getViewModelFromFactory(): VM {
    return ViewModelProviders.of(this, viewModelFactory).get(VM::class.java)
}
