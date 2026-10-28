package com.bizpulse.app.data.model
import com.bizpulse.app.data.db.entity.ProductEntity
import com.bizpulse.app.util.rounded
data class CartItem(val product: ProductEntity, val quantity: Int = 1)
object CartLogic {
    fun subtotal(cart: List<CartItem>) = rounded(cart.sumOf { it.product.price * it.quantity })
    fun vat(cart: List<CartItem>) = rounded(subtotal(cart) * 0.12)
    fun total(cart: List<CartItem>) = rounded(subtotal(cart) + vat(cart))
    fun cost(cart: List<CartItem>) = rounded(cart.sumOf { it.product.cost * it.quantity })
}
