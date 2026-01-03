package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dao.AttireDao;
import com.example.FabriqBackend.dto.StockUpdate;
import com.example.FabriqBackend.model.Attire;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {
    private final SimpMessagingTemplate messagingTemplate;
    private final AttireDao attireDao;

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
        messagingTemplate.convertAndSend("/topic/stock-updates", update);
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

        System.out.println("📤 Broadcasting unreserve: " + update);
        messagingTemplate.convertAndSend("/topic/stock-updates", update);

        return update;
    }
}
