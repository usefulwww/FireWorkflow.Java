package org.fireflow.example.goods_deliver_process.persistence;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Demo entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class TradeInfo extends AbstractTradeInfo implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public TradeInfo() {
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String sn = dFormat.format(new Date());
		this.setSn(sn);
	}
}
