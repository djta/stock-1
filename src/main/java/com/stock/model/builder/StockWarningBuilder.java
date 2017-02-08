package com.stock.model.builder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.springframework.stereotype.Component;

@Component
public class StockWarningBuilder {

	private static final DateFormat DATA_FORMAT_COMPACT = new SimpleDateFormat("yyyyMMdd");
	private static final DateFormat DATA_FORMAT_PRETTY = new SimpleDateFormat("yyyy-MM-dd");
	
	
}
