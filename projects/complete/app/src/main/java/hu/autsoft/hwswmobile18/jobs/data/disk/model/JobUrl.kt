package hu.autsoft.hwswmobile18.jobs.data.disk.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class JobUrl(
        @PrimaryKey
        val id: String,
        val url: String
)
