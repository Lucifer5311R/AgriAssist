package com.example.agriassist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Toast.makeText(this, "MainActivity: onCreate called", Toast.LENGTH_SHORT).show()

        val learnMoreButton = findViewById<Button?>(R.id.learn_more)
        val bottomNav = findViewById<BottomNavigationView?>(R.id.bottom_navigation)


        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_diary -> {
                    val intent = Intent(this, DiaryActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_home ->{
                    true
                }
               else -> false


            }

        }

        learnMoreButton?.setOnClickListener {
            val intent = Intent(this, Learn_More::class.java)
            startActivity(intent)
        }

        findViewById<android.view.View?>(R.id.main)?.let { mainView ->
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(this, "MainActivity: onStart called", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(this, "MainActivity: onResume called", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        Toast.makeText(this, "MainActivity: onPause called", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        Toast.makeText(this, "MainActivity: onStop called", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "MainActivity: onDestroy called", Toast.LENGTH_SHORT).show()
    }

    override fun onRestart() {
        super.onRestart()
        Toast.makeText(this, "MainActivity: onRestart called", Toast.LENGTH_SHORT).show()
    }
}