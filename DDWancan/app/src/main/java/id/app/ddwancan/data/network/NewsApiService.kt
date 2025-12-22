package id.app.ddwancan.data.network

import id.app.ddwancan.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("category") category: String?,
        @Query("sources") sources: String? = null,
        @Query("apiKey") apiKey: String
    ): NewsResponse

    @GET("v2/everything")
    suspend fun getEverything(
        @Query("q") q: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("domains") domains: String? = null,
        @Query("apiKey") apiKey: String
    ): NewsResponse
}


