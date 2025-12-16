package id.app.ddwancan.view.activity

import android.app.Activity
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
                    onSaveClick = { updatedName, updatedEmail, updatedPassword ->
                        // Kirim data yang sudah diubah kembali ke ProfileActivity
                        val resultIntent = Intent()
                        resultIntent.putExtra("name", updatedName)
                        resultIntent.putExtra("email", updatedEmail)
                        resultIntent.putExtra("password", updatedPassword)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    },
                    onCancelClick = {
                        // Jika batal, kembali tanpa menyimpan
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }
}