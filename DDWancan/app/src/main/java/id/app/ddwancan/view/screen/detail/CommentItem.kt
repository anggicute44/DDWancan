package id.app.ddwancan.view.screen.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.ddwancan.data.model.Comment
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CommentItem(
    comment: Comment,
    isAdmin: Boolean = false, // Default false jika tidak dikirim
    onDeleteClick: (String) -> Unit = {}
) {
    // Formatter tanggal sederhana
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val dateString = comment.waktu?.toDate()?.let { dateFormatter.format(it) } ?: "Baru saja"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5) // Warna abu-abu muda
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
                // Icon Profile Avatar (Placeholder)
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Info User & Waktu
                Column(modifier = Modifier.weight(1f)) {
                    // Menampilkan ID User (atau Nama jika ada di masa depan)
                    Text(
                        text = if (comment.id_user.isNotBlank()) "User: ${comment.id_user.take(6)}..." else "Anonymous",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    // Menampilkan Waktu
                    Text(
                        text = dateString,
                        fontSize = 10.sp,
                        color = Color.Gray
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
                            tint = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- ISI KOMENTAR ---
            Text(
                text = comment.komentar,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}