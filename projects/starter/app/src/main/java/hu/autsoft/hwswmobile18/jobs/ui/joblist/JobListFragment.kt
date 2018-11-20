package hu.autsoft.hwswmobile18.jobs.ui.joblist

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.toast
import hu.autsoft.hwswmobile18.jobs.R
import hu.autsoft.hwswmobile18.jobs.arch.BaseFragment
import kotlinx.android.synthetic.main.fragment_job_list.*

class JobListFragment : BaseFragment<JobListViewState, JobListViewModel>(), JobListingAdapter.Listener {

    override fun provideViewModel(): JobListViewModel {
        return ViewModelProviders.of(this, viewModelFactory).get(JobListViewModel::class.java)
    }

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
        }
    }

    override fun onJobListingSelected(jobListingId: String) {
        requireContext().toast("Job listing selected: $jobListingId")
    }

}
