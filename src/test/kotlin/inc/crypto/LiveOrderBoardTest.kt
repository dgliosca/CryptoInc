package inc.crypto

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import inc.crypto.CoinType.Bitcoin
import inc.crypto.CoinType.Ethereum
import inc.crypto.Currency.Companion.GBP
import inc.crypto.AggregatedOrder.BuyOrders
import inc.crypto.AggregatedOrder.SellOrders
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
    fun `summary order by ascending price for sell orders`() {
        val board = LiveOrderBoard()
        board.register(Order.Sell(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0"))))
        board.register(Order.Sell(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0"))))

        assertThat(
            board.sellSummary(), equalTo(
                listOf(
                    SellOrders(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0"))),
                    SellOrders(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0")))
                ),
            )

        )
    }

    @Test
    fun `sell orders sorted in descending order`() {
        val board = LiveOrderBoard()
        board.register(Order.Sell(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0"))))
        board.register(Order.Sell(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0"))))

        assertThat(
            board.sellSummary(), equalTo(
                listOf(
                    SellOrders(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0"))),
                    SellOrders(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0"))),
                ),
            )
        )
    }

    @Test
    fun `buy orders sorted in ascending order`() {
        val board = LiveOrderBoard()
        board.register(Order.Buy(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0"))))
        board.register(Order.Buy(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0"))))

        assertThat(
            board.buySummary(), equalTo(
                listOf(
                    BuyOrders(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0"))),
                    BuyOrders(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0")))
                )
            )
        )
    }

    @Test
    fun `aggregate sell orders with the same price`() {
        val board = LiveOrderBoard()
        board.register(Order.Sell(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("13.6"))))
        board.register(Order.Sell(2, Ethereum, Quantity("50.5"), Money(GBP, CurrencyAmount("14"))))
        board.register(Order.Sell(3, Ethereum, Quantity("441.8"), Money(GBP, CurrencyAmount("13.9"))))
        board.register(Order.Sell(4, Ethereum, Quantity("3.5"), Money(GBP, CurrencyAmount("13.6"))))

        assertThat(
            board.sellSummary(), equalTo(
                listOf(
                    SellOrders(Ethereum, Quantity("353.6"), Money(GBP, CurrencyAmount("13.6"))),
                    SellOrders(Ethereum, Quantity("441.8"), Money(GBP, CurrencyAmount("13.9"))),
                    SellOrders(Ethereum, Quantity("50.5"), Money(GBP, CurrencyAmount("14")))
                )
            )
        )
    }

    @Test
    fun `aggregate buy orders with the same price`() {
        val board = LiveOrderBoard()
        board.register(Order.Buy(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("13.6"))))
        board.register(Order.Buy(2, Ethereum, Quantity("50.5"), Money(GBP, CurrencyAmount("14"))))
        board.register(Order.Buy(3, Ethereum, Quantity("441.8"), Money(GBP, CurrencyAmount("13.9"))))
        board.register(Order.Buy(4, Ethereum, Quantity("3.5"), Money(GBP, CurrencyAmount("13.6"))))

        assertThat(
            board.buySummary(), equalTo(
                listOf(
                    BuyOrders(Ethereum, Quantity("50.5"), Money(GBP, CurrencyAmount("14"))),
                    BuyOrders(Ethereum, Quantity("441.8"), Money(GBP, CurrencyAmount("13.9"))),
                    BuyOrders(Ethereum, Quantity("353.6"), Money(GBP, CurrencyAmount("13.6")))
                )
            )
        )
    }
}

