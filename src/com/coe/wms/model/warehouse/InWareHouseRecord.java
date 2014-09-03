package com.coe.wms.model.warehouse;

import java.io.Serializable;

/**
 * 入库单 (主单)
 * 
 * @author Administrator
 * 
 */
public class InWareHouseRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1475638002960975692L;

	/**
	 * 主键
	 */
	private Long id;

	/**
	 * 仓库id
	 */
	private String wareHouseId;

	/**
	 * 操作员Id
	 */
	private Long userId;

	/**
	 * 批次号
	 */
	private String batchNo;

	/**
	 * 创建时间 (收货时间)
	 */
	private Long createdTime;

	/**
	 * 入库摘要
	 */
	private String remark;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWareHouseId() {
		return wareHouseId;
	}

	public void setWareHouseId(String wareHouseId) {
		this.wareHouseId = wareHouseId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public Long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
