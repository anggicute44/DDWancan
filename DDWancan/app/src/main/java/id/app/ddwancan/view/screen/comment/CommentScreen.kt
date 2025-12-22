package id.app.ddwancan.view.screen.comment

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.ddwancan.data.utils.UserSession
import id.app.ddwancan.viewmodel.CommentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    viewModel: CommentViewModel,
    articleId: String,
    articleUrl: String,
    title: String,
    onBack: () -> Unit
) {
    var input by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(articleId) {
        viewModel.loadComments(sourceId = articleId, articleUrl = articleUrl)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Komentar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Loading Indicator
            if (viewModel.loading.value) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Daftar Komentar
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
            ) {
                val commentsList = viewModel.comments.value

                if (commentsList.isEmpty() && !viewModel.loading.value) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("Belum ada komentar.", color = Color.Gray)
                        }
                    }
                } else {
                    items(commentsList) { comment ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // --- PERUBAHAN DI SINI ---
                                // Menampilkan nama_user. Jika kosong (data lama), tampilkan "User"
                                Text(
                                    text = if (comment.nama_user.isNotBlank()) comment.nama_user else "User",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = comment.komentar,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // Input Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tulis komentar...") },
                    maxLines = 3
                )

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val uid = UserSession.userId
                        if (input.isNotBlank()) {
                            if (uid != null) {
                                viewModel.sendComment(
                                    sourceId = articleId,
                                    articleUrl = articleUrl,
                                    userId = uid,
                                    message = input,
                                    onDone = {
                                        input = "" // Bersihkan input setelah terkirim
                                    }
                                )
                            } else {
                                Toast.makeText(context, "Silakan Login Terlebih Dahulu", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = input.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Kirim",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}