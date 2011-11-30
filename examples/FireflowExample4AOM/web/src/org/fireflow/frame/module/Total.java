/*$Id: Total.java,v 1.1 2009/05/22 09:41:16 libin Exp $
 *--------------------------------------
 * Apusic (Kingdee Middleware)
 *---------------------------------------
 * Copyright By Apusic ,All right Reserved
 * author   date   comment
 * chenhongxin  2008-4-14  Created
*/
package org.fireflow.frame.module;

import java.text.DecimalFormat;

/**
 * 统计报告合计数据，同于统计列表
 * @author chenhongxin
 */
public class Total {
	private int totalEmployee;
	private int totalPeriod;
	private int distinctEmployee;
	private int curEmployee;
	public int getTotalEmployee() {
		return totalEmployee;
	}
	public void setTotalEmployee(int totalEmployee) {
		this.totalEmployee = totalEmployee;
	}
	public int getTotalPeriod() {
		return totalPeriod;
	}
	public void setTotalPeriod(int totalPeriod) {
		this.totalPeriod = totalPeriod;
	}
	public int getDistinctEmployee() {
		return distinctEmployee;
	}
	public void setDistinctEmployee(int distinctEmployee) {
		this.distinctEmployee = distinctEmployee;
	}
	public int getCurEmployee() {
		return curEmployee;
	}
	public void setCurEmployee(int curEmployee) {
		this.curEmployee = curEmployee;
	}
	public String getAvgPeriod() {
		if(curEmployee != 0) {
			DecimalFormat f = new DecimalFormat();
			f.applyPattern("###.00");
			return f.format(Double.valueOf(totalPeriod).doubleValue()
					/ Long.valueOf(curEmployee).doubleValue()).toString();
		} else {
			return "000.00";
		}
	}
	public String getCoverageRate() {
		if(curEmployee != 0) {
			DecimalFormat f = new DecimalFormat();
			f.applyPattern("###.00%");
			return f.format(Double.valueOf(distinctEmployee).doubleValue()
					/ Long.valueOf(curEmployee).doubleValue()).toString();
		} else {
			return "000.00%";
		}
	}
}
