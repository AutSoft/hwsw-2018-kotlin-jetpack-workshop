package hu.autsoft.hwswmobile18.jobs.data.network.model

import com.google.gson.annotations.SerializedName

class Position(

        @SerializedName("id")
        val id: String,

        @SerializedName("created_at")
        val createdAt: String,

        @SerializedName("title")
        val title: String,

        @SerializedName("location")
        val location: String,

        @SerializedName("type")
        val type: String,

        @SerializedName("description")
        val description: String,

        @SerializedName("how_to_apply")
        val howToApply: String,

        @SerializedName("company")
        val company: String,

        @SerializedName("company_url")
        val companyUrl: String?,

        @SerializedName("company_logo")
        val companyLogo: String?,

        @SerializedName("url")
        val url: String

)
