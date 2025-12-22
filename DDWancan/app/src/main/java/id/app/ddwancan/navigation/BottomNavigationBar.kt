package id.app.ddwancan.navigation

import android.app.Activity
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import id.app.ddwancan.view.activity.*

@Composable
fun BottomNavigationBar(currentRoute: NavRoutes) {

    val activity = LocalContext.current as Activity

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == NavRoutes.HOME,
            onClick = {
                if (currentRoute != NavRoutes.HOME) {
                    activity.startActivity(Intent(activity, HomeActivity::class.java))
                    activity.finish()
                }
            },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.SEARCH,
            onClick = {
                if (currentRoute != NavRoutes.SEARCH) {
                    activity.startActivity(Intent(activity, SearchActivity::class.java))
                    activity.finish()
                }
            },
            icon = { Icon(Icons.Default.Search, null) },
            label = { Text("Search") }
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.FAVORITE,
            onClick = {
                if (currentRoute != NavRoutes.FAVORITE) {
                    activity.startActivity(Intent(activity, FavoriteActivity::class.java))
                    activity.finish()
                }
            },
            icon = { Icon(Icons.Default.Favorite, null) },
            label = { Text("Favorite") }
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.PROFILE,
            onClick = {
                if (currentRoute != NavRoutes.PROFILE) {
                    activity.startActivity(Intent(activity, ProfileActivity::class.java))
                    activity.finish()
                }
            },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Profile") }
        )
    }
}
