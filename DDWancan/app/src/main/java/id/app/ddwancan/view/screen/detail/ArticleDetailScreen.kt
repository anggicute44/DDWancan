package id.app.ddwancan.view.screen.detail

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState // Tambahan untuk scroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import id.app.ddwancan.viewmodel.ArticleDetailViewModel
import coil.compose.rememberAsyncImagePainter
import id.app.ddwancan.data.utils.UserSession
import id.app.ddwancan.viewmodel.CommentViewModel
import kotlinx.coroutines.launch // Tambahan untuk coroutine scroll

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
    val currentUserId = UserSession.userId

    // State untuk mengontrol scroll list
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // ViewModel untuk favorite
    val detailViewModel: ArticleDetailViewModel = viewModel()

    // Memuat komentar & favorite saat layar dibuka
    LaunchedEffect(articleUrl) {
        commentViewModel.loadComments(sourceId = sourceId, articleUrl = articleUrl)
        detailViewModel.loadFavoriteState(articleUrl, currentUserId)
    }

    // Efek samping: Jika jumlah komentar bertambah (ada komentar baru masuk), scroll ke bawah
    LaunchedEffect(commentViewModel.comments.value.size) {
        if (commentViewModel.comments.value.isNotEmpty()) {
            // Scroll ke item terakhir (paling baru)
            listState.animateScrollToItem(commentViewModel.comments.value.size - 1)
        }
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
            state = listState, // Pasang state scroll di sini
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
                    // Favorite button
                    val isFav by detailViewModel.isFavorited
                    val favCount by detailViewModel.favoritesCount

                    OutlinedButton(onClick = {
                        detailViewModel.toggleFavorite(
                            articleUrl = articleUrl,
                            userId = currentUserId,
                            title = title,
                            description = content,
                            imageUrl = imageUrl,
                            publishedAt = null
                        )
                    }) {
                        Icon(if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, null,
                            tint = if (isFav) MaterialTheme.colorScheme.primary else Color.Unspecified)
                        Spacer(Modifier.width(6.dp))
                        Text("$favCount")
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

            // BAGIAN JUDUL KOMENTAR
            item { Text("Comments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            item { Spacer(Modifier.height(8.dp)) }

            // --- 2. LIST KOMENTAR ---
            val comments by commentViewModel.comments
            if (comments.isEmpty()) {
                item {
                    Text(
                        "Belum ada komentar",
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(comments) { comment ->
                    CommentItem(comment = comment)
                }
            }

            // INPUT KOMENTAR
            item {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                                if (currentUserId != null) {
                                    commentViewModel.sendComment(
                                        sourceId = sourceId,
                                        articleUrl = articleUrl,
                                        userId = currentUserId,
                                        message = commentText,
                                        onDone = {
                                            commentText = "" // 1. Kosongkan input
                                            Toast.makeText(context, "Komentar terkirim", Toast.LENGTH_SHORT).show()

                                            // 2. REFETCH / RELOAD DATA (Sesuai request)
                                            // Ini akan memicu pengambilan data ulang dari server
                                            commentViewModel.loadComments(sourceId, articleUrl)
                                        }
                                    )
                                } else {
                                    Toast.makeText(context, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}