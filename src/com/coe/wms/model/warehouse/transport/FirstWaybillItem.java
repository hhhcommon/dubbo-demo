package com.coe.wms.model.warehouse.transport;

import java.io.Serializable;

/**
 * 
 * @author yechao
 * @date 2014年11月3日
 */
public class FirstWaybillItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6403535606524196497L;

	/**
	 * 主键
	 */
	private Long id;

	private Long orderId;

	private Long firstWaybillId;
	/**
	 * sku
	 */
	private String sku;
	/**
	 * sku编号 等于顺丰的商品条码
	 */
	private String skuNo;
	/**
	 * 数量
	 */
	private Integer quantity;
	/**
	 * 商品规格
	 */
	private String specification;
	/**
	 * 可用于报关描述(申报品名)
	 */
	private String skuName;
	/**
	 * 可用于报关币种
	 */
	private String skuPriceCurrency;

	/**
	 * sku单价 可用于报关价值
	 */
	private Double skuUnitPrice;
	/**
	 * sku 重量
	 */
	private Double skuNetWeight;

	/**
	 * 备注
	 */
	private String remark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getFirstWaybillId() {
		return firstWaybillId;
	}

	public void setFirstWaybillId(Long firstWaybillId) {
		this.firstWaybillId = firstWaybillId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getSkuNo() {
		return skuNo;
	}

	public void setSkuNo(String skuNo) {
		this.skuNo = skuNo;
	}

	public String getSpecification() {
		return specification;
	}

	public void setSpecification(String specification) {
		this.specification = specification;
	}

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public String getSkuPriceCurrency() {
		return skuPriceCurrency;
	}

	public void setSkuPriceCurrency(String skuPriceCurrency) {
		this.skuPriceCurrency = skuPriceCurrency;
	}

	public Double getSkuUnitPrice() {
		return skuUnitPrice;
	}

	public void setSkuUnitPrice(Double skuUnitPrice) {
		this.skuUnitPrice = skuUnitPrice;
	}

	public Double getSkuNetWeight() {
		return skuNetWeight;
	}

	public void setSkuNetWeight(Double skuNetWeight) {
		this.skuNetWeight = skuNetWeight;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
