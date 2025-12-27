package id.app.ddwancan.view.screen.profile

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.app.ddwancan.R
import id.app.ddwancan.view.activity.FavoriteActivity
import id.app.ddwancan.view.activity.HomeActivity
import id.app.ddwancan.view.activity.SearchActivity
import id.app.ddwancan.view.activity.SettingsActivity
import id.app.ddwancan.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val nameState = viewModel.name.value
    val emailState = viewModel.email.value
    val avatarIndex = viewModel.avatar.value
    val isLoading = viewModel.isLoading.value
    // PERBAIKAN: Dapatkan context di sini
    val context = LocalContext.current
    val settings = remember { id.app.ddwancan.data.local.SettingsPreference(context) }
    val isEnglish by settings.isEnglish.collectAsState(initial = false)

    Scaffold(
        topBar = { ProfileTopBar(isEnglish) },
        bottomBar = { ProfileBottomBar(isEnglish) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            ProfileContent(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                name = nameState,
                email = emailState,
                avatarIndex = avatarIndex,
                onEditClick = onEditClick,
                isEnglish = isEnglish,
                onLogoutClick = {
                    // PERBAIKAN: Kirim context ke fungsi logout
                    viewModel.logout(context) {
                        onNavigateToLogin()
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(isEnglish: Boolean) {
    val context = LocalContext.current
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = if (isEnglish) "Profile" else "Profil",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        actions = {
            IconButton(onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
    avatarIndex: Int = 0,
    onEditClick: () -> Unit,
    isEnglish: Boolean = false,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(30.dp))

        ProfileAvatar(avatarIndex = avatarIndex)

        Spacer(Modifier.height(16.dp))

        Text(
            text = if (name.isNotEmpty()) "@${name.replace(" ", "").lowercase()}" else "@user",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(40.dp))

        ProfileInputRowDisplay(
            label = if (isEnglish) "Name :" else "Nama :",
            value = name.ifEmpty { "Loading..." },
            icon = Icons.Outlined.Person
        )

        Spacer(Modifier.height(24.dp))

        ProfileInputRowDisplay(
            label = if (isEnglish) "Email :" else "Email :",
            value = email.ifEmpty { "Loading..." },
            icon = Icons.Outlined.Email
        )

        Spacer(Modifier.height(50.dp))

        EditProfileButton(onEditClick = onEditClick, isEnglish = isEnglish)

        Spacer(Modifier.height(16.dp))

        LogoutButton(onLogoutClick = onLogoutClick, isEnglish = isEnglish)

        Spacer(Modifier.height(30.dp))
    }
}

@Composable
fun ProfileInputRowDisplay(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.Black
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun ProfileAvatar(avatarIndex: Int) {
    val borderColor = MaterialTheme.colorScheme.primary
    val borderWidth = 3.dp
    val avatarSize = 110.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(avatarSize)
    ) {
        Box(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(borderWidth, borderColor, CircleShape)
                .padding(borderWidth)
        ) {
            val ctx = LocalContext.current
            val resId = ctx.resources.getIdentifier("avatar$avatarIndex", "drawable", ctx.packageName)
            val painter = if (resId != 0) painterResource(id = resId) else painterResource(id = R.drawable.ic_launcher_foreground)
            Image(
                painter = painter,
                contentDescription = "Profile Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

@Composable
fun EditProfileButton(onEditClick: () -> Unit, isEnglish: Boolean = false) {
    Button(
        onClick = onEditClick,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = if (isEnglish) "EDIT PROFILE" else "UBAH PROFIL",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun LogoutButton(onLogoutClick: () -> Unit, isEnglish: Boolean = false) {
    OutlinedButton(
        onClick = onLogoutClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error,
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isEnglish) "LOG OUT" else "KELUAR",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ProfileBottomBar(isEnglish: Boolean = false) {
    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
        val context = LocalContext.current

        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text(if (isEnglish) "Home" else "Beranda") },
                selected = false,
                onClick = {
                    val intent = Intent(context, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    context.startActivity(intent)
                }
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                label = { Text(if (isEnglish) "Search" else "Pencarian") },
                selected = false,
                onClick = {
                    val intent = Intent(context, SearchActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    context.startActivity(intent)
                }
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorite") },
                label = { Text(if (isEnglish) "Favorite" else "Favorit") },
                selected = false,
                onClick = {
                    val intent = Intent(context, FavoriteActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    context.startActivity(intent)
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text(if (isEnglish) "Profile" else "Profil") },
                selected = true,
                onClick = {},
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}
