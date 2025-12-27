package id.app.ddwancan.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import id.app.ddwancan.R
import id.app.ddwancan.data.utils.UserSession

// PERBAIKAN 1: Kembali menjadi ViewModel biasa
class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var name = mutableStateOf("")
    var email = mutableStateOf("")
    var avatar = mutableStateOf(0)
    var isLoading = mutableStateOf(false)

    private var userListener: ListenerRegistration? = null

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: UserSession.userId
        userListener?.remove()

        if (uid != null) {
            isLoading.value = true
            userListener = db.collection("User").document(uid)
                .addSnapshotListener { document, error ->
                    isLoading.value = false
                    if (error != null) {
                        Log.e("ProfileViewModel", "Listener error", error)
                        return@addSnapshotListener
                    }
                    if (document != null && document.exists()) {
                        name.value = document.getString("name") ?: ""
                        email.value = document.getString("email") ?: ""
                        val av = document.getLong("avatar")
                        avatar.value = av?.toInt() ?: 0
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
    }

    fun updateProfile(
        newName: String, newEmail: String, newPass: String, oldPass: String?,
        avatarIndex: Int?, onResult: (Boolean, String) -> Unit
    ) {
         val user = auth.currentUser
        if (user == null) {
            onResult(false, "User not logged in.")
            return
        }

        isLoading.value = true
        val uid = user.uid

        val firestoreUpdateMap = mutableMapOf<String, Any>()
        if (newName.isNotEmpty() && newName != name.value) {
            firestoreUpdateMap["name"] = newName
        }
        if (avatarIndex != null && avatarIndex != avatar.value) {
            firestoreUpdateMap["avatar"] = avatarIndex
        }

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
                val tasks = mutableListOf<com.google.android.gms.tasks.Task<*>>()

                if (newEmail.isNotEmpty() && newEmail != email.value) {
                    tasks.add(user.updateEmail(newEmail).addOnSuccessListener {
                        firestoreUpdateMap["email"] = newEmail
                    })
                }

                tasks.add(user.updatePassword(newPass))

                com.google.android.gms.tasks.Tasks.whenAllSuccess<Any>(tasks).addOnSuccessListener {
                    if (firestoreUpdateMap.isNotEmpty()) {
                        db.collection("User").document(uid).update(firestoreUpdateMap)
                            .addOnSuccessListener {
                                loadUserProfile()
                                onResult(true, "Profile updated successfully!")
                            }
                            .addOnFailureListener { e ->
                                onResult(false, "Failed to update profile data: ${e.message}")
                            }
                    } else {
                        loadUserProfile()
                        onResult(true, "Password updated successfully!")
                    }
                }.addOnFailureListener { e ->
                    isLoading.value = false
                    val errorMessage = e.message ?: "An unknown error occurred."
                    onResult(false, errorMessage)
                }

            }.addOnFailureListener { e ->
                isLoading.value = false
                onResult(false, "Reauthentication failed: ${e.message}")
            }

            return
        }

        val tasks = mutableListOf<com.google.android.gms.tasks.Task<*>>()

        if (newEmail.isNotEmpty() && newEmail != email.value) {
            tasks.add(user.updateEmail(newEmail).addOnSuccessListener {
                firestoreUpdateMap["email"] = newEmail
            })
        }

        com.google.android.gms.tasks.Tasks.whenAllSuccess<Any>(tasks).addOnSuccessListener {
            if (firestoreUpdateMap.isNotEmpty()) {
                db.collection("User").document(uid).update(firestoreUpdateMap)
                    .addOnSuccessListener {
                        loadUserProfile()
                        onResult(true, "Profile updated successfully!")
                    }
                    .addOnFailureListener { e ->
                        onResult(false, "Failed to update profile data: ${e.message}")
                    }
            } else {
                loadUserProfile()
                onResult(true, "Profile updated successfully!")
            }
        }.addOnFailureListener { e ->
            isLoading.value = false
            val errorMessage = e.message ?: "An unknown error occurred."
            onResult(false, errorMessage)
        }
    }

    // PERBAIKAN 2: Fungsi logout tidak lagi menghapus kredensial sidik jari
    fun logout(context: Context, onLogoutSuccess: () -> Unit) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleClient = GoogleSignIn.getClient(context, gso)

        // 1. Logout dari Firebase
        auth.signOut()

        // 2. Logout dari Google
        googleClient.signOut().addOnCompleteListener {
            // 3. Hapus session ID lokal
            UserSession.userId = null
            
            // 4. Panggil callback setelah semua selesai
            onLogoutSuccess()
        }
    }
}
