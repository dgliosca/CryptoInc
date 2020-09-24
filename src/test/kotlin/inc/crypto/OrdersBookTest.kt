package inc.crypto

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import inc.crypto.CoinType.*
import inc.crypto.Currency.Companion.GBP
import org.junit.jupiter.api.Test

class OrdersBookTest {

    @Test
    fun `can register a buy order`() {
        val orderBook = OrdersBook()

        assertThat(
            orderBook.registerOrder(Order.Buy(1, Bitcoin, Quantity("1"), Money(GBP, CurrencyAmount("10.20")))),
            equalTo(true)
        )
    }

    @Test
    fun `can register a sell order`() {
        val orderBook = OrdersBook()
        assertThat(
            orderBook.registerOrder(Order.Sell(1, Bitcoin, Quantity("1"), Money(GBP, CurrencyAmount("10.20")))),
            equalTo(true)
        )
    }

    @Test
    fun `retrieve an order`() {
        val orderBook = OrdersBook()
        val order = Order.Sell(1, Bitcoin, Quantity("1"), Money(GBP, CurrencyAmount("10.20")))
        orderBook.registerOrder(order)

        assertThat(orderBook.get(order), equalTo(order))
    }

    @Test
    fun `cancel an order`() {
        val orderBook = OrdersBook()
        val order = Order.Sell(1, Bitcoin, Quantity("1"), Money(GBP, CurrencyAmount("10.20")))
        orderBook.registerOrder(order)

        assertThat(orderBook.get(order), equalTo(order))
        assertThat(orderBook.cancelOrder(order), equalTo(true))
        assertThat(orderBook.get(order), absent())
    }

    @Test
    fun `cancel an order that does not exist`() {
        val orderBook = OrdersBook()
        val order = Order.Sell(1, Bitcoin, Quantity("1"), Money(GBP, CurrencyAmount("10.20")))

        assertThat(orderBook.cancelOrder(order), equalTo(false))
    }

}

