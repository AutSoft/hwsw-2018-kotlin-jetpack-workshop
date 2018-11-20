package hu.autsoft.hwswmobile18.jobs.data.disk

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import hu.autsoft.hwswmobile18.jobs.data.disk.model.JobUrl

@Database(
        version = 1,
        entities = [
            JobUrl::class
        ]
)
abstract class JobDatabase : RoomDatabase() {

    abstract fun jobUrlDao(): JobUrlDao

}
