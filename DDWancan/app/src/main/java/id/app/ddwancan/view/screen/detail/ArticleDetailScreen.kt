package id.app.ddwancan.view.screen.detail

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import id.app.ddwancan.data.utils.UserSession
import id.app.ddwancan.viewmodel.CommentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    sourceId: String,
    title: String,
    content: String,
    imageUrl: String?,
    articleUrl: String,
    onBack: () -> Unit,
    commentViewModel: CommentViewModel = viewModel()
) {
    val context = LocalContext.current
    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(articleUrl) {
        commentViewModel.loadComments(sourceId = sourceId, articleUrl = articleUrl)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detail Berita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // KONTEN ARTIKEL
            item {
                Spacer(Modifier.height(16.dp))
                imageUrl?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(220.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(12.dp))
                }
                Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Text(text = content, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(24.dp))
            }

            // TOMBOL AKSI
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(onClick = {}, enabled = false) {
                        Icon(Icons.Outlined.Comment, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Comment")
                    }
                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, articleUrl)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                        }
                    ) {
                        Icon(Icons.Outlined.Share, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Share")
                    }
                }
                Spacer(Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
            }
            
            // BAGIAN KOMENTAR
            item { Text("Comments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            item { Spacer(Modifier.height(8.dp)) }

            val comments by commentViewModel.comments
            if (comments.isEmpty()) {
                item { Text("Belum ada komentar", color = Color.Gray) }
            } else {
                items(comments) { comment ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(comment.id_user, fontWeight = FontWeight.Bold)
                        Text(comment.komentar)
                    }
                }
            }

            // INPUT KOMENTAR
            item {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // --- PERBAIKAN DI SINI ---
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Tulis komentar...") }
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                val currentUserId = UserSession.userId
                                commentViewModel.sendComment(
                                    sourceId = sourceId,
                                    articleUrl = articleUrl,
                                    userId = currentUserId,
                                    message = commentText,
                                    onDone = { commentText = "" }
                                )
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Send")
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
