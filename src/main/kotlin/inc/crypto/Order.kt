package inc.crypto

import java.math.BigDecimal

sealed class Order {
    data class Buy(
        val userId: Int,
        val coinType: String,
        val orderQuantity: Int,
        val pricePerCoin: Money
    ) : Order()

    data class Sell(
        val userId: Int,
        val coinType: String,
        val orderQuantity: Int,
        val pricePerCoin: Money
    ) : Order()
}

data class Money(val currency: Currency, val amount: BigDecimal)
data class Currency(val iso4217Code: String) {
    companion object {
        val GBP = Currency("GBP")
    }
}