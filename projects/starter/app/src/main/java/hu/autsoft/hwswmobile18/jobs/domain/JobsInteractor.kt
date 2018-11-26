package hu.autsoft.hwswmobile18.jobs.domain

import hu.autsoft.hwswmobile18.jobs.data.network.NetworkDataSource
import hu.autsoft.hwswmobile18.jobs.domain.model.JobDetails
import hu.autsoft.hwswmobile18.jobs.domain.model.JobListing
import javax.inject.Inject

class JobsInteractor @Inject constructor(
        private val networkDataSource: NetworkDataSource
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
        return networkDataSource.getJobDetailsById(jobId).url
    }

}
