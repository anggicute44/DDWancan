package id.app.ddwancan.view.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import id.app.ddwancan.R
import id.app.ddwancan.ui.theme.PrimaryBlue

/* ============================================================
   MAIN PROFILE SCREEN
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onEditClick: () -> Unit = {}) {
    // State untuk menyimpan data profile
    var name by remember { mutableStateOf("71220197_GianP") }
    var email by remember { mutableStateOf("GianP@students.ukdw.ac.id") }

    Scaffold(
        topBar = { ProfileTopBar() },
        bottomBar = { ProfileBottomBar() },
        containerColor = Color.White
    ) { innerPadding ->
        ProfileContent(
            modifier = Modifier.padding(innerPadding),
            name = name,
            email = email,
            onEditClick = onEditClick
        )
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
            IconButton(onClick = { /* TODO */ }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
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
            IconButton(onClick = { /* TODO */ }) {
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
    onEditClick: () -> Unit
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

        // Username Handle
        Text(
            text = "@71220917_GianP",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(Modifier.height(40.dp))

        // Input Name - Display Only
        ProfileInputRowDisplay(
            label = "Name :",
            value = name,
            icon = Icons.Outlined.Person
        )

        Spacer(Modifier.height(24.dp))

        // Input Email - Display Only
        ProfileInputRowDisplay(
            label = "Email :",
            value = email,
            icon = Icons.Outlined.Email
        )

        Spacer(Modifier.height(50.dp))

        EditProfileButton(onEditClick = onEditClick)
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

                // Hanya menampilkan teks (bukan TextField)
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
            Image(
                painter = painterResource(R.drawable.profilefoto),
                contentDescription = "Profile Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }
    }
}

/* ============================================================
   EDIT PROFILE BUTTON
============================================================ */
@Composable
fun EditProfileButton(onEditClick: () -> Unit) {
    Button(
        onClick = onEditClick,
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        modifier = Modifier
            .padding(horizontal = 20.dp)
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

/* ============================================================
   BOTTOM NAVIGATION
============================================================ */
@Composable
fun ProfileBottomBar() {
    Column {
        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

        NavigationBar(containerColor = Color.White) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = false,
                onClick = {}
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorite") },
                label = { Text("Favorite") },
                selected = false,
                onClick = {}
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                label = { Text("Search") },
                selected = false,
                onClick = {}
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