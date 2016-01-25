package com.stock.model.builder;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.stock.model.StockWindow;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class StockWindowBuilderTest {
	
	@Autowired
	private StockWindowBuilder builder;

	@Test
	public void buildInstanceTest() {
		String content = "2016-01-25 14:46:04 | 11.82 | -2.48% | 11.86(876) 11.85(807) 11.84(585) 11.83(124) 11.82(22) | 11.81(179) 11.80(343) 11.79(61) 11.78(169) 11.77(246) | -41.00% | -1416 | 12.12 | 12.00";
		StockWindow stockWin = builder.buildInstance(content);
		Assert.assertEquals(content, stockWin.toString().trim());
		
		content = "2016-01-21 09:25:14 | 6 279 73 60 28 | 16 70 10 5 54 | 8.57 8.55 8.54 8.53 8.50 | 8.45 8.41 8.40 8.38 8.37";
		stockWin = builder.buildInstance(content);
		String exp = "2016-01-21 09:25:14 | 0.00 | NaN% | 8.57(6) 8.55(279) 8.54(73) 8.53(60) 8.50(28) | 8.45(16) 8.41(70) 8.40(10) 8.38(5) 8.37(54) | -48.00% | -291 | 0.00 | 0.00";
		Assert.assertEquals(exp, stockWin.toString().trim());
	}
}
