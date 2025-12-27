package id.app.ddwancan.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import com.google.firebase.firestore.FirebaseFirestore
import id.app.ddwancan.data.model.UserItem
import id.app.ddwancan.data.local.SettingsPreference
import id.app.ddwancan.ui.theme.DDwancanTheme
import id.app.ddwancan.view.screen.admin.ManageUsersScreen

class ManageUsersActivity : ComponentActivity() {

    private val db = FirebaseFirestore.getInstance()
    // State untuk list user (sebaiknya gunakan ViewModel, tapi ini versi simple di Activity)
    private val userListState = mutableStateListOf<UserItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchUsers()

        setContent {
            val context = this@ManageUsersActivity
            val settings = remember { SettingsPreference(context) }
            val isDarkMode by settings.isDarkMode.collectAsState(initial = false)

            DDwancanTheme(darkTheme = isDarkMode) {
                // State untuk Dialog Konfirmasi
                var showDialog by remember { mutableStateOf(false) }
                var selectedUser by remember { mutableStateOf<UserItem?>(null) }

                ManageUsersScreen(
                    userList = userListState,
                    onBackClick = { finish() },
                    onDeleteClick = { user ->
                        selectedUser = user
                        showDialog = true
                    }
                )

                // Dialog Konfirmasi Hapus
                if (showDialog && selectedUser != null) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Hapus Pengguna?") },
                        text = { Text("Apakah Anda yakin ingin menghapus user '${selectedUser?.email}'? User tidak akan bisa melihat data lagi.") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    deleteUserLogic(selectedUser!!)
                                    showDialog = false
                                }
                            ) {
                                Text("Hapus", color = androidx.compose.ui.graphics.Color.Red)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Batal")
                            }
                        }
                    )
                }
            }
        }
    }

    private fun fetchUsers() {
        // Asumsi data user disimpan di collection "users"
        db.collection("User")
            .get()
            .addOnSuccessListener { result ->
                userListState.clear()
                for (document in result) {
                    val uid = document.id
                    val email = document.getString("email") ?: "No Email"
                    val name = document.getString("name") ?: "No Name"

                    // Jangan tampilkan admin sendiri di list ini
                    if (email != "admin") {
                        userListState.add(UserItem(uid, name, email))
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengambil data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteUserLogic(user: UserItem) {
        // 1. Hapus dokumen dari Firestore (Soft Delete)
        // Ini akan menghapus data profil mereka.
        db.collection("User").document(user.uid)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Data user berhasil dihapus", Toast.LENGTH_SHORT).show()
                // Refresh list local
                userListState.remove(user)

                // OPTIONAL: Tambahkan ke collection "banned_users" agar sistem tahu UID ini dilarang
                banUser(user.uid)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menghapus: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun banUser(uid: String) {
        val banData = hashMapOf("bannedAt" to System.currentTimeMillis())
        db.collection("banned_users").document(uid).set(banData)
    }
}