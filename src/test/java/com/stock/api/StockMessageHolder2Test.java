package com.stock.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class StockMessageHolder2Test {

	@Autowired
	private StockMessageHolder2 stockHolder;
	
	@Test
	public void Test(){
		System.out.println(stockHolder.getAllStocks().size());
	}
}
