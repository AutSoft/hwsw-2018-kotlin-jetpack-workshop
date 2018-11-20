package hu.autsoft.hwswmobile18.jobs.ui.joblist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import hu.autsoft.hwswmobile18.jobs.R
import hu.autsoft.hwswmobile18.jobs.arch.BaseFragment
import hu.autsoft.hwswmobile18.jobs.arch.exhaustive
import hu.autsoft.hwswmobile18.jobs.arch.getViewModelFromFactory
import hu.autsoft.hwswmobile18.jobs.arch.navigator
import hu.autsoft.hwswmobile18.jobs.ui.jobdetail.JobDetailFragment
import kotlinx.android.synthetic.main.fragment_job_list.*

class JobListFragment : BaseFragment<JobListViewState, JobListViewModel>(), JobListingAdapter.Listener {

    override fun provideViewModel() = getViewModelFromFactory()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_job_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private lateinit var adapter: JobListingAdapter

    private fun setupRecyclerView() {
        adapter = JobListingAdapter()
        adapter.listener = this
        jobListings.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        viewModel.load()
    }

    override fun render(viewState: JobListViewState) {
        when (viewState) {
            is JobListReady -> {
                progressBar.isVisible = false
                jobListings.isVisible = true

                adapter.submitList(viewState.jobListings)
            }
            is Loading -> {
                progressBar.isVisible = true
                jobListings.isVisible = false
            }
        }.exhaustive
    }

    override fun onJobListingSelected(jobListingId: String) {
        navigator?.add(JobDetailFragment.newInstance(jobListingId))
    }

}
