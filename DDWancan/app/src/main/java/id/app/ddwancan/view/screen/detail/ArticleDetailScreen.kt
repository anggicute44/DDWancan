package id.app.ddwancan.view.screen.detail

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    articleId: String,
    title: String,
    content: String,
    imageUrl: String?,
    articleUrl: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var commentText by remember { mutableStateOf("") }
    val comments = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // 🖼 IMAGE
        imageUrl?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(12.dp))
        }

        // 📰 TITLE
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        // 📄 CONTENT
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))

        // 🔘 ACTION BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // 💬 COMMENT (ke CommentActivity)
            OutlinedButton(
                onClick = {
                    val intent = Intent(
                        context,
                        id.app.ddwancan.view.activity.CommentActivity::class.java
                    ).apply {
                        putExtra("ARTICLE_ID", articleId)
                        putExtra("TITLE", title)
                    }
                    context.startActivity(intent)
                }
            ) {
                Icon(Icons.Outlined.Comment, null)
                Spacer(Modifier.width(6.dp))
                Text("Comment")
            }

            // 🔗 SHARE
            OutlinedButton(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, articleUrl)
                    }
                    context.startActivity(
                        Intent.createChooser(shareIntent, "Share via")
                    )
                }
            ) {
                Icon(Icons.Outlined.Share, null)
                Spacer(Modifier.width(6.dp))
                Text("Share")
            }
        }

        Spacer(Modifier.height(24.dp))
        Divider()
        Spacer(Modifier.height(16.dp))

        // 💬 COMMENT SECTION
        Text(
            text = "Comments",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Row {
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
                        comments.add(commentText)
                        commentText = ""
                    }
                }
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Send")
            }
        }

        Spacer(Modifier.height(16.dp))

        // 📃 COMMENT LIST
        if (comments.isEmpty()) {
            Text("Belum ada komentar", color = Color.Gray)
        } else {
            comments.forEach {
                CommentItem(it)
            }
        }
    }
}
