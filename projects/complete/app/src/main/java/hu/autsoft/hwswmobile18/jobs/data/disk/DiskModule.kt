package hu.autsoft.hwswmobile18.jobs.data.disk

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class DiskModule {

    @Provides
    @Singleton
    fun provideJobDatabase(context: Context): JobDatabase {
        return Room.databaseBuilder(context, JobDatabase::class.java, "jobdb").build()
    }

    @Provides
    @Singleton
    fun provideJobUrlDao(jobDatabase: JobDatabase): JobUrlDao {
        return jobDatabase.jobUrlDao()
    }

}
