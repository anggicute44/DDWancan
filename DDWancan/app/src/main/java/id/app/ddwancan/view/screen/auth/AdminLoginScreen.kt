package id.app.ddwancan.view.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Pastikan import R sesuai package Anda
import id.app.ddwancan.R

@Composable
fun AdminLoginScreen(
    onLoginClick: (String, String) -> Unit,
    onBackToUserLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 1. State untuk menyimpan status visibilitas password (terlihat/tidak)
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- Logo / Icon Admin ---
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Admin Logo",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Admin Portal",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Input Username ---
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Admin Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- Input Password ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,

            // 2. Ubah VisualTransformation berdasarkan state
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),

            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            ),

            // 3. Tambahkan Trailing Icon (Icon Mata)
            trailingIcon = {
                val image = if (isPasswordVisible)
                    Icons.Filled.Visibility // Icon mata terbuka (biasanya untuk 'klik untuk sembunyikan')
                else
                    Icons.Filled.VisibilityOff // Icon mata dicoret (biasanya untuk 'klik untuk lihat')

                // Logika: Jika visible, tampilkan icon Visibility (atau VisibilityOff tergantung selera UX)
                // Di sini saya pakai logika standar:
                // Teks Hidden -> Tampilkan icon "Visibility" (agar user klik untuk melihat)
                // Teks Visible -> Tampilkan icon "VisibilityOff" (agar user klik untuk menutup)

                val iconToShow = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                val description = if (isPasswordVisible) "Hide Password" else "Show Password"

                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = iconToShow,
                        contentDescription = description,
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Tombol Login ---
        Button(
            onClick = { onLoginClick(username, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "LOGIN ADMIN", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Link Kembali ke User Login ---
        TextButton(onClick = onBackToUserLogin) {
            Text(
                "Bukan Admin? Kembali ke Login User",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}