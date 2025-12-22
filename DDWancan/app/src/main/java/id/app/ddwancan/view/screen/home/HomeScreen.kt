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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
    val newsList by viewModel.newsList
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedCategory) {
        viewModel.fetchNews(selectedCategory)
    }

    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = { HomeBottomBar(context) }
    ) { padding ->
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
                        CategoryChips(selectedCategory = selectedCategory, onCategoryClick = { selectedCategory = it })
                        Spacer(Modifier.height(24.dp))
                        Text("News Today", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        BreakingNewsImage()
                        Spacer(Modifier.height(24.dp))
                    }

                    items(newsList) { article ->
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

                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}


/* ============================================================
   TOP BAR
============================================================ */
@OptIn(ExperimentalMaterial3Api::class) // Ditambahkan: Untuk CenterAlignedTopAppBar
@Composable
fun HomeTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(R.drawable.logo1),
                contentDescription = "D'Wacana Logo",
                modifier = Modifier.height(48.dp),
                contentScale = ContentScale.Fit
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
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
            .height(170.dp)
            .background(Color.LightGray, RoundedCornerShape(12.dp))
    )
}

/* ============================================================
   CATEGORY CHIPS (NEWS API)
============================================================ */
@Composable
fun CategoryChips(
    selectedCategory: String?,
    onCategoryClick: (String?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        // PERBAIKAN: Bungkus setiap CategoryChip dengan item { ... }
        item {
            CategoryChip("All", selectedCategory == null) {
                onCategoryClick(null)
            }
        }
        item {
            CategoryChip("Technology", selectedCategory == "technology") {
                onCategoryClick("technology")
            }
        }
        item {
            CategoryChip("Health", selectedCategory == "health") {
                onCategoryClick("health")
            }
        }
        item {
            CategoryChip("Sports", selectedCategory == "sports") {
                onCategoryClick("sports")
            }
        }
        item {
            CategoryChip("Business", selectedCategory == "business") {
                onCategoryClick("business")
            }
        }
        item {
            CategoryChip("Science", selectedCategory == "science") {
                onCategoryClick("science")
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
        color = if (selected) Color(0xFF2678FF) else Color(0xFFEFF3FF),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (selected) Color.White else Color(0xFF2678FF),
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
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = rememberAsyncImagePainter(article.urlToImage),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.LightGray, RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(article.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                article.description?.let {
                    Text(it, maxLines = 2, fontSize = 13.sp)
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
    Column {
        HorizontalDivider(color = Color(0xFFE0E0E0))
        NavigationBar(containerColor = Color.White) {

            NavItem(Icons.Default.Home, "Home", true) {}

            NavItem(Icons.Default.Search, "Search") {
                context.startActivity(Intent(context, SearchActivity::class.java))
            }

            NavItem(Icons.Default.Favorite, "Favorite") {
                context.startActivity(Intent(context, FavoriteActivity::class.java))
            }

            NavItem(Icons.Default.Person, "Profile") {
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
        label = { Text(label) }
    )
}
