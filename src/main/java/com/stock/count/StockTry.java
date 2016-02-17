package com.stock.count;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.springframework.stereotype.Component;

import com.stock.model.StockTimeVol;

@Component
public class StockTry {
	
	private static final float PERCENT = 0.05f;

	public String find(String content) {
		String[] lines = content.split("\n");
		
		List<StockTimeVol> allStocks = new ArrayList<>();
		for (String line : lines) {
			StockTimeVol stock = StockTimeVol.createInstance(line);
			if (stock != null) {
				allStocks.add(stock);
			}
		}
		
		List<StockTryMode> allTry = new ArrayList<>();
		StockTimeVol lastOne = null;
		for (StockTimeVol stock : allStocks) {
			if (lastOne == null) {
				lastOne = stock;
				continue;
			}
			
			float gap = stock.getPrice() - lastOne.getPrice();
			if (Math.abs(gap) < 0.1f) {
				lastOne = stock;
				continue;
			}
			
			float percent = gap / lastOne.getPrice();
			if (Math.abs(percent) < PERCENT) {
				lastOne = stock;
				continue;
			}
			
			String percentStr = String.format("%.2f", percent);
			allTry.add(new StockTryMode(stock.getTime(), percentStr));
			lastOne = stock;
		}
		
		if (allTry.isEmpty()) {
			return null;
		}
		
		StringBuilder resBuilder = new StringBuilder();
		resBuilder.append(lines[0]).append("\n");
		for (StockTryMode tryMd : allTry) {
			resBuilder.append(tryMd.toString()).append("\n");
		}
		
		return resBuilder.toString();
	}
	
	@Data
	private static class StockTryMode {
		private String time;
		private String value;
		
		private StockTryMode(String time, String value) {
			this.time = time;
			this.value = value;
		}
	}
}
