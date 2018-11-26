package hu.autsoft.hwswmobile18.jobs.domain

import hu.autsoft.hwswmobile18.jobs.data.disk.DiskDataSource
import hu.autsoft.hwswmobile18.jobs.data.network.NetworkDataSource
import hu.autsoft.hwswmobile18.jobs.domain.model.JobDetails
import hu.autsoft.hwswmobile18.jobs.domain.model.JobListing
import timber.log.Timber
import javax.inject.Inject

class JobsInteractor @Inject constructor(
        private val networkDataSource: NetworkDataSource,
        private val diskDataSource: DiskDataSource
) {

    private fun String?.toHttpsUrl(): String? = this?.replaceFirst("http://", "https://")

    suspend fun getAllJobListings(): List<JobListing> {
        return networkDataSource.getAllJobListings().map {
            it.copy(companyLogo = it.companyLogo.toHttpsUrl())
        }
    }

    suspend fun getJobDetailsById(jobId: String): JobDetails {
        return networkDataSource.getJobDetailsById(jobId).let {
            it.copy(companyLogo = it.companyLogo.toHttpsUrl())
        }
    }

    suspend fun getJobUrlById(jobId: String): String {
        val jobUrl = diskDataSource.getJobUrl(jobId)

        if (jobUrl != null) {
            Timber.d("Found job url on disk")

            return jobUrl
        }

        Timber.d("Didn't find job url on disk")

        val position = networkDataSource.getJobDetailsById(jobId)

        Timber.d("Got job url from API")

        diskDataSource.saveJobUrl(position)

        Timber.d("Saved job url to disk")

        return position.url
    }

}
