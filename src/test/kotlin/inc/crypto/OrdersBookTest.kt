package inc.crypto

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import inc.crypto.domain.CoinType.Bitcoin
import inc.crypto.domain.Currency.Companion.GBP
import inc.crypto.domain.CurrencyAmount
import inc.crypto.domain.Money
import inc.crypto.domain.Order
import inc.crypto.domain.Quantity
import org.junit.jupiter.api.Test

class OrdersBookTest {

    @Test
    fun `can place a buy order`() {
        val orderBook = OrdersBook()

        assertThat(
            orderBook.place(Order.Buy(1, Bitcoin, Quantity("1"), Money(GBP, CurrencyAmount("10.20")))),
            equalTo(true)
        )
    }

    @Test
    fun `can place a sell order`() {
        val orderBook = OrdersBook()
        assertThat(
            orderBook.place(Order.Sell(1, Bitcoin, Quantity("1"), Money(GBP, CurrencyAmount("10.20")))),
            equalTo(true)
        )
    }

    @Test
    fun `retrieve an order`() {
        val orderBook = OrdersBook()
        val order = Order.Sell(1, Bitcoin, Quantity("1"), Money(GBP, CurrencyAmount("10.20")))
        orderBook.place(order)

        assertThat(orderBook.get(order), equalTo(order))
    }

    @Test
    fun `cancel an order`() {
        val orderBook = OrdersBook()
        val order = Order.Sell(1, Bitcoin, Quantity("1"), Money(GBP, CurrencyAmount("10.20")))
        orderBook.place(order)

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

