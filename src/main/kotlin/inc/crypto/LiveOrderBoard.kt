package inc.crypto

class LiveOrderBoard {
    private val ordersBook = OrdersBook()

    fun register(order: Order) = ordersBook.registerOrder(order)

    fun cancel(order: Order) = ordersBook.cancelOrder(order)

    fun sellSummary() = aggregateOrders<Order.Sell>().sortedBy { it.money }.take(10)

    fun buySummary() = aggregateOrders<Order.Buy>().sortedByDescending { it.money }.take(10)

    private fun Order.toAggregatedOrder() =
        when (this) {
            is Order.Buy -> AggregatedOrder.BuyOrders(coinType, orderQuantity, pricePerCoin)
            is Order.Sell -> AggregatedOrder.SellOrders(coinType, orderQuantity, pricePerCoin)
        }

    private inline fun <reified T : Order> aggregateOrders() =
        ordersBook.orders()
            .filterIsInstance<T>()
            .map { it.toAggregatedOrder() }
            .groupBy { it.money }
            .map { (_, orders) -> orders.reduce { acc, aggregatedOrder -> acc + aggregatedOrder } }

}