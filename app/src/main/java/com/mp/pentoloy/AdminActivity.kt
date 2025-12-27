package com.mp.pentoloy

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            finish() // Kembali ke Login -> Main
        }

        // Nanti kita tambahkan logika klik Card di sini
    }
}