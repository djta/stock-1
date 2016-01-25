package com.stock.process;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class StockWindowCatchProcessingTest {
	
	@Autowired
	private StockWindowCatchProcessing catchProcess;
	
	private static final int INTERVAL = 1000 * 60;

	@Test
	public void process() {
		while (true) {
			try {
				catchProcess.process();
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void formatTest() {
		try {
			catchProcess.format();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
