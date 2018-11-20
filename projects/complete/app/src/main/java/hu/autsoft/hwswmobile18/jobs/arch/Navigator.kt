package hu.autsoft.hwswmobile18.jobs.arch

import android.support.v4.app.Fragment

interface Navigator {

    fun add(fragment: Fragment)
    fun pop()

}

val Fragment.navigator: Navigator?
    get() {
        val activity = activity ?: return null
        return (activity as? Navigator)
                ?: throw IllegalStateException("Fragment is not in an Activity that implements Navigator")
    }
