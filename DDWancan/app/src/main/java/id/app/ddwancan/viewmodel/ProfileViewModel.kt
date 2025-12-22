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

    fun updateProfile(
        newName: String,
        newEmail: String,
        newPass: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false, "User not logged in.")
            return
        }

        isLoading.value = true
        val uid = user.uid

        val firestoreUpdateMap = mutableMapOf<String, Any>()
        if (newName != name.value) {
            firestoreUpdateMap["name"] = newName
        }

        val tasks = mutableListOf<com.google.android.gms.tasks.Task<*>>()

        // Task 1: Update Email in Firebase Auth
        if (newEmail.isNotEmpty() && newEmail != email.value) {
            tasks.add(user.updateEmail(newEmail).addOnSuccessListener {
                firestoreUpdateMap["email"] = newEmail // Add email to firestore map only if auth succeeds
            })
        }

        // Task 2: Update Password in Firebase Auth
        if (newPass.isNotEmpty()) {
            tasks.add(user.updatePassword(newPass))
        }

        com.google.android.gms.tasks.Tasks.whenAllSuccess<Any>(tasks).addOnSuccessListener {
            // After Auth changes are successful, update Firestore
            if (firestoreUpdateMap.isNotEmpty()) {
                db.collection("User").document(uid).update(firestoreUpdateMap)
                    .addOnSuccessListener {
                        Log.d("ProfileViewModel", "Firestore updated successfully.")
                        loadUserProfile() // Reload data
                        onResult(true, "Profile updated successfully!")
                    }
                    .addOnFailureListener { e ->
                        Log.e("ProfileViewModel", "Firestore update failed", e)
                        onResult(false, "Failed to update profile data: ${e.message}")
                    }
            } else {
                // This case happens if only password was changed, as name/email were not
                loadUserProfile()
                onResult(true, "Password updated successfully!")
            }
        }.addOnFailureListener { e ->
            isLoading.value = false
            Log.e("ProfileViewModel", "Auth update failed", e)
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException ->
                    "This action requires recent login. Please log out and log in again."
                else -> e.message ?: "An unknown error occurred."
            }
            onResult(false, errorMessage)
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        auth.signOut()
        UserSession.userId = null // Bersihkan session lokal
        onLogoutSuccess()
    }
}