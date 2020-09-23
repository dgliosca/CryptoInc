package inc.crypto

import java.math.BigDecimal

sealed class Order {
    data class Buy(
        val userId: Int,
        val coinType: String,
        val orderQuantity: Int,
        val pricePerCoin: BigDecimal
    ) : Order()

    data class Sell(
        val userId: Int,
        val coinType: String,
        val orderQuantity: Int,
        val pricePerCoin: BigDecimal
    ) : Order()
}