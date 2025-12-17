package id.app.ddwancan.data.model

data class Comment(
    val id: Int? = null,
    val articleId: String,
    val name: String,
    val message: String,
    val createdAt: String? = null
)

data class CommentRequest(
    val articleId: String, // Tambahkan ini agar backend tahu ID artikelnya
    val name: String,
    val message: String
)
