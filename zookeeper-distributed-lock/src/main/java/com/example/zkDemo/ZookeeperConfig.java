package com.example.zkDemo;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
@Configuration
public class ZookeeperConfig {
    @Value("${zookeeper.server}")
    private String zookeeperServer;
    @Value("${zookeeper.timeout}")
    private int zookeeperTimeout;


    @Bean(destroyMethod = "close")
    public ZooKeeper zkClient() throws IOException {
        return new ZooKeeper(zookeeperServer, zookeeperTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }

}
