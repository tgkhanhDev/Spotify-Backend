package auth_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.auth.queue.name}")
    private String authQueue;

    @Value("${rabbitmq.error.queue.name}")
    private String deadQueue;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.dlq.exchange.name}")
    private String dlqExchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    @Value("${rabbitmq.dlq.routing.key}")
    private String dlqRoutingKey;



    //!spring bean for rabbitmq queue
    @Bean
    public Queue authQueue() {
        return QueueBuilder.durable(authQueue)
                .withArgument("x-dead-letter-exchange", dlqExchange) // Dead Letter Exchange
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey) // Dead Letter Routing Key
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(deadQueue, true);
    }

    //!spring bean for rabbitMQ exchange
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public TopicExchange dlqExchange() {
        return new TopicExchange(dlqExchange);
    }

    //!binding between queue and exchange using routing key
    @Bean
    public Binding authQueueBinding() {
        return BindingBuilder.bind(authQueue())
                .to(exchange())
                .with(routingKey);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(dlqExchange())
                .with(dlqRoutingKey);
    }
    //
    //!Config for communicating via JSON
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
