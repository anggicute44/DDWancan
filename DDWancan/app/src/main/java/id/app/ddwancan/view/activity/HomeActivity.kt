package id.app.ddwancan.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.app.ddwancan.data.utils.UserSession
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.home.HomeScreen

class HomeActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Ambil user saat ini
        val currentUser = auth.currentUser

        // 2. Set UserSession jika belum ada
        if (UserSession.userId == null && currentUser != null) {
            UserSession.userId = currentUser.uid
            Log.d("HomeActivity", "UserSession initialized with ID: ${UserSession.userId}")
        }

        // 3. Cek Status Banned (Keamanan Tambahan)
        if (currentUser != null) {
            checkIfUserIsBanned(currentUser.uid)
        }

        setContent {
            DDwancanTheme {
                HomeScreen()
            }
        }
    }

    /**
     * Fungsi untuk mengecek apakah UID user ada di list 'banned_users' di Firestore.
     * Jika ada, paksa logout dan kembali ke LoginActivity.
     */
    private fun checkIfUserIsBanned(uid: String) {
        db.collection("banned_users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // --- USER TERDETEKSI DIBLOKIR/DIHAPUS ADMIN ---
                    Log.w("HomeActivity", "User $uid is banned. Forcing logout.")

                    // 1. Logout dari Firebase Auth
                    auth.signOut()
                    UserSession.userId = null // Bersihkan session lokal

                    // 2. Tampilkan Pesan ke User
                    Toast.makeText(
                        this,
                        "Akun Anda telah dinonaktifkan oleh Admin.",
                        Toast.LENGTH_LONG
                    ).show()

                    // 3. Lempar balik ke Login Screen & Hapus Back Stack
                    val intent = Intent(this, LoginActivity::class.java)
                    // Flag ini penting agar user tidak bisa tekan 'Back' kembali ke Home
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Log.d("HomeActivity", "User status: Active (Not banned)")
                }
            }
            .addOnFailureListener { e ->
                // Jika error koneksi, biarkan user masuk dulu (fail-safe)
                Log.e("HomeActivity", "Error checking ban status: ${e.message}")
            }
    }
}