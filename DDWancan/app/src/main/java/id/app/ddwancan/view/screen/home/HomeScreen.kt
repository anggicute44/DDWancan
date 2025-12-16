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
import androidx.compose.runtime.Composable
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

import id.app.ddwancan.R

import id.app.ddwancan.data.model.Berita
import id.app.ddwancan.view.activity.CategoryActivity
import id.app.ddwancan.view.activity.FavoriteActivity
import id.app.ddwancan.view.activity.ProfileActivity
import id.app.ddwancan.view.activity.SearchActivity

/* ============================================================
   DUMMY DATA
============================================================ */
private val dummyBerita = listOf(
    Berita(1, "Universitas dan Organisasi Turki", "UKDW Perluas Jejaring...", "news", 20, "3 Hours ago"),
    Berita(2, "Dies Natalis UKDW", "Fun Run meriah penuh semangat!", "news", 55, "7 Hours ago"),
    Berita(3, "Duta Voice Raih Emas", "Prestasi International Bandung Festival", "news", 150, "12 Hours ago")
)

/* ============================================================
   HOME SCREEN
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = { HomeBottomBar(context) },
        containerColor = Color.White
    ) { padding ->
        HomeContent(
            modifier = Modifier.padding(padding),
            onCategoryClick = { category ->
                val intent = Intent(context, CategoryActivity::class.java)
                intent.putExtra("CATEGORY", category)
                context.startActivity(intent)
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
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
   TOP BAR (LOGO TENGAH)
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
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
   CONTENT
============================================================ */
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        item {
            Spacer(Modifier.height(16.dp))
            CategoryChips(onCategoryClick)
            Spacer(Modifier.height(24.dp))
            Text("News Today", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            BreakingNewsImage()
            Spacer(Modifier.height(24.dp))
        }

        items(dummyBerita) { berita ->
            NewsCard(berita)
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

/* ============================================================
   CATEGORY CHIPS
============================================================ */
@Composable
fun CategoryChips(onCategoryClick: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item { ChipItem("All", true) { onCategoryClick("all") } }
        item { ChipItem("Teknologi") { onCategoryClick("teknologi") } }
        item { ChipItem("Edukasi") { onCategoryClick("edukasi") } }
        item { ChipItem("Sosial") { onCategoryClick("sosial") } }
        item { ChipItem("Kesehatan") { onCategoryClick("kesehatan") } }
        item { ChipItem("Olahraga") { onCategoryClick("olahraga") } }
        item { ChipItem("Seni") { onCategoryClick("seni") } }
    }
}

@Composable
fun ChipItem(
    text: String,
    selected: Boolean = false,
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
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        )
    }
}

/* ============================================================
   NEWS CARD
============================================================ */
@Composable
fun NewsCard(berita: Berita) {
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
                painter = painterResource(R.drawable.news),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.LightGray, RoundedCornerShape(10.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(berita.judul, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(berita.deskripsi, maxLines = 2, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Row {
                    Text("👁 ${berita.views}", fontSize = 12.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(berita.createdAt, fontSize = 12.sp)
                }
            }

            IconButton(onClick = {}) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite")
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
