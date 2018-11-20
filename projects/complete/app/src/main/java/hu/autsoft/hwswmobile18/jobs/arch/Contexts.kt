package hu.autsoft.hwswmobile18.jobs.arch

import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.asCoroutineDispatcher
import java.util.concurrent.Executors

object Contexts {

    val UI = Dispatchers.Main
    val IO = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

}
