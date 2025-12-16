package id.app.ddwancan.view.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.ddwancan.R
import id.app.ddwancan.ui.theme.PrimaryBlue

/* ============================================================
   MAIN EDIT PROFILE SCREEN
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onSaveClick: (String, String, String) -> Unit = { _, _, _ -> },
    onCancelClick: () -> Unit = {}
) {
    // State untuk menyimpan nilai input yang berubah
    var name by remember { mutableStateOf("71220197_GianP") }
    var email by remember { mutableStateOf("GianP@students.ukdw.ac.id") }
    var password by remember { mutableStateOf("password123") }

    Scaffold(
        topBar = { EditProfileTopBar() },
        bottomBar = { ProfileBottomBar() },
        containerColor = Color.White
    ) { innerPadding ->
        EditProfileContent(
            modifier = Modifier.padding(innerPadding),
            name = name,
            onNameChange = { name = it },
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it },
            onSaveClick = {
                onSaveClick(name, email, password)
            },
            onCancelClick = onCancelClick
        )
    }
}

/* ============================================================
   TOP APP BAR
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileTopBar() {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = { /* TODO: Handle Back navigation */ }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        },
        title = {
            Text(
                text = "Edit Profil",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = PrimaryBlue
        )
    )
}

/* ============================================================
   EDIT PROFILE CONTENT
============================================================ */
@Composable
fun EditProfileContent(
    modifier: Modifier = Modifier,
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(30.dp))

        ProfileAvatarSection()

        Spacer(Modifier.height(40.dp))

        // Input Name - EDITABLE
        ProfileInputRow(
            label = "Name :",
            value = name,
            onValueChange = onNameChange,
            icon = Icons.Outlined.Person
        )

        Spacer(Modifier.height(24.dp))

        // Input Email - EDITABLE
        ProfileInputRow(
            label = "Email :",
            value = email,
            onValueChange = onEmailChange,
            icon = Icons.Outlined.Email,
            keyboardType = KeyboardType.Email
        )

        Spacer(Modifier.height(24.dp))

        // Input Confirm Password - EDITABLE
        ProfileInputRow(
            label = "Confirm Password :",
            value = password,
            onValueChange = onPasswordChange,
            icon = Icons.Outlined.Lock,
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(50.dp))

        // Action Buttons
        ActionButtons(
            onSimpanClick = onSaveClick,
            onBatalClick = onCancelClick
        )

        Spacer(Modifier.height(30.dp))
    }
}

/* ============================================================
   AVATAR SECTION
============================================================ */
@Composable
fun ProfileAvatarSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ProfileAvatar()

        Spacer(Modifier.height(12.dp))

        Text(
            text = "@71220917_GianP",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Change Image",
            color = PrimaryBlue,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { /* TODO: Handle change image */ }
        )
    }
}

/* ============================================================
   CUSTOM INPUT ROW - EDITABLE
============================================================ */
@Composable
fun ProfileInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
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

                // TextField - bisa di-edit
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    visualTransformation = visualTransformation,
                    cursorBrush = SolidColor(PrimaryBlue),
                    modifier = Modifier.fillMaxWidth()
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
   ACTION BUTTONS (SIMPAN & BATAL)
============================================================ */
@Composable
fun ActionButtons(
    onSimpanClick: () -> Unit = {},
    onBatalClick: () -> Unit = {}
) {
    val buttonModifier = Modifier
        .fillMaxWidth()
        .height(50.dp)

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        // Tombol SIMPAN
        Button(
            onClick = onSimpanClick,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            modifier = buttonModifier,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "SIMPAN",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }

        // Tombol BATAL
        Button(
            onClick = onBatalClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
            modifier = buttonModifier,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                "BATAL",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}