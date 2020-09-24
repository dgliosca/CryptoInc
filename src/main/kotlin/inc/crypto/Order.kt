package inc.crypto

import java.math.BigDecimal

sealed class Order {
    data class Buy(
        val userId: Int,
        val coinType: CoinType,
        val orderQuantity: Quantity,
        val pricePerCoin: Money
    ) : Order()

    data class Sell(
        val userId: Int,
        val coinType: CoinType,
        val orderQuantity: Quantity,
        val pricePerCoin: Money
    ) : Order()
}

data class Money(val currency: Currency, val amount: CurrencyAmount) : Comparable<Money> {
    override fun compareTo(other: Money) = amount.amount.compareTo(other.amount.amount)
}

data class Currency(val iso4217Code: String) {
    companion object {
        val GBP = Currency("GBP")
    }
}

data class CurrencyAmount(val amount: BigDecimal) {
    constructor(amount: String) : this(amount.toBigDecimal())
}

data class Quantity(val quantity: BigDecimal) {
    constructor(quantity: String) : this(quantity.toBigDecimal())
}