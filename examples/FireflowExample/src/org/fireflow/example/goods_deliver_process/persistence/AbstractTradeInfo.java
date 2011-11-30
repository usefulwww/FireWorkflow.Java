package org.fireflow.example.goods_deliver_process.persistence;

import java.util.Date;

/**
 * AbstractDemo entity provides the base persistence definition of the Demo
 * entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public abstract class AbstractTradeInfo implements java.io.Serializable {

	// Fields

	private String id;
	private String sn;
	private String goodsName;
	private String goodsType;
	private Long quantity;
	private Double unitPrice;
	private Double amount;
	private String customerName;
	private String customerMobile;
	private String customerPhoneFax;
	private String customerAddress;
	private String state;
	private Date payedTime;
	private Date deliveredTime;

	// Constructors

	/** default constructor */
	public AbstractTradeInfo() {
	}

	/** full constructor */
	public AbstractTradeInfo(String sn, String goodsName, String goodsType,
			Long quantity, Double unitPrice, Double amount,
			String customerName, String customerMobile,
			String customerPhoneFax, String customerAddress, String state,
			Date payedTime, Date deliveredTime) {
		this.sn = sn;
		this.goodsName = goodsName;
		this.goodsType = goodsType;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.amount = amount;
		this.customerName = customerName;
		this.customerMobile = customerMobile;
		this.customerPhoneFax = customerPhoneFax;
		this.customerAddress = customerAddress;
		this.state = state;
		this.payedTime = payedTime;
		this.deliveredTime = deliveredTime;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSn() {
		return this.sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getGoodsName() {
		return this.goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsType() {
		return this.goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public Long getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Double getUnitPrice() {
		return this.unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Double getAmount() {
		return this.amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerMobile() {
		return this.customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public String getCustomerPhoneFax() {
		return this.customerPhoneFax;
	}

	public void setCustomerPhoneFax(String customerPhoneFax) {
		this.customerPhoneFax = customerPhoneFax;
	}

	public String getCustomerAddress() {
		return this.customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Date getPayedTime() {
		return this.payedTime;
	}

	public void setPayedTime(Date payedTime) {
		this.payedTime = payedTime;
	}

	public Date getDeliveredTime() {
		return this.deliveredTime;
	}

	public void setDeliveredTime(Date deliveredTime) {
		this.deliveredTime = deliveredTime;
	}

}
