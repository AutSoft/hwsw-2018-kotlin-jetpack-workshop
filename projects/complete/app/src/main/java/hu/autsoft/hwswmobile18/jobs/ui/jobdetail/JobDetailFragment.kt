package hu.autsoft.hwswmobile18.jobs.ui.jobdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import hu.autsoft.hwswmobile18.jobs.R
import hu.autsoft.hwswmobile18.jobs.arch.BaseFragment
import hu.autsoft.hwswmobile18.jobs.arch.OneShotEvent
import hu.autsoft.hwswmobile18.jobs.arch.applyArgs
import hu.autsoft.hwswmobile18.jobs.arch.exhaustive
import hu.autsoft.hwswmobile18.jobs.arch.getViewModelFromFactory
import hu.autsoft.hwswmobile18.jobs.arch.requireArguments
import hu.autsoft.hwswmobile18.jobs.arch.requireString
import hu.autsoft.hwswmobile18.jobs.ui.jobdetail.JobDetailViewModel.BrowseUrlEvent
import hu.autsoft.hwswmobile18.jobs.util.load
import kotlinx.android.synthetic.main.fragment_job_detail.*
import kotlinx.android.synthetic.main.layout_job_detail.*


class JobDetailFragment : BaseFragment<JobDetailViewState, JobDetailViewModel> {

    override fun provideViewModel() = getViewModelFromFactory()

    //region Arguments
    @Suppress("ConvertSecondaryConstructorToPrimary")
    @Deprecated(message = "Use newInstance instead", replaceWith = ReplaceWith("JobDetailFragment.newInstance()"))
    constructor()

    companion object {
        private const val JOB_LISTING_ID = "JOB_LISTING_ID"

        @Suppress("DEPRECATION")
        fun newInstance(jobListingId: String): JobDetailFragment {
            return JobDetailFragment().applyArgs {
                putString(JOB_LISTING_ID, jobListingId)
            }
        }
    }

    private var jobListingId: String = ""

    private fun initArguments() {
        jobListingId = requireArguments().requireString(JOB_LISTING_ID)
    }
    //endregion

    private object Flipper {
        const val LOADING = 0
        const val READY = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_job_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initArguments()

        setupApplyButton()
    }

    private fun setupApplyButton() {
        applyButton.setOnClickListener {
            viewModel.browse(jobListingId)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.load(jobListingId)
    }

    override fun render(viewState: JobDetailViewState) {
        when (viewState) {
            is Loading -> {
                viewFlipper.displayedChild = Flipper.LOADING
            }
            is JobDetailLoaded -> {
                viewFlipper.displayedChild = Flipper.READY
                showJobDetails(viewState.detailedJob)
            }
        }.exhaustive
    }

    private fun showJobDetails(job: JobDetailPresenter.DetailedJob) {
        positionInfoText.text = job.positionInfo
        locationText.text = job.location

        descriptionText.text = Html.fromHtml(job.description)

        companyLogoImage.load(job.companyLogo)
        companyLogoImage.isVisible = job.companyLogo != null
    }

    override fun onEvent(event: OneShotEvent) {
        when (event) {
            is BrowseUrlEvent -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(event.url)
                startActivity(intent)
            }
        }
    }

}
