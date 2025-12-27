package com.mp.pentoloy

// Ini adalah "Gudang Data" sementara agar bisa diakses dari semua Activity
object CartManager {
    val items = mutableListOf<CartItem>()

    fun getTotalPrice(): Int {
        return items.sumOf { it.menu.price * it.qty }
    }

    fun clearCart() {
        items.clear()
    }
}