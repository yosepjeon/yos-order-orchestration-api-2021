package com.yosep.order.mq.producer

import com.fasterxml.jackson.databind.ObjectMapper
import reactor.core.publisher.Flux

import java.text.SimpleDateFormat

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderRecord
import reactor.kafka.sender.SenderResult
import java.util.*

@Component
class CommandToTestKafkaProducer @Autowired constructor(
    private val objectMapper: ObjectMapper
) {
    private val log: Logger = LoggerFactory.getLogger(CommandToTestKafkaProducer::class.java.getName())

    private val BOOTSTRAP_SERVERS = "localhost:9092"
    private val TOPIC = "demo-topic"

    private var sender: KafkaSender<String, String>? = null
//    private var reactiveKafkaProducerTemplate:ReactiveKafkaProducerTemplate<String, String>
    private var dateFormat: SimpleDateFormat? = null

    init {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        props[ProducerConfig.CLIENT_ID_CONFIG] = "sample-producer"
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] =
            org.apache.kafka.common.serialization.IntegerSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] =
            org.apache.kafka.common.serialization.StringSerializer::class.java
        val senderOptions = SenderOptions.create<String, String>(props)
        sender = KafkaSender.create(senderOptions)
        dateFormat = SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy")
    }

    @Throws(InterruptedException::class)
    fun sendMessages(topic: String?, message: String): Flux<SenderResult<Int>> {

        return sender!!.send(Flux.range(1, 1)
            .map { i: Int ->
                SenderRecord.create(
                    ProducerRecord(topic, message),
                    i
                )
            })
            .doOnError { e: Throwable? ->
                log.error(
                    "Send failed",
                    e
                )
            }
            .flatMap { r: SenderResult<Int> ->
                val metadata = r.recordMetadata()
                System.out.printf(
                    "Message %d sent successfully, topic-partition=%s-%d offset=%d timestamp=%s\n",
                    r.correlationMetadata(),
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset(),
                    dateFormat!!.format(Date(metadata.timestamp()))
                )

                Mono.create<SenderResult<Int>> {
                    it.success(r)
                }
            }
    }
}