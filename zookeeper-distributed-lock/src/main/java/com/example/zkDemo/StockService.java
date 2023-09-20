package com.example.zkDemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    @Autowired
    private zkClient client;

    public void checkAndLock() {
        // addLock getLock and while-try
        ZkDistributedLock lock = this.client.getZkDistrubutedLock("lock");
        lock.lock();

        // Stock stock = this.stockMapper.selectById(1L);

        // - stock
//        if (-1)

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        lock.unlock();
    }

}
