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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.app.ddwancan.viewmodel.ProfileViewModel
import id.app.ddwancan.data.local.SettingsPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    onLanguageChange: (Boolean) -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val settings = remember { SettingsPreference(context) }

    val nameState = viewModel.name
    val emailState = viewModel.email
    val loading = viewModel.isLoading.value

    var password by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf(viewModel.avatar.value) }

    var showSettings by remember { mutableStateOf(false) }
    
    val isDarkMode by settings.isDarkMode.collectAsState(initial = false)
    val isEnglish by settings.isEnglish.collectAsState(initial = false)

    LaunchedEffect(viewModel.avatar.value) {
        selectedAvatar = viewModel.avatar.value
    }

    Scaffold(
        topBar = {
            EditProfileTopBar(
                isEnglish = isEnglish,
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
                    onDarkModeChange(it)
                },
                onLanguageChange = {
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
                isEnglish = isEnglish,
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
    isEnglish: Boolean,
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
                text = if (isEnglish) "Edit Profile" else "Ubah Profil",
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
    isEnglish: Boolean = false,
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

        Text(if (isEnglish) "Personal Information" else "Informasi Pribadi", fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        ProfileInputField(label = if (isEnglish) "Name" else "Nama", value = name, onValueChange = onNameChange, icon = Icons.Outlined.Person)
        Spacer(Modifier.height(8.dp))
        ProfileInputField(label = if (isEnglish) "Email" else "Email", value = email, onValueChange = onEmailChange, icon = Icons.Outlined.Email)

        Spacer(Modifier.height(24.dp))

        Text(if (isEnglish) "Change Password" else "Ubah Password", fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        ProfileInputField(label = if (isEnglish) "Old Password" else "Password Lama", value = oldPassword, onValueChange = onOldPasswordChange, icon = Icons.Outlined.Lock, isPassword = true)
        Spacer(Modifier.height(8.dp))
        ProfileInputField(label = if (isEnglish) "New Password" else "Password Baru", value = password, onValueChange = onPasswordChange, icon = Icons.Outlined.Lock, isPassword = true)
        Spacer(Modifier.height(8.dp))
        ProfileInputField(label = if (isEnglish) "Confirm New Password" else "Konfirmasi Password Baru", value = confirmPassword, onValueChange = onConfirmPasswordChange, icon = Icons.Outlined.Lock, isPassword = true)

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
                Text(if (isEnglish) "Save" else "Simpan")
            }
            OutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (isEnglish) "Cancel" else "Batal")
            }
        }
    }
}

@Composable
fun AvatarSelector(selectedAvatar: Int, onSelectedAvatarChange: (Int) -> Unit) {
    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val resId = context.resources.getIdentifier("avatar$selectedAvatar", "drawable", context.packageName)
        val painter = if (resId != 0) painterResource(id = resId) else painterResource(id = id.app.ddwancan.R.drawable.ic_launcher_foreground)
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

        // Avatar selection grid (8 avatars)
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            for (rowStart in listOf(0, 4)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in rowStart until rowStart + 4) {
                        val idRes = context.resources.getIdentifier("avatar$i", "drawable", context.packageName)
                        val imgPainter = if (idRes != 0) painterResource(id = idRes) else painterResource(id = id.app.ddwancan.R.drawable.ic_launcher_foreground)
                        Image(
                            painter = imgPainter,
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
