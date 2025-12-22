package id.app.ddwancan.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.profile.EditProfileScreen

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DDwancanTheme {
                EditProfileScreen(
                    onBackClick = {
                        finish()
                    },
                )
            }
        }
    }
}