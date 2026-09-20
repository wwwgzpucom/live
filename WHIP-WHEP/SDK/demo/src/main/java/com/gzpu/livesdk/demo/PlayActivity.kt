package com.gzpu.livesdk.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gzpu.livesdk.LiveEvent
import com.gzpu.livesdk.LiveEventListener
import com.gzpu.livesdk.LiveSdk
import com.gzpu.livesdk.LiveSessionConfig
import com.gzpu.livesdk.demo.databinding.ActivityPlayBinding

class PlayActivity : AppCompatActivity(), LiveEventListener {
    private lateinit var binding: ActivityPlayBinding
    private val player = LiveSdk.createPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val whepUrl = intent.getStringExtra(MainActivity.EXTRA_WHEP_URL).orEmpty()
        val token = intent.getStringExtra(MainActivity.EXTRA_TOKEN)

        player.setEventListener(this)
        player.attachRenderer(binding.renderer)
        player.start(
            LiveSessionConfig(
                endpoint = whepUrl,
                token = token?.ifBlank { null },
            ),
        )

        binding.btnStop.setOnClickListener {
            player.stop()
            finish()
        }
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    override fun onEvent(event: LiveEvent) {
        runOnUiThread {
            binding.statusText.text = when (event) {
                LiveEvent.Connecting -> "连接中…"
                LiveEvent.Connected -> "播放中"
                LiveEvent.Disconnected -> "已断开"
                is LiveEvent.Error -> "错误: ${event.message}"
                is LiveEvent.IceState -> "ICE: ${event.state}"
            }
        }
    }
}
