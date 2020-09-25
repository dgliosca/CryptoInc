package inc.crypto

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import inc.crypto.domain.*
import inc.crypto.domain.CoinType.Bitcoin
import inc.crypto.domain.CoinType.Ethereum
import inc.crypto.domain.Currency.Companion.GBP
import org.junit.jupiter.api.Test

class LiveOrderBoardTest {

    @Test
    fun `can place an order`() {
        val board = LiveOrderBoard()
        val order = Order.Sell(1, Bitcoin, Quantity("1"), Money(GBP, CurrencyAmount("10.20")))

        assertThat(board.place(order), equalTo(true))
    }

    @Test
    fun `cancel an order`() {
        val board = LiveOrderBoard()
        val order = Order.Sell(1, Bitcoin, Quantity("1"), Money(GBP, CurrencyAmount("10.20")))
        board.place(order)

        assertThat(board.cancel(order), equalTo(true))
    }

    @Test
    fun `summary order by ascending price for sell orders`() {
        val board = LiveOrderBoard()
        board.place(Order.Sell(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0"))))
        board.place(Order.Sell(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0"))))

        assertThat(
            board.sellOrdersSummary(), equalTo(
                listOf(
                    AggregatedOrder(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0"))),
                    AggregatedOrder(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0")))
                ),
            )

        )
    }

    @Test
    fun `sell orders sorted in descending order`() {
        val board = LiveOrderBoard()
        board.place(Order.Sell(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0"))))
        board.place(Order.Sell(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0"))))

        assertThat(
            board.sellOrdersSummary(), equalTo(
                listOf(
                    AggregatedOrder(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0"))),
                    AggregatedOrder(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0"))),
                ),
            )
        )
    }

    @Test
    fun `buy orders sorted in ascending order`() {
        val board = LiveOrderBoard()
        board.place(Order.Buy(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0"))))
        board.place(Order.Buy(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0"))))

        assertThat(
            board.buyOrdersSummary(), equalTo(
                listOf(
                    AggregatedOrder(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("10.0"))),
                    AggregatedOrder(Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("5.0")))
                )
            )
        )
    }

    @Test
    fun `aggregate sell orders with the same price`() {
        val board = LiveOrderBoard()
        board.place(Order.Sell(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("13.6"))))
        board.place(Order.Sell(2, Ethereum, Quantity("50.5"), Money(GBP, CurrencyAmount("14"))))
        board.place(Order.Sell(3, Ethereum, Quantity("441.8"), Money(GBP, CurrencyAmount("13.9"))))
        board.place(Order.Sell(4, Ethereum, Quantity("3.5"), Money(GBP, CurrencyAmount("13.6"))))

        assertThat(
            board.sellOrdersSummary(), equalTo(
                listOf(
                    AggregatedOrder(Ethereum, Quantity("353.6"), Money(GBP, CurrencyAmount("13.6"))),
                    AggregatedOrder(Ethereum, Quantity("441.8"), Money(GBP, CurrencyAmount("13.9"))),
                    AggregatedOrder(Ethereum, Quantity("50.5"), Money(GBP, CurrencyAmount("14")))
                )
            )
        )
    }

    @Test
    fun `aggregate buy orders with the same price`() {
        val board = LiveOrderBoard()
        board.place(Order.Buy(1, Ethereum, Quantity("350.1"), Money(GBP, CurrencyAmount("13.6"))))
        board.place(Order.Buy(2, Ethereum, Quantity("50.5"), Money(GBP, CurrencyAmount("14"))))
        board.place(Order.Buy(3, Ethereum, Quantity("441.8"), Money(GBP, CurrencyAmount("13.9"))))
        board.place(Order.Buy(4, Ethereum, Quantity("3.5"), Money(GBP, CurrencyAmount("13.6"))))

        assertThat(
            board.buyOrdersSummary(), equalTo(
                listOf(
                    AggregatedOrder(Ethereum, Quantity("50.5"), Money(GBP, CurrencyAmount("14"))),
                    AggregatedOrder(Ethereum, Quantity("441.8"), Money(GBP, CurrencyAmount("13.9"))),
                    AggregatedOrder(Ethereum, Quantity("353.6"), Money(GBP, CurrencyAmount("13.6")))
                )
            )
        )
    }

    @Test
    fun `can show just first 10 results`() {
        val board = LiveOrderBoard()
        (1..20).forEach{ i -> board.place(Order.Sell(1 + i, Ethereum, Quantity("1"), Money(GBP, CurrencyAmount("$i"))))}

        assertThat(
            board.sellOrdersSummary(), equalTo(
                listOf(
                    AggregatedOrder(Ethereum, Quantity("1"), Money(GBP, CurrencyAmount("1"))),
                    AggregatedOrder(Ethereum, Quantity("1"), Money(GBP, CurrencyAmount("2"))),
                    AggregatedOrder(Ethereum, Quantity("1"), Money(GBP, CurrencyAmount("3"))),
                    AggregatedOrder(Ethereum, Quantity("1"), Money(GBP, CurrencyAmount("4"))),
                    AggregatedOrder(Ethereum, Quantity("1"), Money(GBP, CurrencyAmount("5"))),
                    AggregatedOrder(Ethereum, Quantity("1"), Money(GBP, CurrencyAmount("6"))),
                    AggregatedOrder(Ethereum, Quantity("1"), Money(GBP, CurrencyAmount("7"))),
                    AggregatedOrder(Ethereum, Quantity("1"), Money(GBP, CurrencyAmount("8"))),
                    AggregatedOrder(Ethereum, Quantity("1"), Money(GBP, CurrencyAmount("9"))),
                    AggregatedOrder(Ethereum, Quantity("1"), Money(GBP, CurrencyAmount("10")))
                )
            )
        )
    }
}

