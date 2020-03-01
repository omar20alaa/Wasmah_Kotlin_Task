package app.task.network

import app.task.model.RepositoryModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRequest {

//============================================================================================

    @GET("JeffreyWay/repos")
    fun fetchRepositories(
            @Query("per_page") per_page: String?,
            @Query("page") page: String?): Call<ArrayList<RepositoryModel>> // fetchRepositories

//============================================================================================
}