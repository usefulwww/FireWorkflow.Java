package org.fireflow.example.loan_process.persistence;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LoanInfo entity.
 * 
 * @author MyEclipse Persistence Tools
 */
public class LoanInfo extends AbstractLoanInfo implements java.io.Serializable {

	// Constructors

	/** default constructor */
	public LoanInfo() {
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String sn = dFormat.format(new Date());
		this.setSn(sn);		
	}



}
