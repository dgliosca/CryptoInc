package inc.crypto

import inc.crypto.domain.AggregatedOrder
import inc.crypto.domain.Order
import inc.crypto.domain.Order.Sell
import inc.crypto.domain.Order.Buy

class LiveOrderBoard {
    private val ordersBook = OrdersBook()

    fun place(order: Order) = ordersBook.place(order)

    fun cancel(order: Order) = ordersBook.cancelOrder(order)

    fun sellOrdersSummary() = aggregateOrders<Sell>().sortedBy { it.money }

    fun buyOrdersSummary() = aggregateOrders<Buy>().sortedByDescending { it.money }

    private fun Order.toAggregatedOrder() = AggregatedOrder(coinType, orderQuantity, pricePerCoin)

    private inline fun <reified T : Order> aggregateOrders(limit: Int = 10) =
        ordersBook.orders()
            .asSequence()
            .filterIsInstance<T>()
            .map { it.toAggregatedOrder() }
            .groupBy { it.money }
            .map { (_, orders) -> orders.reduce { acc, aggregatedOrder -> acc + aggregatedOrder } }
            .take(limit)
            .toList()

}