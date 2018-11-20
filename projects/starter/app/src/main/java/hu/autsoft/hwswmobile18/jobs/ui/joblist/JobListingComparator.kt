package hu.autsoft.hwswmobile18.jobs.ui.joblist

import android.support.v7.util.DiffUtil
import hu.autsoft.hwswmobile18.jobs.ui.joblist.JobListPresenter.JobListing

object JobListingComparator : DiffUtil.ItemCallback<JobListing>() {

    override fun areItemsTheSame(oldItem: JobListing, newItem: JobListing): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: JobListing, newItem: JobListing): Boolean {
        return oldItem == newItem
    }

}
