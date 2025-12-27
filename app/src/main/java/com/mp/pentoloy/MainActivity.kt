package com.mp.pentoloy

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

// --- 1. DATA MODELS ---
data class MenuItem(
    val id: Int,
    val name: String,
    val price: Int,
    var stock: Int, // Ubah val jadi var agar stok bisa diedit
    val colorHex: String
)

data class CartItem(
    val menu: MenuItem,
    var qty: Int
)

class MainActivity : AppCompatActivity() {

    // Komponen Tampilan
    private lateinit var rvMenu: RecyclerView
    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotalPrice: TextView
    private lateinit var btnCheckout: Button
    private lateinit var btnAdmin: ImageButton

    // Adapter
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- A. INISIALISASI TAMPILAN ---
        rvMenu = findViewById(R.id.rvMenu)
        rvCart = findViewById(R.id.rvCart)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        btnCheckout = findViewById(R.id.btnCheckout)
        btnAdmin = findViewById(R.id.btnAdmin)

        // --- B. SETUP MENU (GRID LAYOUT) ---
        rvMenu.layoutManager = GridLayoutManager(this, 3)
        // DATA DIAMBIL DARI REPOSITORY
        menuAdapter = MenuAdapter(DataRepository.menuList) { item ->
            addToCart(item)
        }
        rvMenu.adapter = menuAdapter

        // --- C. SETUP KERANJANG ---
        rvCart.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(CartManager.items,
            onPlus = { item -> updateCartQty(item, 1) },
            onMinus = { item -> updateCartQty(item, -1) }
        )
        rvCart.adapter = cartAdapter

        // --- D. EVENT CLICK TOMBOL ---
        btnCheckout.setOnClickListener {
            if (CartManager.items.isEmpty()) {
                Toast.makeText(this, "Keranjang masih kosong!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, PaymentActivity::class.java)
                startActivity(intent)
            }
        }

        // TOMBOL ADMIN: Pindah ke LoginActivity
        btnAdmin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        updateTotal()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data saat kembali dari Admin (siapa tahu stok berubah)
        menuAdapter.notifyDataSetChanged()
        cartAdapter.notifyDataSetChanged()
        updateTotal()
    }

    // --- FUNGSI LOGIKA ---
    private fun addToCart(item: MenuItem) {
        if (item.stock <= 0) {
            Toast.makeText(this, "Maaf, Stok Habis!", Toast.LENGTH_SHORT).show()
            return
        }

        val existingItem = CartManager.items.find { it.menu.id == item.id }

        if (existingItem != null) {
            if (existingItem.qty < item.stock) {
                existingItem.qty += 1
            } else {
                Toast.makeText(this, "Stok maksimum tercapai!", Toast.LENGTH_SHORT).show()
            }
        } else {
            CartManager.items.add(CartItem(item, 1))
        }

        cartAdapter.notifyDataSetChanged()
        updateTotal()
    }

    private fun updateCartQty(item: CartItem, delta: Int) {
        val newQty = item.qty + delta

        if (delta > 0 && newQty > item.menu.stock) {
            Toast.makeText(this, "Stok tidak cukup!", Toast.LENGTH_SHORT).show()
            return
        }

        if (newQty <= 0) {
            CartManager.items.remove(item)
        } else {
            item.qty = newQty
        }

        cartAdapter.notifyDataSetChanged()
        updateTotal()
    }

    private fun updateTotal() {
        val total = CartManager.getTotalPrice()
        val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        tvTotalPrice.text = formatRp.format(total)

        btnCheckout.isEnabled = CartManager.items.isNotEmpty()
        btnCheckout.alpha = if (CartManager.items.isNotEmpty()) 1.0f else 0.5f
    }

    // --- CLASS ADAPTER (SAMA SEPERTI SEBELUMNYA) ---
    inner class MenuAdapter(
        private val items: List<MenuItem>,
        private val onClick: (MenuItem) -> Unit
    ) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

        inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvMenuName)
            val tvPrice: TextView = view.findViewById(R.id.tvMenuPrice)
            val tvStock: TextView = view.findViewById(R.id.tvStock)
            val imgPlaceholder: FrameLayout = view.findViewById(R.id.imgMenuPlaceholder)
            val btnAdd: ImageButton = view.findViewById(R.id.btnAdd)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
            return MenuViewHolder(view)
        }

        override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
            val item = items[position]
            val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

            holder.tvName.text = item.name
            holder.tvPrice.text = formatRp.format(item.price)

            try { holder.imgPlaceholder.setBackgroundColor(Color.parseColor(item.colorHex)) } catch (e: Exception) { }

            if (item.stock <= 0) {
                holder.itemView.alpha = 0.5f
                holder.btnAdd.isEnabled = false
                holder.tvStock.text = "SOLD OUT"
                holder.tvStock.setTextColor(Color.RED)
                holder.tvStock.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                holder.itemView.alpha = 1.0f
                holder.btnAdd.isEnabled = true
                holder.tvStock.text = "Stok: ${item.stock}"
                holder.tvStock.setTextColor(Color.GRAY)
            }

            holder.itemView.setOnClickListener { onClick(item) }
            holder.btnAdd.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = items.size
    }

    inner class CartAdapter(
        private val items: List<CartItem>,
        private val onPlus: (CartItem) -> Unit,
        private val onMinus: (CartItem) -> Unit
    ) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

        inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvCartName)
            val tvPrice: TextView = view.findViewById(R.id.tvCartPrice)
            val tvQty: TextView = view.findViewById(R.id.tvQty)
            val btnPlus: Button = view.findViewById(R.id.btnPlus)
            val btnMinus: Button = view.findViewById(R.id.btnMinus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
            return CartViewHolder(view)
        }

        override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
            val item = items[position]
            val formatRp = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            holder.tvName.text = item.menu.name
            holder.tvPrice.text = formatRp.format(item.menu.price * item.qty)
            holder.tvQty.text = item.qty.toString()
            holder.btnPlus.setOnClickListener { onPlus(item) }
            holder.btnMinus.setOnClickListener { onMinus(item) }
        }
        override fun getItemCount() = items.size
    }
}