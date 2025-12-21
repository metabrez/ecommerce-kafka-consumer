package com.edu.repo;

import com.edu.model.ProductInventory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<ProductInventory, Long> {

    /**
     * Finds the inventory record for a specific product ID.
     */
    Optional<ProductInventory> findByProductId(Long productId);

    /**
     * Custom query to atomically reduce stock.
     * Using @Modifying and @Query prevents "race conditions" where two
     * orders might try to update the same product at the same time.
     */
    @Modifying
    @Transactional
    @Query("UPDATE ProductInventory p SET p.stockQuantity = p.stockQuantity - :quantity " +
            "WHERE p.productId = :productId AND p.stockQuantity >= :quantity")
    int reduceStock(Long productId, Integer quantity);
}