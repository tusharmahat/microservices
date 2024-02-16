package com.tushar.microservices.inventory.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tushar.microservices.inventory.entity.Inventory;

import java.util.List;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Long> {
	List<Inventory> findBySkuCodeIn(List<String> skucode);
}
