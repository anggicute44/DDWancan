package id.app.ddwancan.view.screen.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import id.app.ddwancan.view.activity.ArticleDetailActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.ddwancan.R
import id.app.ddwancan.data.model.Article
import id.app.ddwancan.viewmodel.FavoriteViewModel
import id.app.ddwancan.navigation.BottomNavigationBar
import id.app.ddwancan.navigation.NavRoutes
import id.app.ddwancan.ui.theme.DDwancanTheme


/* ============================================================
   FAVORITE SCREEN
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen() {
    Scaffold(
        topBar = { FavoriteTopBar() },
        bottomBar = { BottomNavigationBar(NavRoutes.FAVORITE) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        FavoriteContent(
            modifier = Modifier.padding(padding)
        )
    }
}

/* ============================================================
   TOP APP BAR
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Newspaper, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "D’Wacana",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

/* ============================================================
   CONTENT
============================================================ */
@Composable
fun FavoriteContent(modifier: Modifier = Modifier, viewModel: FavoriteViewModel = viewModel()) {
    // Muat data saat pertama kali
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    val favorites by viewModel.favorites

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        item {
            Spacer(Modifier.height(16.dp))
            Text("Artikel Tersimpan", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text("Daftar berita favoritmu", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
            Spacer(Modifier.height(12.dp))
        }

        if (favorites.isEmpty()) {
            item {
                Text("Belum ada artikel favorit", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), modifier = Modifier.padding(8.dp))
            }
        } else {
            items(favorites) { article ->
                FavoriteCard(article)
            }
        }
    }
}

/* ============================================================
   FAVORITE CARD
============================================================ */
@Composable
fun FavoriteCard(article: Article) {
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                val intent = Intent(context, ArticleDetailActivity::class.java).apply {
                    putExtra("SOURCE_ID", article.source?.id ?: "unknown")
                    putExtra("TITLE", article.title)
                    putExtra("CONTENT", article.description ?: "")
                    putExtra("IMAGE", article.urlToImage)
                    putExtra("URL", article.url)
                }
                context.startActivity(intent)
            }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val painter = if (!article.urlToImage.isNullOrBlank()) {
                rememberAsyncImagePainter(article.urlToImage)
            } else {
                painterResource(R.drawable.news)
            }

                Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(article.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(article.description ?: "", maxLines = 2, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Spacer(Modifier.height(6.dp))
                Text(
                    article.publishedAt,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

/* ============================================================
   PREVIEW
============================================================ */
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewFavorite() {
    DDwancanTheme {
        FavoriteScreen()
    }
}
