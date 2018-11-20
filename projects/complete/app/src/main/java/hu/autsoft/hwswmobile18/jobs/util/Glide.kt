@file:Suppress("NOTHING_TO_INLINE")

package hu.autsoft.hwswmobile18.jobs.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

inline fun ImageView.load(path: String?, withCache: Boolean = true) {
    if (path == null) {
        setImageDrawable(null)
        return
    }

    val requestManager = Glide.with(this)

    if (!withCache) {
        requestManager.applyDefaultRequestOptions(
                RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE))

    }

    requestManager.load(path).into(this)
}
