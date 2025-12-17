package id.app.ddwancan.view.screen.comment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    articleId: String,
    title: String,
    onBack: () -> Unit
) {
    // 🔹 state input komentar
    var input by remember { mutableStateOf("") }

    // 🔹 list komentar (sementara / lokal)
    val comments = remember {
        mutableStateListOf<Pair<String, String>>() // Pair<name, message>
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

            // 📃 LIST KOMENTAR
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (comments.isEmpty()) {
                    item {
                        Text(
                            text = "Belum ada komentar",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    items(comments) { comment ->
                        Text(comment.first, fontWeight = FontWeight.Bold)
                        Text(comment.second)
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            HorizontalDivider()

            // ✍️ INPUT KOMENTAR
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tulis komentar...") }
                )

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            comments.add(
                                "Josephine Marcelia" to input
                            )
                            input = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}
