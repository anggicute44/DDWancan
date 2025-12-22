package id.app.ddwancan.view.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.ddwancan.data.model.Comment
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCommentsScreen(
    commentList: List<Pair<Comment, String>>, // Pair: Object Comment & Document Path (untuk delete)
    onBackClick: () -> Unit,
    onDeleteClick: (String) -> Unit // Menerima Path Dokumen
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Semua Komentar") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (commentList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada komentar.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Group by article_url
                val grouped = commentList.groupBy { it.first.article_url }
                grouped.forEach { (articleUrl, listPairs) ->
                    // Article header
                    item {
                        Text(text = "Berita: ${articleUrl.take(50)}", fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // Sort comments: status ok first, then warningTotal desc, then waktu desc
                    val sorted = listPairs.sortedWith(compareBy<Pair<Comment, String>> {
                        if (it.first.status == "ok") 0 else 1
                    }.thenByDescending { it.first.warningTotal }
                        .thenByDescending { it.first.waktu?.toDate()?.time ?: 0L })

                    items(sorted) { (comment, docPath) ->
                        AdminCommentItem(
                            comment = comment,
                            onDelete = { onDeleteClick(docPath) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCommentItem(comment: Comment, onDelete: () -> Unit) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateString = comment.waktu?.toDate()?.let { dateFormatter.format(it) } ?: "-"

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "User ID: ${comment.id_user.take(6)}...",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = comment.komentar,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
                Text(
                    text = dateString,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
            Row {
                // Show warning count
                Text(text = "Reports: ${comment.warningTotal}", modifier = Modifier.align(Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                }
            }
        }
    }
}