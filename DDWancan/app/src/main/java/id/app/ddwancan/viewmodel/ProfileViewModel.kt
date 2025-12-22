package id.app.ddwancan.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.app.ddwancan.data.utils.UserSession

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // State untuk UI
    var name = mutableStateOf("")
    var email = mutableStateOf("")
    var isLoading = mutableStateOf(false)

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: UserSession.userId

        if (uid != null) {
            isLoading.value = true
            db.collection("User").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        name.value = document.getString("name") ?: ""
                        email.value = document.getString("email") ?: ""
                        // Catatan: Password tidak bisa diambil dari Firebase Auth demi keamanan
                    }
                    isLoading.value = false
                }
                .addOnFailureListener {
                    Log.e("ProfileViewModel", "Gagal ambil data", it)
                    isLoading.value = false
                }
        }
    }

    fun updateProfile(newName: String, onResult: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("User").document(uid)
                .update("name", newName)
                .addOnSuccessListener { onResult(true) }
                .addOnFailureListener { onResult(false) }
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        auth.signOut()
        UserSession.userId = null // Bersihkan session lokal
        onLogoutSuccess()
    }
}