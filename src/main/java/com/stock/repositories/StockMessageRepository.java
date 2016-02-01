package com.stock.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.stock.model.StockMessage;

public interface StockMessageRepository extends JpaRepository<StockMessage, Long> {

	@Query("SELECT s FROM StockMessage s ORDER BY s.id")
	public List<StockMessage> findAllOrderById();
}
