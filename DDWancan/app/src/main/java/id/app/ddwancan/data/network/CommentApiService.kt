package id.app.ddwancan.data.network

import id.app.ddwancan.data.model.Comment
import id.app.ddwancan.data.model.CommentRequest
import retrofit2.http.*

interface CommentApiService {

    @GET("comments")
    suspend fun getComments(
        @Query("articleId") articleId: String
    ): List<Comment>

    @POST("comments")
    suspend fun postComment(
        @Body request: CommentRequest
    )
}
