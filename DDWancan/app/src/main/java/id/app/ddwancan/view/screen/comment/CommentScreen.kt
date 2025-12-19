package id.app.ddwancan.view.screen.comment

import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    LaunchedEffect(articleId) {
        viewModel.loadComments(sourceId = articleId, articleUrl = articleUrl)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
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

            if (viewModel.loading.value) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                reverseLayout = true
            ) {
                items(viewModel.comments.value.reversed()) { comment ->
                    Text(comment.id_user, fontWeight = FontWeight.Bold)
                    Text(comment.komentar)
                    Spacer(Modifier.height(12.dp))
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tulis komentar...") }
                )

                Spacer(Modifier.width(8.dp))

                // --- PERBAIKAN: Mengganti IconButton dengan Icon + Modifier.clickable ---
                
            }
        }
    }
}
