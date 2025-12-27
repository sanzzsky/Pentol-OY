package com.mp.pentoloy

object DataRepository {
    // Kita ubah list jadi Mutable agar bisa diedit (tambah/kurang stok) oleh Admin
    val menuList = mutableListOf(
        MenuItem(1, "Pentol Kecil", 1000, 50, "#FFEDD5"),
        MenuItem(2, "Pentol Besar (Urat)", 3000, 20, "#FED7AA"),
        MenuItem(3, "Pentol Ayam", 1000, 35, "#FEF3C7"),
        MenuItem(4, "Pentol Mercon", 2000, 0, "#FEE2E2"),
        MenuItem(5, "Pentol Puyuh", 2500, 15, "#FDE68A"),
        MenuItem(6, "Tahu Bakso", 1500, 30, "#D1FAE5"),
        MenuItem(7, "Es Teh Manis", 3000, 100, "#FDBA74"),
        MenuItem(8, "Air Mineral", 3000, 50, "#BFDBFE")
    )
}