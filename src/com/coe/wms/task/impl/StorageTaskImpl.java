package com.coe.wms.task.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.coe.wms.dao.user.IUserDao;
import com.coe.wms.dao.warehouse.IWarehouseDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseOrderDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseOrderItemDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseOrderStatusDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseRecordDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseRecordItemDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderItemDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderReceiverDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderSenderDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderStatusDao;
import com.coe.wms.model.warehouse.Warehouse;
import com.coe.wms.model.warehouse.storage.order.InWarehouseOrder;
import com.coe.wms.model.warehouse.storage.record.InWarehouseRecord;
import com.coe.wms.model.warehouse.storage.record.InWarehouseRecordItem;
import com.coe.wms.pojo.api.warehouse.EventBody;
import com.coe.wms.pojo.api.warehouse.EventHeader;
import com.coe.wms.pojo.api.warehouse.EventType;
import com.coe.wms.pojo.api.warehouse.LogisticsDetail;
import com.coe.wms.pojo.api.warehouse.LogisticsEvent;
import com.coe.wms.pojo.api.warehouse.LogisticsEventsRequest;
import com.coe.wms.pojo.api.warehouse.LogisticsOrder;
import com.coe.wms.pojo.api.warehouse.Sku;
import com.coe.wms.pojo.api.warehouse.SkuDetail;
import com.coe.wms.task.IStorageTask;
import com.coe.wms.util.Constant;
import com.coe.wms.util.DateUtil;
import com.coe.wms.util.XmlUtil;

@Component
public class StorageTaskImpl implements IStorageTask {

	private static final Logger logger = Logger.getLogger(StorageTaskImpl.class);

	@Resource(name = "warehouseDao")
	private IWarehouseDao warehouseDao;

	@Resource(name = "inWarehouseOrderDao")
	private IInWarehouseOrderDao inWarehouseOrderDao;

	@Resource(name = "inWarehouseOrderStatusDao")
	private IInWarehouseOrderStatusDao inWarehouseOrderStatusDao;

	@Resource(name = "inWarehouseOrderItemDao")
	private IInWarehouseOrderItemDao inWarehouseOrderItemDao;

	@Resource(name = "outWarehouseOrderDao")
	private IOutWarehouseOrderDao outWarehouseOrderDao;

	@Resource(name = "outWarehouseOrderStatusDao")
	private IOutWarehouseOrderStatusDao outWarehouseOrderStatusDao;

	@Resource(name = "outWarehouseOrderItemDao")
	private IOutWarehouseOrderItemDao outWarehouseOrderItemDao;

	@Resource(name = "outWarehouseOrderSenderDao")
	private IOutWarehouseOrderSenderDao outWarehouseOrderSenderDao;

	@Resource(name = "outWarehouseOrderReceiverDao")
	private IOutWarehouseOrderReceiverDao outWarehouseOrderReceiverDao;

	@Resource(name = "inWarehouseRecordDao")
	private IInWarehouseRecordDao inWarehouseRecordDao;

	@Resource(name = "inWarehouseRecordItemDao")
	private IInWarehouseRecordItemDao inWarehouseRecordItemDao;

	@Resource(name = "userDao")
	private IUserDao userDao;

	/**
	 * 发送仓配入库订单信息给客户
	 */
	@Scheduled(cron = "0 0/1 8-18 * * ? ")
	// 早上8点到下午6点,每分钟
	// @Scheduled(cron="0 0/30 8-18 * * ? ") //早上8点到下午6点,每半小时一次
	@Override
	public void sendInWarehouseInfoToCustomer() {
		InWarehouseRecord param = new InWarehouseRecord();
		param.setCallbackIsSuccess(Constant.N);
		List<Long> recordIdList = inWarehouseRecordDao.findCallbackUnSuccessRecordId();
		logger.info("找到待回传SKU入库信息,总数:" + recordIdList.size());
		// 根据id 获取记录
		for (int i = 0; i < recordIdList.size(); i++) {
			Long recordId = recordIdList.get(i);
			InWarehouseRecord inWarehouseRecord = inWarehouseRecordDao.getInWarehouseRecordById(recordId);

			// 仓库
			Warehouse warehouse = warehouseDao.getWarehouseById(inWarehouseRecord.getWarehouseId());

			// 大包跟踪号
			String trackingNo = inWarehouseRecord.getPackageTrackingNo();

			InWarehouseRecordItem recordItemParam = new InWarehouseRecordItem();
			recordItemParam.setInWarehouseRecordId(recordId);
			// 入库明细
			List<InWarehouseRecordItem> recordItemList = inWarehouseRecordItemDao.findInWarehouseRecordItem(recordItemParam, null, null);

			LogisticsEventsRequest logisticsEventsRequest = new LogisticsEventsRequest();
			//
			LogisticsEvent logisticsEvent = new LogisticsEvent();

			// 事件头
			EventHeader eventHeader = new EventHeader();
			eventHeader.setEventType(EventType.WMS_SKU_STOCKIN_INFO);
			eventHeader.setEventTime(DateUtil.dateConvertString(new Date(), DateUtil.yyyy_MM_ddHHmmss));
			// 仓库编码
			eventHeader.setEventSource(warehouse.getWarehouseNo());
			// CP_PARTNERFLAT 为 顺丰文档指定
			eventHeader.setEventTarget("CP_PARTNERFLAT");

			logisticsEvent.setEventHeader(eventHeader);

			// 事件body
			EventBody eventBody = new EventBody();
			// 物流详情
			LogisticsDetail logisticsDetail = new LogisticsDetail();

			List<LogisticsOrder> logisticsOrders = new ArrayList<LogisticsOrder>();
			LogisticsOrder logisticsOrder = new LogisticsOrder();
			// 对应入库订单
			// if (inWarehouseRecord.getInWarehouseOrderId() != null) {
			// inWarehouseOrderDao.getInWarehouseOrderById(inWarehouseRecord.getInWarehouseOrderId());
			InWarehouseOrder inWarehouseOrderParam = new InWarehouseOrder();
			inWarehouseOrderParam.setPackageTrackingNo(trackingNo);
			List<InWarehouseOrder> inWarehouseOrderList = inWarehouseOrderDao.findInWarehouseOrder(inWarehouseOrderParam, null, null);
			if (inWarehouseOrderList.size() > 0) {
				InWarehouseOrder inWarehouseOrder = inWarehouseOrderList.get(0);
				logisticsOrder.setLogisticsType(inWarehouseOrder.getLogisticsType());
				logisticsOrder.setCarrierCode(inWarehouseOrder.getCarrierCode());
			}
			logisticsOrder.setMailNo(trackingNo);
			// sku 详情
			SkuDetail skuDetail = new SkuDetail();
			List<Sku> skuList = new ArrayList<Sku>();
			for (InWarehouseRecordItem recordItem : recordItemList) {
				Sku sku = new Sku();
				sku.setSkuCode(recordItem.getSku());
				sku.setSkuName(recordItem.getSku());
				sku.setSkuInBoundQty(recordItem.getQuantity());
				sku.setSkuBoundTime(DateUtil.dateConvertString(new Date(recordItem.getCreatedTime()), DateUtil.yyyy_MM_ddHHmmss));
				skuList.add(sku);
			}
			skuDetail.setSkus(skuList);
			logisticsOrder.setSkuDetail(skuDetail);

			logisticsOrders.add(logisticsOrder);
			logisticsDetail.setLogisticsOrders(logisticsOrders);
			eventBody.setLogisticsDetail(logisticsDetail);

			logisticsEvent.setEventBody(eventBody);
			logisticsEventsRequest.setLogisticsEvent(logisticsEvent);

			String xml = XmlUtil.toXml(LogisticsEventsRequest.class, logisticsEventsRequest);
			logger.info("回传SKU入库信息: xml=" + xml);
			
			
		}
	}
}
