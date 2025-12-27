package id.app.ddwancan.view.screen.profile

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.app.ddwancan.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    onLanguageChange: (Boolean) -> Unit,
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

    var showSettings by remember { mutableStateOf(false) }
    var isDarkMode by remember { mutableStateOf(false) }
    var isEnglish by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.avatar.value) {
        selectedAvatar = viewModel.avatar.value
    }

    Scaffold(
        topBar = {
            EditProfileTopBar(
                onBackClick = onBackClick,
                onSettingsClick = { showSettings = true }
            )
        }
    ) { innerPadding ->

        if (showSettings) {
            SettingsDialog(
                isDarkMode = isDarkMode,
                isEnglish = isEnglish,
                onDarkModeChange = {
                    isDarkMode = it
                    onDarkModeChange(it)
                },
                onLanguageChange = {
                    isEnglish = it
                    onLanguageChange(it)
                },
                onDismiss = { showSettings = false }
            )
        }

        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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
                        if (success) onBackClick()
                    }
                },
                onCancelClick = onBackClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileTopBar(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        title = {
            Text(
                text = "Edit Profil",
                fontWeight = FontWeight.SemiBold
            )
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )
}

@Composable
fun SettingsDialog(
    isDarkMode: Boolean,
    isEnglish: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onLanguageChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isEnglish) "Settings" else "Pengaturan")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isEnglish) "Dark Mode" else "Mode Gelap")
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = onDarkModeChange
                    )
                }
                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(if (isEnglish) "Language" else "Bahasa")
                        Text(
                            if (isEnglish) "English" else "Indonesia",
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = isEnglish,
                        onCheckedChange = onLanguageChange
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
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
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarSelector(
            selectedAvatar = selectedAvatar,
            onSelectedAvatarChange = onSelectedAvatarChange
        )

        Spacer(Modifier.height(24.dp))

        Text("Informasi Pribadi", fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        ProfileInputField(label = "Nama", value = name, onValueChange = onNameChange, icon = Icons.Outlined.Person)
        Spacer(Modifier.height(8.dp))
        ProfileInputField(label = "Email", value = email, onValueChange = onEmailChange, icon = Icons.Outlined.Email)

        Spacer(Modifier.height(24.dp))

        Text("Ganti Password", fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        ProfileInputField(label = "Password Lama", value = oldPassword, onValueChange = onOldPasswordChange, icon = Icons.Outlined.Lock, isPassword = true)
        Spacer(Modifier.height(8.dp))
        ProfileInputField(label = "Password Baru", value = password, onValueChange = onPasswordChange, icon = Icons.Outlined.Lock, isPassword = true)
        Spacer(Modifier.height(8.dp))
        ProfileInputField(label = "Konfirmasi Password Baru", value = confirmPassword, onValueChange = onConfirmPasswordChange, icon = Icons.Outlined.Lock, isPassword = true)

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onSaveClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Simpan")
            }
            OutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Batal")
            }
        }
    }
}

@Composable
fun AvatarSelector(selectedAvatar: Int, onSelectedAvatarChange: (Int) -> Unit) {
    val avatars = listOf(
        Icons.Outlined.Face,
        Icons.Outlined.AccountCircle,
        Icons.Outlined.Person,
        Icons.Outlined.SupervisorAccount,
        Icons.Outlined.Pets,
        Icons.Outlined.EmojiEmotions
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = avatars.getOrElse(selectedAvatar) { avatars[0] },
            contentDescription = "Selected Avatar",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .padding(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            avatars.forEachIndexed { index, icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = "Avatar $index",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(
                            width = if (index == selectedAvatar) 2.dp else 0.dp,
                            color = if (index == selectedAvatar) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onSelectedAvatarChange(index) }
                        .padding(4.dp),
                    tint = if (index == selectedAvatar) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text)
    )
}
