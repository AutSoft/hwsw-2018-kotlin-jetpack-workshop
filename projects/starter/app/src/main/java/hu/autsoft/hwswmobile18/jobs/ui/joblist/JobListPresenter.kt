package hu.autsoft.hwswmobile18.jobs.ui.joblist

import hu.autsoft.hwswmobile18.jobs.arch.Contexts
import hu.autsoft.hwswmobile18.jobs.domain.JobsInteractor
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject

class JobListPresenter @Inject constructor(
        private val jobsInteractor: JobsInteractor
) {

    suspend fun getJobListings(): List<JobListing> = withContext(Contexts.IO) {
        jobsInteractor.getAllJobListings().map {
            JobListing(
                    id = it.id,
                    title = it.title,
                    company = it.company,
                    location = it.location,
                    imageUrl = it.companyLogo
            )
        }
    }

    data class JobListing(
            val id: String,
            val title: String,
            val company: String,
            val location: String,
            val imageUrl: String?
    )

}
