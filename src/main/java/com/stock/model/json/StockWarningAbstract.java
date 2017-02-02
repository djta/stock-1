package com.stock.model.json;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

//@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="class")
//@JsonSubTypes({
//    @JsonSubTypes.Type(value=StockPriceWarning.class),
//})
@Data
@Accessors(chain = true)
@NoArgsConstructor
public abstract class StockWarningAbstract implements StockWarning {
	
	public enum StockOperation {Buy, Sell}

	protected boolean enable = true;
	protected String stockCode;
	protected String stockName;
	protected StockOperation operation;
	protected String[] phones;
	protected String[] emails;
}
