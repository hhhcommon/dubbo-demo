package com.coe.wms.pojo.api.warehouse;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

public class Sku implements Serializable {

	private static final long serialVersionUID = 551558947068185258L;
	@XmlElement
	private String skuCode;

	@XmlElement
	private String skuName;

	/**
	 * 入库数量
	 */
	@XmlElement
	private Integer skuQty;

	@XmlElement
	private String skuRemark;

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public Integer getSkuQty() {
		return skuQty;
	}

	public void setSkuQty(Integer skuQty) {
		this.skuQty = skuQty;
	}

	public String getSkuRemark() {
		return skuRemark;
	}

	public void setSkuRemark(String skuRemark) {
		this.skuRemark = skuRemark;
	}
}
