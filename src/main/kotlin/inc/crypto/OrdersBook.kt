package inc.crypto

class OrdersBook {
    private val orders = mutableListOf<Order>()

    fun registerOrder(order: Order): Boolean {
        return orders.add(order)
    }

    fun get(order: Order): Order? {
        return orders.find { it == order }
    }

    fun cancelOrder(order: Order): Boolean {
        return orders.remove(order)
    }

    fun orders(): List<Order> = orders
}