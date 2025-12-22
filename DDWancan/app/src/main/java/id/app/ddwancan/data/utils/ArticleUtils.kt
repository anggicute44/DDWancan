package id.app.ddwancan.data.utils

object ArticleUtils {
    // Gunakan hashCode dari URL untuk menghasilkan ID dokumen yang sama dengan yang digunakan saat import
    fun docIdFromUrl(url: String): String = url.hashCode().toString()
}
