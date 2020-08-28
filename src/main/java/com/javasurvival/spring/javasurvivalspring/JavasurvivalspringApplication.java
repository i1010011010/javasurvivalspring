package com.javasurvival.spring.javasurvivalspring;
/*
    RATAJSKI 1.7
 */

import io.vavr.collection.List;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class JavasurvivalspringApplication {

    private List<Message> messages = List.empty();

    private JavasurvivalspringApplication() {
        addMessage(new Message("dupa", "Andrzej"));
        addMessage(new Message("bla bla bla", "SecondAndrzej"));

    }

    public static void main(String[] args) {
        new JavasurvivalspringApplication().serve();

    }

    //Konfiguracja servera
    private void serve() {
        RouterFunction route = nest(path("/api"),
                route(GET("/time"), getTime())
                        .andRoute(GET("/message"), renderMessages()).andRoute(POST("/message"), postMessages()));
        HttpHandler httpHandler = RouterFunctions.toHttpHandler(route);
        ReactorHttpHandlerAdapter httpHandlerAdapter = new ReactorHttpHandlerAdapter(httpHandler);
        DisposableServer server = HttpServer.create().host("localhost").port(8080).handle(httpHandlerAdapter).bindNow();
        System.out.println("press enter");
        Scanner scanner = new Scanner(System.in);
        scanner.next();
        server.disposeNow();
    }

    private HandlerFunction<ServerResponse> postMessages() {
        return request ->
        {
            Mono<Message> postedMessage = request.bodyToMono(Message.class);
            return postedMessage.flatMap(message -> {
                addMessage(message);
                return ServerResponse.ok().contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .body(fromObject(messages.toJavaList()));
            });
        };
    }

    private HandlerFunction<ServerResponse> renderMessages() {
        return request ->
                ServerResponse.ok().contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .body(fromObject(getMessages().toJavaList()));
    }

    private HandlerFunction<ServerResponse> getTime() {
        return request ->
        {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return ServerResponse.ok().contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                    .body(fromObject(formatter.format(now)));
        };
    }

    private synchronized void addMessage(Message message) {
        messages = messages.append(message);
    }

    private synchronized List<Message> getMessages() {
        return messages;
    }
}

