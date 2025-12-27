package id.app.ddwancan.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import id.app.ddwancan.view.screen.start.WelcomeScreen

class WelcomeActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // PERBAIKAN: Gunakan SharedPreferences untuk mendeteksi instalasi baru
        val prefs = getSharedPreferences("DDWancanPrefs", Context.MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            // Jika ini adalah instalasi baru, SELALU tampilkan WelcomeScreen
            setContent {
                WelcomeScreen(
                    onStartClick = {
                        // Tandai bahwa aplikasi sudah pernah dijalankan
                        prefs.edit().putBoolean("isFirstRun", false).apply()

                        // Lanjutkan ke halaman Login
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        } else {
            // Jika bukan instalasi baru, barulah cek status login Firebase
            auth = FirebaseAuth.getInstance()
            if (auth.currentUser != null) {
                // Pengguna sudah login, langsung ke Home
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // Pengguna belum login, langsung ke Login
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            // Tutup WelcomeActivity karena kita langsung navigasi
            finish()
        }
    }
}
