package com.yosep.order.saga.http.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.vo.OrderProductDtoForCreation
import org.springframework.web.reactive.function.BodyInserters

import reactor.core.publisher.Mono

import com.yosep.order.saga.http.WorkflowStepStatus

import org.springframework.web.reactive.function.client.WebClient

import com.yosep.order.saga.http.WorkflowStep
import org.springframework.web.reactive.function.BodyInserter


class ProductStep(
    @JsonIgnore
    private val webClient: WebClient? = null,
    private val orderProductsDtoForCreation: List<OrderProductDtoForCreation> = emptyList(),
    stepType: String = "PRODUCT"
) : WorkflowStep<OrderDtoForCreation, CreatedOrderDto>(
    stepType
) {
    override fun process(t: OrderDtoForCreation): Mono<CreatedOrderDto> {
        webClient!!
            .post()
            .uri("/test")
            .body(BodyInserters.fromValue(""))
            .retrieve()
            .bodyToMono(String::class.java)


        return Mono.empty()
    }

    override fun revert(): Mono<CreatedOrderDto> {
        return Mono.empty()
    }

    private fun checkProductPrices() {

    }
}

//internal class InventoryStep(private val webClient: WebClient, requestDTO: InventoryRequestDTO) : WorkflowStep {
//    private val requestDTO: InventoryRequestDTO
//    override var status = WorkflowStepStatus.PENDING
//        private set
//
//    override fun process(): Mono<Boolean?> {
//        return webClient
//            .post()
//            .uri("/inventory/deduct")
//            .body(BodyInserters.fromValue<Any>(requestDTO))
//            .retrieve()
//            .bodyToMono(InventoryResponseDTO::class.java)
//            .map { r -> r.getStatus().equals(InventoryStatus.AVAILABLE) }
//            .doOnNext { b -> status = if (b) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED }
//    }
//
//    override fun revert(): Mono<Boolean?> {
//        return webClient
//            .post()
//            .uri("/inventory/add")
//            .body(BodyInserters.fromValue<Any>(requestDTO))
//            .retrieve()
//            .bodyToMono(Void::class.java)
//            .map { r: Void? -> true }
//            .onErrorReturn(false)
//    }
//
//    init {
//        this.requestDTO = requestDTO
//    }
//}