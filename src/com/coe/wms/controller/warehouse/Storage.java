package com.coe.wms.controller.warehouse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.coe.wms.controller.Application;
import com.coe.wms.model.user.User;
import com.coe.wms.model.warehouse.storage.order.InWarehouseOrder;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrder;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderItem;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderStatus;
import com.coe.wms.model.warehouse.storage.order.OutWarehouseOrderStatus.OutWarehouseOrderStatusCode;
import com.coe.wms.model.warehouse.storage.record.InWarehouseRecord;
import com.coe.wms.service.storage.IStorageService;
import com.coe.wms.service.user.IUserService;
import com.coe.wms.util.Constant;
import com.coe.wms.util.DateUtil;
import com.coe.wms.util.GsonUtil;
import com.coe.wms.util.Pagination;
import com.coe.wms.util.SessionConstant;
import com.coe.wms.util.StringUtil;

/**
 * 仓库仓配业务 控制类
 * 
 * 
 * 
 * api/Storage 是:API接受预报订单
 * 
 * 
 * warehouse/Storage 是操作员真实收货,出库
 * 
 * @author Administrator
 * 
 */
@Controller("storage")
@RequestMapping("/warehouse/storage")
public class Storage {

	private static final Logger logger = Logger.getLogger(Storage.class);

	@Resource(name = "storageService")
	private IStorageService storageService;

	@Resource(name = "userService")
	private IUserService userService;

	/**
	 * 入库订单 查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/listInWarehouseOrder", method = RequestMethod.GET)
	public ModelAndView listInWarehouseOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute(SessionConstant.USER_ID);
		ModelAndView view = new ModelAndView();
		view.addObject("userId", userId);
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		User user = userService.getUserById(userId);
		view.addObject("warehouseList", storageService.findAllWarehouse(user.getDefaultWarehouseId()));
		view.addObject("sevenDaysAgoStart", DateUtil.getSevenDaysAgoStart());
		view.setViewName("warehouse/storage/listInWarehouseOrder");
		return view;
	}

	/**
	 * 获取入库订单
	 * 
	 * @param request
	 * @param response
	 * @param userLoginName
	 *            客户登录名,仅当根据跟踪号无法找到订单时,要求输入
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/getInWarehouseOrderData")
	public String getInWarehouseOrderData(HttpServletRequest request, String sortorder, String sortname, int page, int pagesize,
			String userLoginName, Long warehouseId, String trackingNo, String createdTimeStart, String createdTimeEnd) throws IOException {
		if (StringUtil.isNotNull(createdTimeStart) && createdTimeStart.contains(",")) {
			createdTimeStart = createdTimeStart.substring(createdTimeStart.lastIndexOf(",") + 1, createdTimeStart.length());
		}

		HttpSession session = request.getSession();
		// 当前操作员
		Long userIdOfOperator = (Long) session.getAttribute(SessionConstant.USER_ID);
		Pagination pagination = new Pagination();
		pagination.curPage = page;
		pagination.pageSize = pagesize;
		pagination.sortName = sortname;
		pagination.sortOrder = sortorder;

		InWarehouseOrder param = new InWarehouseOrder();
		param.setTrackingNo(trackingNo);
		if (StringUtil.isNotNull(userLoginName)) {
			Long userIdOfCustomer = userService.findUserIdByLoginName(userLoginName);
			param.setUserIdOfCustomer(userIdOfCustomer);
		}
		param.setWarehouseId(warehouseId);
		// 更多参数
		Map<String, String> moreParam = new HashMap<String, String>();
		moreParam.put("createdTimeStart", createdTimeStart);
		moreParam.put("createdTimeEnd", createdTimeEnd);

		pagination = storageService.getInWarehouseOrderData(param, moreParam, pagination);
		Map map = new HashMap();
		map.put("Rows", pagination.rows);
		map.put("Total", pagination.total);
		return GsonUtil.toJson(map);
	}

	/**
	 * 出库订单 查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/listOutWarehouseOrder", method = RequestMethod.GET)
	public ModelAndView listOutWarehouseOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute(SessionConstant.USER_ID);
		ModelAndView view = new ModelAndView();
		view.addObject("userId", userId);
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		List<OutWarehouseOrderStatus> outWarehouseOrderStatusList = storageService.findAllOutWarehouseOrderStatus();
		view.addObject("outWarehouseOrderStatusList", outWarehouseOrderStatusList);
		User user = userService.getUserById(userId);
		view.addObject("warehouseList", storageService.findAllWarehouse(user.getDefaultWarehouseId()));
		view.addObject("sevenDaysAgoStart", DateUtil.getSevenDaysAgoStart());
		view.setViewName("warehouse/storage/listOutWarehouseOrder");
		return view;
	}

	/**
	 * 获取出库订单
	 * 
	 * @param request
	 * @param response
	 * @param userLoginName
	 *            客户登录名,仅当根据跟踪号无法找到订单时,要求输入
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/getOutWarehouseOrderData", method = RequestMethod.POST)
	public String getOutWarehouseOrderData(HttpServletRequest request, String sortorder, String sortname, int page, int pagesize,
			String userLoginName, Long warehouseId, String customerReferenceNo, String createdTimeStart, String createdTimeEnd,
			String status) throws IOException {
		if (StringUtil.isNotNull(createdTimeStart) && createdTimeStart.contains(",")) {
			createdTimeStart = createdTimeStart.substring(createdTimeStart.lastIndexOf(",") + 1, createdTimeStart.length());
		}
		HttpSession session = request.getSession();
		// 当前操作员
		Long userIdOfOperator = (Long) session.getAttribute(SessionConstant.USER_ID);
		Pagination pagination = new Pagination();
		pagination.curPage = page;
		pagination.pageSize = pagesize;
		pagination.sortName = sortname;
		pagination.sortOrder = sortorder;

		OutWarehouseOrder param = new OutWarehouseOrder();
		param.setStatus(status);
		// 客户参考号
		param.setCustomerReferenceNo(customerReferenceNo);
		// 客户帐号
		if (StringUtil.isNotNull(userLoginName)) {
			Long userIdOfCustomer = userService.findUserIdByLoginName(userLoginName);
			param.setUserIdOfCustomer(userIdOfCustomer);
		}
		// 仓库
		param.setWarehouseId(warehouseId);
		// 更多参数
		Map<String, String> moreParam = new HashMap<String, String>();
		moreParam.put("createdTimeStart", createdTimeStart);
		moreParam.put("createdTimeEnd", createdTimeEnd);

		pagination = storageService.getOutWarehouseOrderData(param, moreParam, pagination);
		Map map = new HashMap();
		map.put("Rows", pagination.rows);
		map.put("Total", pagination.total);
		return GsonUtil.toJson(map);
	}

	/**
	 * 待审核出库订单 查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/listWaitCheckOutWarehouseOrder", method = RequestMethod.GET)
	public ModelAndView listWaitCheckOutWarehouseOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute(SessionConstant.USER_ID);
		ModelAndView view = new ModelAndView();
		view.addObject("userId", userId);
		view.addObject("sevenDaysAgoStart", DateUtil.getSevenDaysAgoStart());
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		User user = userService.getUserById(userId);
		view.addObject("warehouseList", storageService.findAllWarehouse(user.getDefaultWarehouseId()));
		view.setViewName("warehouse/storage/listWaitCheckOutWarehouseOrder");
		return view;
	}

	/**
	 * 获取待审核出库订单
	 * 
	 * @param request
	 * @param response
	 * @param userLoginName
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/getWaitCheckOutWarehouseOrderData", method = RequestMethod.POST)
	public String getWaitCheckOutWarehouseOrderData(HttpServletRequest request, String sortorder, String sortname, int page, int pagesize,
			String userLoginName, Long warehouseId, String customerReferenceNo, String createdTimeStart, String createdTimeEnd,
			String status) throws IOException {
		if (StringUtil.isNotNull(createdTimeStart) && createdTimeStart.contains(",")) {
			createdTimeStart = createdTimeStart.substring(createdTimeStart.lastIndexOf(",") + 1, createdTimeStart.length());
		}
		HttpSession session = request.getSession();
		// 当前操作员
		Long userIdOfOperator = (Long) session.getAttribute(SessionConstant.USER_ID);
		Pagination pagination = new Pagination();
		pagination.curPage = page;
		pagination.pageSize = pagesize;
		pagination.sortName = sortname;
		pagination.sortOrder = sortorder;

		OutWarehouseOrder param = new OutWarehouseOrder();
		param.setStatus(OutWarehouseOrderStatusCode.WWC);
		// 客户参考号
		param.setCustomerReferenceNo(customerReferenceNo);
		// 客户帐号
		if (StringUtil.isNotNull(userLoginName)) {
			Long userIdOfCustomer = userService.findUserIdByLoginName(userLoginName);
			param.setUserIdOfCustomer(userIdOfCustomer);
		}
		// 仓库
		param.setWarehouseId(warehouseId);
		// 更多参数
		Map<String, String> moreParam = new HashMap<String, String>();
		moreParam.put("createdTimeStart", createdTimeStart);
		moreParam.put("createdTimeEnd", createdTimeEnd);

		pagination = storageService.getOutWarehouseOrderData(param, moreParam, pagination);
		Map map = new HashMap();
		map.put("Rows", pagination.rows);
		map.put("Total", pagination.total);
		return GsonUtil.toJson(map);
	}

	/**
	 * 入库订单收货
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/inWarehouse", method = RequestMethod.GET)
	public ModelAndView inWarehouse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute(SessionConstant.USER_ID);
		ModelAndView view = new ModelAndView();
		view.addObject("userId", userId);
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		User user = userService.getUserById(userId);
		view.addObject("warehouseList", storageService.findAllWarehouse(user.getDefaultWarehouseId()));
		view.setViewName("warehouse/storage/inWarehouse");
		return view;
	}

	/**
	 * 检查 跟踪号和客户帐号是否能找到唯一的入库订单
	 * 
	 * @param request
	 * @param trackingNo
	 * @param userLoginName
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/checkFindInWarehouseOrder")
	public String checkFindInWarehouseOrder(HttpServletRequest request, String trackingNo, String userLoginName) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constant.STATUS, Constant.FAIL);
		InWarehouseOrder param = new InWarehouseOrder();
		param.setTrackingNo(trackingNo);
		List<Map<String, String>> mapList = storageService.checkInWarehouseOrder(param);
		if (mapList.size() < 1) {
			map.put(Constant.STATUS, "-1");
			map.put(Constant.MESSAGE, "该单号无预报入库订单,请先添加入库订单.");
			return GsonUtil.toJson(map);
		}
		map.put("mapList", mapList);
		if (mapList.size() > 1) {
			// 找到多个入库订单,返回跟踪号,承运商,参考号,客户等信息供操作员选择
			map.put(Constant.MESSAGE, "该单号找到超过一个入库订单,请选择其中一个.");
			map.put(Constant.STATUS, "2");
			return GsonUtil.toJson(map);
		}
		map.put(Constant.STATUS, Constant.SUCCESS);
		return GsonUtil.toJson(map);
	}

	/**
	 * 保存入库主单
	 * 
	 * @param request
	 * @param trackingNo
	 * @param userLoginName
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/saveInWarehouseRecord")
	public String saveInWarehouseRecord(HttpServletRequest request, String trackingNo, Long warehouseId, Long inWarehouseOrderId,
			String remark) throws IOException {
		// 操作员
		Long userIdOfOperator = (Long) request.getSession().getAttribute(SessionConstant.USER_ID);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.STATUS, Constant.FAIL);
		// 校验和保存
		Map<String, String> serviceResult = storageService.saveInWarehouseRecord(trackingNo, remark, userIdOfOperator, warehouseId,
				inWarehouseOrderId);
		// 成功,返回id
		map.put("id", serviceResult.get("id"));
		// 失败
		if (!StringUtil.isEqual(serviceResult.get(Constant.STATUS), Constant.SUCCESS)) {
			map.put(Constant.MESSAGE, serviceResult.get(Constant.MESSAGE));
			return GsonUtil.toJson(map);
		}
		map.put(Constant.STATUS, Constant.SUCCESS);
		return GsonUtil.toJson(map);
	}

	/**
	 * 保存入库明细
	 * 
	 * @param request
	 * @param itemSku
	 *            物品sku
	 * @param itemQuantity
	 *            收货数量
	 * @param itemRemark
	 *            备注
	 * @param warehouseId
	 *            仓库id
	 * @param shelvesNo
	 *            货架
	 * @param seatNo
	 *            货位
	 * @param inWarehouseRecordId
	 *            所属的入库主单
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/saveInWarehouseRecordItem", method = RequestMethod.POST)
	public String saveInWarehouseRecordItem(HttpServletRequest request, String itemSku, Integer itemQuantity, String itemRemark,
			Long warehouseId, String shelvesNo, String seatNo, Long inWarehouseRecordId) throws IOException {
		// 操作员
		Long userIdOfOperator = (Long) request.getSession().getAttribute(SessionConstant.USER_ID);
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constant.STATUS, Constant.FAIL);
		// 校验和保存
		Map<String, String> serviceResult = storageService.saveInWarehouseRecordItem(itemSku, itemQuantity, itemRemark, warehouseId,
				inWarehouseRecordId, userIdOfOperator);
		// 失败
		if (!StringUtil.isEqual(serviceResult.get(Constant.STATUS), Constant.SUCCESS)) {
			map.put(Constant.MESSAGE, serviceResult.get(Constant.MESSAGE));
			return GsonUtil.toJson(map);
		}
		map.put(Constant.STATUS, Constant.SUCCESS);
		return GsonUtil.toJson(map);
	}

	/**
	 * 获取入库记录明细
	 * 
	 * @param request
	 * @param response
	 * @param userLoginName
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/getInWarehouseRecordItemData")
	public String getInWarehouseRecordItemData(HttpServletRequest request, String sortorder, String sortname, int page, int pagesize,
			Long inWarehouseRecordId) throws IOException {
		Map map = new HashMap();
		if (inWarehouseRecordId == null) {
			return GsonUtil.toJson(map);
		}
		HttpSession session = request.getSession();
		// 当前操作员
		Long userIdOfOperator = (Long) session.getAttribute(SessionConstant.USER_ID);
		Pagination pagination = new Pagination();
		pagination.curPage = page;
		pagination.pageSize = pagesize;
		pagination.sortName = sortname;
		pagination.sortOrder = sortorder;
		// 客户id
		pagination = storageService.getInWarehouseRecordItemData(inWarehouseRecordId, pagination);
		if (pagination != null) {
			map.put("Rows", pagination.rows);
			map.put("Total", pagination.total);
		}
		return GsonUtil.toJson(map);
	}

	@ResponseBody
	@RequestMapping(value = "/getInWarehouseOrderItemByOrderId", method = RequestMethod.POST)
	public String getInWarehouseOrderItemByOrderId(Long orderId) {
		List<Map<String, String>> mapList = storageService.getInWarehouseOrderItemMap(orderId);
		return GsonUtil.toJson(mapList);
	}

	@ResponseBody
	@RequestMapping(value = "/getInWarehouseRecordItemByRecordId", method = RequestMethod.POST)
	public String getInWarehouseRecordItemByRecordId(Long recordId) {
		List<Map<String, String>> mapList = storageService.getInWarehouseRecordItemMapByRecordId(recordId);
		return GsonUtil.toJson(mapList);
	}

	@ResponseBody
	@RequestMapping(value = "/getOutWarehouseOrderItemByOrderId", method = RequestMethod.POST)
	public String getOutWarehouseOrderItemByOrderId(Long orderId) {
		List<OutWarehouseOrderItem> outWarehouseOrderItemList = storageService.getOutWarehouseOrderItem(orderId);
		return GsonUtil.toJson(outWarehouseOrderItemList);
	}

	/**
	 * 获取入库订单明细(头程运单号下的所有SKU和数量)
	 * 
	 * @param request
	 * @param response
	 * @param userLoginName
	 *            客户登录名,仅当根据跟踪号无法找到订单时,要求输入
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/getInWarehouseOrderItem", method = RequestMethod.POST)
	public String getInWarehouseOrderItem(HttpServletRequest request, String sortorder, String sortname, int page, int pagesize,
			String trackingNo, String userLoginName) throws IOException {
		Map map = new HashMap();
		logger.info("trackingNo:" + trackingNo + "  userLoginName:" + userLoginName);
		logger.info("sortorder:" + sortorder + "  sortname:" + sortname + " page:" + page + " pagesize:" + pagesize);
		HttpSession session = request.getSession();
		// 当前操作员
		Long userIdOfOperator = (Long) session.getAttribute(SessionConstant.USER_ID);
		Pagination pagination = new Pagination();
		pagination.curPage = page;
		pagination.pageSize = pagesize;
		pagination.sortName = sortname;
		pagination.sortOrder = sortorder;
		// 客户id
		Long userIdOfCustomer = userService.findUserIdByLoginName(userLoginName);
		InWarehouseOrder param = new InWarehouseOrder();
		param.setTrackingNo(trackingNo);
		param.setUserIdOfCustomer(userIdOfCustomer);
		// 执行查询(应当只有1个结果, 多个结果,只取第一个结果. 跟踪号重复的情况 ,待处理)
		List<InWarehouseOrder> inWarehouseOrderList = storageService.findInWarehouseOrder(param, null, null);
		if (inWarehouseOrderList.size() < 0) {
			return GsonUtil.toJson(map);
		}
		InWarehouseOrder order = inWarehouseOrderList.get(0);
		pagination = storageService.getInWarehouseOrderItemData(order.getId(), pagination);
		map.put("Rows", pagination.rows);
		map.put("Total", pagination.total);

		return GsonUtil.toJson(map);
	}

	/**
	 * 入库主单 查询
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/listInWarehouseRecord", method = RequestMethod.GET)
	public ModelAndView listInWarehouseRecord(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute(SessionConstant.USER_ID);
		ModelAndView view = new ModelAndView();
		view.addObject("userId", userId);
		User user = userService.getUserById(userId);
		view.addObject("warehouseList", storageService.findAllWarehouse(user.getDefaultWarehouseId()));
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		view.addObject("sevenDaysAgoStart", DateUtil.getSevenDaysAgoStart());
		view.setViewName("warehouse/storage/listInWarehouseRecord");
		return view;
	}

	/**
	 * 获取入库记录
	 * 
	 * @param request
	 * @param response
	 * @param userLoginName
	 *            客户登录名,仅当根据跟踪号无法找到订单时,要求输入
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/getInWarehouseRecordData")
	public String getInWarehouseRecordData(HttpServletRequest request, String sortorder, String sortname, int page, int pagesize,
			String userLoginName, Long warehouseId, String trackingNo, String batchNo, String createdTimeStart, String createdTimeEnd)
			throws IOException {
		if (StringUtil.isNotNull(createdTimeStart) && createdTimeStart.contains(",")) {
			createdTimeStart = createdTimeStart.substring(createdTimeStart.lastIndexOf(",") + 1, createdTimeStart.length());
		}

		HttpSession session = request.getSession();
		// 当前操作员
		Long userIdOfOperator = (Long) session.getAttribute(SessionConstant.USER_ID);
		Pagination pagination = new Pagination();
		pagination.curPage = page;
		pagination.pageSize = pagesize;
		pagination.sortName = sortname;
		pagination.sortOrder = sortorder;
		InWarehouseRecord param = new InWarehouseRecord();
		param.setTrackingNo(trackingNo);
		if (StringUtil.isNotNull(userLoginName)) {
			Long userIdOfCustomer = userService.findUserIdByLoginName(userLoginName);
			param.setUserIdOfCustomer(userIdOfCustomer);
		}
		param.setWarehouseId(warehouseId);
		param.setBatchNo(batchNo);

		// 更多参数
		Map<String, String> moreParam = new HashMap<String, String>();
		moreParam.put("createdTimeStart", createdTimeStart);
		moreParam.put("createdTimeEnd", createdTimeEnd);
		pagination = storageService.getInWarehouseRecordData(param, moreParam, pagination);
		Map map = new HashMap();
		map.put("Rows", pagination.rows);
		map.put("Total", pagination.total);
		return GsonUtil.toJson(map);
	}

	/**
	 * 
	 * 审核出库订单
	 * 
	 * checkResult:1 审核通过 2审核不通过
	 */
	@ResponseBody
	@RequestMapping(value = "/checkOutWarehouseOrder")
	public String checkOutWarehouseOrder(HttpServletRequest request, String orderIds, Integer checkResult) throws IOException {
		HttpSession session = request.getSession();
		// 当前操作员
		Long userIdOfOperator = (Long) session.getAttribute(SessionConstant.USER_ID);
		logger.info("审核出库 操作员id:" + userIdOfOperator + " checkResult:" + checkResult + " 订单:" + orderIds);
		Map<String, String> checkResultMap = storageService.checkOutWarehouseOrder(orderIds, checkResult, userIdOfOperator);
		return GsonUtil.toJson(checkResultMap);
	}

	/**
	 * 出库称重和打印发货单
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/outWarehouseCheckPackage", method = RequestMethod.GET)
	public ModelAndView outWarehouseCheckPackage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute(SessionConstant.USER_ID);
		ModelAndView view = new ModelAndView();
		view.addObject("userId", userId);
		User user = userService.getUserById(userId);
		view.addObject("warehouseList", storageService.findAllWarehouse(user.getDefaultWarehouseId()));
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		view.setViewName("warehouse/storage/outWarehouseCheckPackage");
		return view;
	}

	/**
	 * 
	 * 扫描捡货单上的清单号 (客户参考号) 进行复核重量和SKU数量
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/outWarehouseSubmitCustomerReferenceNo", method = RequestMethod.POST)
	public String outWarehouseSubmitCustomerReferenceNo(HttpServletRequest request, HttpServletResponse response, String customerReferenceNo)
			throws IOException {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute(SessionConstant.USER_ID);
		Map<String, Object> checkResultMap = storageService.outWarehouseSubmitCustomerReferenceNo(customerReferenceNo, userId);
		return GsonUtil.toJson(checkResultMap);
	}

	/**
	 * 复核SKU数量与称重,提交称重
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/outWarehouseSubmitWeight", method = RequestMethod.POST)
	public String outWarehouseSubmitWeight(HttpServletRequest request, HttpServletResponse response, String customerReferenceNo,
			Double outWarehouseOrderWeight) throws IOException {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute(SessionConstant.USER_ID);
		Map<String, Object> checkResultMap = storageService.outWarehouseSubmitWeight(customerReferenceNo, outWarehouseOrderWeight, userId);
		return GsonUtil.toJson(checkResultMap);
	}

	/**
	 * 出库扫描运单界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/outWarehouseShipping", method = RequestMethod.GET)
	public ModelAndView outWarehouseShipping(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute(SessionConstant.USER_ID);
		ModelAndView view = new ModelAndView();
		view.addObject("userId", userId);
		User user = userService.getUserById(userId);
		view.addObject("warehouseList", storageService.findAllWarehouse(user.getDefaultWarehouseId()));
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		view.setViewName("warehouse/storage/outWarehouseShipping");
		return view;
	}

	/**
	 * 扫运单动作,确认出库
	 * 
	 * 目前采用扫描出货条码,可能重复, 待添加 当重复时,扫描客户参考号等
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/outWarehouseShippingConfirm")
	public String outWarehouseShippingConfirm(HttpServletRequest request, HttpServletResponse response, String trackingNo)
			throws IOException {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute(SessionConstant.USER_ID);
		Map<String, String> checkResultMap = storageService.outWarehouseShippingConfirm(trackingNo, userId);
		return GsonUtil.toJson(checkResultMap);
	}

	/**
	 * 上架界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/onShelves", method = RequestMethod.GET)
	public ModelAndView onShelves(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView view = new ModelAndView();
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		view.setViewName("warehouse/storage/onShelves");
		return view;
	}
}
