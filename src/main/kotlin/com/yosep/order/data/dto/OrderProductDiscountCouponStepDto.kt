package com.yosep.order.data.dto

import com.yosep.order.data.vo.OrderProductDiscountCouponDto
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class OrderProductDiscountCouponStepDto(
    val orderId: String,

    @field:NotEmpty
    @field:Size(min = 1)
    val orderProductDiscountCouponDtos: List<OrderProductDiscountCouponDto>,
    @field:NotEmpty
    var state: String = "READY"
)