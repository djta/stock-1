package com.stock.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class StockLineK {

	private float low;
	private float high;
	private boolean down;
	private boolean turningPoint = false;		//趋势转折点，分型的第三根K线
	
	public StockLineK(float low, float high) {
		this.low = low;
		this.high = high;
	}
	
	public boolean isContain(StockLineK k) {
		return low <= k.getLow() && high >= k.getHigh();
	}
	
	//底分型
	public boolean isBottom() {
		return turningPoint && down == false;
	}
	
	//顶分型
	public boolean isTop() {
		return turningPoint && down;
	}
}
