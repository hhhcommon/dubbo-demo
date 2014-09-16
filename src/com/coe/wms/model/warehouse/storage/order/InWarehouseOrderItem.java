package com.coe.wms.model.warehouse.storage.order;

import java.io.Serializable;

public class InWarehouseOrderItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 61592453340395240L;

	private Long id;

	/**
	 * 大包号 同客户下, packageNo 不可重复
	 */
	private Long packageId;

	/**
	 * sku下产品数量
	 */
	private int quantity;

	private String sku;

	/**
	 * 该SKU已经入库的数量
	 * 
	 * 在添加入库单时,更新此字段.也显示此字段
	 */
	private int receivedQuantity;

	public int getReceivedQuantity() {
		return receivedQuantity;
	}

	public void setReceivedQuantity(int receivedQuantity) {
		this.receivedQuantity = receivedQuantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPackageId() {
		return packageId;
	}

	public void setPackageId(Long packageId) {
		this.packageId = packageId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}
}
