package hu.autsoft.hwswmobile18.jobs.ui.jobdetail

import hu.autsoft.hwswmobile18.jobs.arch.BaseViewModel
import hu.autsoft.hwswmobile18.jobs.arch.OneShotEvent
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class JobDetailViewModel @Inject constructor(
        private val jobDetailPresenter: JobDetailPresenter
) : BaseViewModel<JobDetailViewState>(Loading) {

    class BrowseUrlEvent(val url: String) : OneShotEvent

    fun load(jobListingId: String) = launch {
        if (viewState is JobDetailLoaded) {
            return@launch
        }

        viewState = Loading

        val detailedJob = jobDetailPresenter.getDetailedJob(jobListingId)
        viewState = JobDetailLoaded(detailedJob)
    }

    fun browse(jobListingId: String) = launch {
        val url = jobDetailPresenter.getUrl(jobListingId)
        postEvent(BrowseUrlEvent(url))
    }

}
