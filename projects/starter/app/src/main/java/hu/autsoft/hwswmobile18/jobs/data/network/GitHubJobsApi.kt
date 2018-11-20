package hu.autsoft.hwswmobile18.jobs.data.network

import hu.autsoft.hwswmobile18.jobs.data.network.model.Position
import kotlinx.coroutines.experimental.Deferred
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubJobsApi {

    @GET("/positions.json")
    fun getPositions(
            @Query("search") searchTerm: String? = null,
            @Query("location") location: String? = null,
            @Query("markdown") markdown: Boolean = true,
            @Query("full_time") fullTime: String? = null
    ): Deferred<List<Position>>

    @GET("/positions/{positionId}.json")
    fun getPositionById(
            @Path("positionId") positionId: String
    ): Deferred<Position>

}
