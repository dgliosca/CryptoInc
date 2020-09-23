package inc.crypto

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class LiveOrderBoardTest {

    @Test
    fun `can register an order`() {
        val board = LiveOrderBoard()
        val order = Order.Sell(1, "Bitcoin", 1, 10.20.toBigDecimal())

        assertThat(board.register(order), equalTo(true))
    }

    @Test
    fun `cancel an order`() {
        val board = LiveOrderBoard()
        val order = Order.Sell(1, "Bitcoin", 1, 10.20.toBigDecimal())
        board.register(order)

        assertThat(board.cancel(order), equalTo(true))
    }
}

class LiveOrderBoard {
    private val ordersBook = OrdersBook()

    fun register(order: Order): Boolean {
        return ordersBook.registerOrder(order)
    }

    fun cancel(order: Order) : Boolean {
        return ordersBook.cancelOrder(order)
    }

}
