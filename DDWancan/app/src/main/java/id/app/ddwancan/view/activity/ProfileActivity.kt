package id.app.ddwancan.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.profile.ProfileScreen

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DDwancanTheme {
                ProfileScreen(
                    onEditClick = {
                        // Navigasi ke EditProfileActivity
                        val intent = Intent(this@ProfileActivity, EditProfileActivity::class.java)
                        startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE)
                    }
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK && data != null) {
            // Refresh ProfileScreen dengan data terbaru
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        const val REQUEST_CODE_EDIT_PROFILE = 1001
    }
}