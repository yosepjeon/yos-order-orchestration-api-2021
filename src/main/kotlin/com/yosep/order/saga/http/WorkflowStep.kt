package com.yosep.order.saga.http

import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


//interface WorkflowStep<T,R> {
//    fun getStepType(): String
//
//    fun process(t: T): Mono<R>
//    fun revert(t: T): Mono<R>
//}

open class WorkflowStep<T,R>(
    private val stepType: String = "NONE",
    protected var state: String = "READY"
) {
    open fun getStepType(): String = this.stepType

    open fun process(t: T): Mono<R> = Mono.empty()
    open fun revert(): Mono<R> = Mono.empty()
}