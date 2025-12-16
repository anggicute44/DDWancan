package id.app.ddwancan.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.ddwancan.R
import id.app.ddwancan.ui.theme.DDwancanTheme

class ArticleDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DDwancanTheme {
                ArticleDetailScreen()
            }
        }
    }
}

@Composable
fun ArticleDetailScreen() {
    Scaffold(
        topBar = { ArticleTopBar() }, // Menggunakan TopBar baru
        bottomBar = { ArticleBottomInteractionBar() },
        containerColor = Color.White
    ) { innerPadding ->
        ArticleContent(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/* ============================================================
   1. TOP BAR (UPDATED)
   Menggunakan desain Biru + Icon MenuBook
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleTopBar() {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = { /* TODO: Back Action */ }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White // Icon Back Putih
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Icon Buku
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                // Teks Judul Aplikasi
                Text(
                    text = "D’Wacana",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF2678FF) // Background Biru
        )
    )
}

/* ============================================================
   2. MAIN CONTENT (Scrollable)
============================================================ */
/* ============================================================
   2. MAIN CONTENT (Updated dengan Full Text)
============================================================ */
@Composable
fun ArticleContent(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // Scroll aktif
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // --- GAMBAR ARTIKEL ---
        Image(
            painter = painterResource(id = R.drawable.logo2), // Ganti dengan gambar 'article_image'
            contentDescription = "Article Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp) // Sedikit diperbesar agar proporsional
                .padding(bottom = 16.dp)
        )

        // --- JUDUL ---
        Text(
            text = "Universitas dan Organisasi Turki",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            lineHeight = 30.sp // Jarak antar baris judul
        )

        Spacer(modifier = Modifier.height(10.dp))

        // --- METADATA (Views & Time) ---
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetadataItem(icon = Icons.Outlined.Visibility, text = "20")
            Spacer(modifier = Modifier.width(16.dp))
            MetadataItem(icon = Icons.Outlined.AccessTime, text = "3 Hours ago")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- BODY TEXT (Sesuai Gambar Asli) ---
        // Menggunakan trimIndent() agar format paragraf rapi di kodingan
        val fullText = """
            Dalam beberapa tahun terakhir, kerja sama antara universitas di Indonesia dan berbagai organisasi pendidikan di Turki menunjukkan peningkatan yang signifikan. Banyak institusi Turki yang mulai membuka peluang kolaborasi melalui program beasiswa, pertukaran mahasiswa, dan proyek penelitian bersama.
            
            Banyak peserta menyampaikan bahwa interaksi lintas budaya ini memberikan pemahaman baru tentang cara kerja organisasi, nilai-nilai masyarakat Turki, serta potensi peluang profesional yang mungkin hadir setelah menyelesaikan studi.
        """.trimIndent()

        Text(
            text = fullText,
            fontSize = 14.sp, // Ukuran font artikel standar
            color = Color.Black,
            textAlign = TextAlign.Justify, // Rata Kiri-Kanan (Justify) agar rapi seperti koran
            lineHeight = 24.sp, // Memberi jarak antar baris agar nyaman dibaca
            modifier = Modifier.fillMaxWidth()
        )

        // Spacer di bawah agar teks terakhir tidak menempel dengan navbar/batas layar
        Spacer(modifier = Modifier.height(40.dp))
    }
}

// Helper Composable untuk Metadata
@Composable
fun MetadataItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

/* ============================================================
   3. BOTTOM INTERACTION BAR
============================================================ */
@Composable
fun ArticleBottomInteractionBar() {
    Column {
        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Black
                )
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Comment",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Black
                )
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "Share",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Black
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewArticleDetail() {
    DDwancanTheme {
        ArticleDetailScreen()
    }
}