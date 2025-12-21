package com.edu.controller;

import com.edu.model.ProductInventory;
import com.edu.repo.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryRepository inventoryRepository;

    // Get stock level for a product
    @GetMapping("/{productId}")
    public ResponseEntity<Integer> getStock(@PathVariable Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(inv -> ResponseEntity.ok(inv.getStockQuantity()))
                .orElse(ResponseEntity.notFound().build());
    }

    // Add new product stock manually
    @PostMapping("/add")
    public ResponseEntity<String> addStock(@RequestBody ProductInventory inventory) {
        inventoryRepository.save(inventory);
        return ResponseEntity.ok("Stock added successfully");
    }
}