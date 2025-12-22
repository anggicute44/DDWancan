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
    var avatar = mutableStateOf(0)
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
                        val av = document.getLong("avatar")
                        avatar.value = av?.toInt() ?: 0
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
        oldPass: String?,
        avatarIndex: Int?,
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
        if (avatarIndex != null && avatarIndex != avatar.value) {
            firestoreUpdateMap["avatar"] = avatarIndex
        }

        // If user wants to change password, require reauthentication with oldPass
        if (newPass.isNotEmpty()) {
            if (oldPass.isNullOrEmpty()) {
                isLoading.value = false
                onResult(false, "Masukkan kata sandi lama untuk mengubah kata sandi.")
                return
            }

            val currentEmailForReauth = user.email ?: email.value
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(
                currentEmailForReauth,
                oldPass
            )

            user.reauthenticate(credential).addOnSuccessListener {
                // After successful reauth, perform updates (email and password)
                val tasks = mutableListOf<com.google.android.gms.tasks.Task<*>>()

                if (newEmail.isNotEmpty() && newEmail != email.value) {
                    tasks.add(user.updateEmail(newEmail).addOnSuccessListener {
                        firestoreUpdateMap["email"] = newEmail
                    })
                }

                tasks.add(user.updatePassword(newPass))

                com.google.android.gms.tasks.Tasks.whenAllSuccess<Any>(tasks).addOnSuccessListener {
                    // update firestore if needed
                    if (firestoreUpdateMap.isNotEmpty()) {
                        db.collection("User").document(uid).update(firestoreUpdateMap)
                            .addOnSuccessListener {
                                Log.d("ProfileViewModel", "Firestore updated successfully.")
                                loadUserProfile()
                                onResult(true, "Profile updated successfully!")
                            }
                            .addOnFailureListener { e ->
                                Log.e("ProfileViewModel", "Firestore update failed", e)
                                onResult(false, "Failed to update profile data: ${e.message}")
                            }
                    } else {
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

            }.addOnFailureListener { e ->
                isLoading.value = false
                Log.e("ProfileViewModel", "Reauthentication failed", e)
                onResult(false, "Reauthentication failed: ${e.message}")
            }

            return
        }

        // If no password change requested, proceed to update email/name as before
        val tasks = mutableListOf<com.google.android.gms.tasks.Task<*>>()

        if (newEmail.isNotEmpty() && newEmail != email.value) {
            tasks.add(user.updateEmail(newEmail).addOnSuccessListener {
                firestoreUpdateMap["email"] = newEmail // Add email to firestore map only if auth succeeds
            })
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
                loadUserProfile()
                onResult(true, "Profile updated successfully!")
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