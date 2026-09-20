package com.gzpu.livesdk.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gzpu.livesdk.LiveEvent
import com.gzpu.livesdk.LiveEventListener
import com.gzpu.livesdk.LiveSdk
import com.gzpu.livesdk.LiveSessionConfig
import com.gzpu.livesdk.demo.databinding.ActivityPublishBinding

class PublishActivity : AppCompatActivity(), LiveEventListener {
    private lateinit var binding: ActivityPublishBinding
    private val publisher = LiveSdk.createPublisher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPublishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val whipUrl = intent.getStringExtra(MainActivity.EXTRA_WHIP_URL).orEmpty()
        val token = intent.getStringExtra(MainActivity.EXTRA_TOKEN)

        publisher.setEventListener(this)
        publisher.attachPreview(binding.preview)
        publisher.start(
            this,
            LiveSessionConfig(
                endpoint = whipUrl,
                token = token?.ifBlank { null },
            ),
        )

        binding.btnStop.setOnClickListener {
            publisher.stop()
            finish()
        }
    }

    override fun onDestroy() {
        publisher.release()
        super.onDestroy()
    }

    override fun onEvent(event: LiveEvent) {
        runOnUiThread {
            binding.statusText.text = when (event) {
                LiveEvent.Connecting -> "连接中…"
                LiveEvent.Connected -> "推流中"
                LiveEvent.Disconnected -> "已断开"
                is LiveEvent.Error -> "错误: ${event.message}"
                is LiveEvent.IceState -> "ICE: ${event.state}"
            }
        }
    }
}
