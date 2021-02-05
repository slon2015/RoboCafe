package com.robocafe.all.cucumber

fun readMoneyAmount(text: String, currencyPostfix: String = "Руб."): Double {
    val numericText = text.substring(0, text.length - currencyPostfix.length).trim()
    return numericText.toDouble()
}