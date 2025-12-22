package id.app.ddwancan.view.screen.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import id.app.ddwancan.data.model.Comment
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CommentItem(
    comment: Comment,
    isAdmin: Boolean = false, // Default false jika tidak dikirim
    onDeleteClick: (String) -> Unit = {},
    currentUserId: String? = null,
    onReport: (String) -> Unit = {}
) {
    // Formatter tanggal sederhana
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val dateString = comment.waktu?.toDate()?.let { dateFormatter.format(it) } ?: "Baru saja"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // --- HEADER: Icon User, ID User, Tanggal, & Tombol Hapus ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                    // Profile Avatar & Name: load live from User document so updates reflect immediately
                    val ctx = LocalContext.current
                    var userName by remember { mutableStateOf<String?>(null) }
                    var userAvatar by remember { mutableStateOf(0) }

                    DisposableEffect(key1 = comment.id_user) {
                        val db = FirebaseFirestore.getInstance()
                        val listener = db.collection("User").document(comment.id_user)
                            .addSnapshotListener { doc, e ->
                                if (doc != null && doc.exists()) {
                                    userName = doc.getString("name") ?: ""
                                    userAvatar = doc.getLong("avatar")?.toInt() ?: 0
                                } else {
                                    userName = null
                                    userAvatar = 0
                                }
                            }
                        onDispose { listener.remove() }
                    }

                    // render avatar image using user's current avatar index
                    val resId = ctx.resources.getIdentifier("avatar${userAvatar}", "drawable", ctx.packageName)
                    if (resId != 0) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = resId),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                Spacer(modifier = Modifier.width(8.dp))

                // Info User & Waktu
                Column(modifier = Modifier.weight(1f)) {
                    // Tampilkan nama user dari dokumen User jika tersedia, fallback ke UID
                    val displayName = userName?.takeIf { it.isNotBlank() }
                        ?: if (comment.id_user.isNotBlank()) "User: ${comment.id_user.take(6)}..." else "Anonymous"

                    Text(
                        text = displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Menampilkan Waktu
                    Text(
                        text = dateString,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // --- TOMBOL DELETE (Hanya muncul jika Admin) ---
                if (isAdmin) {
                    IconButton(
                        onClick = { onDeleteClick(comment.id) }, // Mengirim ID dokumen ke callback
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus Komentar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                // --- TOMBOL REPORT (Hanya untuk user biasa jika komentar masih ok)
                // Sembunyikan tombol jika komentar milik user saat ini ---
                if (!isAdmin && comment.status == "ok" && comment.id_user != currentUserId) {
                    var showReportDialog by remember { mutableStateOf(false) }
                    if (showReportDialog) {
                        AlertDialog(
                            onDismissRequest = { showReportDialog = false },
                            title = { Text("Lapor Komentar?") },
                            text = { Text("Apakah Anda yakin ingin melaporkan komentar ini sebagai pelanggaran?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    showReportDialog = false
                                    onReport(comment.id)
                                }) { Text("Ya") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showReportDialog = false }) { Text("Batal") }
                            }
                        )
                    }

                    IconButton(onClick = { showReportDialog = true }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Warning,
                            contentDescription = "Report",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- ISI KOMENTAR ---
            Text(
                text = comment.komentar,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
