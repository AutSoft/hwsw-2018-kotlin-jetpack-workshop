package hu.autsoft.hwswmobile18.jobs.ui.joblist

import hu.autsoft.hwswmobile18.jobs.arch.BaseViewModel
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class JobListViewModel @Inject constructor(
        private val jobListPresenter: JobListPresenter
) : BaseViewModel<JobListViewState>(Loading) {

    fun load() = launch {
        val jobListings = jobListPresenter.getJobListings()
        viewState = JobListReady(jobListings)
    }

}
