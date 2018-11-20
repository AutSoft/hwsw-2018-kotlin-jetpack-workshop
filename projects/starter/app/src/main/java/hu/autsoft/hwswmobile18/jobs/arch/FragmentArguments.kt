@file:Suppress("NOTHING_TO_INLINE")

package hu.autsoft.hwswmobile18.jobs.arch

import android.os.Bundle
import android.support.v4.app.Fragment

inline fun <T : Fragment> T.applyArgs(argSetup: Bundle.() -> Unit): T = apply {
    val bundle = Bundle()
    bundle.argSetup()
    arguments = bundle
}

inline fun Fragment.requireArguments(): Bundle {
    return arguments ?: throw IllegalStateException("Fragment has no arguments Bundle.")
}

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
inline fun Bundle.requireString(key: String): String {
    return if (containsKey(key)) getString(key) else throw IllegalStateException("Bundle has no key $key")
}
