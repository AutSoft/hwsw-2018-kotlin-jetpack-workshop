package hu.autsoft.hwswmobile18.jobs.data.disk

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import hu.autsoft.hwswmobile18.jobs.data.disk.model.JobUrl

@Dao
interface JobUrlDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(jobUrl: JobUrl)

    @Query("SELECT * FROM joburl WHERE id = :id")
    fun getJobUrlById(id: String): JobUrl?


}
