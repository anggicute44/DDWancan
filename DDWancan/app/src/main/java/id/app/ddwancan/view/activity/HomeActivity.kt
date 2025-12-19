package id.app.ddwancan.view.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import id.app.ddwancan.data.utils.UserSession
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.home.HomeScreen


class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set UserSession jika belum ada (misalnya saat aplikasi dibuka ulang)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (UserSession.userId == null && currentUser != null) {
            UserSession.userId = currentUser.uid
            Log.d("HomeActivity", "UserSession initialized with ID: ${UserSession.userId}")
        }
        
        Log.d("HomeActivity", "Current UserSession.userId: ${UserSession.userId}")
        
        setContent {
            DDwancanTheme {
                HomeScreen()
            }
        }
    }
}





