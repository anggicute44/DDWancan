package id.app.ddwancan.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.ddwancan.R
import id.app.ddwancan.ui.theme.DDwancanTheme

/* ============================================================
   DATA MODEL
============================================================ */
data class CommentData(
    val id: Int,
    val name: String,
    val comment: String,
    val avatarRes: Int // Resource ID untuk avatar
)

class CommentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DDwancanTheme {
                CommentScreen()
            }
        }
    }
}

/* ============================================================
   MAIN SCREEN
============================================================ */
@Composable
fun CommentScreen() {
    Scaffold(
        topBar = { CommentTopBar() },
        bottomBar = { CommentInputBar() },
        containerColor = Color.White
    ) { innerPadding ->
        CommentContent(Modifier.padding(innerPadding))
    }
}

/* ============================================================
   TOP BAR (Konsisten dengan Search/Article)
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentTopBar() {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = { /* TODO: Back Action */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook, // Ikon Buku
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "D’Wacana",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF2678FF) // Biru Utama
        )
    )
}

/* ============================================================
   CONTENT: LIST KOMENTAR
============================================================ */
@Composable
fun CommentContent(modifier: Modifier = Modifier) {
    // Data Dummy Sesuai Gambar
    val comments = remember {
        listOf(
            CommentData(1, "Budi Santoso", "Artikel yang sangat informatif! Terima kasih sudah berbagi.", R.drawable.ic_launcher_background),
            CommentData(2, "Citra Lestari", "Sangat menarik! Saya jadi ingin belajar lebih banyak tentang kerja sama pendidikan ini.", R.drawable.ic_launcher_background),
            CommentData(3, "Dewi Anggraini", "Apakah ada informasi lebih lanjut mengenai program beasiswa yang disebutkan?", R.drawable.ic_launcher_background),
            CommentData(4, "Eko Prasetyo", "Setuju dengan Budi, pembahasannya sangat mendalam.", R.drawable.ic_launcher_background)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header Judul
        Text(
            text = "Komentar",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // List Komentar
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(comments) { item ->
                CommentItem(item)
            }
        }
    }
}

/* ============================================================
   COMPONENT: COMMENT ITEM BUBBLE
============================================================ */
@Composable
fun CommentItem(data: CommentData) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        // 1. Avatar (Circle)
        Image(
            painter = painterResource(id = data.avatarRes), // Ganti dengan gambar profil user
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray) // Placeholder color
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 2. Bubble Chat (Background Abu-abu)
        Column(
            modifier = Modifier
                .weight(1f) // Mengisi sisa ruang
                .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp)) // Warna abu muda rounded
                .padding(12.dp)
        ) {
            // Nama User
            Text(
                text = data.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Isi Komentar
            Text(
                text = data.comment,
                fontSize = 14.sp,
                color = Color(0xFF424242), // Abu tua untuk teks agar nyaman dibaca
                lineHeight = 20.sp
            )
        }
    }
}

/* ============================================================
   BOTTOM BAR: INPUT FIELD
============================================================ */
@Composable
fun CommentInputBar() {
    var text by remember { mutableStateOf("") }

    Column {
        // Garis Pembatas Tipis
        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Input Text Field
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF5F5F5)) // Background input abu
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text("Tulis komentar...", color = Color.Gray, fontSize = 14.sp)
                }
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Tombol Kirim
            Button(
                onClick = { /* TODO: Send Comment */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2678FF)),
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "Kirim",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewComment() {
    DDwancanTheme { CommentScreen() }
}
