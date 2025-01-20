package api_gateway.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    //? DEAD LETTER
    @Value("${rabbitmq.dlq.exchange.name}")
    private String dlqExchange;
    @Value("${rabbitmq.error.queue.name}")
    private String deadQueue;
    @Value("${rabbitmq.dlq.routing.key}")
    private String dlqRoutingKey;

    //TODO: Auth Service
    @Value("${rabbitmq.auth.queue.name}")
    private String authQueue;
    @Value("${rabbitmq.auth.routing.key}")
    private String authRoutingKey;
    @Value("${rabbitmq.user.queue.name}")
    private String userQueue;
    @Value("${rabbitmq.user.routing.key}")
    private String userRoutingKey;

    //TODO: Product Service
    @Value("${rabbitmq.playlist.queue.name}")
    private String playlistQueue;
    @Value("${rabbitmq.playlist.routing.key}")
    private String playlistRoutingKey;
    @Value("${rabbitmq.music.queue.name}")
    private String musicQueue;
    @Value("${rabbitmq.music.routing.key}")
    private String musicRoutingKey;

    //TODO: MediaIO Service
    @Value("${rabbitmq.mediaIO.queue.name}")
    private String mediaIOQueue;
    @Value("${rabbitmq.mediaIO.routing.key}")
    private String mediaIORoutingKey;

    //TODO: Artists Service
    @Value("${rabbitmq.artist.queue.name}")
    private String artistQueue;
    @Value("${rabbitmq.artist.routing.key}")
    private String artistRoutingKey;

    //!spring bean for rabbitmq queue
    @Bean
    public Queue authQueue() {
        return QueueBuilder.durable(authQueue)
                .withArgument("x-dead-letter-exchange", dlqExchange) // Dead Letter Exchange
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey) // Dead Letter Routing Key
                .build();
    }

    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable(userQueue)
                .withArgument("x-dead-letter-exchange", dlqExchange) // Dead Letter Exchange
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey) // Dead Letter Routing Key
                .build();
    }

    @Bean
    public Queue playlistQueue() {
        return QueueBuilder.durable(playlistQueue)
                .withArgument("x-dead-letter-exchange", dlqExchange) // Dead Letter Exchange
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey) // Dead Letter Routing Key
                .build();
    }

    @Bean
    public Queue musicQueue() {
        return QueueBuilder.durable(musicQueue)
                .withArgument("x-dead-letter-exchange", dlqExchange) // Dead Letter Exchange
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey) // Dead Letter Routing Key
                .build();
    }

    @Bean
    public Queue mediaIOQueue() {
        return QueueBuilder.durable(mediaIOQueue)
                .withArgument("x-dead-letter-exchange", dlqExchange) // Dead Letter Exchange
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey) // Dead Letter Routing Key
                .build();
    }

    @Bean
    public Queue artistQueue() {
        return QueueBuilder.durable(artistQueue)
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
    public Binding authBinding() {
        return BindingBuilder.bind(authQueue())
                .to(exchange())
                .with(authRoutingKey);
    }

    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(userQueue())
                .to(exchange())
                .with(userRoutingKey);
    }

    @Bean
    public Binding playlistBinding() {
        return BindingBuilder.bind(playlistQueue())
                .to(exchange())
                .with(playlistRoutingKey);
    }

    @Bean
    public Binding musicBinding() {
        return BindingBuilder.bind(musicQueue())
                .to(exchange())
                .with(musicRoutingKey);
    }

    @Bean
    public Binding mediaIOBinding() {
        return BindingBuilder.bind(mediaIOQueue())
                .to(exchange())
                .with(mediaIORoutingKey);
    }

    @Bean
    public Binding artistBinding() {
        return BindingBuilder.bind(artistQueue())
                .to(exchange())
                .with(artistRoutingKey);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(dlqExchange())
                .with(dlqRoutingKey);
    }

    //Config for communicating via JSON
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
