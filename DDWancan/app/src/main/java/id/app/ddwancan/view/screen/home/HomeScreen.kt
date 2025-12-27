package id.app.ddwancan.view.screen.home

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import id.app.ddwancan.data.local.SettingsPreference
import id.app.ddwancan.navigation.BottomNavigationBar
import id.app.ddwancan.navigation.NavRoutes
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import id.app.ddwancan.R
import id.app.ddwancan.data.model.Article
import id.app.ddwancan.data.model.NewsViewModel
import id.app.ddwancan.view.activity.ArticleDetailActivity
import id.app.ddwancan.view.activity.FavoriteActivity
import id.app.ddwancan.view.activity.ProfileActivity
import id.app.ddwancan.view.activity.SearchActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: NewsViewModel = viewModel()
) {
    val context = LocalContext.current
    val settings = remember { SettingsPreference(context) }
    val isEnglish by settings.isEnglish.collectAsState(initial = false)
    val newsList by viewModel.newsList
    val favoriteViewModel: id.app.ddwancan.viewmodel.FavoriteViewModel = viewModel()
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // State for filter dialog
    var showFilterDialog by remember { mutableStateOf(false) }
    var authorFilter by remember { mutableStateOf("") }
    var dateFrom by remember { mutableStateOf("") } // format: YYYY-MM-DD
    var dateTo by remember { mutableStateOf("") }

    // NewsViewModel now observes local DB and refreshes automatically; filter locally

    val filteredList = newsList.filter {
        val matchesCategory = selectedCategory.isNullOrBlank() || (
            it.title.contains(selectedCategory ?: "", ignoreCase = true) ||
            (it.description?.contains(selectedCategory ?: "", ignoreCase = true) == true)
        )
        matchesCategory &&
        (authorFilter.isBlank() || it.author?.contains(authorFilter, ignoreCase = true) == true) &&
        (dateFrom.isBlank() || it.publishedAt >= dateFrom) &&
        (dateTo.isBlank() || it.publishedAt <= dateTo)
    }

    Scaffold(
        topBar = { HomeTopBar(onFilterClick = { showFilterDialog = true }) },
        bottomBar = { BottomNavigationBar(NavRoutes.HOME) }
    ) { padding ->

        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = { Text(if (isEnglish) "Filter News" else "Filter Berita") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = authorFilter,
                            onValueChange = { authorFilter = it },
                            label = { Text(if (isEnglish) "Author contains" else "Penulis mengandung") },
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = dateFrom,
                            onValueChange = { dateFrom = it },
                            label = { Text(if (isEnglish) "Published from (YYYY-MM-DD)" else "Diterbitkan dari (YYYY-MM-DD)") },
                            singleLine = true
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = dateTo,
                            onValueChange = { dateTo = it },
                            label = { Text(if (isEnglish) "Published to (YYYY-MM-DD)" else "Diterbitkan sampai (YYYY-MM-DD)") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFilterDialog = false }) { Text(if (isEnglish) "Apply" else "Terapkan") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        authorFilter = ""
                        dateFrom = ""
                        dateTo = ""
                        showFilterDialog = false
                    }) { Text(if (isEnglish) "Clear" else "Kosongkan") }
                }
            )
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage ?: "Terjadi kesalahan", color = Color.Red)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)
                ) {
                    item {
                        Spacer(Modifier.height(16.dp))
                        CategoryChips(selectedCategory = selectedCategory, onCategoryClick = { selectedCategory = it }, isEnglish = isEnglish)
                        Spacer(Modifier.height(24.dp))
                        Text(if (isEnglish) "News Today" else "Berita Hari Ini", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        BreakingNewsImage()
                        Spacer(Modifier.height(24.dp))
                    }

                    items(filteredList) { article ->
                        ApiNewsCard(article,
                            onClick = {
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
                        }, onFavorite = {
                            favoriteViewModel.addFavorite(article.url) 
                        })
                    }

                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}


/* ============================================================
   TOP BAR
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(onFilterClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook, // 📖 buku terbuka
                    contentDescription = "D'Wacana Icon",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "D'Wacana",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Filter",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}


/* ============================================================
   BREAKING IMAGE
============================================================ */
@Composable
fun BreakingNewsImage() {
    Image(
        painter = painterResource(R.drawable.logo2),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray, RoundedCornerShape(12.dp))
    )
}

/* ============================================================
   CATEGORY CHIPS (NEWS API)
============================================================ */
@Composable
fun CategoryChips(
    selectedCategory: String?,
    onCategoryClick: (String?) -> Unit,
    isEnglish: Boolean = false
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        item {
            CategoryChip(if (isEnglish) "All" else "Semua", selectedCategory == null) {
                onCategoryClick(null)
            }
        }
        item {
            CategoryChip("Apple", selectedCategory == "apple") {
                onCategoryClick("apple")
            }
        }
        item {
            CategoryChip("Tesla", selectedCategory == "tesla") {
                onCategoryClick("tesla")
            }
        }
        item {
            CategoryChip("Business", selectedCategory == "business") {
                onCategoryClick("business")
            }
        }
        item {
            CategoryChip("TechCrunch", selectedCategory == "techcrunch") {
                onCategoryClick("techcrunch")
            }
        }
        item {
            CategoryChip("Wall Street", selectedCategory == "wall street") {
                onCategoryClick("wall street")
            }
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/* ============================================================
   NEWS CARD
============================================================ */
@Composable
fun ApiNewsCard(
    article: Article,
    onClick: () -> Unit,
    onFavorite: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = rememberAsyncImagePainter(
                    model = article.urlToImage,
                    placeholder = painterResource(id = R.drawable.news)
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(article.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 2, color = MaterialTheme.colorScheme.onSurface)
                article.description?.let {
                    Text(it, maxLines = 2, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
            }

            if (onFavorite != null) {
                IconButton(onClick = onFavorite) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

/* ============================================================
   BOTTOM NAVIGATION
============================================================ */
@Composable
fun HomeBottomBar(context: android.content.Context) {
    val ctx = LocalContext.current
    val settings = remember { SettingsPreference(ctx) }
    val isEnglish by settings.isEnglish.collectAsState(initial = false)

    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {

            NavItem(Icons.Default.Home, if (isEnglish) "Home" else "Beranda", true) {}

            NavItem(Icons.Default.Search, if (isEnglish) "Search" else "Pencarian") {
                context.startActivity(Intent(context, SearchActivity::class.java))
            }

            NavItem(Icons.Default.Favorite, if (isEnglish) "Favorite" else "Favorit") {
                context.startActivity(Intent(context, FavoriteActivity::class.java))
            }

            NavItem(Icons.Default.Person, if (isEnglish) "Profile" else "Profil") {
                context.startActivity(Intent(context, ProfileActivity::class.java))
            }
        }
    }
}

@Composable
fun RowScope.NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                )
    )
}
