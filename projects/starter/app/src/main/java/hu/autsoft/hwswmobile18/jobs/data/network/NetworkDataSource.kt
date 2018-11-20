package hu.autsoft.hwswmobile18.jobs.data.network

import hu.autsoft.hwswmobile18.jobs.domain.model.JobDetails
import hu.autsoft.hwswmobile18.jobs.domain.model.JobListing
import javax.inject.Inject

class NetworkDataSource @Inject constructor(
        private val api: GitHubJobsApi
) {

    suspend fun getAllJobListings(): List<JobListing> {
        return api.getPositions()
                .await()
                .map {
                    JobListing(
                            id = it.id,
                            title = it.title,
                            company = it.company,
                            location = it.location,
                            companyLogo = it.companyLogo
                    )
                }
    }

    suspend fun getJobDetailsById(jobListingId: String): JobDetails {
        return api.getPositionById(jobListingId)
                .await()
                .let {
                    JobDetails(
                            id = it.id,
                            title = it.title,
                            company = it.company,
                            location = it.location,
                            createdAt = it.createdAt,
                            type = it.type,
                            description = it.description,
                            howToApply = it.howToApply,
                            companyLogo = it.companyLogo,
                            companyUrl = it.companyUrl,
                            url = it.url
                    )
                }
    }

}
