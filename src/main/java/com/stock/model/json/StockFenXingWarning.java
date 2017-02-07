package com.stock.model.json;

import java.util.Date;
import java.util.LinkedList;

import com.stock.model.StockLineK;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class StockFenXingWarning extends StockWarningAbstract {
	
	public enum FenXingType 	{Bottom, Top}
	public enum FenXingCycle 	{Month, Week, Day, Min_60, Min_30, Min_15, Min_5}
	
	private FenXingType type;
	private FenXingCycle cycle;
	private Date startDate;
	
	private LinkedList<StockLineK> hisKLines;
	private float high;
	private float low;
	
	@Override
	public void prepare() {
		
	}

	@Override
	public boolean isTrigger() {
		StockLineK kLineLast = hisKLines.getLast();
		
		if (type == FenXingType.Bottom) {
			return kLineLast.isDown() && high > kLineLast.getHigh() && low > kLineLast.getLow();
		} else if (type == FenXingType.Top) {
			return kLineLast.isDown() == false && high < kLineLast.getHigh() && low < kLineLast.getLow();
		}
		
		return false;
	}

	@Override
	public String getSMSTemplateCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSMSTemplateParam() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmailSubject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmailBody() {
		// TODO Auto-generated method stub
		return null;
	}

}
