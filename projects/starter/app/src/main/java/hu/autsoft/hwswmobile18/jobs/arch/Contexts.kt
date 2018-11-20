package hu.autsoft.hwswmobile18.jobs.arch

import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.android.Main

object Contexts {

    val UI = Dispatchers.Main
    val IO = Dispatchers.IO

}
