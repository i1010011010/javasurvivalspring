package com.javasurvival.spring.javasurvivalspring;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Option;

public class MessageBoardService {
    private Map<String,Topic>topics;

    public MessageBoardService() {
        this.topics = List.of("java","ogólny","dzowne")
                .map(name -> Topic.createTopic(name))
                .toMap(topic -> topic.name,topic -> topic);
    }

    public synchronized Option<Topic> getTopic(String topicName){
        return topics.get(topicName);
    }

    public synchronized Option<Topic> addMessageToTopic(String topicName,Message message){
        Option<Topic> newTopic = getTopic(topicName).map(topic -> topic.addMessage(message));
        Option<Map<String, Topic>> topicsMap = newTopic.map(topic -> this.topics.put(topicName, topic));
        topicsMap.forEach(topics -> this.topics = topics);
        return newTopic;
    }
}
