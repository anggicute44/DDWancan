package id.app.ddwancan.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import id.app.ddwancan.data.model.Comment
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class CommentViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _comments = mutableStateOf<List<Comment>>(emptyList())
    val comments: State<List<Comment>> = _comments

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun loadComments(sourceId: String, articleUrl: String) {
        _loading.value = true
        val documentId = URLEncoder.encode(articleUrl, StandardCharsets.UTF_8.toString())

        Log.d("CommentViewModel", "=== LOAD COMMENTS ===")
        Log.d("CommentViewModel", "sourceId: $sourceId")
        Log.d("CommentViewModel", "articleUrl: $articleUrl")
        Log.d("CommentViewModel", "documentId (encoded): $documentId")
        Log.d("CommentViewModel", "Firestore Path: Comment/$sourceId/Articles/$documentId/Comments")

        // Path baru yang lebih terstruktur
        db.collection("Comment")
            .document(sourceId)
            .collection("Articles")
            .document(documentId)
            .collection("Comments")
            .orderBy("waktu", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                _loading.value = false
                if (e != null) {
                    Log.e("CommentViewModel", "Error loading comments: ${e.message}", e)
                    _error.value = e.localizedMessage
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    Log.d("CommentViewModel", "Comments loaded: ${snapshot.size()} items")
                    _comments.value = snapshot.toObjects(Comment::class.java)
                }
            }
    }

    fun sendComment(
        sourceId: String,
        articleUrl: String,
        userId: String?,
        message: String,
        onDone: () -> Unit
    ) {
        Log.d("CommentViewModel", "=== SEND COMMENT ===")
        Log.d("CommentViewModel", "sourceId: $sourceId")
        Log.d("CommentViewModel", "articleUrl: $articleUrl")
        Log.d("CommentViewModel", "userId: $userId")
        Log.d("CommentViewModel", "message: $message")

        if (userId.isNullOrBlank()) {
            Log.e("CommentViewModel", "Error: User not logged in")
            _error.value = "User not logged in."
            return
        }

        val documentId = URLEncoder.encode(articleUrl, StandardCharsets.UTF_8.toString())
        val commentData = hashMapOf(
            "id_user" to userId,
            "komentar" to message,
            "waktu" to FieldValue.serverTimestamp()
        )

        Log.d("CommentViewModel", "documentId (encoded): $documentId")
        Log.d("CommentViewModel", "Firestore Path: Comment/$sourceId/Articles/$documentId/Comments")
        Log.d("CommentViewModel", "Comment Data: $commentData")

        // Path baru yang lebih terstruktur
        db.collection("Comment")
            .document(sourceId)
            .collection("Articles")
            .document(documentId)
            .collection("Comments")
            .add(commentData)
            .addOnSuccessListener { documentReference ->
                Log.d("CommentViewModel", "✓ Comment successfully added with ID: ${documentReference.id}")
                onDone()
            }
            .addOnFailureListener { e ->
                Log.e("CommentViewModel", "✗ Failed to add comment: ${e.message}", e)
                _error.value = e.localizedMessage
            }
    }
}
