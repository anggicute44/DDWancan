package id.app.ddwancan.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import id.app.ddwancan.data.model.Comment

class CommentViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _comments = mutableStateOf<List<Comment>>(emptyList())
    val comments: State<List<Comment>> = _comments

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // --- 1. LOAD COMMENTS (DENGAN STRUKTUR FLAT) ---
    fun loadComments(sourceId: String, articleUrl: String) {
        _loading.value = true

        // Tidak perlu encode URL lagi untuk path, karena sekarang URL jadi value field
        // Query: Cari di koleksi "Comment" yang field "article_url" == URL berita ini
        db.collection("Comment")
            .whereEqualTo("article_url", articleUrl) // <--- FILTER PENTING
            .orderBy("waktu", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                _loading.value = false
                if (e != null) {
                    Log.e("CommentViewModel", "Error loading: ${e.message}")
                    _error.value = e.localizedMessage
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.toObjects(Comment::class.java)
                    // Hanya tampilkan komentar yang statusnya "ok" untuk user biasa
                    _comments.value = list.filter { it.status == "ok" }
                        .sortedBy { it.waktu?.toDate()?.time ?: 0L }
                }
            }
    }

    // --- 2. SEND COMMENT (DENGAN STRUKTUR FLAT) ---
    fun sendComment(
        sourceId: String,
        articleUrl: String,
        userId: String?,
        message: String,
        onDone: () -> Unit
    ) {
        if (userId.isNullOrBlank()) {
            _error.value = "User not logged in."
            return
        }

        // Ambil nama user dulu
        db.collection("User").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                var userName = "Unknown User"
                var userAvatar = 0
                if (documentSnapshot.exists()) {
                    userName = documentSnapshot.getString("name") ?: "Unknown"
                    userAvatar = documentSnapshot.getLong("avatar")?.toInt() ?: 0
                }

                // DATA YANG DISIMPAN (FLAT)
                val commentData = hashMapOf(
                    "id_user" to userId,
                    "nama_user" to userName,
                    "komentar" to message,
                    "avatar" to userAvatar,
                    "waktu" to FieldValue.serverTimestamp(),

                    // Kita simpan ID Berita di dalam dokumen komentar
                    "article_url" to articleUrl,
                    "source_id" to sourceId,
                    "warningTotal" to 0,
                    "status" to "ok"
                )

                // Simpan langsung ke root collection "Comment"
                db.collection("Comment")
                    .add(commentData)
                    .addOnSuccessListener {
                        onDone()
                    }
                    .addOnFailureListener { e ->
                        _error.value = e.localizedMessage
                    }
            }
            .addOnFailureListener {
                _error.value = "Gagal ambil user"
            }
    }

    // --- 4. REPORT COMMENT ---
    fun reportComment(
        commentId: String,
        reporterId: String,
        onSuccess: () -> Unit,
        onAlreadyReported: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Ambil dokumen komentar dulu untuk memeriksa kepemilikan
        db.collection("Comment").document(commentId).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    onFailure("Komentar tidak ditemukan")
                    return@addOnSuccessListener
                }

                val ownerId = doc.getString("id_user")
                if (ownerId != null && ownerId == reporterId) {
                    onFailure("Anda tidak bisa melaporkan komentar sendiri")
                    return@addOnSuccessListener
                }

                // Cek apakah reporter sudah pernah report comment ini
                db.collection("reports")
                    .whereEqualTo("comment_id", commentId)
                    .whereEqualTo("reporter_id", reporterId)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            onAlreadyReported()
                            return@addOnSuccessListener
                        }

                        // Tambah dokumen report baru
                        val reportData = hashMapOf(
                            "comment_id" to commentId,
                            "reporter_id" to reporterId,
                            "waktu" to FieldValue.serverTimestamp()
                        )

                        db.collection("reports").add(reportData)
                            .addOnSuccessListener {
                                // Increment warningTotal pada comment
                                db.collection("Comment").document(commentId)
                                    .update("warningTotal", FieldValue.increment(1))
                                    .addOnSuccessListener { onSuccess() }
                                    .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Unknown") }
                            }
                            .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Unknown") }
                    }
                    .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Unknown") }
            }
            .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Unknown") }
    }

    // --- 5. SET COMMENT STATUS (hide / delete) ---
    fun setCommentStatus(commentId: String, status: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.collection("Comment").document(commentId)
            .update("status", status)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Unknown") }
    }

    // --- 3. DELETE COMMENT (ADMIN) ---
    fun deleteComment(
        // sourceId & articleUrl TIDAK DIPERLUKAN LAGI UNTUK HAPUS (karena pathnya langsung)
        commentId: String,
        onSuccess: () -> Unit
    ) {
        // Hapus langsung berdasarkan ID dokumen di root collection
        db.collection("Comment")
            .document(commentId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                _error.value = "Gagal menghapus: ${e.localizedMessage}"
            }
    }
}