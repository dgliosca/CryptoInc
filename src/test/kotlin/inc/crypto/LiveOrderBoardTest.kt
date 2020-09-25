package inc.crypto

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import inc.crypto.CoinType.Bitcoin
import inc.crypto.CoinType.Ethereum
import inc.crypto.Currency.Companion.GBP
import inc.crypto.LiveOrderBoard.AggregatedOrder.BuyOrders
import inc.crypto.LiveOrderBoard.AggregatedOrder.SellOrders
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

class LiveOrderBoard {
    private val ordersBook = OrdersBook()

    fun register(order: Order) = ordersBook.registerOrder(order)

    fun cancel(order: Order) = ordersBook.cancelOrder(order)

    fun sellSummary() = aggregateOrders<Order.Sell>().sortedBy { it.money }

    fun buySummary() = aggregateOrders<Order.Buy>().sortedByDescending { it.money }

    private fun Order.toAggregatedOrder() =
        when (this) {
            is Order.Buy -> BuyOrders(coinType, orderQuantity, pricePerCoin)
            is Order.Sell -> SellOrders(coinType, orderQuantity, pricePerCoin)
        }

    private inline fun <reified T : Order> aggregateOrders() =
        ordersBook.orders()
            .filterIsInstance<T>()
            .map { it.toAggregatedOrder() }
            .groupBy { it.money }
            .map { (_, orders) -> orders.reduce { acc, aggregatedOrder -> acc + aggregatedOrder } }

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
}
