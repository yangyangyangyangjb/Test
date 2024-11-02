package com.example.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Slf4j
@Component
public class SpringRabbitListener {

    /**
     * 消费者
     * @param message
     */
    @RabbitListener(queues = "simple.queue")
    public void receiveMessage(String message) {
        log.info("Received <" + message + ">");
    }

    /**
     * 消费者2
     * @param message
     */
    @RabbitListener(queues = "work.queue")
    public void receiveMessage2(String message) throws InterruptedException {
        log.info("Received2 <" + message + ">");
    }

    /**
     * 消费者3
     * @param message
     */
    @RabbitListener(queues = "work.queue")
    public void receiveMessage3(String message) throws InterruptedException {
        log.error("Received3 <" + message + ">");
    }

    /**
     * 消费者4
     * @param message
     */
    @RabbitListener(queues = "fanout.queue1")
    public void receiveMessage4(String message) throws InterruptedException {
        log.info("receiveMessage4 <" + message + ">");
    }

    /**
     * 消费者5
     * @param message
     */
    @RabbitListener(queues = "fanout.queue2")
    public void receiveMessage5(String message) throws InterruptedException {
        log.error("receiveMessage5 <" + message + ">");
    }

    /**
     * 消费者6
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1", durable = "true"),
            exchange = @Exchange(name = "hmall.direct", type = ExchangeTypes.DIRECT),
            key = {"queue1"}
    ))
    public void receiveMessage6(String message) throws InterruptedException {
        log.info("receiveMessage6 <" + message + ">");
    }

    /**
     * 消费者7
     * @param message
     */
    /**
     * 消费者6
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue2", durable = "true"),
            exchange = @Exchange(name = "hmall.direct", type = ExchangeTypes.DIRECT),
            key = {"queue2"}
    ))
    public void receiveMessage7(String message) throws InterruptedException {
        log.error("receiveMessage7 <" + message + ">");
    }

    /**
     * 消费者8
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue", durable = "true"),
            exchange = @Exchange(name = "cs.topic", type = ExchangeTypes.TOPIC),
            key = {"shanDong.#"}
    ))
    public void receiveMessage8(String message) throws InterruptedException {
        log.error("receiveMessage8 <" + message + ">");
    }


    /**
     * 消费者8
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "object.queue", durable = "true"),
            exchange = @Exchange(name = "cs.topic", type = ExchangeTypes.TOPIC),
            key = {"shanDong.#"}
    ))
    public void objectQueue(String message) throws InterruptedException {
        log.error("objectQueue <" + message + ">");
    }
}
