
package id.app.ddwancan.view.screen.search

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import id.app.ddwancan.R
import id.app.ddwancan.data.model.Article
import id.app.ddwancan.data.model.NewsViewModel
import id.app.ddwancan.navigation.BottomNavigationBar
import id.app.ddwancan.navigation.NavRoutes
import id.app.ddwancan.view.activity.ArticleDetailActivity
import id.app.ddwancan.view.screen.home.ApiNewsCard

/* ============================================================
   MAIN SCREEN
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
    Scaffold(
        topBar = { SearchTopBar() },
        bottomBar = { BottomNavigationBar(NavRoutes.SEARCH) },
        containerColor = Color.White
    ) { padding ->
        SearchContent(
            modifier = Modifier.padding(padding)
        )
    }
}

/* ============================================================
   TOP BAR
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "D’Wacana",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF2678FF)
        )
    )
}

/* ============================================================
   CONTENT
============================================================ */
@Composable
fun SearchContent(modifier: Modifier = Modifier, viewModel: NewsViewModel = viewModel()) {
    val context = LocalContext.current
    var query by remember { mutableStateOf(TextFieldValue("")) }
    val news by viewModel.newsList

    LaunchedEffect(Unit) { viewModel.fetchNews(null) }

    val filtered = if (query.text.isBlank()) news else news.filter {
        it.title.contains(query.text, ignoreCase = true)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        item {
            Text("Cari Berita", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            SearchBarField(query) { newVal -> query = newVal }
            Spacer(Modifier.height(20.dp))
            Text("Hasil Pencarian", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
        }

        items(filtered) { article ->
            ApiNewsCard(article) {
                val intent = Intent(context, ArticleDetailActivity::class.java).apply {
                    putExtra("SOURCE_ID", article.source?.id)
                    putExtra("TITLE", article.title)
                    putExtra("CONTENT", article.description ?: "")
                    putExtra("IMAGE", article.urlToImage)
                    putExtra("URL", article.url)
                }
                context.startActivity(intent)
            }
        }
    }
}

/* ============================================================
   SEARCH BAR
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarField(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Cari berita...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFEDEDED),
            unfocusedContainerColor = Color(0xFFEDEDED),
            disabledContainerColor = Color(0xFFEDEDED),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

/* ============================================================
   SEARCH NEWS CARD
============================================================ */
@Composable
fun SearchNewsCard(berita: Article) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = rememberAsyncImagePainter(berita.urlToImage),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.LightGray, RoundedCornerShape(10.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(berita.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(berita.description ?: "", maxLines = 2, fontSize = 13.sp)
            }

            IconButton(onClick = { /* TODO favorite */ }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = null)
            }
        }
    }
}
