package id.app.ddwancan.view.screen.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.app.ddwancan.R
import id.app.ddwancan.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current

    val nameState = viewModel.name
    val emailState = viewModel.email
    val loading = viewModel.isLoading.value

    var password by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf(viewModel.avatar.value) }

    LaunchedEffect(key1 = viewModel.avatar.value) {
        selectedAvatar = viewModel.avatar.value
    }

    Scaffold(
        topBar = { EditProfileTopBar(onBackClick) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            EditProfileContent(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
                name = nameState.value,
                onNameChange = { nameState.value = it },
                email = emailState.value,
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
                            onBackClick() // Kembali jika sukses
                        }
                    }
                },
                onCancelClick = onBackClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileTopBar(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        title = {
            Text(
                text = "Edit Profil",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

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
    val context = LocalContext.current

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

        ProfileAvatarSection(userName = name, avatarIndex = selectedAvatar)

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Pilih Avatar:",
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(Modifier.height(8.dp))

        // Avatar selection grid (8 avatars)
        Column(modifier = Modifier.fillMaxWidth()) {
            for (rowStart in listOf(0, 4)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (i in rowStart until rowStart + 4) {
                        val resId = context.resources.getIdentifier("avatar$i", "drawable", context.packageName)
                        val painter = if (resId != 0) painterResource(id = resId) else painterResource(id = R.drawable.ic_launcher_foreground)
                        Image(
                            painter = painter,
                            contentDescription = "avatar_$i",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (selectedAvatar == i) 2.dp else 1.dp,
                                    color = if (selectedAvatar == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                                .clickable { onSelectedAvatarChange(i) },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(40.dp))

        ProfileInputRow(label = "Name :", value = name, onValueChange = onNameChange, icon = Icons.Outlined.Person)
        Spacer(Modifier.height(24.dp))
        ProfileInputRow(label = "Email :", value = email, onValueChange = onEmailChange, icon = Icons.Outlined.Email, keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(24.dp))
        ProfileInputRow(label = "Current Password :", value = oldPassword, onValueChange = onOldPasswordChange, icon = Icons.Outlined.Lock, keyboardType = KeyboardType.Password, visualTransformation = PasswordVisualTransformation(), placeholder = "Masukkan kata sandi lama")
        Spacer(Modifier.height(12.dp))
        ProfileInputRow(label = "New Password (Optional) :", value = password, onValueChange = onPasswordChange, icon = Icons.Outlined.Lock, keyboardType = KeyboardType.Password, visualTransformation = PasswordVisualTransformation(), placeholder = "******")
        Spacer(Modifier.height(12.dp))
        ProfileInputRow(label = "Confirm New Password :", value = confirmPassword, onValueChange = onConfirmPasswordChange, icon = Icons.Outlined.Lock, keyboardType = KeyboardType.Password, visualTransformation = PasswordVisualTransformation(), placeholder = "Konfirmasi kata sandi baru")

        Spacer(Modifier.height(40.dp))

        ActionButtons(
            onSimpanClick = {
                var canSave = true
                if (password.isNotEmpty()) {
                    if (oldPassword.isEmpty()) {
                        Toast.makeText(context, "Masukkan kata sandi lama", Toast.LENGTH_SHORT).show()
                        canSave = false
                    } else if (password != confirmPassword) {
                        Toast.makeText(context, "Kata sandi baru tidak cocok", Toast.LENGTH_SHORT).show()
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

@Composable
fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Perubahan", fontWeight = FontWeight.SemiBold) },
        text = { Text("Apakah Anda yakin ingin simpan perubahan profile?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Simpan", color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        },
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun ProfileAvatarSection(userName: String, avatarIndex: Int = 0) {
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val resId = context.resources.getIdentifier("avatar$avatarIndex", "drawable", context.packageName)
        val painter = if (resId != 0) painterResource(id = resId) else painterResource(id = R.drawable.ic_launcher_foreground)
        Image(
            painter = painter,
            contentDescription = "Current Avatar",
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(8.dp))
        Text(text = userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    placeholder: String = ""
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Spacer(Modifier.height(4.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(icon, contentDescription = null) },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            placeholder = { Text(placeholder) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun ActionButtons(onSimpanClick: () -> Unit, onBatalClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(onClick = onBatalClick, modifier = Modifier.weight(1f)) {
            Text("Batal")
        }
        Button(
            onClick = onSimpanClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Simpan", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
