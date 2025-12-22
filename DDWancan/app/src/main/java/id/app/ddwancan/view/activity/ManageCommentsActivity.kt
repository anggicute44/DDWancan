package id.app.ddwancan.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
                        title = { Text("Hapus Komentar?") },
                        text = { Text("Tindakan ini tidak dapat dibatalkan.") },
                        confirmButton = {
                            TextButton(onClick = {
                                deleteComment(selectedPath!!)
                                showDialog = false
                            }) {
                                Text("Hapus", color = Color.Red)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Batal")
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
        // Hapus langsung dari collection "Comment"
        db.collection("Comment").document(docId).delete()
            .addOnSuccessListener {
                commentsState.removeIf { it.second == docId }
            }
    }
}