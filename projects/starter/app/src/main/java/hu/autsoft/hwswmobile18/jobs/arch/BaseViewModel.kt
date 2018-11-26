package hu.autsoft.hwswmobile18.jobs.arch

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<VS : Any>(initialState: VS) : ViewModel(), CoroutineScope {

    //region State handling
    private val _state = MutableLiveData<VS>()

    init {
        _state.value = initialState
    }

    val state: LiveData<VS> = _state

    protected var viewState: VS
        get() = _state.value!!
        set(value) {
            _state.value = value
        }
    //endregion

    //region CoroutineScope implementation
    private val rootJob = Job()

    final override val coroutineContext: CoroutineContext = Contexts.UI + rootJob

    override fun onCleared() {
        super.onCleared()
        rootJob.cancel()
    }
    //endregion

    //region Event handling
    private val viewEvents = SingleShotLiveData<OneShotEvent>()

    val events: LiveData<OneShotEvent> = viewEvents

    protected fun postEvent(event: OneShotEvent) = viewEvents.postValue(event)
    //endregion

}
