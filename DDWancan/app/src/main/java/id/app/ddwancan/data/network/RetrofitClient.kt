package id.app.ddwancan.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // NOTE: Base URL ini mungkin perlu disesuaikan jika API komentar Anda ada di server yang berbeda
    private const val BASE_URL = "https://newsapi.org/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Service untuk API Berita
    val apiService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }

    // Service untuk API Komentar
    val commentService: CommentApiService by lazy {
        retrofit.create(CommentApiService::class.java)
    }
}
