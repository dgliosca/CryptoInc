package inc.crypto

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import inc.crypto.CoinType.Bitcoin
import inc.crypto.CoinType.Ethereum
import inc.crypto.Currency.Companion.GBP
import inc.crypto.LiveOrderBoard.AggregatedOrder.BuyOrders
import inc.crypto.LiveOrderBoard.AggregatedOrder.SellOrders
import inc.crypto.testing.hasTheSameElementsAs
import org.junit.jupiter.api.Test

class LiveOrderBoardTest {

    @Test
    fun `can register an order`() {
        val board = LiveOrderBoard()
        val order = Order.Sell(1, Bitcoin, Quantity("1"), Money(GBP, CurrencyAmount("10.20")))

        assertThat(board.register(order), equalTo(true))
    }

    @Test
    fun `cancel an order`() {
        val board = LiveOrderBoard()
        val order = Order.Sell(1, Bitcoin, Quantity("1"), Money(GBP, CurrencyAmount("10.20")))
        board.register(order)

        assertThat(board.cancel(order), equalTo(true))
    }

    @Test
    fun `can get a summary order`() {
        val board = LiveOrderBoard()
        board.register(Order.Sell(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("13.6"))))
        board.register(Order.Buy(1, Bitcoin, Quantity("350.1"), Money(GBP, CurrencyAmount("13.6"))))

        assertThat(
            board.summary(), hasTheSameElementsAs(
                listOf(
                    SellOrders(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("13.6"))),
                    BuyOrders(Bitcoin, Quantity("350.1"), Money(GBP, CurrencyAmount("13.6"))),
                )
            )
        )
    }

    @Test
    fun `summary order by descending price for sell orders`() {
        val board = LiveOrderBoard()
        board.register(Order.Sell(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0"))))
        board.register(Order.Sell(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0"))))

        assertThat(
            board.summary(), equalTo(
                listOf(
                    SellOrders(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0"))),
                    SellOrders(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0")))
                ),
            )

        )
    }

}

class LiveOrderBoard {
    private val ordersBook = OrdersBook()

    fun register(order: Order) = ordersBook.registerOrder(order)

    fun cancel(order: Order) = ordersBook.cancelOrder(order)

    fun summary() =
        ordersBook.orders().map { it.aggregatedOrder() }.sortedByDescending { it.money }

    private fun Order.aggregatedOrder() =
        when (this) {
            is Order.Buy -> BuyOrders(coinType, orderQuantity, pricePerCoin)
            is Order.Sell -> SellOrders(coinType, orderQuantity, pricePerCoin)
        }

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
    }
}
