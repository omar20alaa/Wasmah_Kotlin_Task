package app.task.network

import app.task.global.Constant
import app.task.model.RepositoryModel
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class retrofitClient {

    private val apiRequest: ApiRequest
    var logging = HttpLoggingInterceptor()

//============================================================================================
    fun fetchRepositories(page_index: String?): Call<ArrayList<RepositoryModel>> {
        return apiRequest.fetchRepositories(
                Constant.Limit,
                page_index
        )
    } // fetchSliders

//============================================================================================
    companion object {

//============================================================================================
        // get Instance
        @JvmStatic
        var instance: retrofitClient? = null
            get() {
                if (field == null) {
                    field = retrofitClient()
                }
                return field
            }
            private set
    }

//============================================================================================

    init {
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        val gson = GsonBuilder()
                .setLenient()
                .create()
        val retrofit = Retrofit.Builder()
                .baseUrl(Constant.Base_Url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build()
        apiRequest = retrofit.create(ApiRequest::class.java)
    } // Retrofit Client Method

}