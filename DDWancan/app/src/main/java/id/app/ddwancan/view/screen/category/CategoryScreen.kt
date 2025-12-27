package id.app.ddwancan.view.screen.category

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.ddwancan.R
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.activity.FavoriteActivity
import id.app.ddwancan.view.activity.ProfileActivity
import id.app.ddwancan.view.activity.SearchActivity
//import id.app.ddwancan.view.screen.home.NavItem

/* ============================================================
   CONSTANT (BIAR UKURAN KONSISTEN)
============================================================ */
private val MetaIconSize = 14.dp
private val MetaTextSize = 11.sp
private val MetaRowHeight = 18.dp

/* ============================================================
   DATA MODEL
============================================================ */
data class ArticleData(
    val id: Int,
    val title: String,
    val description: String,
    val views: Int,
    val timeAgo: String,
    val isFavorite: Boolean
)

/* ============================================================
   MAIN SCREEN
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    category: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val settings = remember { id.app.ddwancan.data.local.SettingsPreference(context) }
    val isEnglish by settings.isEnglish.collectAsState(initial = false)
    Scaffold(
        topBar = { CategoryTopBar(category, onBack) },
        bottomBar = { id.app.ddwancan.navigation.BottomNavigationBar(id.app.ddwancan.navigation.NavRoutes.HOME) },
        containerColor = Color.White
    ) { padding ->
        CategoryContent(
            modifier = Modifier.padding(padding),
            category = category,
            isEnglish = isEnglish
        )
    }
}

/* ============================================================
   TOP BAR
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTopBar(
    category: String,
    onBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        title = {
            Text(
                text = category,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
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
fun CategoryContent(
    modifier: Modifier = Modifier,
    category: String,
    isEnglish: Boolean
) {
    val articles = remember {
        listOf(
            ArticleData(1, "Universitas dan Organisasi Turki", "UKDW Perluas Jejaring Internasional", 20, "3 Hours ago", false),
            ArticleData(2, "Dies Natalis UKDW", "Fun Run meriah penuh semangat", 55, "7 Hours ago", true),
            ArticleData(3, "Duta Voice Raih Emas", "Prestasi internasional membanggakan", 150, "12 Hours ago", false),
            ArticleData(4, "UKDW Kunjungi Kansai University", "Kerja sama internasional", 80, "1 Day ago", true)
        )
    }

    Column(modifier = modifier.fillMaxSize()) {

        /* HEADER */
        Column(Modifier.padding(16.dp)) {
            Text(
                text = category.uppercase(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isEnglish) "Explore news about $category" else "Jelajahi berita seputar $category",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val chips = if (isEnglish) listOf("All", "Popular", "Latest") else listOf("Semua", "Populer", "Terbaru")
                items(chips) {
                    CategoryChip(it)
                }
            }
        }

        /* LIST BERITA */
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(articles) { article ->
                ArticleCard(article)
            }
        }
    }
}

/* ============================================================
   CHIP
============================================================ */
@Composable
fun CategoryChip(text: String) {
    Box(
        modifier = Modifier
            .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}

/* ============================================================
   ARTICLE CARD
============================================================ */
@Composable
fun ArticleCard(article: ArticleData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(R.drawable.news),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        article.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        article.description,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                /* METADATA (RAPI & SAMA BESAR) */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetaItem(Icons.Outlined.Visibility, article.views.toString())
                        MetaItem(Icons.Outlined.AccessTime, article.timeAgo)
                    }

                    Icon(
                        imageVector = if (article.isFavorite)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (article.isFavorite) Color.Red else Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/* ============================================================
   META ITEM (ICON + TEXT SAMA BESAR)
============================================================ */
@Composable
fun MetaItem(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(MetaRowHeight)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(MetaIconSize),
            tint = Color.Gray
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = MetaTextSize,
            color = Color.Gray
        )
    }
}

/* Bottom navigation moved to `navigation/BottomNavigationBar.kt` */

/* ============================================================
   PREVIEW
============================================================ */
@Preview(showSystemUi = true)
@Composable
fun PreviewCategory() {
    DDwancanTheme {
        CategoryScreen(category = "Education", onBack = {})
    }
}
