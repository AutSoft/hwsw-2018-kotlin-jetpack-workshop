package hu.autsoft.hwswmobile18.jobs.domain.model

data class JobListing(
        val id: String,
        val title: String,
        val company: String,
        val location: String,
        val companyLogo: String?
)
