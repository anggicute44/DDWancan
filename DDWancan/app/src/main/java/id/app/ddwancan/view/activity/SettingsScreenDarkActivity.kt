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
class SettingsDarkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DDwancanTheme {
                SettingsScreenDark()
            }
        }
    }
}

/* ============================================================
   MAIN SCREEN (LOGIC DARK MODE ADA DI SINI)
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenDark() {
    // STATE: Default true agar langsung terlihat Dark Mode
    var isDarkTheme by remember { mutableStateOf(true) }

    // COLOR PALETTE (Berubah dinamis berdasarkan state)
    val backgroundColor = if (isDarkTheme) Color(0xFF121212) else Color.White
    val contentColor = if (isDarkTheme) Color.White else Color.Black
    val subTextColor = if (isDarkTheme) Color.LightGray else Color.Gray
    val dividerColor = if (isDarkTheme) Color(0xFF333333) else Color(0xFFE0E0E0)
    val bottomBarColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White

    Scaffold(
        topBar = { SettingsTopBarDark() },
        bottomBar = { SettingsBottomBarDark(containerColor = bottomBarColor, contentColor = contentColor) },
        containerColor = backgroundColor // Set background scaffold
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {

            // 1. SELECT LANGUAGE
            SettingItemRowDark(
                icon = Icons.Outlined.Translate,
                title = "Select Language",
                textColor = contentColor,
                dividerColor = dividerColor
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "English",
                        fontSize = 14.sp,
                        color = contentColor // Text warna putih/hitam
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = subTextColor
                    )
                }
            }

            // 2. DARK THEME (SWITCH)
            SettingItemRowDark(
                icon = Icons.Outlined.WbSunny,
                title = "Dark Theme",
                textColor = contentColor,
                dividerColor = dividerColor
            ) {
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { isDarkTheme = it }, // Ubah state saat diklik
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF2678FF),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.DarkGray
                    ),
                    modifier = Modifier.scale(0.8f)
                )
            }

            // 3. SIGN OUT (Tetap Merah)
            SettingItemRowDark(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Sign out",
                titleColor = Color(0xFFFF5252), // Merah sedikit lebih terang agar jelas di gelap
                iconColor = Color(0xFFFF5252),
                dividerColor = dividerColor,
                textColor = contentColor, // Fallback, tapi dioverride titleColor
                onClick = { /* TODO */ }
            ) {
                // Kosong
            }
        }
    }
}

/* ============================================================
   TOP APP BAR
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBarDark() {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
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
            containerColor = Color(0xFF2678FF) // Biru tetap sama
        )
    )
}

/* ============================================================
   REUSABLE ROW (UPDATED WITH COLORS)
============================================================ */
@Composable
fun SettingItemRowDark(
    icon: ImageVector,
    title: String,
    textColor: Color, // Menerima warna teks dinamis
    dividerColor: Color, // Menerima warna garis dinamis
    titleColor: Color? = null, // Opsional jika ingin override (misal: merah)
    iconColor: Color? = null,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable () -> Unit
) {
    // Tentukan warna final (prioritaskan titleColor jika ada, jika tidak pakai textColor)
    val finalTitleColor = titleColor ?: textColor
    val finalIconColor = iconColor ?: textColor

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = finalIconColor,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = finalTitleColor,
                modifier = Modifier.weight(1f)
            )

            trailingContent()
        }

        HorizontalDivider(
            color = dividerColor,
            thickness = 1.dp
        )
    }
}

/* ============================================================
   BOTTOM NAVIGATION (DARK MODE AWARE)
============================================================ */
@Composable
fun SettingsBottomBarDark(
    containerColor: Color,
    contentColor: Color
) {
    val dividerColor = if (containerColor == Color.White) Color(0xFFE0E0E0) else Color(0xFF333333)

    Column {
        HorizontalDivider(color = dividerColor, thickness = 1.dp)

        NavigationBar(
            containerColor = containerColor, // Warna background navbar berubah
            contentColor = contentColor      // Warna item navbar menyesuaikan
        ) {
            // Helper local untuk item navbar agar kodenya pendek
            val navItemColors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0xFF2678FF).copy(alpha = 0.2f),
                selectedIconColor = Color(0xFF2678FF),
                unselectedIconColor = if(containerColor == Color.White) Color.Gray else Color.Gray,
                unselectedTextColor = if(containerColor == Color.White) Color.Gray else Color.Gray,
                selectedTextColor = Color(0xFF2678FF)
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, "Home") },
                label = { Text("Home") },
                selected = false,
                onClick = {},
                colors = navItemColors
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Favorite, "Favorite") },
                label = { Text("Favorite") },
                selected = false,
                onClick = {},
                colors = navItemColors
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Search, "Search") },
                label = { Text("Search") },
                selected = false,
                onClick = {},
                colors = navItemColors
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, "Profile") },
                label = { Text("Profile") },
                selected = true,
                onClick = {},
                colors = navItemColors
            )
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewSettingsDark() {
    DDwancanTheme { SettingsScreenDark() }
}
