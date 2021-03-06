package com.yosep.order.data.repository

import com.yosep.order.data.entity.Order
import com.yosep.order.data.entity.OrderProduct
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface OrderRepository: R2dbcRepository<Order, String> {
    @Query("SELECT * FROM yos_order WHERE user_id = :userId")
    fun findOrdersBySenderId(userId: String): Flux<Order>

    @Query("SELECT * FROM yos_product_in_order WHERE order_id = :orderId")
    fun findProductsByOrderId(orderId: String): Flux<OrderProduct>
}