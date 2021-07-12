package com.gomides.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudeDetectorService {

    public static void main(String[] args) {
        var fraudeService = new FraudeDetectorService();
        try(var service = new KafkaService<>(FraudeDetectorService.class.getSimpleName(),
                "ECOMMERCE_NOVO_PEDIDO", fraudeService::parse, Order.class,
                Map.of())) {
        service.run();
    }
    }

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException {
        System.out.println("---------------------------------");
        System.out.println("Processando novo pedido");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        var order = record.value();
        if (isFraud(order)) {
            System.out.println("Order is a fraud!");
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), order);
        }else {
            System.out.println("Approved: " + order);
            orderDispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(), order);

        }
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
    }

}
