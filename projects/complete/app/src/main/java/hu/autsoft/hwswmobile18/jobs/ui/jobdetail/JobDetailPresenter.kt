package hu.autsoft.hwswmobile18.jobs.ui.jobdetail

import hu.autsoft.hwswmobile18.jobs.arch.Contexts
import hu.autsoft.hwswmobile18.jobs.domain.JobsInteractor
import kotlinx.coroutines.withContext
import javax.inject.Inject

class JobDetailPresenter @Inject constructor(
        private val jobsInteractor: JobsInteractor
) {

    suspend fun getDetailedJob(jobListingId: String): DetailedJob = withContext(Contexts.IO) {
        jobsInteractor.getJobDetailsById(jobListingId).let {
            DetailedJob(
                    id = it.id,
                    positionInfo = "${it.title} @ ${it.company}",
                    location = it.location,
                    description = it.description,
                    companyLogo = it.companyLogo
            )
        }
    }

    suspend fun getUrl(jobListingId: String): String = withContext(Contexts.IO) {
        jobsInteractor.getJobUrlById(jobListingId)
    }

    class DetailedJob(
            val id: String,
            val positionInfo: String,
            val location: String,
            val description: String,
            val companyLogo: String?
    )

}
