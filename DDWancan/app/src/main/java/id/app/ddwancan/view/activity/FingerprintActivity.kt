package id.app.ddwancan.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import id.app.ddwancan.data.utils.BiometricUtils

class FingerprintActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!BiometricUtils.isBiometricAvailable(this)) {
            goToHome()
            return
        }

        BiometricUtils.showBiometricPrompt(
            activity = this,
            onSuccess = {
                goToHome()
            },
            onError = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                finish()
            }
        )
    }

    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
