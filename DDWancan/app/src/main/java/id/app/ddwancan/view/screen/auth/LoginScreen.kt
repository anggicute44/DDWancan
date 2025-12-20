package id.app.ddwancan.view.screen.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation // Import ini penting
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.app.ddwancan.R

@Composable
fun LoginScreen(
    onEmailLogin: (String, String) -> Unit,
    onGoogleLogin: () -> Unit,
    onAdminLoginClick: () -> Unit,
    onSignUpClick: () -> Unit = {}
) {
    val PrimaryBlue = Color(0xFF1976D2)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 1. State untuk mengontrol visibilitas password
    var isPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(containerColor = Color.White) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // LOGO
                Image(
                    painter = painterResource(R.drawable.logo1),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .width(180.dp)
                        .height(80.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(Modifier.height(40.dp))

                // EMAIL
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Phone / Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                // PASSWORD (DIPERBARUI)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    // 2. Logika Visual Transformation
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    // 3. Menambahkan Ikon Mata (Toggle)
                    trailingIcon = {
                        val image = if (isPasswordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff

                        // Deskripsi untuk aksesibilitas
                        val description = if (isPasswordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    }
                )

                Spacer(Modifier.height(24.dp))

                // LOGIN BUTTON
                Button(
                    onClick = { onEmailLogin(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(16.dp))

                // FORGOT PASSWORD
                Text(
                    "Forgot Password?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    modifier = Modifier.clickable { /* Handle Forgot Password */ }
                )

                Spacer(Modifier.height(30.dp))

                // GOOGLE LOGIN
                Text(
                    "Sign in with",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                        .clickable { onGoogleLogin() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "G",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4285F4)
                    )
                }

                Spacer(Modifier.height(30.dp))

                // SIGN UP SECTION
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Don't have an account? ", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        "Sign Up",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier.clickable { onSignUpClick() }
                    )
                }

                Spacer(Modifier.height(24.dp))

                // LOGIN AS ADMIN
                TextButton(
                    onClick = onAdminLoginClick
                ) {
                    Text(
                        text = "Login as Admin",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}