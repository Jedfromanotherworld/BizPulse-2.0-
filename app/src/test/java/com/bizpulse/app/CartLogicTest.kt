package com.bizpulse.app
import com.bizpulse.app.data.model.*
import com.bizpulse.app.data.db.entity.ProductEntity
import org.junit.Assert.*
import org.junit.Test
class CartLogicTest {
    private fun product(price:Double,cost:Double=20.0)=ProductEntity(1,"Cola","123",price,cost,10,2,1)
    @Test fun subtotalVatAndTotal() {val cart=listOf(CartItem(product(89.0),2));assertEquals(178.0,CartLogic.subtotal(cart),0.001);assertEquals(21.36,CartLogic.vat(cart),0.001);assertEquals(199.36,CartLogic.total(cart),0.001)}
    @Test fun emptyCart() {assertEquals(0.0,CartLogic.total(emptyList()),0.001)}
    @Test fun decimalPricesRoundOnceAtCheckout() {val cart=listOf(CartItem(product(0.10),3),CartItem(product(0.20),1));assertEquals(0.50,CartLogic.subtotal(cart),0.001);assertEquals(0.06,CartLogic.vat(cart),0.001);assertEquals(0.56,CartLogic.total(cart),0.001)}
}
