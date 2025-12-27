package id.app.ddwancan.view.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode2 // Jika error, ganti QrCode biasa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import id.app.ddwancan.ui.theme.DDwancanTheme

@Composable
fun ShareQrDialog(
    onDismiss: () -> Unit // Callback saat tombol X atau Done diklik
) {
    // Komponen Dialog akan membuat background layar belakang menjadi gelap otomatis
    Dialog(onDismissRequest = onDismiss) {

        val context = LocalContext.current
        val settings = remember { id.app.ddwancan.data.local.SettingsPreference(context) }
        val isEnglish by settings.isEnglish.collectAsState(initial = false)

        // Kartu Putih Utama
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Jarak kartu dari tepi layar
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp), // Padding konten di dalam kartu
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- 1. HEADER (Judul & Tombol Close) ---
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isEnglish) "Share Article" else "Bagikan Artikel",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    // Tombol X di pojok kanan
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // --- 2. AREA QR CODE ---
                Box(
                    modifier = Modifier
                        .size(200.dp) // Ukuran kotak QR
                        .border(
                            width = 2.dp,
                            color = Color(0xFF2678FF), // Border Biru
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Ikon QR Code (Bisa diganti Image jika punya gambar asli)
                    Icon(
                        imageVector = Icons.Default.QrCode2, // Gunakan Icons.Default.QrCode jika QrCode2 merah
                        contentDescription = "QR Code",
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- 3. TEKS INSTRUKSI ---
                Text(
                    text = if (isEnglish) "Scan to read on another device" else "Pindai untuk membaca di perangkat lain",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(30.dp))

                // --- 4. TOMBOL DONE ---
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2678FF)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isEnglish) "Done" else "Selesai",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Preview Function for the Dialog
@Preview(showBackground = true)
@Composable
fun PreviewShareQrDialog() {
    DDwancanTheme {
        // Dialog needs a background to be visible in preview
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            ShareQrDialog(onDismiss = {})
        }
    }
}
