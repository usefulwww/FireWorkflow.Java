package org.fireflow.example.goods_deliver_process.mbeans;

import org.fireflow.example.goods_deliver_process.persistence.TradeInfoDAO;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class PrepareGoodsBean {

	TradeInfoDAO tradeInfoDao = null;
	TransactionTemplate transactionTemplate = null;

	public PrepareGoodsBean() {
	}

	public void setTransactionManager(
			PlatformTransactionManager transactionManager) {
		this.transactionTemplate = new TransactionTemplate(transactionManager);
	}

	public TradeInfoDAO getTradeInfoDao() {
		return tradeInfoDao;
	}

	public void setTradeInfoDao(TradeInfoDAO demoDao) {
		this.tradeInfoDao = demoDao;
	}

	/**
	 * 保存业务数据。 在设个demo中，备货操作实际上没有什么数据需要保存，所以没有具体的逻辑代码。
	 * 此方法只是说明逻辑操作和流程操作（例如completeWorkItem)一般情况下是分开的。
	 * 
	 * @return
	 */
	public String save() {
		return "SELF";
	}

}
