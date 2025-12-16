package id.app.ddwancan.view.screen.favorite

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.ddwancan.R
import id.app.ddwancan.data.model.Berita
import id.app.ddwancan.navigation.BottomNavigationBar
import id.app.ddwancan.navigation.NavRoutes
import id.app.ddwancan.ui.theme.DDwancanTheme

/* ============================================================
   DUMMY DATA (SEMENTARA)
============================================================ */
private val dummyFavoriteBerita = listOf(
    Berita(
        id = 1,
        judul = "Universitas dan Organisasi Turki",
        deskripsi = "UKDW Perluas Jejaring...",
        gambar = "news",
        views = 20,
        createdAt = "3 Hours ago"
    ),
    Berita(
        id = 2,
        judul = "Dies Natalis UKDW",
        deskripsi = "Fun Run meriah penuh semangat!",
        gambar = "news",
        views = 55,
        createdAt = "7 Hours ago"
    )
)

/* ============================================================
   FAVORITE SCREEN
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen() {
    Scaffold(
        topBar = { FavoriteTopBar() },
        bottomBar = { BottomNavigationBar(NavRoutes.FAVORITE) },
        containerColor = Color.White
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
                Icon(Icons.Default.Newspaper, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "D’Wacana",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
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
fun FavoriteContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        item {
            Spacer(Modifier.height(16.dp))
            Text("Artikel Tersimpan", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Daftar berita favoritmu", fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(12.dp))
        }

        items(dummyFavoriteBerita) { berita ->
            FavoriteCard(berita)
        }
    }
}

/* ============================================================
   FAVORITE CARD
============================================================ */
@Composable
fun FavoriteCard(berita: Berita) {
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

            Column(Modifier.weight(1f)) {
                Text(berita.judul, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(berita.deskripsi, maxLines = 2, fontSize = 13.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "👁 ${berita.views} • ${berita.createdAt}",
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.Red
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
