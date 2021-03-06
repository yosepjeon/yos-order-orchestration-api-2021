package com.yosep.order.order.data.repository.legacy

import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.common.reactive.Transaction
import com.yosep.order.data.dto.OrderDtoForCreationLegacy
import com.yosep.order.data.entity.OrderLegacy
import com.yosep.order.data.repository.OrderLegacyRepository
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class OrderLegacyRepositoryCreationTest @Autowired constructor(
    private val orderLegacyRepository: OrderLegacyRepository,
    private val randomIdGenerator: RandomIdGenerator
) {
    val log = Slf4JLoggerFactory.getInstance(OrderLegacyRepositoryCreationTest::class.java)
    var orderId = ""

    @BeforeEach
    fun createOrder() {
        val order = OrderLegacy(
            "test0",
            "product-01",
            "sender1",
            "요깨비",
            "이재훈",
            "123123123",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "READY",
            LocalDateTime.now(),
            null,
            null
        )

        order.setAsNew()

        orderLegacyRepository.save(order)
            .map {
                orderId = it.orderId
            }
            .block()

        log.info("===================================================== START =====================================================")
    }

    @AfterEach
    fun deleteOrder() {
        orderLegacyRepository
            .deleteById(orderId)
            .block()

        log.info("===================================================== END =====================================================")
    }

    @Test
    @DisplayName("[OrderRepository] 주문 생성 성공 테스트")
    fun createOrderSuccessTest() {
        val order = OrderLegacy(
            "create-order-test",
            "product-01",
            "sender1",
            "요깨비",
            "이재훈",
            "123123123",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "READY",
            LocalDateTime.now(),
            null,
            null
        )

        order.setAsNew()

        log.info("[OrderRepository] 주문 생성 성공 테스트")
//        1. Spring Test에서는 R2dbc관련 @Transactional을 아직 지원하지 않는다.
//        StepVerifier.create(orderRepository.save(order))
//            .expectSubscription()
//            .consumeNextWith {
//                Assertions.assertEquals("create-order-test", it.orderId)
//                log.info("$it")
//                log.info("END")
//            }
//            .verifyComplete()

//        2. map은 동기 방식으로 성능에 문제를 초래 flatMap으로 바꿀 예정
//        orderRepository.save(order)
//            .map(Order::orderId)
//            .flatMap(orderRepository::findById)
//            .`as`(Transaction::withRollback)
//            .`as`(StepVerifier::create)
//            .assertNext { order ->
//                Assertions.assertEquals(true, order!!.orderId == "create-order-test")
//            }
//            .verifyComplete()

        orderLegacyRepository.save(order)
            .flatMap {
                orderLegacyRepository.findById(it.orderId)
            }
            .`as`(Transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext { order ->
                Assertions.assertEquals(true, order!!.orderId == "create-order-test")
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("[OrderRepository] 주문 생성 실패 테스트")
    fun createOrderFailTest() {
        val order = OrderLegacy(
            "create-order-test",
            "product-01",
            "sender1",
            "요깨비",
            "이재훈",
            "123123123",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "READY",
            LocalDateTime.now(),
            null,
            null
        )

        order.setAsNew()

        log.info("[OrderRepository] 주문 생성 실패 테스트")

        orderLegacyRepository.save(order)
            .flatMap {
                orderLegacyRepository.findById(it.orderId)
            }
            .`as`(Transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext { order ->
                Assertions.assertEquals(false, order!!.orderId == "create-order-test1")
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("[OrderRepository] 주문 생성 시 임의로 생성된 주문 아이디가 중복될 경우 해결 테스트")
    fun 주문_생성시_임의로_생성된_주문_아이디가_중복될_경우_해결_테스트() {
        log.info("[OrderRepository] 주문 생성 시 임의로 생성된 주문 아이디가 중복될 경우 해결 테스트")

        val orderDtoForCreation = OrderDtoForCreationLegacy(
            "test",
            "product-01",
            "sender1",
            "요깨비",
            "이재훈",
            "123123123",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "READY",
        )

        var num = 0
        randomIdGenerator.generate()
            .flatMap {
                log.info(it)
                val order = OrderLegacy(
                    "test$num",
                    orderDtoForCreation.productId,
                    orderDtoForCreation.userId,
                    orderDtoForCreation.senderName,
                    orderDtoForCreation.receiverName,
                    orderDtoForCreation.phone,
                    orderDtoForCreation.postCode,
                    orderDtoForCreation.roadAddr,
                    orderDtoForCreation.jibunAddr,
                    orderDtoForCreation.extraAddr,
                    orderDtoForCreation.detailAddr,
                    orderDtoForCreation.orderState,
                )
                order.setAsNew()
                num++

                Mono.create<OrderLegacy> { sink ->
                    sink.success(order)
                }
            }
            .flatMap(orderLegacyRepository::save)
            .retry()
            .`as`(Transaction::withRollback)
            .subscribe()
    }
}