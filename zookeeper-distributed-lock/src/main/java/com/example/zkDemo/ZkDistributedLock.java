package com.example.zkDemo;

import com.google.errorprone.annotations.Keep;
import io.netty.util.internal.StringUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.common.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZkDistributedLock {
    private static final String ROOT_PATH = "/distribute";
    private String path;
    private ZooKeeper zooKeeper;

    private static final ThreadLocal<Integer> THREAD_LOCAL = new ThreadLocal<>();

    public ZkDistributedLock(ZooKeeper zooKeeper, String lockname) {
        this.zooKeeper = zooKeeper;
//        this.path = ROOT_PATH + "/" + lockname;
        try {
            this.path = zooKeeper.create(ROOT_PATH + "/" + lockname + "_",
                    null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void lock() {
//            zooKeeper.create(path, null , ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        Integer flag = THREAD_LOCAL.get();
        if (flag != null && flag > 0) {
            THREAD_LOCAL.set(flag + 1);
            return;
        }

        String preNode = getpreNode(path);

        if (StringUtils.isEmpty(preNode)) {
            return;
        } else {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            try {
                if (this.zooKeeper.exists(ROOT_PATH + "/" + preNode, watchedEvent -> {
                    // add watch  less check;
                    countDownLatch.countDown();
                }) == null) {
                    return;
                }
                countDownLatch.await();
                THREAD_LOCAL.set(1);
                return;

            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(200);
            lock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getpreNode(String path) {
        Long curSerial = Long.valueOf(StringUtil.substringAfter(path, '_'));

        try {
            List<String> nodes = this.zooKeeper.getChildren(ROOT_PATH, false);

            if (CollectionUtils.isEmpty(nodes)) {
                return null;
            }
            // get preNode
            Long flag = 0L;
            String preNode = null;
            for (String node : nodes) {
                Long serial = Long.valueOf(StringUtil.substringAfter(path, '_'));
                if (serial < curSerial && serial > flag) {
                    flag = serial;
                    preNode = node;
                }
            }
            return preNode;
        } catch (KeeperException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void unlock() {
        try {
            THREAD_LOCAL.set(THREAD_LOCAL.get() - 1);
            if (THREAD_LOCAL.get() == 0) {
                this.zooKeeper.delete(path, 0);
                THREAD_LOCAL.remove();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }


}
