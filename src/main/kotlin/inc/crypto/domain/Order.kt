package inc.crypto.domain

import java.math.BigDecimal

sealed class Order(
    open val userId: Int,
    open val coinType: CoinType,
    open val orderQuantity: Quantity,
    open val pricePerCoin: Money
) {
    data class Buy(
        override val userId: Int,
        override val coinType: CoinType,
        override val orderQuantity: Quantity,
        override val pricePerCoin: Money
    ) : Order(userId, coinType, orderQuantity, pricePerCoin)

    data class Sell(
        override val userId: Int,
        override val coinType: CoinType,
        override val orderQuantity: Quantity,
        override val pricePerCoin: Money
    ) : Order(userId, coinType, orderQuantity, pricePerCoin)
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

    operator fun plus(other: Quantity) = Quantity(this.quantity + other.quantity)
}