package com.stock.model.json;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	
	private static final Pattern  DATE_PATTERN = Pattern.compile("^(\\d{4})(\\-|\\/|\\.)\\d{1,2}\\2\\d{1,2}$");
	
	private FenXingType type;
	private FenXingCycle cycle;
	private String startDate;
	
	@JsonIgnore
	private LinkedList<StockLineK> hisKLines;
	@JsonIgnore
	private float high;
	@JsonIgnore
	private float low;

	@Override
	@JsonIgnore
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
	
	public static void main(String... args) {
//		StockFenXingWarning warning = new StockFenXingWarning();
//		warning.setCycle(FenXingCycle.Month)
//		.setType(FenXingType.Bottom)
//		.setStartDate(new Date(System.currentTimeMillis()));
		
//		System.out.println(JsonUtil.toJsonString(warning));
		
		String dateStr = "2017-1-4";
		Matcher matcher = DATE_PATTERN.matcher(dateStr);
		System.out.println(matcher.matches());
		if (matcher.matches()) {
			System.out.println(matcher.group(0));
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
			System.out.println(matcher.group(3));
		}
	}

}
