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

}

class LiveOrderBoard {
    private val ordersBook = OrdersBook()

    fun register(order: Order): Boolean {
        return ordersBook.registerOrder(order)
    }

    fun cancel(order: Order): Boolean {
        return ordersBook.cancelOrder(order)
    }

    fun summary(): List<AggregatedOrder> {
        return ordersBook.orders().map { order ->
            when (order) {
                is Order.Buy -> BuyOrders(
                    order.coinType,
                    order.orderQuantity,
                    order.pricePerCoin
                )
                is Order.Sell -> SellOrders(
                    order.coinType,
                    order.orderQuantity,
                    order.pricePerCoin
                )
            }
        }
    }

    sealed class AggregatedOrder {
        data class BuyOrders(val coinType: CoinType, val quantity: Quantity, val money: Money) :
            AggregatedOrder()

        data class SellOrders(val coinType: CoinType, val quantity: Quantity, val money: Money) :
            AggregatedOrder()
    }
}
