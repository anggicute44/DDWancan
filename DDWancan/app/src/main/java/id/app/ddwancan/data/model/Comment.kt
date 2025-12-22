package id.app.ddwancan.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

// Model utama untuk Firebase (Menyimpan data komentar dari DB)
data class Comment(
    @DocumentId
    val id: String = "",

    @get:PropertyName("id_user") @set:PropertyName("id_user")
    var id_user: String = "",

    @get:PropertyName("nama_user") @set:PropertyName("nama_user")
    var nama_user: String = "",

    @get:PropertyName("komentar") @set:PropertyName("komentar")
    var komentar: String = "",

    // 👇 Field untuk mendukung Struktur Flat (tidak bersarang)
    @get:PropertyName("article_url") @set:PropertyName("article_url")
    var article_url: String = "",

    @get:PropertyName("source_id") @set:PropertyName("source_id")
    var source_id: String = "",

    @get:PropertyName("waktu") @set:PropertyName("waktu")
    var waktu: Timestamp? = null
,
    @get:PropertyName("warningTotal") @set:PropertyName("warningTotal")
    var warningTotal: Int = 0,

    @get:PropertyName("status") @set:PropertyName("status")
    var status: String = "ok"
,
    @get:PropertyName("avatar") @set:PropertyName("avatar")
    var avatar: Int = 0
) {
    // Constructor kosong wajib untuk Firestore deserialization
    constructor() : this("", "", "", "", "", "", null, 0, "ok")
}

// 👇 CommentRequest ditambahkan di sini
// Class ini berguna jika Anda masih memiliki kode API lama atau
// ingin membungkus data input dari UI sebelum dikirim ke ViewModel.
data class CommentRequest(
    val articleId: String,
    val name: String,
    val message: String
)