package hu.autsoft.hwswmobile18.jobs.ui.joblist

import hu.autsoft.hwswmobile18.jobs.ui.joblist.JobListPresenter.JobListing

sealed class JobListViewState

data class JobListReady(val jobListings: List<JobListing>) : JobListViewState()

object Loading : JobListViewState()
