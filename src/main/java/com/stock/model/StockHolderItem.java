package com.stock.model;

import java.util.Date;

import lombok.Data;

@Data
public class StockHolderItem {

	private Date releaseDate;
	private double peopleNum;
	private double stockPerPeople;
}
