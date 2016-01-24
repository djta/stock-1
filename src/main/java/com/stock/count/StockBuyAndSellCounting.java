package com.stock.count;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.springframework.stereotype.Component;

import com.stock.model.StockTimeVol;

@Component
public class StockBuyAndSellCounting {

	public void count(String content) {
		String[] lines = content.split("\n");
		List<StockTimeVol> allStocks = new ArrayList<>();
		
		for (String line : lines) {
			StockTimeVol stock = StockTimeVol.createInstance(line);
			if (stock != null) {
				allStocks.add(stock);
			}
		}
		
		Map<String, JumpVol> maps = new HashMap<>();
		StockTimeVol lastOne = null;
		for (StockTimeVol stock : allStocks) {
			if (lastOne == null) {
				lastOne = stock;
				continue;
			}
			
			float gap = stock.getPrice() - lastOne.getPrice();
			if ( (gap > -0.011f && gap < 0.011f) || stock.getVol() < 300) {
				lastOne = stock;
				continue;
			}
			String key = String.format("%.2f", gap);
			
			JumpVol jumpVol = new JumpVol(gap, stock.getVol());
			if (maps.containsKey(key)) {
				maps.get(key).addVol(jumpVol.getVol());
			} else {
				maps.put(key, jumpVol);
			}
			
//			if (stock.getVol() >= 500) {
//				System.out.println(String.format("%.2f    %d     %.2f", gap, stock.getVol(), stock.getPrice()));
//			}
			
			lastOne = stock;
		}
		
//		System.out.println("\n\n");
		
		Collection<JumpVol> allJumpVols = maps.values();
		List<JumpVol> allJumpVolList = new ArrayList<>(allJumpVols);
		Collections.sort(allJumpVolList);
		int allVol = 0;
		for (JumpVol jv : allJumpVolList) {
			System.out.println(String.format("%.2f    %d     %d", jv.getValue(), jv.getVol(), jv.getCount()));
			allVol += jv.getVol();
		}
		System.out.println(String.format("Jump VOL: %d", allVol));
	}
	
	@Data
	private static class JumpVol implements Comparable<JumpVol> {
		private float value;
		private int vol;
		private int count =1;
		
		private JumpVol(float value, int vol) {
			this.value = value;
			if (value > 0) {
				this.vol = vol;
			} else {
				this.vol = -1 * vol;
			}
		}
		
		private void addVol(int vol) {
			this.vol += vol;
			count ++;
		}
		
		@Override
		public int compareTo(JumpVol o) {
			if (this.value < o.getValue()) {
				return -1;
			} else if (this.value == o.getValue()) {
				return 0;
			} else {
				return 1;
			}
		}
	}
}
