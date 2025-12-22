package id.app.ddwancan.view.screen.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.app.ddwancan.R
import id.app.ddwancan.ui.theme.PrimaryBlue
import id.app.ddwancan.viewmodel.ProfileViewModel

/* ============================================================
   MAIN EDIT PROFILE SCREEN
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
    // Parameter onLogoutClick dihapus
) {
    val context = LocalContext.current

    // Mengambil data dari ViewModel
    val nameState = viewModel.name
    val emailState = viewModel.email
    val loading = viewModel.isLoading.value

    // Password tetap kosong karena alasan keamanan (user isi jika ingin ubah)
    var password by remember { mutableStateOf("") }
    // Old password untuk konfirmasi saat mengganti password
    var oldPassword by remember { mutableStateOf("") }
    // Confirm new password
    var confirmPassword by remember { mutableStateOf("") }
    // Selected avatar index (0..7) initialized from ViewModel
    var selectedAvatar by remember { mutableStateOf(viewModel.avatar.value) }

    Scaffold(
        topBar = { EditProfileTopBar(onBackClick) },
        containerColor = Color.White
    ) { innerPadding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            EditProfileContent(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()), // Agar bisa discroll
                name = nameState.value,
                onNameChange = { nameState.value = it },
                email = emailState.value, // Email biasanya read-only, tapi di sini editable
                onEmailChange = { emailState.value = it },
                password = password,
                onPasswordChange = { password = it },
                oldPassword = oldPassword,
                onOldPasswordChange = { oldPassword = it },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it },
                selectedAvatar = selectedAvatar,
                onSelectedAvatarChange = { selectedAvatar = it },
                onSaveClick = {
                    viewModel.updateProfile(
                        newName = nameState.value,
                        newEmail = emailState.value,
                        newPass = password,
                        oldPass = oldPassword,
                        avatarIndex = selectedAvatar
                    ) { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        if (success) {
                            // Optionally navigate back or refresh data
                        }
                    }


                },
                onCancelClick = onBackClick
            )
        }
    }
}

/* ============================================================
   TOP APP BAR
============================================================ */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileTopBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
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
    oldPassword: String,
    onOldPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    selectedAvatar: Int,
    onSelectedAvatarChange: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {

    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        ConfirmationDialog(
            onConfirm = {
                onSaveClick()
                showDialog.value = false
            },
            onDismiss = {
                showDialog.value = false
            }
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(30.dp))

        // Avatar Section (show current)
        ProfileAvatarSection(userName = name, avatarIndex = selectedAvatar)

        Spacer(Modifier.height(16.dp))

        // Avatar selection grid (8 avatars)
        val ctx = LocalContext.current
        Text(text = "Pilih Avatar:", modifier = Modifier.fillMaxWidth(), color = Color.Gray)
        Spacer(Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            for (rowStart in listOf(0, 4)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (i in rowStart until rowStart + 4) {
                        val resId = ctx.resources.getIdentifier("avatar$i", "drawable", ctx.packageName)
                        val painter = if (resId != 0) painterResource(id = resId) else painterResource(id = R.drawable.ic_launcher_foreground)
                        Image(
                            painter = painter,
                            contentDescription = "avatar_$i",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(if (selectedAvatar == i) 2.dp else 1.dp, if (selectedAvatar == i) PrimaryBlue else Color.Gray, CircleShape)
                                .clickable { onSelectedAvatarChange(i) },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(40.dp))

        // Input Name
        ProfileInputRow(
            label = "Name :",
            value = name,
            onValueChange = onNameChange,
            icon = Icons.Outlined.Person
        )

        Spacer(Modifier.height(24.dp))

        // Input Email
        ProfileInputRow(
            label = "Email :",
            value = email,
            onValueChange = onEmailChange,
            icon = Icons.Outlined.Email,
            keyboardType = KeyboardType.Email
        )

        Spacer(Modifier.height(24.dp))

        // Password fields: Old, New, Confirm (user enters old password to confirm change)
        ProfileInputRow(
            label = "Current Password :",
            value = oldPassword,
            onValueChange = onOldPasswordChange,
            icon = Icons.Outlined.Lock,
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation(),
            placeholder = "Masukkan kata sandi lama"
        )

        Spacer(Modifier.height(12.dp))

        ProfileInputRow(
            label = "New Password (Optional) :",
            value = password,
            onValueChange = onPasswordChange,
            icon = Icons.Outlined.Lock,
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation(),
            placeholder = "******"
        )

        Spacer(Modifier.height(12.dp))

        ProfileInputRow(
            label = "Confirm New Password :",
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            icon = Icons.Outlined.Lock,
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation(),
            placeholder = "Konfirmasi kata sandi baru"
        )

        Spacer(Modifier.height(40.dp))

        // Action Buttons (Logout removed from here)
        val ctx2 = LocalContext.current
        ActionButtons(
            onSimpanClick = {
                var canSave = true
                if (password.isNotEmpty()) {
                    if (oldPassword.isEmpty()) {
                        Toast.makeText(ctx2, "Masukkan kata sandi lama", Toast.LENGTH_SHORT).show()
                        canSave = false
                    } else if (password != confirmPassword) {
                        Toast.makeText(ctx2, "Kata sandi baru tidak cocok", Toast.LENGTH_SHORT).show()
                        canSave = false
                    }
                }
                if (canSave) showDialog.value = true
            },
            onBatalClick = onCancelClick
        )

        Spacer(Modifier.height(30.dp))
    }
}


/* ============================================================
   CONFIRMATION DIALOG
============================================================ */
@Composable
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Konfirmasi Perubahan",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text("Apakah Anda yakin ingin simpan perubahan profile?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Simpan", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(12.dp)
    )
}

/* ============================================================
   AVATAR SECTION
============================================================ */
@Composable
fun ProfileAvatarSection(userName: String, avatarIndex: Int = 0) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Use selected avatar resource if exists
        val ctx = LocalContext.current
        val resId = ctx.resources.getIdentifier("avatar$avatarIndex", "drawable", ctx.packageName)
        val painter = if (resId != 0) painterResource(id = resId) else painterResource(id = R.drawable.ic_launcher_foreground)
        Image(
            painter = painter,
            contentDescription = "Profile Pic",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .border(1.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = userName.ifEmpty { "User" },
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
   CUSTOM INPUT ROW
============================================================ */
@Composable
fun ProfileInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    placeholder: String = ""
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

                Box {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(text = placeholder, color = Color.LightGray, fontSize = 16.sp)
                    }
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
        }

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = Color.LightGray
        )
    }
}

/* ============================================================
   ACTION BUTTONS (Hanya Simpan & Batal)
============================================================ */
@Composable
fun ActionButtons(
    onSimpanClick: () -> Unit,
    onBatalClick: () -> Unit
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
            Text("SIMPAN", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
        }

        // Tombol BATAL
        Button(
            onClick = onBatalClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
            modifier = buttonModifier,
            shape = RoundedCornerShape(8.dp),
        ) {
            Text("BATAL", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
        }
    }
}