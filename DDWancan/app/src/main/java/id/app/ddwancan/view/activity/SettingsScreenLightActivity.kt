package id.app.ddwancan.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.ddwancan.ui.theme.DDwancanTheme

/* ============================================================
   ACTIVITY
============================================================ */
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DDwancanTheme {
                SettingsScreen()
            }
        }
    }
}

/* ============================================================
   MAIN SCREEN
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = { SettingsTopBar() },
        bottomBar = { SettingsBottomBar() },
        containerColor = Color.White
    ) { innerPadding ->
        SettingsContent(Modifier.padding(innerPadding))
    }
}

/* ============================================================
   TOP APP BAR
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar() {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = { /* TODO: Handle Back */ }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        title = {
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF2678FF) // Warna biru utama sesuai gambar
        )
    )
}

/* ============================================================
   CONTENT
============================================================ */
@Composable
fun SettingsContent(modifier: Modifier = Modifier) {
    // State untuk Switch Dark Theme
    var isDarkTheme by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {

        // 1. SELECT LANGUAGE
        SettingItemRow(
            icon = Icons.Outlined.Translate, // Ikon terjemahan
            title = "Select Language",
            showDivider = true
        ) {
            // Bagian Kanan: Text "English" + Arrow Down
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "English",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }

        // 2. DARK THEME
        SettingItemRow(
            icon = Icons.Outlined.WbSunny, // Ikon matahari/brightness
            title = "Dark Theme",
            showDivider = true
        ) {
            // Bagian Kanan: Switch Toggle
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { isDarkTheme = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF2678FF),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.LightGray.copy(alpha = 0.4f)
                ),
                modifier = Modifier.scale(0.8f) // Perkecil sedikit switch agar pas
            )
        }

        // 3. SIGN OUT
        SettingItemRow(
            icon = Icons.AutoMirrored.Filled.ExitToApp, // Ikon keluar
            title = "Sign out",
            titleColor = Color.Red, // Judul warna merah
            iconColor = Color.Red,  // Ikon warna merah
            showDivider = true,
            onClick = { /* TODO: Handle Sign Out */ }
        ) {
            // Bagian Kanan: Kosong
        }
    }
}

/* ============================================================
   REUSABLE ROW COMPONENT
============================================================ */
@Composable
fun SettingItemRow(
    icon: ImageVector,
    title: String,
    titleColor: Color = Color.Black,
    iconColor: Color = Color.Black,
    showDivider: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp), // Padding atas bawah item
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon Kiri
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Teks Judul
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = titleColor,
                modifier = Modifier.weight(1f) // Dorong konten kanan ke ujung
            )

            // Konten Kanan (Switch, Text, dll)
            trailingContent()
        }

        // Garis Pembatas (Divider)
        if (showDivider) {
            HorizontalDivider(
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )
        }
    }
}

/* ============================================================
   BOTTOM NAVIGATION (Sama seperti Profile)
============================================================ */
@Composable
fun SettingsBottomBar() {
    Column {
        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

        NavigationBar(containerColor = Color.White) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = false,
                onClick = {}
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorite") },
                label = { Text("Favorite") },
                selected = false,
                onClick = {}
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                label = { Text("Search") },
                selected = false,
                onClick = {}
            )
            // Profile tetap selected karena Settings biasanya sub-menu dari Profile
            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text("Profile") },
                selected = true,
                onClick = {},
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFE3F2FD), // Biru muda background seleksi
                    selectedIconColor = Color(0xFF2678FF) // Ikon biru
                )
            )
        }
    }
}


/* ============================================================
   PREVIEW
============================================================ */
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewSettings() {
    DDwancanTheme { SettingsScreen() }
}
