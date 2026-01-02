package com.example.paymentApi.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;


@Configuration
public class WebClientConfig {


    @Bean
    public WebClient webClient() {

        HttpClient httpClient = HttpClient.create()

                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)

                .responseTimeout(Duration.ofMinutes(2))

                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(60))
                                .addHandlerLast(new WriteTimeoutHandler(60))
                );

        return org.springframework.web.reactive.function.client.WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}