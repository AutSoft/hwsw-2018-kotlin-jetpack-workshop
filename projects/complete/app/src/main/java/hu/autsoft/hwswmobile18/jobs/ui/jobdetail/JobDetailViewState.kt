package hu.autsoft.hwswmobile18.jobs.ui.jobdetail

import hu.autsoft.hwswmobile18.jobs.ui.jobdetail.JobDetailPresenter.DetailedJob

sealed class JobDetailViewState

data class JobDetailLoaded(val detailedJob: DetailedJob) : JobDetailViewState()

object Loading : JobDetailViewState()
