package inc.crypto.domain

sealed class AggregatedOrder(open val coinType: CoinType, open val quantity: Quantity, open val money: Money) {
    data class BuyOrders(
        override val coinType: CoinType,
        override val quantity: Quantity,
        override val money: Money
    ) : AggregatedOrder(
        coinType,
        quantity,
        money
    )

    data class SellOrders(
        override val coinType: CoinType,
        override val quantity: Quantity,
        override val money: Money
    ) : AggregatedOrder(
        coinType,
        quantity,
        money,
    )

    operator fun plus(other: AggregatedOrder): AggregatedOrder {
        return when {
            this.coinType != other.coinType -> {
                throw IllegalArgumentException("Cannot sum orders with different coin type: $coinType vs ${other.coinType}")
            }
            this.money != other.money -> {
                throw IllegalArgumentException("Cannot sum orders with different money type: $money vs ${other.money}")
            }
            else -> when (this) {
                is BuyOrders -> this.copy(quantity = this.quantity + other.quantity)
                is SellOrders -> this.copy(quantity = this.quantity + other.quantity)
            }
        }
    }
}