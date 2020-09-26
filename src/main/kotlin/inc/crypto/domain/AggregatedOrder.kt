package inc.crypto.domain

data class AggregatedOrder(val coinType: CoinType, val quantity: Quantity, val money: Money) {
    operator fun plus(other: AggregatedOrder): AggregatedOrder {
        return when {
            this.coinType != other.coinType -> {
                throw IllegalArgumentException("Cannot sum orders with different coin type: $coinType vs ${other.coinType}")
            }
            this.money != other.money -> {
                throw IllegalArgumentException("Cannot sum orders with different money type: $money vs ${other.money}")
            }
            else -> this.copy(quantity = this.quantity + other.quantity)
        }
    }
}
