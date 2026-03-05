package com.example.agriassist

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class Learn_More : AppCompatActivity() {

    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_more)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar_learn_more)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        initializePlayer()
    }

    private fun initializePlayer() {
        val playerView = findViewById<PlayerView>(R.id.player_view)
        
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        // Sample video - replace with your actual farming tutorial URL
        val videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" 
        val mediaItem = MediaItem.fromUri(videoUrl)
        
        player?.setMediaItem(mediaItem)
        player?.prepare()
    }

    override fun onStop() {
        super.onStop()
        player?.pause() // Pause video when app goes to background
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release() // Crucial to avoid memory leaks
        player = null
    }
}
