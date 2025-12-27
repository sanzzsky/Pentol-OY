package com.mp.pentoloy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class QrisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qris)

        val total = CartManager.getTotalPrice()
        val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        findViewById<TextView>(R.id.tvQrisTotal).text = formatRp.format(total)

        // Simulasi tombol sukses (nanti ini diganti logic API)
        findViewById<Button>(R.id.btnSimulateSuccess).setOnClickListener {
            Toast.makeText(this, "Pembayaran Diterima!", Toast.LENGTH_SHORT).show()

            CartManager.clearCart()

            // Kembali ke Menu Utama (Reset)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}