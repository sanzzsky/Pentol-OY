package com.mp.pentoloy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val total = CartManager.getTotalPrice()
        val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

        findViewById<TextView>(R.id.tvPaymentTotal).text = formatRp.format(total)

        // PILIHAN TUNAI
        findViewById<LinearLayout>(R.id.btnPayCash).setOnClickListener {
            Toast.makeText(this, "Mencetak Struk...", Toast.LENGTH_SHORT).show()
            finishPayment()
        }

        // PILIHAN QRIS
        findViewById<LinearLayout>(R.id.btnPayQris).setOnClickListener {
            // Pindah ke Halaman QRIS
            val intent = Intent(this, QrisActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish() // Kembali ke Menu
        }
    }

    private fun finishPayment() {
        CartManager.clearCart()
        // Nanti di sini kita arahkan ke halaman Sukses/Struk
        // Untuk sekarang kita kembali ke menu utama dulu
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}