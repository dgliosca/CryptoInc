package inc.crypto

import inc.crypto.domain.Order

class OrdersBook {
    private val orders = mutableListOf<Order>()

    fun registerOrder(order: Order) = orders.add(order)

    fun get(order: Order) = orders.find { it == order }

    fun cancelOrder(order: Order) = orders.remove(order)

    fun orders(): List<Order> = orders
}