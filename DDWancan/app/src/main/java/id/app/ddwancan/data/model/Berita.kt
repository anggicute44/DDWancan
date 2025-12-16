package id.app.ddwancan.data.model

data class Berita(
    val id: Int,
    val judul: String,
    val deskripsi: String,
    val gambar: String,   // atau Int kalau drawable
    val views: Int,
    val createdAt: String
)
