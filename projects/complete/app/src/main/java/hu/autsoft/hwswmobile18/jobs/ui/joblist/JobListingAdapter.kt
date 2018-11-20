package hu.autsoft.hwswmobile18.jobs.ui.joblist

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.autsoft.hwswmobile18.jobs.R
import hu.autsoft.hwswmobile18.jobs.ui.joblist.JobListPresenter.JobListing
import hu.autsoft.hwswmobile18.jobs.util.load
import kotlinx.android.synthetic.main.row_job_listing.view.*

class JobListingAdapter : ListAdapter<JobListing, JobListingAdapter.JobListingViewHolder>(JobListingComparator) {

    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobListingViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.row_job_listing, parent, false)
        return JobListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: JobListingViewHolder, position: Int) {
        val jobListing = getItem(position)

        holder.jobListing = jobListing

        holder.title.text = jobListing.title
        holder.company.text = jobListing.company
        holder.location.text = jobListing.location

        holder.image.load(jobListing.imageUrl)
    }

    inner class JobListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.companyLogoImage
        val company = itemView.companyText
        val location = itemView.locationText
        val title = itemView.titleText

        var jobListing: JobListing? = null

        init {
            itemView.setOnClickListener {
                jobListing?.id?.let { jobId -> listener?.onJobListingSelected(jobId) }
            }
        }

    }

    interface Listener {
        fun onJobListingSelected(jobListingId: String)
    }

}
