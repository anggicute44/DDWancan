package id.app.ddwancan.navigation

import android.app.Activity
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import id.app.ddwancan.data.local.SettingsPreference
import androidx.compose.ui.platform.LocalContext
import id.app.ddwancan.view.activity.*

@Composable
fun BottomNavigationBar(currentRoute: NavRoutes) {

    val activity = LocalContext.current as Activity
    val context = LocalContext.current
    val settings = remember { SettingsPreference(context) }
    val isEnglish by settings.isEnglish.collectAsState(initial = false)

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = currentRoute == NavRoutes.HOME,
            onClick = {
                if (currentRoute != NavRoutes.HOME) {
                    activity.startActivity(Intent(activity, HomeActivity::class.java))
                    activity.finish()
                }
            },
            icon = { Icon(Icons.Default.Home, null, tint = if (currentRoute == NavRoutes.HOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
            label = { Text(if (isEnglish) "Home" else "Beranda", color = if (currentRoute == NavRoutes.HOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.SEARCH,
            onClick = {
                if (currentRoute != NavRoutes.SEARCH) {
                    activity.startActivity(Intent(activity, SearchActivity::class.java))
                    activity.finish()
                }
            },
            icon = { Icon(Icons.Default.Search, null, tint = if (currentRoute == NavRoutes.SEARCH) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
            label = { Text(if (isEnglish) "Search" else "Pencarian", color = if (currentRoute == NavRoutes.SEARCH) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.FAVORITE,
            onClick = {
                if (currentRoute != NavRoutes.FAVORITE) {
                    activity.startActivity(Intent(activity, FavoriteActivity::class.java))
                    activity.finish()
                }
            },
            icon = { Icon(Icons.Default.Favorite, null, tint = if (currentRoute == NavRoutes.FAVORITE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
            label = { Text(if (isEnglish) "Favorite" else "Favorit", color = if (currentRoute == NavRoutes.FAVORITE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.PROFILE,
            onClick = {
                if (currentRoute != NavRoutes.PROFILE) {
                    activity.startActivity(Intent(activity, ProfileActivity::class.java))
                    activity.finish()
                }
            },
            icon = { Icon(Icons.Default.Person, null, tint = if (currentRoute == NavRoutes.PROFILE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
            label = { Text(if (isEnglish) "Profile" else "Profil", color = if (currentRoute == NavRoutes.PROFILE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
        )
    }
}
