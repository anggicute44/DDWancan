package id.app.ddwancan.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Comment(
    // 1. TAMBAHAN PENTING: @DocumentId
    // Field ini akan otomatis diisi oleh Firestore dengan ID dokumen saat data diambil (get).
    // Admin butuh ID ini untuk menghapus komentar spesifik.
    @DocumentId
    val id: String = "",

    @get:PropertyName("id_user") @set:PropertyName("id_user")
    var id_user: String = "",

    @get:PropertyName("komentar") @set:PropertyName("komentar")
    var komentar: String = "",

    @get:PropertyName("waktu") @set:PropertyName("waktu")
    var waktu: Timestamp? = null
) {
    // Constructor kosong diperlukan Firestore untuk deserialization.
    // Kita update constructor ini agar sesuai dengan jumlah parameter di atas (4 parameter).
    constructor() : this("", "", "", null)
}

// Biarkan saja agar tidak error di bagian lain code yang belum dihapus
data class CommentRequest(
    val articleId: String,
    val name: String,
    val message: String
)