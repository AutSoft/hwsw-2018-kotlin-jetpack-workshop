package hu.autsoft.hwswmobile18.jobs.domain.model

data class JobDetails(
        val id: String,
        val createdAt: String,
        val title: String,
        val location: String,
        val type: String,
        val description: String,
        val howToApply: String,
        val company: String,
        val companyUrl: String?,
        val companyLogo: String?,
        val url: String
)
