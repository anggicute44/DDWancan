package id.app.ddwancan.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

// Model ini disederhanakan untuk sub-koleksi
data class Comment(
    @get:PropertyName("id_user") @set:PropertyName("id_user")
    var id_user: String = "",

    @get:PropertyName("komentar") @set:PropertyName("komentar")
    var komentar: String = "",

    @get:PropertyName("waktu") @set:PropertyName("waktu")
    var waktu: Timestamp? = null
) {
    // Firestore membutuhkan constructor kosong
    constructor() : this("", "", null)
}

// CommentRequest tidak lagi relevan dengan alur Firebase, namun dibiarkan agar tidak error
data class CommentRequest(
    val articleId: String,
    val name: String,
    val message: String
)
