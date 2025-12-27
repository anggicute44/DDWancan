package id.app.ddwancan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Palet warna untuk Mode Gelap
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,           // Biru yang lebih cerah untuk dark mode
    background = DarkBackground,       // Latar belakang utama (hitam pekat)
    surface = DarkSurface,           // Warna untuk Card, Surface, dll. (abu-abu gelap)
    onPrimary = TextLight,           // Teks di atas warna primer (putih)
    onBackground = TextLight,        // Teks utama di atas background
    onSurface = TextLight            // Teks di atas surface
)

// Palet warna untuk Mode Terang
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,           // Biru cerah untuk light mode
    background = Color.White,          // Latar belakang putih
    surface = LightBlue,             // Warna surface (biru sangat muda)
    onPrimary = Color.White,           // Teks di atas warna primer (putih)
    onBackground = TextDark,         // Teks utama di atas background (hitam)
    onSurface = TextDark             // Teks di atas surface (hitam)
)


@Composable
fun DDwancanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // ⬅️ DEFAULT
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

