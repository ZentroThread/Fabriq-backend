package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dao.AttireDao;
import com.example.FabriqBackend.dto.StockUpdate;
import com.example.FabriqBackend.model.Attire;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class StockService {
    private final SimpMessagingTemplate messagingTemplate;
    private final AttireDao attireDao;
    private final Logger log = LoggerFactory.getLogger(StockService.class);

    public StockService(SimpMessagingTemplate messagingTemplate, AttireDao attireDao) {
        this.messagingTemplate = messagingTemplate;
        this.attireDao = attireDao;
    }

    @Transactional
    public StockUpdate reserveItem(String itemCode, String customerCode) {
        Attire attire = attireDao.findByAttireCode(itemCode);
        if (attire == null) {
            throw new IllegalArgumentException("Attire not found: " + itemCode);
        }

        if (attire.getAttireStock() == null || attire.getAttireStock() <= 0) {
            throw new IllegalStateException("No stock available for: " + itemCode);
        }

        attire.setAttireStock(attire.getAttireStock() - 1);
        attireDao.save(attire);

        StockUpdate update = new StockUpdate(itemCode, attire.getAttireStock(), customerCode);

        log.info("Reserved item {} for {}. New stock: {}", itemCode, customerCode, attire.getAttireStock());

        // Send the websocket update only after the surrounding transaction commits
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        messagingTemplate.convertAndSend("/topic/stock-updates", update);
                        log.info("Broadcasted stock update after commit: {} -> {}", itemCode, update.getAttireStock());
                    } catch (Exception ex) {
                        log.error("Failed to broadcast stock update after commit", ex);
                    }
                }
            });
        } else {
            // Fallback: if no transaction active, send immediately
            messagingTemplate.convertAndSend("/topic/stock-updates", update);
        }

        return update;
    }

    @Transactional
    public StockUpdate unreserveItem(String itemCode, String customerCode) {
        Attire attire = attireDao.findByAttireCode(itemCode);
        if (attire == null) {
            throw new IllegalArgumentException("Attire not found: " + itemCode);
        }

        attire.setAttireStock(attire.getAttireStock() + 1);
        attireDao.save(attire);

        StockUpdate update = new StockUpdate(itemCode, attire.getAttireStock(), customerCode);

        log.info("Unreserved item {} for {}. New stock: {}", itemCode, customerCode, attire.getAttireStock());

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        messagingTemplate.convertAndSend("/topic/stock-updates", update);
                        log.info("Broadcasted unreserve update after commit: {} -> {}", itemCode, update.getAttireStock());
                    } catch (Exception ex) {
                        log.error("Failed to broadcast unreserve update after commit", ex);
                    }
                }
            });
        } else {
            messagingTemplate.convertAndSend("/topic/stock-updates", update);
        }

        return update;
    }
}
