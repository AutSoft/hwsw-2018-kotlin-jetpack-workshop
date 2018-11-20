package hu.autsoft.hwswmobile18.jobs.data.disk

import hu.autsoft.hwswmobile18.jobs.data.disk.model.JobUrl
import hu.autsoft.hwswmobile18.jobs.domain.model.JobDetails
import javax.inject.Inject

class DiskDataSource @Inject constructor(
        private val jobUrlDao: JobUrlDao
) {

    fun saveJobUrl(jobDetails: JobDetails) {
        val jobUrl = jobDetails.let {
            JobUrl(
                    id = it.id,
                    url = it.url
            )
        }
        jobUrlDao.upsert(jobUrl)
    }

    fun getJobUrl(jobId: String): String? {
        return jobUrlDao.getJobUrlById(jobId)?.url
    }

}
