package com.coe.wms.pojo.api.warehouse;

import java.io.Serializable;

public class ClearanceDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7769929815846807840L;
	private String carrierCode;

	private String mailNo;

	/**
	 * 寄方地址，需要打印在顺丰运单上
	 */
	private String senderAddress;
	/**
	 * 月结卡号，需要打印在顺丰运单上
	 */
	private String custId;
	/**
	 * 付款方式，需要打印在顺丰运单上，默认寄付月结
	 */
	private String payMethod;

	/**
	 * 寄件方代码，系统定义，需要打印在顺丰运单上
	 */
	private String shipperCode;
	/**
	 * 到件方代码，目的地代码BSP返回，需打印到顺丰运单上
	 */
	private String deliveryCode;

	/**
	 * 用户订单号，需要打印到顺丰运单上
	 */
	private String orderId;

	public String getCarrierCode() {
		return carrierCode;
	}

	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}

	public String getMailNo() {
		return mailNo;
	}

	public void setMailNo(String mailNo) {
		this.mailNo = mailNo;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}

	public String getShipperCode() {
		return shipperCode;
	}

	public void setShipperCode(String shipperCode) {
		this.shipperCode = shipperCode;
	}

	public String getDeliveryCode() {
		return deliveryCode;
	}

	public void setDeliveryCode(String deliveryCode) {
		this.deliveryCode = deliveryCode;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
}
