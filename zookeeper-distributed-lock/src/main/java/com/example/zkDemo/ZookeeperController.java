package com.example.zkDemo;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ZookeeperController {
    @Autowired
    ZooKeeper zkClient;

    @GetMapping("/zookeeper")
    public String getData() throws KeeperException, InterruptedException {
        String path = "/";
        boolean watch = true;
        byte[] data = zkClient.getData(path, watch, null);
        return new String(data);
    }

    @PostMapping("/zkCreate")
    public String create() throws InterruptedException, KeeperException {
        String path = "/test";
        String data = "Hello Zookeeper";
        ArrayList<ACL> aclList = ZooDefs.Ids.OPEN_ACL_UNSAFE;

        CreateMode createMode = CreateMode.PERSISTENT;
        String result = zkClient.create(path, data.getBytes(), aclList, createMode);
        return result;
    }

    @GetMapping("/zkGetData")
    public String getdata() throws InterruptedException, KeeperException {
        String path = "/test";
        boolean watch = true;

        byte[] data = zkClient.getData(path, watch, null);
        String str = new String(data);
        return str;

    }
    @PostMapping("/zkDelete")
    public void delete() throws InterruptedException, KeeperException {
        String path = "/test";
        int version = 0;

        zkClient.delete(path, version);
    }


}
