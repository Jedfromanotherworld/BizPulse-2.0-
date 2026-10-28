package com.bizpulse.app.util
object Validation {
    fun username(value: String) = value.matches(Regex("[A-Za-z0-9_]{3,30}"))
    fun password(value: String) = value.length >= 6
    fun price(value: Double) = value.isFinite() && value > 0 && value <= 1_000_000_000
    fun amount(value: Double) = value.isFinite() && value >= 0 && value <= 1_000_000_000
    fun count(value: Int) = value in 0..1_000_000
    fun role(value: String) = value in listOf("owner", "cashier")
    fun payment(value: String) = value in listOf("Cash", "QR")
}
