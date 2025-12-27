
package id.app.ddwancan.view.screen.search

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import id.app.ddwancan.data.local.SettingsPreference
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
    val context = LocalContext.current
    val settings = remember { SettingsPreference(context) }
    val isEnglish by settings.isEnglish.collectAsState(initial = false)

    Scaffold(
        topBar = { SearchTopBar() },
        bottomBar = { BottomNavigationBar(NavRoutes.SEARCH) },
        containerColor = MaterialTheme.colorScheme.background
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
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "D’Wacana",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
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
fun SearchContent(modifier: Modifier = Modifier, viewModel: NewsViewModel = viewModel()) {
    val context = LocalContext.current
    var query by remember { mutableStateOf(TextFieldValue("")) }
    val news by viewModel.newsList

    // NewsViewModel observes local DB and refreshes automatically
    val filtered = if (query.text.isBlank()) news else news.filter {
        it.title.contains(query.text, ignoreCase = true)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        item {
            val context = LocalContext.current
            val settings = remember { SettingsPreference(context) }
            val isEnglish by settings.isEnglish.collectAsState(initial = false)

            Text(if (isEnglish) "Search News" else "Cari Berita", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(12.dp))
            SearchBarField(query) { newVal -> query = newVal }
            Spacer(Modifier.height(20.dp))
            Text(if (isEnglish) "Search Results" else "Hasil Pencarian", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(12.dp))
        }

        items(filtered) { article ->
            val favVm: id.app.ddwancan.viewmodel.FavoriteViewModel = viewModel()
            LaunchedEffect(Unit) { favVm.loadFavorites() }
            ApiNewsCard(article, onClick = {
                val intent = Intent(context, ArticleDetailActivity::class.java).apply {
                    putExtra("SOURCE_ID", article.source?.id)
                    putExtra("TITLE", article.title)
                    putExtra("CONTENT", article.description ?: "")
                    putExtra("IMAGE", article.urlToImage)
                    putExtra("URL", article.url)
                    putExtra("AUTHOR", article.author)
                    putExtra("PUBLISHED_AT", article.publishedAt)
                }
                context.startActivity(intent)
            }, isFavorited = favVm.favorites.value.any { it.url == article.url }, onFavorite = {
                if (favVm.favorites.value.any { it.url == article.url }) favVm.removeFavorite(article.url) else favVm.addFavorite(article.url)
            })
        }
    }
}

/* ============================================================
   SEARCH BAR
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarField(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { 
            val context = LocalContext.current
            val settings = remember { SettingsPreference(context) }
            val isEnglish by settings.isEnglish.collectAsState(initial = false)
            Text(if (isEnglish) "Search news..." else "Cari berita...")
        },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus()
        }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
        color = MaterialTheme.colorScheme.surface,
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
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(berita.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(berita.description ?: "", maxLines = 2, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }

            IconButton(onClick = { /* TODO favorite */ }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
