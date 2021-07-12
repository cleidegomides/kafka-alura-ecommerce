package com.gomides.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMan {

    public static void main(String[] args) throws ExecutionException, InterruptedException {


        //CRIAR O PORDUTOR
        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            try (var emailDispatcher = new KafkaDispatcher<String>()) {
                var email = Math.random() + "@email.com";
                for (var i = 0; i < 10; i++) {
                    //key cria um id para direcionar p/ uma partição diferente
                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);


                    var order = new Order(orderId, amount, email );
                    //Envia o produto
                    orderDispatcher.send("ECOMMERCE_NOVO_PEDIDO", email, order);

                    var emailCode = "Thank you for your order! We are prossessing your order.";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);
                }
            }

        }
    }


}
