package com.javasurvival.spring.javasurvivalspring;

import io.vavr.collection.Map;
import io.vavr.control.Option;

public class MessageBoardService {
    public static final String MESSAGES_JSONS = "messages.jsons";
    private Map<String, Topic> topics;
    private final BoardMessageWriter writer = new BoardMessageWriter(MESSAGES_JSONS);

    public MessageBoardService() {
        this.topics = new BoardMessageReader().readAllTopics(MESSAGES_JSONS);
    }

    public synchronized Option<Topic> getTopic(String topicName) {
        return topics.get(topicName);
    }

    public synchronized Option<Topic> addMessageToTopic(String topicName, Message message) {
        Option<Topic> newTopic = getTopic(topicName).map(topic -> topic.addMessage(message));
        newTopic.forEach(topic -> writer.write(topic.name, message));
        Option<Map<String, Topic>> topicsMap = newTopic.map(topic -> this.topics.put(topicName, topic));
        topicsMap.forEach(topics -> this.topics = topics);
        return newTopic;
    }
}
