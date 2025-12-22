package id.app.ddwancan.data.network

import id.app.ddwancan.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("category") category: String?,
        @Query("apiKey") apiKey: String
    ): NewsResponse
}