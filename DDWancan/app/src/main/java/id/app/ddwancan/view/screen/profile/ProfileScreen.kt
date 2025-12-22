package id.app.ddwancan.view.screen.profile

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.app.ddwancan.R
import id.app.ddwancan.ui.theme.PrimaryBlue
import id.app.ddwancan.viewmodel.ProfileViewModel
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import id.app.ddwancan.view.activity.HomeActivity
import id.app.ddwancan.view.activity.SearchActivity
import id.app.ddwancan.view.activity.FavoriteActivity

/* ============================================================
   MAIN PROFILE SCREEN
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onEditClick: () -> Unit,
    onNavigateToLogin: () -> Unit, // Callback untuk kembali ke Login
    viewModel: ProfileViewModel = viewModel() // Inject ViewModel
) {
    // Mengambil data dari ViewModel (Realtime / dari Firestore)
    val nameState = viewModel.name.value
    val emailState = viewModel.email.value
    val isLoading = viewModel.isLoading.value

    Scaffold(
        topBar = { ProfileTopBar() },
        bottomBar = { ProfileBottomBar() },
        containerColor = Color.White
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            ProfileContent(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                name = nameState,
                email = emailState,
                onEditClick = onEditClick,
                onLogoutClick = {
                    // Panggil fungsi logout di ViewModel
                    viewModel.logout {
                        onNavigateToLogin()
                    }
                }
            )
        }
    }
}

/* ============================================================
   TOP APP BAR
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar() {
    CenterAlignedTopAppBar(
        navigationIcon = {
            // Biasanya di halaman utama profil (Home) tidak ada tombol back
            // Tapi jika ini submenu, biarkan ada.
        },
        title = {
            Text(
                text = "Profil",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        },
        actions = {
            // Opsional: Ikon Settings
            IconButton(onClick = { /* TODO: Settings */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = PrimaryBlue
        )
    )
}

/* ============================================================
   PROFILE CONTENT
============================================================ */
@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(30.dp))

        ProfileAvatar()

        Spacer(Modifier.height(16.dp))

        // Username Handle (Bisa diambil dari nama atau field username khusus)
        Text(
            text = if (name.isNotEmpty()) "@${name.replace(" ", "").lowercase()}" else "@user",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(Modifier.height(40.dp))

        // Input Name - Display Only
        ProfileInputRowDisplay(
            label = "Name :",
            value = name.ifEmpty { "Loading..." },
            icon = Icons.Outlined.Person
        )

        Spacer(Modifier.height(24.dp))

        // Input Email - Display Only
        ProfileInputRowDisplay(
            label = "Email :",
            value = email.ifEmpty { "Loading..." },
            icon = Icons.Outlined.Email
        )

        Spacer(Modifier.height(50.dp))

        // Tombol Edit
        EditProfileButton(onEditClick = onEditClick)

        Spacer(Modifier.height(16.dp))

        // Tombol Logout
        LogoutButton(onLogoutClick = onLogoutClick)

        Spacer(Modifier.height(30.dp))
    }
}

/* ============================================================
   CUSTOM INPUT ROW - DISPLAY ONLY
============================================================ */
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
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = Color.LightGray
        )
    }
}

/* ============================================================
   PROFILE AVATAR
============================================================ */
@Composable
fun ProfileAvatar() {
    val borderColor = PrimaryBlue
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
                .background(Color.White)
                .border(borderWidth, borderColor, CircleShape)
                .padding(borderWidth)
        ) {
            // Gunakan gambar default android jika gambar user belum diset
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground), // Ganti dengan R.drawable.profilefoto jika ada
                contentDescription = "Profile Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.LightGray) // Background jika transparan
            )
        }
    }
}

/* ============================================================
   BUTTONS
============================================================ */
@Composable
fun EditProfileButton(onEditClick: () -> Unit) {
    Button(
        onClick = onEditClick,
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "EDIT PROFIL",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}

@Composable
fun LogoutButton(onLogoutClick: () -> Unit) {
    OutlinedButton(
        onClick = onLogoutClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Red),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.Red,
            containerColor = Color.White
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "LOG OUT",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

/* ============================================================
   BOTTOM NAVIGATION
============================================================ */
@Composable
fun ProfileBottomBar() {
    Column {
        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        val context = LocalContext.current

        NavigationBar(containerColor = Color.White) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = false,
                onClick = {
                    val intent = Intent(context, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    context.startActivity(intent)
                }
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                label = { Text("Search") },
                selected = false,
                onClick = {
                    val intent = Intent(context, SearchActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    context.startActivity(intent)
                }
            )

            NavigationBarItem(
                icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorite") },
                label = { Text("Favorite") },
                selected = false,
                onClick = {
                    val intent = Intent(context, FavoriteActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    context.startActivity(intent)
                }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text("Profile") },
                selected = true,
                onClick = {},
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFE3F2FD),
                    selectedIconColor = PrimaryBlue
                )
            )
        }
    }
}