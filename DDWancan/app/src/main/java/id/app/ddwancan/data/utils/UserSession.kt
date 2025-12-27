package id.app.ddwancan.data.utils

/**
 * Singleton object to hold the current user's session data.
 * This provides a simple way to access the userId from anywhere in the app
 * after the user has logged in.
 */


object UserSession {
    var userId: String? = null
    var isLoggedIn: Boolean = false
}
