package com.stock.repositories;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.model.StockHistory;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

//	@Query("SELECT s FROM StockHistory s WHERE s.code = ?1")
	public List<StockHistory> findAllByCode(String code);
	
	public List<StockHistory> findAllByCodeAndDateBetween(String code, Timestamp dateStart, Timestamp dateEnd);
}
