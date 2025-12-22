package id.app.ddwancan.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import id.app.ddwancan.data.model.Comment
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.admin.ManageCommentsScreen

class ManageCommentsActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()
    // Menyimpan Pair: (Object Comment, Full Document Path untuk referensi hapus)
    private val commentsState = mutableStateListOf<Pair<Comment, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchAllComments()

        setContent {
            DDwancanTheme {
                var showDialog by remember { mutableStateOf(false) }
                var selectedPath by remember { mutableStateOf<String?>(null) }

                ManageCommentsScreen(
                    commentList = commentsState,
                    onBackClick = { finish() },
                    onDeleteClick = { docPath ->
                        selectedPath = docPath
                        showDialog = true
                    }
                )

                if (showDialog && selectedPath != null) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Aksi Komentar") },
                        text = { Text("Pilih tindakan untuk komentar ini:") },
                        confirmButton = {
                            TextButton(onClick = {
                                // Set status to "hide"
                                setCommentStatus(selectedPath!!, "hide")
                                showDialog = false
                            }) {
                                Text("Hide", color = Color.Black)
                            }
                        },
                        dismissButton = {
                            Row {
                                TextButton(onClick = {
                                    // Set status to delete
                                    setCommentStatus(selectedPath!!, "delete")
                                    showDialog = false
                                }) {
                                    Text("Delete", color = Color.Red)
                                }
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Batal")
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun fetchAllComments() {
        // Karena sekarang semua ada di root collection "Comment", querynya sangat mudah:
        db.collection("Comment")
            .orderBy("waktu", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                commentsState.clear()
                for (document in result) {
                    try {
                        val comment = document.toObject(Comment::class.java)
                        // doc.id sudah cukup untuk menghapus
                        commentsState.add(Pair(comment, document.id))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            .addOnFailureListener {
                // ... handle error
            }
    }

    private fun deleteComment(docId: String) {
        // deprecated: not used. Use setCommentStatus instead.
        db.collection("Comment").document(docId).delete()
            .addOnSuccessListener {
                commentsState.removeIf { it.second == docId }
            }
    }

    private fun setCommentStatus(docId: String, status: String) {
        db.collection("Comment").document(docId)
            .update("status", status)
            .addOnSuccessListener {
                // update local state item
                for (i in commentsState.indices) {
                    if (commentsState[i].second == docId) {
                        val (c, path) = commentsState[i]
                        val newC = c.copy(status = status)
                        commentsState[i] = Pair(newC, path)
                        break
                    }
                }
            }
            .addOnFailureListener {
                // ignore for now
            }
    }
}