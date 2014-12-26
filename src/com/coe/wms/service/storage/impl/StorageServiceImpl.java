package com.coe.wms.service.storage.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.coe.wms.dao.product.IProductDao;
import com.coe.wms.dao.user.IUserDao;
import com.coe.wms.dao.warehouse.ISeatDao;
import com.coe.wms.dao.warehouse.IShelfDao;
import com.coe.wms.dao.warehouse.IShipwayDao;
import com.coe.wms.dao.warehouse.ITrackingNoDao;
import com.coe.wms.dao.warehouse.IWarehouseDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseOrderDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseOrderItemDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseOrderStatusDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseRecordDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseRecordItemDao;
import com.coe.wms.dao.warehouse.storage.IInWarehouseRecordStatusDao;
import com.coe.wms.dao.warehouse.storage.IItemInventoryDao;
import com.coe.wms.dao.warehouse.storage.IItemShelfInventoryDao;
import com.coe.wms.dao.warehouse.storage.IOnShelfDao;
import com.coe.wms.dao.warehouse.storage.IOutShelfDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderAdditionalSfDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderItemDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderItemShelfDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderReceiverDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderSenderDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseOrderStatusDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehousePackageDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseRecordDao;
import com.coe.wms.dao.warehouse.storage.IOutWarehouseRecordItemDao;
import com.coe.wms.dao.warehouse.storage.IReportDao;
import com.coe.wms.dao.warehouse.storage.IReportTypeDao;
import com.coe.wms.exception.ServiceException;
import com.coe.wms.model.product.Product;
import com.coe.wms.model.unit.Currency.CurrencyCode;
import com.coe.wms.model.unit.Weight;
import com.coe.wms.model.unit.Weight.WeightCode;
import com.coe.wms.model.user.User;
import com.coe.wms.model.warehouse.Shipway;
import com.coe.wms.model.warehouse.TrackingNo;
import com.coe.wms.model.warehouse.Warehouse;
import com.coe.wms.model.warehouse.storage.order.InWarehouseOrder;
import com.coe.wms.model.warehouse.storage.order.InWarehouseOrderItem;
import com.coe.wms.model.warehouse.storage.order.InWarehouseOrderStatus;
import com.coe.wms.model.warehouse.storage.order.InWarehouseOrderStatus.InWarehouseOrderStatusCode;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrder;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderAdditionalSf;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderItem;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderItemShelf;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderReceiver;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderSender;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderStatus;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderStatus.OutWarehouseOrderStatusCode;
import com.coe.wms.model.warehouse.storage.record.InWarehouseRecord;
import com.coe.wms.model.warehouse.storage.record.InWarehouseRecordItem;
import com.coe.wms.model.warehouse.storage.record.InWarehouseRecordStatus;
import com.coe.wms.model.warehouse.storage.record.InWarehouseRecordStatus.InWarehouseRecordStatusCode;
import com.coe.wms.model.warehouse.storage.record.ItemInventory;
import com.coe.wms.model.warehouse.storage.record.ItemShelfInventory;
import com.coe.wms.model.warehouse.storage.record.OutWarehousePackage;
import com.coe.wms.model.warehouse.storage.record.OutWarehouseRecord;
import com.coe.wms.model.warehouse.storage.record.OutWarehouseRecordItem;
import com.coe.wms.pojo.api.warehouse.Buyer;
import com.coe.wms.pojo.api.warehouse.ClearanceDetail;
import com.coe.wms.pojo.api.warehouse.ErrorCode;
import com.coe.wms.pojo.api.warehouse.EventBody;
import com.coe.wms.pojo.api.warehouse.EventHeader;
import com.coe.wms.pojo.api.warehouse.LogisticsDetail;
import com.coe.wms.pojo.api.warehouse.LogisticsEvent;
import com.coe.wms.pojo.api.warehouse.LogisticsEventsRequest;
import com.coe.wms.pojo.api.warehouse.LogisticsOrder;
import com.coe.wms.pojo.api.warehouse.ReceiverDetail;
import com.coe.wms.pojo.api.warehouse.Response;
import com.coe.wms.pojo.api.warehouse.Responses;
import com.coe.wms.pojo.api.warehouse.SenderDetail;
import com.coe.wms.pojo.api.warehouse.Sku;
import com.coe.wms.pojo.api.warehouse.SkuDetail;
import com.coe.wms.pojo.api.warehouse.TradeDetail;
import com.coe.wms.pojo.api.warehouse.TradeOrder;
import com.coe.wms.service.storage.IStorageService;
import com.coe.wms.util.Config;
import com.coe.wms.util.Constant;
import com.coe.wms.util.DateUtil;
import com.coe.wms.util.NumberUtil;
import com.coe.wms.util.Pagination;
import com.coe.wms.util.StringUtil;
import com.coe.wms.util.XmlUtil;

/**
 * 仓配服务
 * 
 * @author Administrator
 * 
 */
@Service("storageService")
public class StorageServiceImpl implements IStorageService {

	private static final Logger logger = Logger.getLogger(StorageServiceImpl.class);

	@Resource(name = "warehouseDao")
	private IWarehouseDao warehouseDao;

	@Resource(name = "reportTypeDao")
	private IReportTypeDao reportTypeDao;

	@Resource(name = "reportDao")
	private IReportDao reportDao;

	@Resource(name = "trackingNoDao")
	private ITrackingNoDao trackingNoDao;

	@Resource(name = "seatDao")
	private ISeatDao seatDao;

	@Resource(name = "shelfDao")
	private IShelfDao shelfDao;

	@Resource(name = "onShelfDao")
	private IOnShelfDao onShelfDao;

	@Resource(name = "outShelfDao")
	private IOutShelfDao outShelfDao;
	@Resource(name = "inWarehouseOrderDao")
	private IInWarehouseOrderDao inWarehouseOrderDao;

	@Resource(name = "inWarehouseOrderStatusDao")
	private IInWarehouseOrderStatusDao inWarehouseOrderStatusDao;

	@Resource(name = "inWarehouseRecordStatusDao")
	private IInWarehouseRecordStatusDao inWarehouseRecordStatusDao;

	@Resource(name = "outWarehouseOrderItemShelfDao")
	private IOutWarehouseOrderItemShelfDao outWarehouseOrderItemShelfDao;

	@Resource(name = "inWarehouseOrderItemDao")
	private IInWarehouseOrderItemDao inWarehouseOrderItemDao;

	@Resource(name = "outWarehouseOrderDao")
	private IOutWarehouseOrderDao outWarehouseOrderDao;

	@Resource(name = "outWarehouseRecordDao")
	private IOutWarehouseRecordDao outWarehouseRecordDao;

	@Resource(name = "outWarehousePackageDao")
	private IOutWarehousePackageDao outWarehousePackageDao;

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

	@Resource(name = "outWarehouseRecordItemDao")
	private IOutWarehouseRecordItemDao outWarehouseRecordItemDao;

	@Resource(name = "userDao")
	private IUserDao userDao;

	@Resource(name = "itemInventoryDao")
	private IItemInventoryDao itemInventoryDao;

	@Resource(name = "itemShelfInventoryDao")
	private IItemShelfInventoryDao itemShelfInventoryDao;

	@Resource(name = "outWarehouseOrderAdditionalSfDao")
	private IOutWarehouseOrderAdditionalSfDao outWarehouseOrderAdditionalSfDao;

	@Resource(name = "productDao")
	private IProductDao productDao;

	@Resource(name = "config")
	private Config config;
	
	@Resource(name = "shipwayDao")
	private IShipwayDao shipwayDao;

	/**
	 * 根据入库订单id, 查找入库物品明细
	 * 
	 * @param orderId
	 * @param pagination
	 * @return
	 */
	@Override
	public Pagination getInWarehouseOrderItemData(Long orderId, Pagination pagination) {
		InWarehouseOrderItem param = new InWarehouseOrderItem();
		param.setOrderId(orderId);
		List<InWarehouseOrderItem> inWarehouseOrderItemList = inWarehouseOrderItemDao.findInWarehouseOrderItem(param, null, pagination);

		return pagination;
	}

	/**
	 * 根据入库订单id, 查找入库物品明细
	 * 
	 * @param orderId
	 * @param pagination
	 * @return
	 */
	@Override
	public List<InWarehouseOrderItem> getInWarehouseOrderItem(Long orderId) {
		InWarehouseOrderItem param = new InWarehouseOrderItem();
		param.setOrderId(orderId);
		List<InWarehouseOrderItem> inWarehouseOrderItemList = inWarehouseOrderItemDao.findInWarehouseOrderItem(param, null, null);
		return inWarehouseOrderItemList;
	}

	public List<Map<String, String>> getInWarehouseOrderItemMap(Long orderId) {
		InWarehouseOrderItem param = new InWarehouseOrderItem();
		param.setOrderId(orderId);
		List<InWarehouseOrderItem> inWarehouseOrderItemList = inWarehouseOrderItemDao.findInWarehouseOrderItem(param, null, null);
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		for (InWarehouseOrderItem item : inWarehouseOrderItemList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("sku", item.getSku());
			map.put("skuName", item.getSkuName());
			map.put("skuNo", item.getSkuNo() == null ? "" : item.getSkuNo());
			map.put("quantity", NumberUtil.intToString(item.getQuantity()));
			int receivedQuantity = inWarehouseRecordItemDao.countInWarehouseItemSkuQuantityByOrderId(orderId, item.getSku());
			map.put("receivedQuantity", receivedQuantity + "");
			mapList.add(map);
		}
		return mapList;
	}

	/**
	 * 保存入库明细
	 */
	@Override
	public Map<String, String> saveInWarehouseRecordItem(String itemSku, Integer itemQuantity, String itemRemark, Long warehouseId, Long inWarehouseRecordId, Long userIdOfOperator, String isConfirm) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.STATUS, Constant.FAIL);
		if (StringUtil.isNull(itemSku)) {
			map.put(Constant.MESSAGE, "请输入商品条码.");
			return map;
		}
		if (itemQuantity == null) {
			map.put(Constant.MESSAGE, "请输入商品数量.");
			return map;
		}
		Long orderId = inWarehouseRecordDao.getInWarehouseOrderIdByRecordId(inWarehouseRecordId);
		if (orderId == null) {
			map.put(Constant.MESSAGE, "找不到入库订单Id.");
			return map;
		}
		// 检查该SKU是否存在入库订单中
		InWarehouseOrderItem inWarehouseOrderItemParam = new InWarehouseOrderItem();
		inWarehouseOrderItemParam.setSku(itemSku);
		inWarehouseOrderItemParam.setOrderId(orderId);
		List<InWarehouseOrderItem> inWarehouseOrderItemList = inWarehouseOrderItemDao.findInWarehouseOrderItem(inWarehouseOrderItemParam, null, null);
		if (inWarehouseOrderItemList.size() <= 0) {
			// 2014-12-02 改成无预报时判断是否
			// 入库订单是否直有一个明细.如果只有一个明细,并且无sku的情况,视为薄库存.把操作员扫描的sku更新到预报中
			InWarehouseOrderItem orderItemParam2 = new InWarehouseOrderItem();
			orderItemParam2.setOrderId(orderId);
			List<InWarehouseOrderItem> orderItems = inWarehouseOrderItemDao.findInWarehouseOrderItem(orderItemParam2, null, null);
			if (orderItems.size() == 1 && StringUtil.isNull(orderItems.get(0).getSku())) {
				// isConfirm = 'N' 表示不确认是否绑定,弹出询问框
				if (StringUtil.isEqual(isConfirm, Constant.N)) {
					map.put(Constant.STATUS, "2");// 该订单是薄库存情况,你确定将此SKU绑定到商品吗?
					return map;
				}
				// 薄库存,把sku更新到物品明细记录
				long updateCount = inWarehouseOrderItemDao.saveInWarehouseOrderItemSku(orderItems.get(0).getId(), itemSku);
				inWarehouseOrderItemList = inWarehouseOrderItemDao.findInWarehouseOrderItem(inWarehouseOrderItemParam, null, null);
			} else {
				map.put(Constant.MESSAGE, "该商品条码在此订单中无预报,且不符合薄库存情况,请在下面列表补齐商品条码");
				return map;
			}
		}
		InWarehouseOrderItem orderItem = inWarehouseOrderItemList.get(0);
		map.put(Constant.STATUS, Constant.SUCCESS);
		// 查询入库主单信息,用于更新库存
		InWarehouseRecord inWarehouseRecord = inWarehouseRecordDao.getInWarehouseRecordById(inWarehouseRecordId);
		Long userIdOfCustomer = inWarehouseRecord.getUserIdOfCustomer();
		// 检查该SKU是否已经存在,已经存在则直接改变数量(同一个入库主单,同一个SKU只允许一个收货明细)
		InWarehouseRecordItem param = new InWarehouseRecordItem();
		param.setInWarehouseRecordId(inWarehouseRecordId);
		param.setSku(itemSku);
		List<InWarehouseRecordItem> inWarehouseRecordItemList = inWarehouseRecordItemDao.findInWarehouseRecordItem(param, null, null);
		if (inWarehouseRecordItemList.size() > 0) {
			// 返回入库主单的id
			Long recordItemId = inWarehouseRecordItemList.get(0).getId();
			map.put("id", "" + recordItemId);
			int newQuantity = inWarehouseRecordItemList.get(0).getQuantity() + itemQuantity;
			int updateCount = inWarehouseRecordItemDao.updateInWarehouseRecordItemReceivedQuantity(recordItemId, newQuantity);
			// 更新入库明细成功,则添加库存
			if (updateCount > 0) {
				itemInventoryDao.addItemInventory(warehouseId, userIdOfCustomer, inWarehouseRecord.getBatchNo(), itemSku, itemQuantity);
			}
			return map;
		}
		InWarehouseRecordItem inWarehouseRecordItem = new InWarehouseRecordItem();
		inWarehouseRecordItem.setCreatedTime(System.currentTimeMillis());
		inWarehouseRecordItem.setInWarehouseRecordId(inWarehouseRecordId);
		inWarehouseRecordItem.setQuantity(itemQuantity);
		inWarehouseRecordItem.setRemark(itemRemark);
		inWarehouseRecordItem.setSku(itemSku);
		inWarehouseRecordItem.setSkuNo(orderItem.getSkuNo());
		inWarehouseRecordItem.setUserIdOfOperator(userIdOfOperator);
		// 返回id
		long id = inWarehouseRecordItemDao.saveInWarehouseRecordItem(inWarehouseRecordItem);
		map.put("id", "" + id);
		// 保存成功,添加库存
		if (id > 0) {
			itemInventoryDao.addItemInventory(warehouseId, userIdOfCustomer, inWarehouseRecord.getBatchNo(), itemSku, itemQuantity);
		}
		// 入库订单物品加入商品库 --------------开始
		Product productParam = new Product();
		productParam.setBarcode(itemSku);// 根据商品条码查询产品库, 同一个客户下的商品条码不能重复
		productParam.setUserIdOfCustomer(userIdOfCustomer);
		Long countProduct = productDao.countProduct(productParam, null);
		if (countProduct > 0) {
			return map;
		}
		// sku未存在,新增
		Product product = new Product();
		product.setCreatedTime(System.currentTimeMillis());
		product.setCurrency(CurrencyCode.CNY);
		product.setModel(orderItem.getSpecification());
		product.setIsNeedBatchNo(Constant.N);
		product.setProductName(orderItem.getSkuName());
		product.setWarehouseSku(orderItem.getSkuNo());
		product.setSku(orderItem.getSkuNo());
		product.setBarcode(itemSku);
		product.setUserIdOfCustomer(userIdOfCustomer);
		productDao.addProduct(product);
		// 入库订单物品加入商品库 --------------结束
		return map;
	}

	/**
	 * 查找入库订单
	 */
	@Override
	public List<InWarehouseOrder> findInWarehouseOrder(InWarehouseOrder inWarehouseOrder, Map<String, String> moreParam, Pagination page) {
		List<InWarehouseOrder> inWarehouseOrderList = inWarehouseOrderDao.findInWarehouseOrder(inWarehouseOrder, moreParam, page);
		return inWarehouseOrderList;
	}

	/***
	 * 根据入库订单 找用户id
	 */
	@Override
	public List<User> findUserByInWarehouseOrder(List<InWarehouseOrder> inWarehouseOrderList) {
		List<User> userList = new ArrayList<User>();
		for (InWarehouseOrder inWarehouseOrder : inWarehouseOrderList) {
			if (inWarehouseOrder.getUserIdOfCustomer() == null) {
				continue;
			}
			boolean bool = true;
			for (User user : userList) {
				if (user.getId() == inWarehouseOrder.getUserIdOfCustomer()) {
					bool = false;
					break;
				}
			}
			if (bool) {
				User user = userDao.getUserById(inWarehouseOrder.getUserIdOfCustomer());
				userList.add(user);
			}
		}
		return userList;
	}

	/**
	 * 
	 */
	@Override
	public Map<String, Object> warehouseInterfaceEventType(String logisticsInterface, Long userIdOfCustomer, String dataDigest, String msgType, String msgId, String version) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constant.STATUS, Constant.FAIL);

		Responses responses = new Responses();
		List<Response> responseItems = new ArrayList<Response>();
		Response response = new Response();
		response.setSuccess(Constant.FALSE);
		responseItems.add(response);
		responses.setResponseItems(responseItems);

		// logisticsInterface 转请求对象
		LogisticsEventsRequest logisticsEventsRequest = (LogisticsEventsRequest) XmlUtil.toObject(logisticsInterface, LogisticsEventsRequest.class);
		if (logisticsEventsRequest == null) {
			// xml 转对象得到空
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("logisticsInterface消息内容转LogisticsEventsRequest对象得到Null");
			map.put(Constant.MESSAGE, XmlUtil.toXml(responses));
			return map;
		}
		LogisticsEvent logisticsEvent = logisticsEventsRequest.getLogisticsEvent();
		if (logisticsEvent == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("LogisticsEventsRequest对象获取LogisticsEvent对象得到Null");

			map.put(Constant.MESSAGE, XmlUtil.toXml(responses));
			return map;
		}
		// 事件头
		EventHeader eventHeader = logisticsEvent.getEventHeader();
		if (eventHeader == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("LogisticsEvent对象获取EventHeader对象得到Null");

			map.put(Constant.MESSAGE, XmlUtil.toXml(responses));
			return map;
		}
		// 得到事件类型,根据事件类型,分发事件Body 到不同方法处理
		String eventType = eventHeader.getEventType();
		// 事件目标,仓库编码
		String eventTarget = eventHeader.getEventTarget();
		if (StringUtil.isNull(eventType)) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("EventHeader对象获取eventType得到Null");
			map.put(Constant.MESSAGE, XmlUtil.toXml(responses));
			return map;
		}

		// 事件Body
		EventBody eventBody = logisticsEvent.getEventBody();
		if (eventBody == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("LogisticsEvent对象获取EventBody对象得到Null");
			map.put(Constant.MESSAGE, XmlUtil.toXml(responses));
			return map;
		}
		// 成功得到事件类型,返回body
		map.put(Constant.STATUS, Constant.SUCCESS);
		map.put("eventType", eventType);
		map.put("eventTarget", eventTarget);
		map.put("eventBody", eventBody);
		return map;
	}

	@Override
	public Map<String, String> warehouseInterfaceValidate(String logisticsInterface, String msgSource, String dataDigest, String msgType, String msgId, String version) {
		Map<String, String> map = new HashMap<String, String>();
		Responses responses = new Responses();
		List<Response> responseItems = new ArrayList<Response>();
		Response response = new Response();
		response.setSuccess(Constant.FALSE);
		responseItems.add(response);
		responses.setResponseItems(responseItems);

		map.put(Constant.STATUS, Constant.FAIL);
		// 缺少关键字段
		if (StringUtil.isNull(logisticsInterface) || StringUtil.isNull(msgSource) || StringUtil.isNull(dataDigest) || StringUtil.isNull(msgType) || StringUtil.isNull(msgId)) {
			response.setReason(ErrorCode.S12_CODE);
			response.setReasonDesc("缺少关键字段,请检查以下字段:logistics_interface,data_digest,msg_type,msg_id");
			map.put(Constant.MESSAGE, XmlUtil.toXml(responses));
			return map;
		}

		// 根据msgSource 找到客户(token),找到密钥
		User user = userDao.findUserByMsgSource(msgSource);
		if (user == null) {
			response.setReason(ErrorCode.B0008_CODE);
			response.setReasonDesc("根据msg_source 找不到客户");
			map.put(Constant.MESSAGE, XmlUtil.toXml(responses));
			return map;
		}

		// 验证内容和签名字符串
		String md5dataDigest = StringUtil.encoderByMd5(logisticsInterface + user.getToken());
		if (!StringUtil.isEqual(md5dataDigest, dataDigest)) {
			// 签名错误
			response.setReason(ErrorCode.S02_CODE);
			response.setReasonDesc("收到消息签名:" + dataDigest + " 系统计算消息签名:" + md5dataDigest);
			map.put(Constant.MESSAGE, XmlUtil.toXml(responses));
			return map;
		}
		map.put(Constant.STATUS, Constant.SUCCESS);
		map.put(Constant.USER_ID_OF_CUSTOMER, "" + user.getId());
		return map;
	}

	/**
	 * 保存入库记录主单
	 */
	@Override
	public Map<String, String> saveInWarehouseRecord(String trackingNo, String remark, Long userIdOfOperator, Long warehouseId, Long inWarehouseOrderId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.STATUS, Constant.FAIL);
		if (StringUtil.isNull(trackingNo)) {
			map.put(Constant.MESSAGE, "请输入跟踪单号.");
			return map;
		}

		Long userIdOfCustomer = inWarehouseOrderDao.getUserIdByInWarehouseOrderId(inWarehouseOrderId);
		InWarehouseRecord inWarehouseRecord = new InWarehouseRecord();
		inWarehouseRecord.setCreatedTime(System.currentTimeMillis());
		inWarehouseRecord.setTrackingNo(trackingNo);
		inWarehouseRecord.setUserIdOfCustomer(userIdOfCustomer);
		inWarehouseRecord.setInWarehouseOrderId(inWarehouseOrderId);
		inWarehouseRecord.setRemark(remark);
		inWarehouseRecord.setUserIdOfOperator(userIdOfOperator);
		// 2014-12-23修改成新入库状态,界面未提交保存,不能发送入库给顺丰
		inWarehouseRecord.setStatus(InWarehouseRecordStatusCode.NEW);
		// 创建批次号
		String batchNo = InWarehouseRecord.generateBatchNo(null, null, Constant.SYMBOL_UNDERLINE, trackingNo, null, null);
		inWarehouseRecord.setBatchNo(batchNo);
		inWarehouseRecord.setWarehouseId(warehouseId);
		// 返回id
		long id = inWarehouseRecordDao.saveInWarehouseRecord(inWarehouseRecord);
		map.put("id", "" + id);
		map.put(Constant.STATUS, Constant.SUCCESS);
		return map;
	}

	/**
	 * 获取入库记录明细数据
	 */
	@Override
	public Pagination getInWarehouseRecordItemData(Long inWarehouseRecordId, Pagination pagination) {
		InWarehouseRecord inWarehouseRecord = inWarehouseRecordDao.getInWarehouseRecordById(inWarehouseRecordId);
		Long inWarehouseOrderId = inWarehouseRecord.getInWarehouseOrderId();
		Long warehouseId = inWarehouseRecord.getWarehouseId();

		InWarehouseOrderItem param = new InWarehouseOrderItem();
		param.setOrderId(inWarehouseOrderId);
		List<InWarehouseOrderItem> orderItemList = inWarehouseOrderItemDao.findInWarehouseOrderItem(param, null, null);
		List<Object> list = new ArrayList<Object>();
		for (InWarehouseOrderItem orderItem : orderItemList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("orderItemId", orderItem.getId());
			map.put("sku", orderItem.getSku());
			map.put("skuNo", orderItem.getSkuNo());
			map.put("totalQuantity", orderItem.getQuantity());
			int totalReceivedQuantity = inWarehouseRecordItemDao.countInWarehouseItemSkuQuantityByOrderId(inWarehouseOrderId, orderItem.getSku());
			map.put("totalReceivedQuantity", totalReceivedQuantity);
			int unReceivedquantity = orderItem.getQuantity() - totalReceivedQuantity;
			map.put("unReceivedquantity", unReceivedquantity);
			// 根据SKU查询收货记录的物品明细
			InWarehouseRecordItem inWarehouseRecordItemParam = new InWarehouseRecordItem();
			inWarehouseRecordItemParam.setInWarehouseRecordId(inWarehouseRecordId);
			inWarehouseRecordItemParam.setSku(orderItem.getSku());
			List<InWarehouseRecordItem> recordItemList = inWarehouseRecordItemDao.findInWarehouseRecordItem(inWarehouseRecordItemParam, null, null);
			if (recordItemList != null && recordItemList.size() > 0) {
				InWarehouseRecordItem recordItem = recordItemList.get(0);
				if (recordItem.getCreatedTime() != null) {
					map.put("createdTime", DateUtil.dateConvertString(new Date(recordItem.getCreatedTime()), DateUtil.yyyy_MM_ddHHmmss));
				}
				map.put("receivedQuantity", recordItem.getQuantity());
				map.put("remark", recordItem.getRemark() == null ? "" : recordItem.getRemark());
				if (NumberUtil.greaterThanZero(recordItem.getUserIdOfOperator())) {
					User user = userDao.getUserById(recordItem.getUserIdOfOperator());
					map.put("userLoginNameOfOperator", user.getLoginName());
				}
				Warehouse warehouse = warehouseDao.getWarehouseById(warehouseId);
				if (warehouse != null) {
					map.put("warehouse", warehouse.getWarehouseName());
				}
			}
			list.add(map);
		}
		pagination.total = inWarehouseOrderItemDao.countInWarehouseOrderItem(param, null);
		pagination.rows = list;
		return pagination;
	}

	/**
	 * 获取出库订单数据
	 */
	@Override
	public Pagination getInWarehouseOrderData(InWarehouseOrder inWarehouseOrder, Map<String, String> moreParam, Pagination pagination) {
		List<InWarehouseOrder> inWarehouseOrderList = inWarehouseOrderDao.findInWarehouseOrder(inWarehouseOrder, moreParam, pagination);
		List<Object> list = new ArrayList<Object>();
		for (InWarehouseOrder order : inWarehouseOrderList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", order.getId());
			if (order.getCreatedTime() != null) {
				map.put("createdTime", DateUtil.dateConvertString(new Date(order.getCreatedTime()), DateUtil.yyyy_MM_ddHHmmss));
			}
			// 查询用户名
			User user = userDao.getUserById(order.getUserIdOfCustomer());
			map.put("userNameOfCustomer", user.getLoginName());
			map.put("trackingNo", order.getTrackingNo());
			map.put("customerReferenceNo", order.getCustomerReferenceNo());
			// 承运商
			map.put("carrierCode", order.getCarrierCode());
			if (order.getWeight() != null) {
				map.put("weight", Weight.gTurnToKg(order.getWeight()));
			}

			if (order.getWarehouseId() != null) {
				Warehouse warehouse = warehouseDao.getWarehouseById(order.getWarehouseId());
				if (warehouse != null) {
					map.put("warehouse", warehouse.getWarehouseName());
				}
			}
			map.put("remark", order.getRemark());
			InWarehouseOrderStatus inWarehouseOrderStatus = inWarehouseOrderStatusDao.findInWarehouseOrderStatusByCode(order.getStatus());
			if (inWarehouseOrderStatus != null) {
				map.put("status", inWarehouseOrderStatus.getCn());
			}
			InWarehouseOrderItem param = new InWarehouseOrderItem();
			param.setOrderId(order.getId());
			List<InWarehouseOrderItem> inWarehouseOrderItemList = inWarehouseOrderItemDao.findInWarehouseOrderItem(param, null, null);
			String skus = "";
			for (InWarehouseOrderItem item : inWarehouseOrderItemList) {
				skus += item.getSku() + "*" + item.getQuantity() + " ";
			}
			map.put("skus", skus);
			list.add(map);
		}
		pagination.total = inWarehouseOrderDao.countInWarehouseOrder(inWarehouseOrder, moreParam);
		pagination.rows = list;
		return pagination;
	}

	/**
	 * 获取出库订单列表数据
	 */
	@Override
	public Pagination getOutWarehouseOrderData(OutWarehouseOrder outWarehouseOrder, Map<String, String> moreParam, Pagination pagination) {
		List<OutWarehouseOrder> outWarehouseOrderList = outWarehouseOrderDao.findOutWarehouseOrder(outWarehouseOrder, moreParam, pagination);
		List<Object> list = new ArrayList<Object>();
		for (OutWarehouseOrder order : outWarehouseOrderList) {
			Map<String, Object> map = new HashMap<String, Object>();
			Long outWarehouseOrderId = order.getId();
			map.put("id", order.getId());
			if (order.getCreatedTime() != null) {
				map.put("createdTime", DateUtil.dateConvertString(new Date(order.getCreatedTime()), DateUtil.yyyy_MM_ddHHmmss));
			}
			map.put("shipwayCode", order.getShipwayCode());
			map.put("trackingNo", order.getTrackingNo());
			// 回传称重
			if (StringUtil.isEqual(order.getCallbackSendWeightIsSuccess(), Constant.Y)) {
				map.put("callbackSendWeightIsSuccess", "成功");
			} else {
				if (order.getCallbackSendWeighCount() != null && order.getCallbackSendWeighCount() > 0) {
					map.put("callbackSendWeightIsSuccess", "失败次数:" + order.getCallbackSendWeighCount());
				} else {
					map.put("callbackSendWeightIsSuccess", "未回传");
				}
			}

			// 回传出库
			if (StringUtil.isEqual(order.getCallbackSendStatusIsSuccess(), Constant.Y)) {
				map.put("callbackSendStatusIsSuccess", "成功");
			} else {
				if (order.getCallbackSendStatusCount() != null && order.getCallbackSendStatusCount() > 0) {
					map.put("callbackSendStatusIsSuccess", "失败次数:" + order.getCallbackSendStatusCount());
				} else {
					map.put("callbackSendStatusIsSuccess", "未回传");
				}
			}

			// 查询用户名
			User user = userDao.getUserById(order.getUserIdOfCustomer());
			map.put("userNameOfCustomer", user.getLoginName());
			map.put("customerReferenceNo", order.getCustomerReferenceNo());
			if (NumberUtil.greaterThanZero(order.getWarehouseId())) {
				Warehouse warehouse = warehouseDao.getWarehouseById(order.getWarehouseId());
				if (warehouse != null) {
					map.put("warehouse", warehouse.getWarehouseName());
				}
			}
			map.put("remark", order.getRemark());
			map.put("printedCount", order.getPrintedCount());
			OutWarehouseOrderStatus outWarehouseOrderStatus = outWarehouseOrderStatusDao.findOutWarehouseOrderStatusByCode(order.getStatus());
			if (outWarehouseOrderStatus != null) {
				map.put("status", outWarehouseOrderStatus.getCn());
			}
			// 收件人信息
			OutWarehouseOrderReceiver outWarehouseOrderReceiver = outWarehouseOrderReceiverDao.getOutWarehouseOrderReceiverByOrderId(outWarehouseOrderId);
			if (outWarehouseOrderReceiver != null) {
				map.put("receiverAddressLine1", outWarehouseOrderReceiver.getAddressLine1());
				map.put("receiverAddressLine2", outWarehouseOrderReceiver.getAddressLine2());
				map.put("receiverCity", outWarehouseOrderReceiver.getCity());
				map.put("receiverCompany", outWarehouseOrderReceiver.getCompany());
				map.put("receiverCountryCode", outWarehouseOrderReceiver.getCountryCode());
				map.put("receiverCountryName", outWarehouseOrderReceiver.getCountryName());
				map.put("receiverCounty", outWarehouseOrderReceiver.getCounty());
				map.put("receiverEmail", outWarehouseOrderReceiver.getEmail());
				map.put("receiverFirstName", outWarehouseOrderReceiver.getFirstName());
				map.put("receiverLastName", outWarehouseOrderReceiver.getLastName());
				map.put("receiverMobileNumber", outWarehouseOrderReceiver.getMobileNumber());
				map.put("receiverName", outWarehouseOrderReceiver.getName());
				map.put("receiverPhoneNumber", outWarehouseOrderReceiver.getPhoneNumber());
				map.put("receiverPostalCode", outWarehouseOrderReceiver.getPostalCode());
				map.put("receiverStateOrProvince", outWarehouseOrderReceiver.getStateOrProvince());
			}
			// 发件人
			OutWarehouseOrderSender outWarehouseOrderSender = outWarehouseOrderSenderDao.getOutWarehouseOrderSenderByOrderId(outWarehouseOrderId);
			if (outWarehouseOrderSender != null) {
				map.put("senderName", outWarehouseOrderSender.getName());
			}
			// 物品明细(目前仅展示SKU*数量)
			String itemStr = "";
			OutWarehouseOrderItem outWarehouseOrderItemParam = new OutWarehouseOrderItem();
			outWarehouseOrderItemParam.setOutWarehouseOrderId(outWarehouseOrderId);
			List<OutWarehouseOrderItem> outWarehouseOrderItemList = outWarehouseOrderItemDao.findOutWarehouseOrderItem(outWarehouseOrderItemParam, null, null);
			for (OutWarehouseOrderItem outWarehouseOrderItem : outWarehouseOrderItemList) {
				itemStr += outWarehouseOrderItem.getSku() + "*" + outWarehouseOrderItem.getQuantity() + " ";
			}
			map.put("items", itemStr);
			list.add(map);
		}
		pagination.total = outWarehouseOrderDao.countOutWarehouseOrder(outWarehouseOrder, moreParam);
		pagination.rows = list;
		return pagination;
	}

	/**
	 * api 创建入库订单
	 */
	@Override
	public String warehouseInterfaceSaveInWarehouseOrder(EventBody eventBody, Long userIdOfCustomer, String warehouseNo) throws ServiceException {
		Responses responses = new Responses();
		List<Response> responseItems = new ArrayList<Response>();
		Response response = new Response();
		response.setSuccess(Constant.FALSE);
		responseItems.add(response);
		responses.setResponseItems(responseItems);
		LogisticsDetail logisticsDetail = eventBody.getLogisticsDetail();

		if (logisticsDetail == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("EventBody对象获取LogisticsDetail对象得到Null");
			return XmlUtil.toXml(responses);
		}
		List<LogisticsOrder> logisticsOrders = logisticsDetail.getLogisticsOrders();
		if (logisticsOrders == null || logisticsOrders.size() == 0) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("LogisticsDetail对象获取logisticsOrders对象得到Null");
			return XmlUtil.toXml(responses);
		}

		Warehouse warehouse = warehouseDao.getWarehouseByNo(warehouseNo);
		if (warehouse == null) {
			response.setReason(ErrorCode.B0003_CODE);
			response.setReasonDesc("根据仓库编号(eventTarget)获取仓库得到Null");
			return XmlUtil.toXml(responses);
		}
		if (logisticsOrders.size() > 1) {
			throw new ServiceException("每次仅能处理一条订单入库,此次请求处理失败");
		}
		// 开始入库
		LogisticsOrder logisticsOrder = logisticsOrders.get(0);
		logger.info("正在入库:跟踪单号(mailNo):" + logisticsOrder.getMailNo());
		if (StringUtil.isNull(logisticsOrder.getMailNo())) {
			response.setReason(ErrorCode.S13_CODE);
			response.setReasonDesc("跟踪单号(mailNo)为空,订单入库失败");
			return XmlUtil.toXml(responses);
		}
		if (StringUtil.isNull(logisticsOrder.getSkuStockInId())) {
			response.setReason(ErrorCode.S13_CODE);
			response.setReasonDesc("客户订单号(skuStockInId)为空,订单入库失败");
			return XmlUtil.toXml(responses);
		}
		// 判断是否已经存在相同的跟踪单号和承运商(目前仅判断相同的跟踪单号就不可以入库)
		InWarehouseOrder param = new InWarehouseOrder();
		param.setTrackingNo(logisticsOrder.getMailNo());
		param.setCustomerReferenceNo(logisticsOrder.getSkuStockInId());
		Long validate = inWarehouseOrderDao.countInWarehouseOrder(param, null);
		if (validate >= 1) {
			response.setReason(ErrorCode.B0200_CODE);
			response.setReasonDesc("跟踪单号:" + logisticsOrder.getMailNo() + "和客户订单号(skuStockInId):" + logisticsOrder.getSkuStockInId() + "重复,订单入库失败");
			return XmlUtil.toXml(responses);
		}
		// pojo 转 model
		InWarehouseOrder inWarehouseOrder = new InWarehouseOrder();
		inWarehouseOrder.setCreatedTime(System.currentTimeMillis());
		inWarehouseOrder.setTrackingNo(logisticsOrder.getMailNo());
		inWarehouseOrder.setCustomerReferenceNo(logisticsOrder.getSkuStockInId());
		inWarehouseOrder.setUserIdOfCustomer(userIdOfCustomer);
		// 已预报,未入库
		inWarehouseOrder.setStatus(InWarehouseOrderStatusCode.NONE);
		inWarehouseOrder.setCarrierCode(logisticsOrder.getCarrierCode());
		inWarehouseOrder.setLogisticsType(logisticsOrder.getLogisticsType());
		inWarehouseOrder.setWarehouseId(warehouse.getId());
		// sku明细
		SkuDetail skuDetail = logisticsOrder.getSkuDetail();
		if (skuDetail == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("LogisticsOrder对象获取SkuDetail对象得到Null");
			return XmlUtil.toXml(responses);
		}
		List<Sku> skuList = skuDetail.getSkus();
		if (skuList == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("SkuDetail对象获取Skus对象得到Null");
			return XmlUtil.toXml(responses);
		}
		List<InWarehouseOrderItem> inwarehouseOrderItemList = new ArrayList<InWarehouseOrderItem>();
		for (int j = 0; j < skuList.size(); j++) {
			Sku sku = skuList.get(j);
			if (sku.getSkuQty() == null || (StringUtil.isNull(sku.getSkuCode()) && StringUtil.isNull(sku.getSkuId()))) {
				continue;
			}
			InWarehouseOrderItem inwarehouseOrderItem = new InWarehouseOrderItem();
			inwarehouseOrderItem.setSku(sku.getSkuCode());// 商品条码
			inwarehouseOrderItem.setSkuNo(sku.getSkuId());// 商品条码
			inwarehouseOrderItem.setQuantity(sku.getSkuQty());
			inwarehouseOrderItem.setSkuName(sku.getSkuName());
			inwarehouseOrderItem.setSkuRemark(sku.getSkuRemark());
			inwarehouseOrderItem.setSpecification(sku.getSpecification());
			// 入库主单的id
			inwarehouseOrderItemList.add(inwarehouseOrderItem);
		}
		// 保存入库订单得到入库订单id
		Long orderId = inWarehouseOrderDao.saveInWarehouseOrder(inWarehouseOrder);
		int count = inWarehouseOrderItemDao.saveBatchInWarehouseOrderItemWithOrderId(inwarehouseOrderItemList, orderId);
		logger.info("入库主单id:" + orderId + " 入库明细数量:" + count);
		response.setSuccess(Constant.TRUE);
		return XmlUtil.toXml(responses);
	}

	/**
	 * API创建出库订单
	 */
	@Override
	public String warehouseInterfaceSaveOutWarehouseOrder(EventBody eventBody, Long userIdOfCustomer, String warehouseNo) throws ServiceException {
		Responses responses = new Responses();
		List<Response> responseItems = new ArrayList<Response>();
		Response response = new Response();
		response.setSuccess(Constant.FALSE);
		responseItems.add(response);
		responses.setResponseItems(responseItems);

		// 取 tradeDetail 中tradeOrderId 作为客户订单号
		TradeDetail tradeDetail = eventBody.getTradeDetail();
		if (tradeDetail == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("EventBody对象获取TradeDetail对象得到Null");
			return XmlUtil.toXml(responses);
		}
		List<TradeOrder> tradeOrderList = tradeDetail.getTradeOrders();
		if (tradeOrderList == null || tradeOrderList.size() == 0) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("TradeDetail对象获取TradeOrders对象得到Null");
			return XmlUtil.toXml(responses);
		}
		TradeOrder tradeOrder = tradeOrderList.get(0);
		// 客户订单号
		String customerReferenceNo = tradeOrder.getTradeOrderId();
		if (StringUtil.isNull(customerReferenceNo)) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("TradeOrder对象获取tradeOrderId得到Null");
			return XmlUtil.toXml(responses);
		}
		// 交易备注,等于打印捡货单上的买家备注
		String tradeRemark = tradeOrder.getTradeRemark();
		Buyer buyer = tradeOrder.getBuyer();
		if (buyer == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("TradeOrder对象获取Buyer对象得到Null");
			return XmlUtil.toXml(responses);
		}

		// 出库订单发件人信息
		LogisticsDetail logisticsDetail = eventBody.getLogisticsDetail();
		if (logisticsDetail == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("EventBody对象获取LogisticsDetail对象得到Null");
			return XmlUtil.toXml(responses);
		}
		List<LogisticsOrder> logisticsOrders = logisticsDetail.getLogisticsOrders();
		if (logisticsOrders == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("LogisticsDetail对象获取LogisticsOrders对象得到Null");
			return XmlUtil.toXml(responses);
		}
		Warehouse warehouse = warehouseDao.getWarehouseByNo(warehouseNo);
		if (warehouse == null) {
			response.setReason(ErrorCode.B0003_CODE);
			response.setReasonDesc("根据仓库编号(eventTarget)获取仓库得到Null");
			return XmlUtil.toXml(responses);
		}
		// 保存
		for (int i = 0; i < logisticsOrders.size(); i++) {
			LogisticsOrder logisticsOrder = logisticsOrders.get(i);
			if (logisticsOrder == null) {
				throw new ServiceException("LogisticsOrders对象获取LogisticsOrder对象得到Null");
			}
			ReceiverDetail receiverDetail = logisticsOrder.getReceiverDetail();
			if (receiverDetail == null) {
				throw new ServiceException("LogisticsOrder对象获取ReceiverDetail对象得到Null");
			}
			SenderDetail senderDetail = logisticsOrder.getSenderDetail();
			if (senderDetail == null) {
				throw new ServiceException("LogisticsOrder对象获取SenderDetail对象得到Null");
			}
			SkuDetail skuDetail = logisticsOrder.getSkuDetail();
			if (skuDetail == null) {
				throw new ServiceException("LogisticsOrder对象获取SkuDetail对象得到Null");
			}

			List<Sku> skus = skuDetail.getSkus();
			if (skus == null) {
				throw new ServiceException("SkuDetail对象获取List<Sku>对象得到Null");
			}
			logger.info("出库订单:第" + (i + 1) + "(customerReferenceNo):" + customerReferenceNo);
			// 检查客户订单号是否重复
			OutWarehouseOrder outWarehouseOrderParam = new OutWarehouseOrder();
			outWarehouseOrderParam.setCustomerReferenceNo(customerReferenceNo);
			Long count = outWarehouseOrderDao.countOutWarehouseOrder(outWarehouseOrderParam, null);
			if (count > 0) {
				response.setReason(ErrorCode.B0200_CODE);
				response.setReasonDesc("客户订单号(tradeOrderId)重复,保存失败");
				return XmlUtil.toXml(responses);
			}
			// 主单
			OutWarehouseOrder outWarehouseOrder = new OutWarehouseOrder();
			outWarehouseOrder.setCreatedTime(System.currentTimeMillis());
			outWarehouseOrder.setLogisticsRemark(logisticsOrder.getLogisticsRemark());
			outWarehouseOrder.setStatus(OutWarehouseOrderStatusCode.WWC);
			outWarehouseOrder.setUserIdOfCustomer(userIdOfCustomer);
			outWarehouseOrder.setWarehouseId(warehouse.getId());
			outWarehouseOrder.setTradeRemark(tradeRemark);
			ClearanceDetail clearanceDetail = eventBody.getClearanceDetail();
			if (clearanceDetail != null) {
				// 顺丰指定,出货运单号和渠道
				outWarehouseOrder.setTrackingNo(clearanceDetail.getMailNo());
				outWarehouseOrder.setShipwayCode(clearanceDetail.getCarrierCode());
			}
			// 客户订单号,用于后面客户对该出库订单进行修改,确认等,以及回传出库状态
			outWarehouseOrder.setCustomerReferenceNo(customerReferenceNo);
			// 保存主单 得到主单Id
			Long outWarehouseOrderId = outWarehouseOrderDao.saveOutWarehouseOrder(outWarehouseOrder);

			logger.info("出库订单:第" + (i + 1) + "客户订单号customerReferenceNo(tradeOrderId):" + customerReferenceNo + " 保存出库订单,得到Id:" + outWarehouseOrderId);

			// 出库订单明细信息
			List<OutWarehouseOrderItem> itemList = new ArrayList<OutWarehouseOrderItem>();
			for (int j = 0; j < skus.size(); j++) {
				Sku sku = skus.get(j);
				if (sku == null) {
					throw new ServiceException("SkuDetail对象获取Sku对象得到Null");
				}
				OutWarehouseOrderItem outWarehouseOrderItem = new OutWarehouseOrderItem();
				outWarehouseOrderItem.setQuantity(sku.getSkuQty());
				outWarehouseOrderItem.setRemark(sku.getSkuRemark());
				outWarehouseOrderItem.setSku(sku.getSkuCode());
				outWarehouseOrderItem.setSkuNo(sku.getSkuId());
				outWarehouseOrderItem.setSpecification(sku.getSpecification());
				outWarehouseOrderItem.setSkuName(sku.getSkuName());
				outWarehouseOrderItem.setSkuUnitPrice(sku.getSkuUnitPrice());
				outWarehouseOrderItem.setSkuPriceCurrency(sku.getSkuPriceCurrency());
				outWarehouseOrderItem.setSkuNetWeight(sku.getSkuNetWeight());
				outWarehouseOrderItem.setOutWarehouseOrderId(outWarehouseOrderId);
				itemList.add(outWarehouseOrderItem);

			}
			// 保存出库订单明细
			long itemCount = outWarehouseOrderItemDao.saveBatchOutWarehouseOrderItemWithOrderId(itemList, outWarehouseOrderId);
			logger.info("出库订单:第" + (i + 1) + "客户订单号customerReferenceNo(tradeOrderId):" + customerReferenceNo + " 保存出库订单明细条数:" + itemCount);
			// 收件人

			OutWarehouseOrderReceiver outWarehouseOrderReceiver = new OutWarehouseOrderReceiver();
			outWarehouseOrderReceiver.setAddressLine1(buyer.getStreetAddress());
			outWarehouseOrderReceiver.setCity(buyer.getCity());
			outWarehouseOrderReceiver.setCountryCode(OutWarehouseOrderReceiver.CN);
			outWarehouseOrderReceiver.setCountryName(OutWarehouseOrderReceiver.CN_VALUE);
			outWarehouseOrderReceiver.setCounty(buyer.getDistrict());
			outWarehouseOrderReceiver.setEmail(buyer.getEmail());
			outWarehouseOrderReceiver.setName(buyer.getName());
			outWarehouseOrderReceiver.setPhoneNumber(buyer.getPhone());
			outWarehouseOrderReceiver.setPostalCode(buyer.getZipCode());
			outWarehouseOrderReceiver.setStateOrProvince(buyer.getProvince());
			outWarehouseOrderReceiver.setMobileNumber(buyer.getMobile());
			outWarehouseOrderReceiver.setOutWarehouseOrderId(outWarehouseOrderId);
			// 保存收件人
			Long outWarehouseOrderReceiverId = outWarehouseOrderReceiverDao.saveOutWarehouseOrderReceiver(outWarehouseOrderReceiver);
			logger.info("出库订单:第" + (i + 1) + "客户订单号customerReferenceNo(tradeOrderId):" + customerReferenceNo + " 保存收件人,outWarehouseOrderReceiverId:" + outWarehouseOrderReceiverId);
			// 顺丰标签附加内容
			if (clearanceDetail != null) {
				OutWarehouseOrderAdditionalSf additionalSf = new OutWarehouseOrderAdditionalSf();
				additionalSf.setCarrierCode(clearanceDetail.getCarrierCode());
				additionalSf.setCustId(clearanceDetail.getCustId());
				additionalSf.setDeliveryCode(clearanceDetail.getDeliveryCode());
				additionalSf.setMailNo(clearanceDetail.getMailNo());
				additionalSf.setOrderId(clearanceDetail.getOrderId());
				additionalSf.setOutWarehouseOrderId(outWarehouseOrderId);
				additionalSf.setPayMethod(clearanceDetail.getPayMethod());
				additionalSf.setSenderAddress(clearanceDetail.getSenderAddress());
				additionalSf.setShipperCode(clearanceDetail.getShipperCode());
				Long outWarehouseOrderAdditionalSfId = outWarehouseOrderAdditionalSfDao.saveOutWarehouseOrderAdditionalSf(additionalSf);
				logger.info("出库订单:第" + (i + 1) + "客户订单号customerReferenceNo(tradeOrderId):" + customerReferenceNo + " 保存顺丰标签附近内容人,outWarehouseOrderAdditionalSfId:" + outWarehouseOrderAdditionalSfId);
			}
			// 发件人信息
			OutWarehouseOrderSender outWarehouseOrderSender = new OutWarehouseOrderSender();
			outWarehouseOrderSender.setAddressLine1(senderDetail.getStreetAddress());
			outWarehouseOrderSender.setCity(senderDetail.getCity());
			outWarehouseOrderSender.setCountryCode(senderDetail.getCountry());
			outWarehouseOrderSender.setCountryName(senderDetail.getCountry());
			outWarehouseOrderSender.setCounty(senderDetail.getDistrict());
			outWarehouseOrderSender.setEmail(senderDetail.getEmail());
			outWarehouseOrderSender.setName(senderDetail.getName());
			outWarehouseOrderSender.setPhoneNumber(senderDetail.getPhone());
			outWarehouseOrderSender.setPostalCode(senderDetail.getZipCode());
			outWarehouseOrderSender.setStateOrProvince(senderDetail.getProvince());
			outWarehouseOrderSender.setMobileNumber(senderDetail.getMobile());
			outWarehouseOrderSender.setOutWarehouseOrderId(outWarehouseOrderId);
			// 保存发件人
			Long outWarehouseOrderSenderId = outWarehouseOrderSenderDao.saveOutWarehouseOrderSender(outWarehouseOrderSender);
			logger.info("出库订单:第" + (i + 1) + "客户订单号customerReferenceNo(tradeOrderId):" + customerReferenceNo + " 保存发件人,outWarehouseOrderSenderId:" + outWarehouseOrderSenderId);
		}
		response.setSuccess(Constant.TRUE);
		return XmlUtil.toXml(responses);
	}

	/**
	 * 获取入库记录
	 */
	@Override
	public Pagination getInWarehouseRecordData(InWarehouseRecord inWarehouseRecord, Map<String, String> moreParam, Pagination pagination) {
		List<InWarehouseRecord> inWarehouseRecordList = inWarehouseRecordDao.findInWarehouseRecord(inWarehouseRecord, moreParam, pagination);
		List<Object> list = new ArrayList<Object>();
		for (InWarehouseRecord record : inWarehouseRecordList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", record.getId());
			if (record.getCreatedTime() != null) {
				map.put("createdTime", DateUtil.dateConvertString(new Date(record.getCreatedTime()), DateUtil.yyyy_MM_ddHHmmss));
			}
			// 查询用户名
			User user = userDao.getUserById(record.getUserIdOfCustomer());
			map.put("userLoginNameOfCustomer", user.getLoginName());
			// 查询操作员
			if (NumberUtil.greaterThanZero(record.getUserIdOfOperator())) {
				User userOfOperator = userDao.getUserById(record.getUserIdOfOperator());
				map.put("userLoginNameOfOperator", userOfOperator.getLoginName());
			}
			map.put("trackingNo", record.getTrackingNo());
			InWarehouseOrder inWarehouseOrder = inWarehouseOrderDao.getInWarehouseOrderById(record.getInWarehouseOrderId());
			if (inWarehouseOrder != null) {
				map.put("customerReferenceNo", inWarehouseOrder.getCustomerReferenceNo());
			}
			map.put("inWarehouseOrderId", record.getInWarehouseOrderId());
			map.put("batchNo", record.getBatchNo());
			if (NumberUtil.greaterThanZero(record.getWarehouseId())) {
				Warehouse warehouse = warehouseDao.getWarehouseById(record.getWarehouseId());
				map.put("warehouse", warehouse.getWarehouseName());
			}
			map.put("remark", record.getRemark());
			InWarehouseRecordItem param = new InWarehouseRecordItem();
			param.setInWarehouseRecordId(record.getId());
			List<InWarehouseRecordItem> inWarehouseRecordItemList = inWarehouseRecordItemDao.findInWarehouseRecordItem(param, null, null);
			Integer receivedQuantity = 0;
			String skus = "";
			for (InWarehouseRecordItem item : inWarehouseRecordItemList) {
				skus += item.getSku() + "*" + item.getQuantity() + " ";
				receivedQuantity += item.getQuantity();
			}
			map.put("skus", skus);
			map.put("receivedQuantity", receivedQuantity);
			// 预报物品数量,根据跟踪单号找入库订单
			Long orderItemsize = inWarehouseOrderDao.countInWarehouseOrderItemByInWarehouseOrderId(record.getInWarehouseOrderId());
			map.put("quantity", orderItemsize);

			String status = "";
			if (record.getStatus() != null) {
				InWarehouseRecordStatus inWarehouseRecordStatus = inWarehouseRecordStatusDao.findInWarehouseRecordStatusByCode(record.getStatus());
				if (inWarehouseRecordStatus != null) {
					status = inWarehouseRecordStatus.getCn();
				}
			}
			map.put("status", status);
			// 回传成功
			if (StringUtil.isEqual(record.getCallbackIsSuccess(), Constant.Y)) {
				map.put("callbackIsSuccess", "成功");
			} else {
				if (record.getCallbackCount() != null && record.getCallbackCount() > 0) {
					map.put("callbackIsSuccess", "失败次数:" + record.getCallbackCount());
				} else {
					map.put("callbackIsSuccess", "未回传");
				}
			}
			list.add(map);
		}
		pagination.total = inWarehouseRecordDao.countInWarehouseRecord(inWarehouseRecord, moreParam);
		pagination.rows = list;
		return pagination;
	}

	@Override
	public Map<String, String> checkOutWarehouseOrder(String orderIds, Integer checkResult, Long userIdOfOperator) throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.STATUS, Constant.FAIL);
		if (StringUtil.isNull(orderIds)) {
			map.put(Constant.MESSAGE, "出库订单id(orderIds)为空,无法处理");
			return map;
		}
		if (checkResult == null) {
			map.put(Constant.MESSAGE, "审核结果(checkResult)为空,无法处理");
			return map;
		}

		int updateQuantity = 0;
		int noUpdateQuantity = 0;
		int notEnougnQuantity = 0;
		String orderIdArr[] = orderIds.split(",");
		for (String orderId : orderIdArr) {
			if (StringUtil.isNull(orderId)) {
				continue;
			}
			Long orderIdLong = Long.valueOf(orderId);
			// 查询订单的当前状态
			String oldStatus = outWarehouseOrderDao.getOutWarehouseOrderStatus(orderIdLong);
			// 如果不是等待审核状态的订单,直接跳过
			if (!StringUtil.isEqual(oldStatus, OutWarehouseOrderStatusCode.WWC)) {
				noUpdateQuantity++;
				continue;
			}

			// ==========================================================================================================================================================================
			// 分配从库位库存找商品库位,预生成打印捡货单需要的信息
			List<OutWarehouseOrderItemShelf> outWarehouseOrderItemShelfList = new ArrayList<OutWarehouseOrderItemShelf>();
			List<ItemShelfInventory> waitUpdateavAilableQuantityList = new ArrayList<ItemShelfInventory>();
			OutWarehouseOrder outWarehouseOrder = outWarehouseOrderDao.getOutWarehouseOrderById(orderIdLong);
			OutWarehouseOrderItem itemParam = new OutWarehouseOrderItem();
			itemParam.setOutWarehouseOrderId(orderIdLong);
			List<OutWarehouseOrderItem> outWarehouseOrderItemList = outWarehouseOrderItemDao.findOutWarehouseOrderItem(itemParam, null, null);
			boolean isNotEnough = false;
			for (OutWarehouseOrderItem item : outWarehouseOrderItemList) {
				// 根据出库订单物品 SKU和数量,按批次,SKU查找上架表
				List<ItemShelfInventory> itemShelfInventoryList = itemShelfInventoryDao.findItemShelfInventoryForPreOutShelf(outWarehouseOrder.getUserIdOfCustomer(), outWarehouseOrder.getWarehouseId(), item.getSku());
				int needQuantity = item.getQuantity();// 需要预下架的商品数量
				int isEnoughQuantity = needQuantity;// 循环执行完后,isEnoughQuantity大于0,代表可用库存不足,审核失败
				for (ItemShelfInventory itemShelfInventory : itemShelfInventoryList) {
					int quantity = 0;
					// 该库位的可用库存数量
					int availableQuantity = itemShelfInventory.getAvailableQuantity();
					boolean isBreak = false;
					if (availableQuantity > needQuantity) {
						// 如果此库位的可用库存大于需要的数量
						quantity = needQuantity;
						// 无需再找下一个库位,更新可用库存
						availableQuantity = availableQuantity - needQuantity;
						isBreak = true;
					} else {
						// 否则此库位的可用库存全部使用,并继续找下一库位
						// 需要下架的商品减去此货架可用库存
						needQuantity = needQuantity - availableQuantity;
						quantity = availableQuantity;
						// 更新可用库存=0
						availableQuantity = 0;
						if (needQuantity <= 0) {
							isBreak = true;
						}
					}
					isEnoughQuantity = isEnoughQuantity - quantity;
					// 执行更新可用库存
					itemShelfInventory.setAvailableQuantity(availableQuantity);
					waitUpdateavAilableQuantityList.add(itemShelfInventory);
					// 打印捡货单,记录出库订单对应的货位和物品.下次打印时 使用已经保存的货位和物品信息
					OutWarehouseOrderItemShelf outWarehouseOrderItemShelf = OutWarehouseOrderItemShelf.createOutWarehouseOrderItemShelf(orderIdLong, quantity, itemShelfInventory.getSeatCode(), item.getSku(), item.getSkuName(),
							item.getSkuNetWeight(), item.getSkuPriceCurrency(), item.getSkuUnitPrice(), itemShelfInventory.getBatchNo(), item.getSpecification());
					outWarehouseOrderItemShelfList.add(outWarehouseOrderItemShelf);
					if (isBreak) {
						break;
					}
				}
				// 如果所有库位的可用库存都不足,不允许出库,审核失败
				if (isEnoughQuantity > 0) {
					isNotEnough = true;
				}
			}

			if (!isNotEnough) {
				// 更新库位的可用库存
				itemShelfInventoryDao.updateBatchItemShelfInventoryAvailableQuantity(waitUpdateavAilableQuantityList);
				// 保存打印捡货单需要的库位和物品信息
				outWarehouseOrderItemShelfDao.saveBatchOutWarehouseOrderItemShelf(outWarehouseOrderItemShelfList);
				// COE审核通过,等待打印捡货单
				int updateResult = outWarehouseOrderDao.updateOutWarehouseOrderStatus(orderIdLong, OutWarehouseOrderStatusCode.WPP);
				updateQuantity++;
			} else {
				notEnougnQuantity++;
			}
			// ==========================================================================================================================================================================
		}
		map.put(Constant.MESSAGE, "审核通过:" + updateQuantity + "个订单,  审核不通过:" + noUpdateQuantity + "个非待审核状态订单," + notEnougnQuantity + "个库存不足订单");
		map.put(Constant.STATUS, Constant.SUCCESS);
		return map;
	}

	@Override
	public List<OutWarehouseOrderStatus> findAllOutWarehouseOrderStatus() throws ServiceException {
		return outWarehouseOrderStatusDao.findAllOutWarehouseOrderStatus();
	}

	/**
	 * API确认出库订单
	 */
	@Override
	public String warehouseInterfaceConfirmOutWarehouseOrder(EventBody eventBody, Long userIdOfCustomer, String warehouseNo) throws ServiceException {
		Responses responses = new Responses();
		List<Response> responseItems = new ArrayList<Response>();
		Response response = new Response();
		response.setSuccess(Constant.FALSE);
		responseItems.add(response);
		responses.setResponseItems(responseItems);
		// 取 tradeDetail 中tradeOrderId 作为客户订单号
		TradeDetail tradeDetail = eventBody.getTradeDetail();
		if (tradeDetail == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("EventBody对象获取TradeDetail对象得到Null");
			return XmlUtil.toXml(responses);
		}
		List<TradeOrder> tradeOrderList = tradeDetail.getTradeOrders();
		if (tradeOrderList == null || tradeOrderList.size() == 0) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("TradeDetail对象获取TradeOrders对象得到Null");
			return XmlUtil.toXml(responses);
		}
		// 客户订单号
		String customerReferenceNo = tradeOrderList.get(0).getTradeOrderId();
		if (StringUtil.isNull(customerReferenceNo)) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("TradeOrder对象获取tradeOrderId得到Null");
			return XmlUtil.toXml(responses);
		}
		// 根据客户订单号和客户帐号查找出库订单
		OutWarehouseOrder outWarehouseOrderParam = new OutWarehouseOrder();
		outWarehouseOrderParam.setUserIdOfCustomer(userIdOfCustomer);
		outWarehouseOrderParam.setCustomerReferenceNo(customerReferenceNo);
		List<OutWarehouseOrder> outWarehouseOrderList = outWarehouseOrderDao.findOutWarehouseOrder(outWarehouseOrderParam, null, null);
		if (outWarehouseOrderList.size() <= 0) {
			response.setReason(ErrorCode.B0005_CODE);
			response.setReasonDesc("根据客户订单号(tradeOrderId)和客户帐号(msgSource)查找订单得到Null");
			return XmlUtil.toXml(responses);
		}
		OutWarehouseOrder outWarehouseOrder = outWarehouseOrderList.get(0);
		// 只有当前状态 是等待顺丰确认的订单 才允许处理
		if (!StringUtil.isEqual(outWarehouseOrder.getStatus(), OutWarehouseOrderStatusCode.WCC)) {
			response.setReason(ErrorCode.B0100_CODE);
			response.setReasonDesc("出库订单当前状态非等待客户确认状态,不能修改");
			return XmlUtil.toXml(responses);
		}
		int count = outWarehouseOrderDao.updateOutWarehouseOrderStatus(outWarehouseOrder.getId(), OutWarehouseOrderStatusCode.WWO);
		logger.info("确认出库成功: 更新状态影响行数=" + count);

		response.setSuccess(Constant.TRUE);
		return XmlUtil.toXml(responses);
	}

	@Override
	public String warehouseInterfaceCancelOutWarehouseOrder(EventBody eventBody, Long userIdOfCustomer, String warehouseNo) throws ServiceException {
		Responses responses = new Responses();
		List<Response> responseItems = new ArrayList<Response>();
		Response response = new Response();
		response.setSuccess(Constant.FALSE);
		responseItems.add(response);
		responses.setResponseItems(responseItems);
		// 取 tradeDetail 中tradeOrderId 作为客户订单号
		TradeDetail tradeDetail = eventBody.getTradeDetail();
		if (tradeDetail == null) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("EventBody对象获取TradeDetail对象得到Null");
			return XmlUtil.toXml(responses);
		}
		List<TradeOrder> tradeOrderList = tradeDetail.getTradeOrders();
		if (tradeOrderList == null || tradeOrderList.size() == 0) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("TradeDetail对象获取TradeOrders对象得到Null");
			return XmlUtil.toXml(responses);
		}
		// 客户订单号
		String customerReferenceNo = tradeOrderList.get(0).getTradeOrderId();
		if (StringUtil.isNull(customerReferenceNo)) {
			response.setReason(ErrorCode.S01_CODE);
			response.setReasonDesc("TradeOrder对象获取tradeOrderId得到Null");
			return XmlUtil.toXml(responses);
		}
		// 根据客户订单号和客户帐号查找出库订单
		OutWarehouseOrder outWarehouseOrderParam = new OutWarehouseOrder();
		outWarehouseOrderParam.setUserIdOfCustomer(userIdOfCustomer);
		outWarehouseOrderParam.setCustomerReferenceNo(customerReferenceNo);
		List<OutWarehouseOrder> outWarehouseOrderList = outWarehouseOrderDao.findOutWarehouseOrder(outWarehouseOrderParam, null, null);
		if (outWarehouseOrderList.size() <= 0) {
			response.setReason(ErrorCode.B0005_CODE);
			response.setReasonDesc("根据客户订单号(tradeOrderId)和客户帐号(msgSource)查找订单得到Null");
			return XmlUtil.toXml(responses);
		}
		OutWarehouseOrder outWarehouseOrder = outWarehouseOrderList.get(0);
		// 只有当前状态 是等待顺丰确认的订单 才允许处理
		if (StringUtil.isEqual(outWarehouseOrder.getStatus(), OutWarehouseOrderStatusCode.SUCCESS)) {
			response.setReason(ErrorCode.B0100_CODE);
			response.setReasonDesc("出库订单已经完成出库,不能取消");
			return XmlUtil.toXml(responses);
		}
		if (!StringUtil.isEqual(outWarehouseOrder.getStatus(), OutWarehouseOrderStatusCode.WWC)) {
			response.setReason(ErrorCode.B0100_CODE);
			response.setReasonDesc("出库订单已经开始出库,不能取消");
			return XmlUtil.toXml(responses);
		}
		int count = outWarehouseOrderDao.deleteOutWarehouseOrder(outWarehouseOrder.getId());
		logger.info("取消出库成功: 影响行数=" + count);
		response.setSuccess(Constant.TRUE);
		return XmlUtil.toXml(responses);
	}

	/**
	 * 获取所有仓库
	 */
	@Override
	public List<Warehouse> findAllWarehouse() throws ServiceException {
		return warehouseDao.findAllWarehouse();
	}

	@Override
	public List<Warehouse> findAllWarehouse(Long firstWarehouseId) throws ServiceException {
		if (firstWarehouseId == null) {
			return warehouseDao.findAllWarehouse();
		}
		List<Warehouse> newWarehouseList = new ArrayList<Warehouse>();
		newWarehouseList.add(warehouseDao.getWarehouseById(firstWarehouseId));

		List<Warehouse> warehouseList = warehouseDao.findAllWarehouse();
		for (int i = 0; i < warehouseList.size(); i++) {
			Warehouse warehouse = warehouseList.get(i);
			if (warehouse.getId() - firstWarehouseId == 0) {
				warehouseList.remove(i);
				break;
			}
		}
		newWarehouseList.addAll(warehouseList);
		return newWarehouseList;
	}

	/**
	 * 根据入库订单id, 查找入库物品明细
	 * 
	 * @param orderId
	 * @param pagination
	 * @return
	 */
	@Override
	public List<OutWarehouseOrderItem> getOutWarehouseOrderItem(Long orderId) {
		OutWarehouseOrderItem param = new OutWarehouseOrderItem();
		param.setOutWarehouseOrderId(orderId);
		List<OutWarehouseOrderItem> outWarehouseOrderItemList = outWarehouseOrderItemDao.findOutWarehouseOrderItem(param, null, null);
		return outWarehouseOrderItemList;
	}

	@Override
	public List<Map<String, String>> checkInWarehouseOrder(InWarehouseOrder inWarehouseOrder) {
		List<InWarehouseOrder> inWarehouseOrderList = inWarehouseOrderDao.findInWarehouseOrder(inWarehouseOrder, null, null);
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		for (InWarehouseOrder order : inWarehouseOrderList) {
			Map<String, String> map = new HashMap<String, String>();
			Long userId = order.getUserIdOfCustomer();
			User user = userDao.getUserById(userId);
			map.put("inWarehouseOrderId", String.valueOf(order.getId()));
			map.put("userLoginName", user.getLoginName());
			map.put("trackingNo", order.getTrackingNo());
			map.put("customerReferenceNo", order.getCustomerReferenceNo());
			map.put("carrierCode", order.getCarrierCode());
			String time = DateUtil.dateConvertString(new Date(order.getCreatedTime()), DateUtil.yyyy_MM_ddHHmmss);
			map.put("createdTime", time);
			mapList.add(map);
		}
		return mapList;
	}

	@Override
	public List<Map<String, String>> getInWarehouseRecordItemMapByRecordId(Long recordId) {
		InWarehouseRecordItem param = new InWarehouseRecordItem();
		param.setInWarehouseRecordId(recordId);
		List<InWarehouseRecordItem> inWarehouseRecordItemList = inWarehouseRecordItemDao.findInWarehouseRecordItem(param, null, null);

		// 获取入库订单id
		Long inWarehouseOrderId = inWarehouseRecordDao.getInWarehouseOrderIdByRecordId(recordId);
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		for (InWarehouseRecordItem item : inWarehouseRecordItemList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("sku", item.getSku());
			// 获取sku商品名
			InWarehouseOrderItem orderItemParam = new InWarehouseOrderItem();
			orderItemParam.setOrderId(inWarehouseOrderId);
			orderItemParam.setSku(item.getSku());
			List<InWarehouseOrderItem> inWarehouseOrderItemList = inWarehouseOrderItemDao.findInWarehouseOrderItem(orderItemParam, null, null);
			if (inWarehouseOrderItemList != null && inWarehouseOrderItemList.size() == 1) {
				map.put("skuName", inWarehouseOrderItemList.get(0).getSkuName());
				map.put("skuNo", inWarehouseOrderItemList.get(0).getSkuNo());
				// 预报数量
				map.put("orderQuantity", inWarehouseOrderItemList.get(0).getQuantity() + "");
			} else {
				map.put("skuName", "");
				map.put("skuNo", "");
				map.put("orderQuantity", "");
			}
			map.put("quantity", NumberUtil.intToString(item.getQuantity()));
			mapList.add(map);
		}
		return mapList;
	}

	/**
	 * 出货检查每个跟踪号是否有效,并保存到出货单OutWarehouseShipping表;
	 * 
	 */
	@Override
	public Map<String, String> checkOutWarehouseShipping(String trackingNo, Long userIdOfOperator, Long coeTrackingNoId, String coeTrackingNo, String addOrSub, String orderIds) throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.STATUS, Constant.FAIL);
		if (StringUtil.isNull(trackingNo)) {
			map.put(Constant.MESSAGE, "请输入出货跟踪单号");
			return map;
		}
		OutWarehouseOrder param = new OutWarehouseOrder();
		param.setTrackingNo(trackingNo);
		List<OutWarehouseOrder> outWarehouseOrderList = outWarehouseOrderDao.findOutWarehouseOrder(param, null, null);
		if (outWarehouseOrderList == null || outWarehouseOrderList.size() == 0) {
			map.put(Constant.MESSAGE, "查询不到出库订单,请重新输入出货跟踪单号");
			return map;
		}
		if (outWarehouseOrderList.size() > 1) {
			map.put(Constant.MESSAGE, "查询不到唯一的出库订单,暂无法处理");
			// 找到多个出库订单的情况,待处理
			// map.put(Constant.MESSAGE, "查询到多个出库订单,请输入客户订单号");
			return map;
		}
		OutWarehouseOrder outWarehouseOrder = outWarehouseOrderList.get(0);
		// 只有顺丰确认出库,顺丰已确认的订单 可以出库
		if (StringUtil.isEqual(outWarehouseOrder.getStatus(), OutWarehouseOrderStatusCode.WWO)) {
			Long outWarehouseOrderId = outWarehouseOrder.getId();
			map.put("orderId", outWarehouseOrderId + "");
			if (StringUtil.isEqual(addOrSub, "1")) {
				// 检查出库订单 是否已经和COE交接单号绑定
				OutWarehouseRecordItem checkTrackingNoParam = new OutWarehouseRecordItem();
				checkTrackingNoParam.setOutWarehouseOrderId(outWarehouseOrderId);
				Long countTrackingNoResult = outWarehouseRecordItemDao.countOutWarehouseRecordItem(checkTrackingNoParam, null);
				if (countTrackingNoResult > 0) {
					// 说明该出库订单已经和其他COE交接单号绑定了,不能再绑定此单号
					map.put(Constant.MESSAGE, "该出库订单已经绑定的了其他COE交接单号");
					return map;
				}
				// 保存到OutWarehouseShipping,但不改变出库订单的状态.
				// 只有当操作员点击完成出货总单才改变一个COE单号下面对应的所有出库订单的状态
				OutWarehouseRecordItem outWarehouseRecordItem = new OutWarehouseRecordItem();
				outWarehouseRecordItem.setCoeTrackingNo(coeTrackingNo);
				outWarehouseRecordItem.setCoeTrackingNoId(coeTrackingNoId);
				outWarehouseRecordItem.setCreatedTime(System.currentTimeMillis());
				outWarehouseRecordItem.setOutWarehouseOrderTrackingNo(trackingNo);
				outWarehouseRecordItem.setOutWarehouseOrderId(outWarehouseOrder.getId());
				outWarehouseRecordItem.setUserIdOfCustomer(outWarehouseOrder.getUserIdOfCustomer());
				outWarehouseRecordItem.setUserIdOfOperator(userIdOfOperator);
				outWarehouseRecordItem.setWarehouseId(outWarehouseOrder.getWarehouseId());
				long outShippingId = outWarehouseRecordItemDao.saveOutWarehouseRecordItem(outWarehouseRecordItem);
				map.put("outWarehouseShippingId", outShippingId + "");
				map.put(Constant.STATUS, Constant.SUCCESS);
			} else {
				// 1 = 添加出货运单号 ,2 是减去
				// 根据出货运单号+coe单号查找出货记录
				OutWarehouseRecordItem shippingParam = new OutWarehouseRecordItem();
				shippingParam.setCoeTrackingNoId(coeTrackingNoId);
				shippingParam.setCoeTrackingNo(coeTrackingNo);
				shippingParam.setOutWarehouseOrderTrackingNo(trackingNo);
				List<OutWarehouseRecordItem> outWarehouseShippingList = outWarehouseRecordItemDao.findOutWarehouseRecordItem(shippingParam, null, null);
				String deleteShippingIds = "";
				int sub = 0;
				for (OutWarehouseRecordItem shipping : outWarehouseShippingList) {
					outWarehouseRecordItemDao.deleteOutWarehouseRecordItemById(shipping.getId());
					// 加#是为了 jquery可以直接$("#id1,#id2,#id3,#id4")
					deleteShippingIds += ("#" + shipping.getId() + ",");
					orderIds = orderIds.replaceAll(shipping.getOutWarehouseOrderId() + "\\|\\|", "");
					sub++;
				}
				map.put("sub", sub + "");
				map.put("deleteShippingIds", deleteShippingIds);
				map.put("orderIds", orderIds);
				map.put(Constant.STATUS, "2");
			}
		} else if (StringUtil.isEqual(outWarehouseOrder.getStatus(), OutWarehouseOrderStatusCode.SUCCESS)) {
			map.put(Constant.MESSAGE, "出库订单当前状态已经是出库成功");
		} else if (StringUtil.isEqual(outWarehouseOrder.getStatus(), OutWarehouseOrderStatusCode.WCC)) {
			map.put(Constant.MESSAGE, "出库订单当前状态是等待客户确认出库,不能出库");
		} else if (StringUtil.isEqual(outWarehouseOrder.getStatus(), OutWarehouseOrderStatusCode.WSW)) {
			map.put(Constant.MESSAGE, "出库订单当前状态是等待发送出库重量给客户,不能出库");
		} else {
			map.put(Constant.MESSAGE, "出库订单当前状态不能出库");
		}
		return map;
	}

	@Override
	public Map<String, String> outWarehouseShippingConfirm(String coeTrackingNo, Long userIdOfOperator) throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.STATUS, Constant.FAIL);
		if (StringUtil.isNull(coeTrackingNo)) {
			map.put(Constant.MESSAGE, "请输入COE交接单号");
			return map;
		}
		List<TrackingNo> trackingNoList = trackingNoDao.findTrackingNo(coeTrackingNo, TrackingNo.TYPE_COE);
		if (trackingNoList == null || trackingNoList.size() < 1) {
			map.put(Constant.MESSAGE, "该COE交接单号无效,请输入新单号");
			return map;
		}
		TrackingNo trackingNo = trackingNoList.get(0);
		Long coeTrackingNoId = trackingNo.getId();
		// 根据出库交接单号查询建包记录.
		OutWarehousePackage outWarehousePackage = new OutWarehousePackage();
		outWarehousePackage.setCoeTrackingNoId(coeTrackingNoId);
		Long countOutWarehousePackage = outWarehousePackageDao.countOutWarehousePackage(outWarehousePackage, null);
		if (countOutWarehousePackage <= 0) {
			map.put(Constant.MESSAGE, "没有找到出库建包记录,请先完成建包");
			return map;
		}
		// 出库记录
		OutWarehouseRecord outWarehouseRecord = new OutWarehouseRecord();
		outWarehouseRecord.setCoeTrackingNoId(coeTrackingNoId);
		Long countOutWarehouseRecord = outWarehouseRecordDao.countOutWarehouseRecord(outWarehouseRecord, null);
		if (countOutWarehouseRecord >= 1) {
			map.put(Constant.MESSAGE, "该交接单号对应大包已经出库,请勿重复操作");
			return map;
		}

		Long userIdOfCustomer = null;
		Long warehouseId = null;
		// 根据coe交接单号 获取建包记录,获取每个出库订单(小包)
		OutWarehouseRecordItem itemParam = new OutWarehouseRecordItem();
		itemParam.setCoeTrackingNoId(coeTrackingNoId);
		List<OutWarehouseRecordItem> outWarehouseRecordItemList = outWarehouseRecordItemDao.findOutWarehouseRecordItem(itemParam, null, null);
		// 迭代,检查跟踪号
		for (OutWarehouseRecordItem recordItem : outWarehouseRecordItemList) {
			// 改变状态 ,发送到哲盟
			Long orderId = recordItem.getOutWarehouseOrderId();
			// logger.info("出货,待发送到哲盟新系统的出库订单id: = " + orderId);

			if (userIdOfCustomer == null) {
				OutWarehouseOrder outWarehouseOrder = outWarehouseOrderDao.getOutWarehouseOrderById(orderId);
				userIdOfCustomer = outWarehouseOrder.getUserIdOfCustomer();
				warehouseId = outWarehouseOrder.getWarehouseId();
			}
			outWarehouseOrderDao.updateOutWarehouseOrderStatus(orderId, OutWarehouseOrderStatusCode.SUCCESS);
			// 更新出库成功,并改变商品批次库存
			// --------------------------------------------------------------------------------------------------------------------------------------------
			// 查找下架时的批次,货位,sku,数量记录
			OutWarehouseOrderItemShelf outWarehouseOrderItemShelfParam = new OutWarehouseOrderItemShelf();
			outWarehouseOrderItemShelfParam.setOutWarehouseOrderId(orderId);
			List<OutWarehouseOrderItemShelf> outWarehouseOrderItemShelfList = outWarehouseOrderItemShelfDao.findOutWarehouseOrderItemShelf(outWarehouseOrderItemShelfParam, null, null);
			for (OutWarehouseOrderItemShelf oItemShelf : outWarehouseOrderItemShelfList) {
				ItemInventory inventoryParam = new ItemInventory();
				inventoryParam.setSku(oItemShelf.getSku());
				inventoryParam.setBatchNo(oItemShelf.getBatchNo());
				inventoryParam.setWarehouseId(warehouseId);
				inventoryParam.setUserIdOfCustomer(userIdOfCustomer);
				List<ItemInventory> itemInventoryList = itemInventoryDao.findItemInventory(inventoryParam, null, null);
				if (itemInventoryList != null && itemInventoryList.size() > 0) {
					ItemInventory itemInventory = itemInventoryList.get(0);
					int outQuantity = oItemShelf.getQuantity();
					int updateCount = itemInventoryDao.updateItemInventoryQuantity(itemInventory.getId(), itemInventory.getQuantity() - outQuantity);
					if (updateCount <= 0) {
						map.put(Constant.MESSAGE, "执行商品批次库存更新失败,出库不成功");// 待添加事务回滚
					}
				} else {
					map.put(Constant.MESSAGE, "找不到商品批次库存,出库不成功");// 待添加事务回滚
				}
			}
			// 更新库存结束----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
		}
		// 保存出库记录
		outWarehouseRecord.setCoeTrackingNo(coeTrackingNo);
		outWarehouseRecord.setCreatedTime(System.currentTimeMillis());
		outWarehouseRecord.setUserIdOfCustomer(userIdOfCustomer);
		outWarehouseRecord.setUserIdOfOperator(userIdOfOperator);
		outWarehouseRecord.setWarehouseId(warehouseId);
		outWarehouseRecordDao.saveOutWarehouseRecord(outWarehouseRecord);
		map.put(Constant.STATUS, Constant.SUCCESS);
		map.put(Constant.MESSAGE, "完成出货总单成功,请继续下一批!");
		return map;
	}

	@Override
	public Map<String, String> outWarehousePackageConfirm(String coeTrackingNo, Long coeTrackingNoId, String orderIds, Long userIdOfOperator) throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.STATUS, Constant.FAIL);
		if (StringUtil.isNull(orderIds)) {
			map.put(Constant.MESSAGE, "请输入出货跟踪单号再按完成出货建包!");
			return map;
		}
		// 前端用||分割多个跟踪单号
		String orderIdsArray[] = orderIds.split("\\|\\|");
		if (orderIdsArray.length < 1) {
			map.put(Constant.MESSAGE, "请输入出货跟踪单号再按完成出货建包!");
			return map;
		}
		if (StringUtil.isNull(coeTrackingNo)) {
			map.put(Constant.MESSAGE, "请输入COE交接单号,或刷新页面重试!");
			return map;
		}
		TrackingNo trackingNo = trackingNoDao.getTrackingNoById(coeTrackingNoId);
		if (trackingNo == null) {
			map.put(Constant.MESSAGE, "该COE交接单号无效,请输入新单号");
			return map;
		}
		if (StringUtil.isEqual(trackingNo.getStatus(), TrackingNo.STATUS_USED + "")) {
			map.put(Constant.MESSAGE, "该COE交接单号已经使用,请输入新单号");
			return map;
		}
		Long userIdOfCustomer = null;
		Long warehouseId = null;
		// 迭代,检查跟踪号
		for (String orderId : orderIdsArray) {
			if (StringUtil.isNotNull(orderId)) {
				Long orderIdLong = Long.valueOf(orderId);
				if (userIdOfCustomer == null) {
					OutWarehouseOrder outWarehouseOrder = outWarehouseOrderDao.getOutWarehouseOrderById(orderIdLong);
					userIdOfCustomer = outWarehouseOrder.getUserIdOfCustomer();
					warehouseId = outWarehouseOrder.getWarehouseId();
				}
			}
		}
		if (userIdOfCustomer == null) {
			map.put(Constant.MESSAGE, "请输入出货跟踪单号再按完成出货建包!");
			return map;
		}

		// 保存出库建包记录
		OutWarehousePackage outWarehousePackage = new OutWarehousePackage();
		outWarehousePackage.setCoeTrackingNo(coeTrackingNo);
		outWarehousePackage.setCoeTrackingNoId(coeTrackingNoId);
		outWarehousePackage.setCreatedTime(System.currentTimeMillis());
		outWarehousePackage.setUserIdOfCustomer(userIdOfCustomer);
		outWarehousePackage.setUserIdOfOperator(userIdOfOperator);
		outWarehousePackage.setWarehouseId(warehouseId);
		outWarehousePackageDao.saveOutWarehousePackage(outWarehousePackage);
		// 标记coe单号已经使用
		trackingNoDao.usedTrackingNo(coeTrackingNoId);
		// 返回新COE单号,供下一批出库
		TrackingNo nextTrackingNo = trackingNoDao.getAvailableTrackingNoByType(TrackingNo.TYPE_COE);
		if (nextTrackingNo == null) {
			map.put(Constant.MESSAGE, "本次出货建包已完成,但COE单号不足,不能继续操作建包!");
			map.put(Constant.STATUS, "2");
			map.put("coeTrackingNo", "");
			map.put("coeTrackingNoId", "");
			return map;
		}
		trackingNoDao.lockTrackingNo(nextTrackingNo.getId());
		map.put("coeTrackingNo", nextTrackingNo.getTrackingNo());
		map.put("coeTrackingNoId", nextTrackingNo.getId().toString());
		map.put(Constant.STATUS, Constant.SUCCESS);
		map.put(Constant.MESSAGE, "完成出货建包成功,请继续下一批!");
		return map;
	}

	@Override
	public Map<String, Object> outWarehouseSubmitCustomerReferenceNo(String customerReferenceNo, Long userIdOfOperator) throws ServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constant.STATUS, Constant.FAIL);

		// 根据客户参考好查询出库订单customerReferenceNo
		OutWarehouseOrder param = new OutWarehouseOrder();
		param.setCustomerReferenceNo(customerReferenceNo);
		List<OutWarehouseOrder> outWarehouseOrderList = outWarehouseOrderDao.findOutWarehouseOrder(param, null, null);
		if (outWarehouseOrderList == null || outWarehouseOrderList.size() == 0) {
			map.put(Constant.MESSAGE, "根据客户订单号找不到出库订单,请重新输入");
			return map;
		}
		OutWarehouseOrder outWarehouseOrder = outWarehouseOrderList.get(0);
		map.put("shipwayCode", outWarehouseOrder.getShipwayCode());
		map.put("trackingNo", outWarehouseOrder.getTrackingNo());
		map.put("outWarehouseOrderId", outWarehouseOrder.getId());
		OutWarehouseOrderStatus outWarehouseOrderStatus = outWarehouseOrderStatusDao.findOutWarehouseOrderStatusByCode(outWarehouseOrder.getStatus());
		if (outWarehouseOrderStatus != null) {
			map.put("outWarehouseOrderStatus", outWarehouseOrderStatus.getCn());
		}
		// 查询出库订单SKU
		OutWarehouseOrderItem outWarehouseOrderItemParam = new OutWarehouseOrderItem();
		outWarehouseOrderItemParam.setOutWarehouseOrderId(outWarehouseOrder.getId());
		List<OutWarehouseOrderItem> outWarehouseOrderItemList = outWarehouseOrderItemDao.findOutWarehouseOrderItem(outWarehouseOrderItemParam, null, null);
		map.put("outWarehouseOrderItemList", outWarehouseOrderItemList);
		map.put(Constant.STATUS, Constant.SUCCESS);
		return map;
	}

	@Override
	public Map<String, Object> outWarehouseSubmitWeight(String customerReferenceNo, Double weight, Long userIdOfOperator) throws ServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constant.STATUS, Constant.FAIL);
		// 根据客户参考好查询出库订单customerReferenceNo
		if (StringUtil.isNull(customerReferenceNo)) {
			map.put(Constant.MESSAGE, "请先输入客户订单号");
			return map;
		}
		if (weight == null || weight <= 0) {
			map.put(Constant.MESSAGE, "重量必须是大于0的数字");
		}
		OutWarehouseOrder param = new OutWarehouseOrder();
		param.setCustomerReferenceNo(customerReferenceNo);
		List<OutWarehouseOrder> outWarehouseOrderList = outWarehouseOrderDao.findOutWarehouseOrder(param, null, null);
		if (outWarehouseOrderList == null || outWarehouseOrderList.size() == 0) {
			map.put(Constant.MESSAGE, "根据客户订单号找不到出库订单,请重新输入");
			return map;
		}
		OutWarehouseOrder outWarehouseOrder = outWarehouseOrderList.get(0);
		if (!(StringUtil.isEqual(outWarehouseOrder.getStatus(), OutWarehouseOrderStatusCode.WWW) || StringUtil.isEqual(outWarehouseOrder.getStatus(), OutWarehouseOrderStatusCode.WSW))) {
			map.put(Constant.MESSAGE, "出库订单当前状态不允许改变出库总重量");
			return map;
		}
		// 称重,并改变订单状态为已经称重,等待回传给顺丰
		outWarehouseOrder.setStatus(OutWarehouseOrderStatusCode.WSW);
		outWarehouseOrder.setOutWarehouseWeight(weight);
		outWarehouseOrder.setWeightCode(WeightCode.KG);
		int updateCount = outWarehouseOrderDao.updateOutWarehouseOrderWeight(outWarehouseOrder);
		if (updateCount > 0) {
			map.put(Constant.STATUS, Constant.SUCCESS);
		} else {
			map.put(Constant.MESSAGE, "执行数据库更新时失败,数据库返回更新行数:" + updateCount);
		}
		return map;
	}

	@Override
	public Pagination getInWarehouseRecordItemListData(Map<String, String> moreParam, Pagination pagination) {
		List<Map<String, Object>> recordItemList = inWarehouseRecordItemDao.getInWarehouseRecordItemListData(moreParam, pagination);
		List<Object> list = new ArrayList<Object>();
		for (Map<String, Object> recordItem : recordItemList) {
			Long warehouseId = (Long) recordItem.get("warehouse_id");
			Long userIdOfOperator = (Long) recordItem.get("user_id_of_operator");
			Long userIdOfCustomer = (Long) recordItem.get("user_id_of_customer");
			// 查询用户名
			User userOfOperator = userDao.getUserById(Long.valueOf(userIdOfOperator));
			recordItem.put("userLoginNameOfOperator", userOfOperator.getLoginName());
			// 查询用户名
			User userOfCustomer = userDao.getUserById(Long.valueOf(userIdOfCustomer));
			recordItem.put("userLoginNameOfCustomer", userOfCustomer.getLoginName());
			Warehouse warehouse = warehouseDao.getWarehouseById(Long.valueOf(warehouseId));
			if (warehouse != null) {
				recordItem.put("warehouse", warehouse.getWarehouseName());
			}

			list.add(recordItem);
		}
		pagination.total = inWarehouseRecordItemDao.countInWarehouseRecordItemList(moreParam);
		pagination.rows = list;
		return pagination;
	}

	@Override
	public TrackingNo getCoeTrackingNoforOutWarehouseShipping() throws ServiceException {
		TrackingNo trackingNo = trackingNoDao.getAvailableTrackingNoByType(TrackingNo.TYPE_COE);
		if (trackingNo == null) {
			return null;
		}
		// lock
		trackingNoDao.lockTrackingNo(trackingNo.getId());
		return trackingNo;
	}

	@Override
	public Map<String, String> saveInWarehouseOrderRemark(String remark, Long id) throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		if (inWarehouseOrderDao.updateInWarehouseOrderRemark(id, remark) > 0) {
			map.put(Constant.STATUS, Constant.SUCCESS);
		} else {
			map.put(Constant.STATUS, Constant.FAIL);
		}
		return map;
	}

	@Override
	public Map<String, String> saveInWarehouseRecordRemark(String remark, Long id) throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		if (inWarehouseRecordDao.updateInWarehouseRecordRemark(id, remark) > 0) {
			map.put(Constant.STATUS, Constant.SUCCESS);
		} else {
			map.put(Constant.STATUS, Constant.FAIL);
		}
		return map;
	}

	@Override
	public Map<String, String> saveOutWarehouseRecordRemark(String remark, Long id) throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		if (outWarehouseRecordDao.updateOutWarehouseRecordRemark(id, remark) > 0) {
			map.put(Constant.STATUS, Constant.SUCCESS);
		} else {
			map.put(Constant.STATUS, Constant.FAIL);
		}
		return map;
	}

	@Override
	public Map<String, String> saveOutWarehousePackageRemark(String remark, Long id) throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		if (outWarehousePackageDao.updateOutWarehousePackageRemark(id, remark) > 0) {
			map.put(Constant.STATUS, Constant.SUCCESS);
		} else {
			map.put(Constant.STATUS, Constant.FAIL);
		}
		return map;
	}

	@Override
	public Map<String, Object> outWarehouseShippingEnterCoeTrackingNo(String coeTrackingNo) throws ServiceException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constant.STATUS, Constant.FAIL);
		OutWarehouseRecordItem outWarehouseShipping = new OutWarehouseRecordItem();
		outWarehouseShipping.setCoeTrackingNo(coeTrackingNo);
		List<OutWarehouseRecordItem> outWarehouseShippingList = outWarehouseRecordItemDao.findOutWarehouseRecordItem(outWarehouseShipping, null, null);
		List<TrackingNo> trackingNos = trackingNoDao.findTrackingNo(coeTrackingNo, TrackingNo.TYPE_COE);
		// 暂不处理,单号可能重复问题
		if (trackingNos == null || trackingNos.size() <= 0) {
			map.put(Constant.MESSAGE, "该COE交接单号无效,请输入新单号");
			return map;
		}
		TrackingNo trackingNo = trackingNos.get(0);
		if (StringUtil.isEqual(trackingNo.getStatus(), TrackingNo.STATUS_USED + "")) {
			map.put(Constant.MESSAGE, "该COE交接单号已经使用,请输入新单号");
			return map;
		}
		map.put("coeTrackingNo", trackingNo);
		map.put(Constant.STATUS, Constant.SUCCESS);
		map.put("outWarehouseShippingList", outWarehouseShippingList);
		return map;
	}

	@Override
	public Warehouse getWarehouseById(Long fwarehouseId) throws ServiceException {
		return warehouseDao.getWarehouseById(fwarehouseId);
	}

	/**
	 * 获取出库记录
	 */
	@Override
	public Pagination getOutWarehouseRecordData(OutWarehouseRecord outWarehouseRecord, Map<String, String> moreParam, Pagination page) {
		List<OutWarehouseRecord> outWarehouseRecordList = outWarehouseRecordDao.findOutWarehouseRecord(outWarehouseRecord, moreParam, page);
		List<Object> list = new ArrayList<Object>();
		for (OutWarehouseRecord record : outWarehouseRecordList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", record.getId());
			if (record.getCreatedTime() != null) {
				map.put("createdTime", DateUtil.dateConvertString(new Date(record.getCreatedTime()), DateUtil.yyyy_MM_ddHHmmss));
			}
			// 查询用户名
			User user = userDao.getUserById(record.getUserIdOfCustomer());
			map.put("userLoginNameOfCustomer", user.getLoginName());
			// 查询操作员
			if (NumberUtil.greaterThanZero(record.getUserIdOfOperator())) {
				User userOfOperator = userDao.getUserById(record.getUserIdOfOperator());
				map.put("userLoginNameOfOperator", userOfOperator.getLoginName());
			}
			map.put("coeTrackingNo", record.getCoeTrackingNo());
			map.put("coeTrackingNoId", record.getCoeTrackingNoId());
			if (NumberUtil.greaterThanZero(record.getWarehouseId())) {
				Warehouse warehouse = warehouseDao.getWarehouseById(record.getWarehouseId());
				map.put("warehouse", warehouse.getWarehouseName());
			}
			map.put("remark", record.getRemark() == null ? "" : record.getRemark());
			OutWarehouseRecordItem param = new OutWarehouseRecordItem();
			param.setCoeTrackingNoId(record.getCoeTrackingNoId());
			List<OutWarehouseRecordItem> outWarehouseShippingList = outWarehouseRecordItemDao.findOutWarehouseRecordItem(param, null, null);
			Integer quantity = 0;
			String orders = "";
			for (OutWarehouseRecordItem item : outWarehouseShippingList) {
				orders += item.getOutWarehouseOrderTrackingNo() + " ; ";
				quantity++;
			}
			map.put("orders", orders);
			map.put("quantity", quantity);
			list.add(map);
		}
		page.total = outWarehouseRecordDao.countOutWarehouseRecord(outWarehouseRecord, moreParam);
		page.rows = list;
		return page;
	}

	/**
	 * 获取出库建包记录
	 */
	@Override
	public Pagination getOutWarehousePackageData(OutWarehousePackage outWarehousePackage, Map<String, String> moreParam, Pagination page) {
		List<OutWarehousePackage> outWarehousePackageList = outWarehousePackageDao.findOutWarehousePackage(outWarehousePackage, moreParam, page);
		List<Object> list = new ArrayList<Object>();
		for (OutWarehousePackage oPackage : outWarehousePackageList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", oPackage.getId());
			if (oPackage.getCreatedTime() != null) {
				map.put("packageTime", DateUtil.dateConvertString(new Date(oPackage.getCreatedTime()), DateUtil.yyyy_MM_ddHHmmss));
			}
			OutWarehouseRecord recordParam = new OutWarehouseRecord();
			recordParam.setCoeTrackingNoId(oPackage.getCoeTrackingNoId());
			List<OutWarehouseRecord> recordList = outWarehouseRecordDao.findOutWarehouseRecord(recordParam, null, null);
			if (recordList != null && recordList.size() >= 1) {
				map.put("isShipped", "已发货");
				OutWarehouseRecord record = recordList.get(0);
				if (record.getCreatedTime() != null) {
					map.put("shippedTime", DateUtil.dateConvertString(new Date(record.getCreatedTime()), DateUtil.yyyy_MM_ddHHmmss));
				}
			} else {
				map.put("isShipped", "未发货");
			}
			// 查询用户名
			User user = userDao.getUserById(oPackage.getUserIdOfCustomer());
			map.put("userLoginNameOfCustomer", user.getLoginName());
			// 查询操作员
			if (NumberUtil.greaterThanZero(oPackage.getUserIdOfOperator())) {
				User userOfOperator = userDao.getUserById(oPackage.getUserIdOfOperator());
				map.put("userLoginNameOfOperator", userOfOperator.getLoginName());
			}
			map.put("coeTrackingNo", oPackage.getCoeTrackingNo());
			map.put("coeTrackingNoId", oPackage.getCoeTrackingNoId());
			if (NumberUtil.greaterThanZero(oPackage.getWarehouseId())) {
				Warehouse warehouse = warehouseDao.getWarehouseById(oPackage.getWarehouseId());
				map.put("warehouse", warehouse.getWarehouseName());
			}
			map.put("remark", oPackage.getRemark() == null ? "" : oPackage.getRemark());
			OutWarehouseRecordItem param = new OutWarehouseRecordItem();
			param.setCoeTrackingNoId(oPackage.getCoeTrackingNoId());
			List<OutWarehouseRecordItem> outWarehouseShippingList = outWarehouseRecordItemDao.findOutWarehouseRecordItem(param, null, null);
			Integer quantity = 0;
			String orders = "";
			for (OutWarehouseRecordItem item : outWarehouseShippingList) {
				orders += item.getOutWarehouseOrderTrackingNo() + " ; ";
				quantity++;
			}
			map.put("orders", orders);
			map.put("quantity", quantity);
			list.add(map);
		}
		page.total = outWarehousePackageDao.countOutWarehousePackage(outWarehousePackage, moreParam);
		page.rows = list;
		return page;
	}

	@Override
	public List<Map<String, String>> getOutWarehouseRecordItemMapByRecordId(Long recordId) {
		OutWarehouseRecord outWarehouseRecord = outWarehouseRecordDao.getOutWarehouseRecordById(recordId);
		OutWarehouseRecordItem param = new OutWarehouseRecordItem();
		param.setCoeTrackingNoId(outWarehouseRecord.getCoeTrackingNoId());
		List<OutWarehouseRecordItem> outWarehouseShippingList = outWarehouseRecordItemDao.findOutWarehouseRecordItem(param, null, null);
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		for (OutWarehouseRecordItem item : outWarehouseShippingList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("orderId", item.getOutWarehouseOrderId() + "");
			map.put("trackingNo", item.getOutWarehouseOrderTrackingNo());
			User user = userDao.getUserById(item.getUserIdOfCustomer());
			map.put("customer", user.getLoginName());
			OutWarehouseOrder outWarehouseOrder = outWarehouseOrderDao.getOutWarehouseOrderById(item.getOutWarehouseOrderId());
			map.put("weight", outWarehouseOrder.getOutWarehouseWeight() + "");
			mapList.add(map);
		}
		return mapList;
	}

	@Override
	public List<Map<String, String>> getOutWarehouseRecordItemByPackageId(Long packageId) {
		OutWarehousePackage outWarehousePackage = outWarehousePackageDao.getOutWarehousePackageById(packageId);
		OutWarehouseRecordItem param = new OutWarehouseRecordItem();
		param.setCoeTrackingNoId(outWarehousePackage.getCoeTrackingNoId());
		List<OutWarehouseRecordItem> outWarehouseShippingList = outWarehouseRecordItemDao.findOutWarehouseRecordItem(param, null, null);
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
		for (OutWarehouseRecordItem item : outWarehouseShippingList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("orderId", item.getOutWarehouseOrderId() + "");
			map.put("trackingNo", item.getOutWarehouseOrderTrackingNo());
			User user = userDao.getUserById(item.getUserIdOfCustomer());
			map.put("customer", user.getLoginName());
			OutWarehouseOrder outWarehouseOrder = outWarehouseOrderDao.getOutWarehouseOrderById(item.getOutWarehouseOrderId());
			map.put("weight", outWarehouseOrder.getOutWarehouseWeight() + "");
			mapList.add(map);
		}
		return mapList;
	}

	@Override
	public Map<String, String> executeSearchOutWarehouseOrder(String nos, String noType) throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.STATUS, Constant.FAIL);
		if (StringUtil.isNull(nos)) {
			map.put(Constant.MESSAGE, "请输入单号");
			return map;
		}
		if (!(StringUtil.isEqual(noType, "1") || StringUtil.isEqual(noType, "2"))) {
			map.put(Constant.MESSAGE, "单号类型必须是 客户订单号或出货运单号");
			return map;
		}
		String noArray[] = StringUtil.splitW(nos);
		// 不可以
		String unAbleNos = "";
		int unAbleNoCount = 0;
		int orderCount = 0;
		String allNos = "";// 单号全部返回到页面,
		for (String no : noArray) {
			if (StringUtil.isNull(no)) {
				continue;
			}
			allNos += no + ",";
			OutWarehouseOrder param = new OutWarehouseOrder();
			if (StringUtil.isEqual(noType, "1")) {// 客户单号
				param.setCustomerReferenceNo(no);
			} else if (StringUtil.isEqual(noType, "2")) {// 顺丰运单号
				param.setTrackingNo(no);
			}
			long count = outWarehouseOrderDao.countOutWarehouseOrder(param, null);
			if (count <= 0) {
				// 单号不可以查到出库订单
				unAbleNos += no + ",";
				unAbleNoCount++;
			} else {
				orderCount += count;
			}
		}
		if (unAbleNos.endsWith(",")) {
			unAbleNos = unAbleNos.substring(0, unAbleNos.length() - 1);
		}
		map.put("unAbleNos", unAbleNos);
		map.put("allNos", allNos);
		map.put("orderCount", orderCount + "");
		map.put("unAbleNoCount", unAbleNoCount + "");
		map.put(Constant.STATUS, Constant.SUCCESS);
		return map;
	}

	@Override
	public TrackingNo getTrackingNoById(Long id) throws ServiceException {
		return trackingNoDao.getTrackingNoById(id);
	}

	@Override
	public Map<String, String> saveInWarehouseOrderItemSku(Long id, String sku) throws ServiceException {
		Map<String, String> map = new HashMap<String, String>();
		if (inWarehouseOrderItemDao.saveInWarehouseOrderItemSku(id, sku) > 0) {
			map.put(Constant.STATUS, Constant.SUCCESS);
		} else {
			map.put(Constant.STATUS, Constant.FAIL);
		}
		return map;
	}

	@Override
	public List<InWarehouseOrderStatus> findAllInWarehouseOrderStatus() throws ServiceException {
		return inWarehouseOrderStatusDao.findAllInWarehouseOrderStatus();
	}

	@Override
	public Map<String, String> submitInWarehouseRecord(Long inWarehouseRecordId) {
		InWarehouseRecord inWarehouseRecord = new InWarehouseRecord();
		inWarehouseRecord.setId(inWarehouseRecordId);
		inWarehouseRecord.setStatus(InWarehouseRecordStatusCode.NONE);
		Map<String, String> map = new HashMap<String, String>();
		if (inWarehouseRecordDao.updateInWarehouseRecordStatus(inWarehouseRecord) > 0) {
			map.put(Constant.STATUS, Constant.SUCCESS);
		} else {
			map.put(Constant.STATUS, Constant.FAIL);
		}
		return map;
	}

	@Override
	public List<Shipway> findAllShipway() throws ServiceException {
		return shipwayDao.findAllShipway();
	}
}
