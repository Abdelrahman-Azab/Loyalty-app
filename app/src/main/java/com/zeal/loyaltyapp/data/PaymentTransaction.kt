package com.zeal.loyaltyapp.data

data class PaymentTransaction(
    val id: String,
    val time: String,
    val cardNumber: String,
    var totalAmount: String,
    var discountedAmount: String = "0.0"
)
