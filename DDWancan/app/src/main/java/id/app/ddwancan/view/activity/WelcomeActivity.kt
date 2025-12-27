package id.app.ddwancan.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import id.app.ddwancan.view.screen.start.WelcomeScreen

class WelcomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ❗ TIDAK CEK FirebaseAuth di sini
        // Supaya Welcome SELALU muncul

        setContent {
            WelcomeScreen(
                onStartClick = {
                    startActivity(
                        Intent(this, LoginActivity::class.java)
                    )
                    finish()
                }
            )
        }
    }
}
