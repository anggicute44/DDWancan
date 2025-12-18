package id.app.ddwancan.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

/**
 * NONAKTIFKAN SEMENTARA:
 * File ini menyebabkan error kompilasi karena fungsinya sudah dipindahkan
 * langsung ke dalam ArticleDetailScreen. Untuk menghentikan error,
 * Activity ini dibuat tidak melakukan apa-apa dan langsung ditutup.
 */
class CommentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text("Activity ini sudah tidak digunakan.")
        }
        // Langsung tutup activity ini jika tidak sengaja terbuka
        finish()
    }
}
