package com.gzpu.livesdk.demo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gzpu.livesdk.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        val granted = result.values.all { it }
        if (!granted) {
            Toast.makeText(this, "需要相机与麦克风权限才能推流", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ensurePermissions()

        binding.btnPublish.setOnClickListener {
            ensurePermissions()
            startActivity(
                Intent(this, PublishActivity::class.java).apply {
                    putExtra(EXTRA_WHIP_URL, binding.inputWhipUrl.text?.toString().orEmpty())
                    putExtra(EXTRA_TOKEN, binding.inputToken.text?.toString())
                },
            )
        }

        binding.btnPlay.setOnClickListener {
            startActivity(
                Intent(this, PlayActivity::class.java).apply {
                    putExtra(EXTRA_WHEP_URL, binding.inputWhepUrl.text?.toString().orEmpty())
                    putExtra(EXTRA_TOKEN, binding.inputToken.text?.toString())
                },
            )
        }
    }

    private fun ensurePermissions() {
        val needed = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            .filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
        if (needed.isNotEmpty()) {
            permissionLauncher.launch(needed.toTypedArray())
        }
    }

    companion object {
        const val EXTRA_WHIP_URL = "whip_url"
        const val EXTRA_WHEP_URL = "whep_url"
        const val EXTRA_TOKEN = "token"
    }
}
