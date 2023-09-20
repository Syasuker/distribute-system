package com.example.zkDemo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.zookeeper.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class zkClient {
    private static final String connectString = "localhost";

    private static final String ROOT_PATH = "/distributed";

    private ZooKeeper zookeeper;

    @PostConstruct
    public void init() throws IOException {
        this.zookeeper = new ZooKeeper(connectString, 30000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println("zookeeper get-link success!");
            }
        });

        // create Distribute lock root node.

        try {
            if (this.zookeeper.exists(ROOT_PATH, false) == null) {
                this.zookeeper.create(ROOT_PATH, null,
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @PreDestroy
    public void destroy() {
        if (zookeeper != null) {
            try {
                zookeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ZkDistributedLock getZkDistrubutedLock(String lockname) {
        return new ZkDistributedLock(zookeeper, lockname);
    }


}
