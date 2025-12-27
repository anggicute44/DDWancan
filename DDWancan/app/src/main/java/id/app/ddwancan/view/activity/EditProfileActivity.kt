package id.app.ddwancan.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.profile.EditProfileScreen

// KODE INI MENGEMBALIKAN FILE KE BENTUK ACTIVITY YANG BENAR
class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DDwancanTheme(darkTheme = isSystemInDarkTheme()) {
                EditProfileScreen(
                    onBackClick = { 
                        finish() // Aksi untuk tombol kembali
                    },
                    // Menambahkan parameter yang dibutuhkan oleh EditProfileScreen
                    onDarkModeChange = {
                        // TODO: Implementasikan logika Dark Mode di level Activity/Aplikasi
                        Toast.makeText(this, "Mode Gelap diubah: $it", Toast.LENGTH_SHORT).show()
                    },
                    onLanguageChange = {
                        // TODO: Implementasikan logika ganti bahasa di level Activity/Aplikasi
                        Toast.makeText(this, "Bahasa diubah: $it", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
