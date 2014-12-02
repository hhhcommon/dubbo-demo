package com.coe.wms.controller.product;

import java.util.HashMap;
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
import com.coe.wms.model.product.Product;
import com.coe.wms.model.product.ProductType;
import com.coe.wms.model.user.User;
import com.coe.wms.service.product.IProductService;
import com.coe.wms.service.product.IProductTypeService;
import com.coe.wms.service.storage.IStorageService;
import com.coe.wms.service.user.IUserService;
import com.coe.wms.util.Constant;
import com.coe.wms.util.DateUtil;
import com.coe.wms.util.GsonUtil;
import com.coe.wms.util.Pagination;
import com.coe.wms.util.SessionConstant;
import com.coe.wms.util.StringUtil;

@Controller("products")
@RequestMapping("/products")
public class Products {

	Logger logger = Logger.getLogger(Products.class);

	@Resource(name = "storageService")
	private IStorageService storageService;

	@Resource(name = "userService")
	private IUserService userService;

	@Resource(name = "productService")
	private IProductService productService;

	@Resource(name = "productTypeService")
	private IProductTypeService productTypeService;

	/**
	 * 产品 界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/listProduct", method = RequestMethod.GET)
	public ModelAndView listProduct(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute(SessionConstant.USER_ID);
		ModelAndView view = new ModelAndView();
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		User user = userService.getUserById(userId);
		view.addObject("warehouseList",
				storageService.findAllWarehouse(user.getDefaultWarehouseId()));
		view.addObject("sevenDaysAgoStart", DateUtil.getSevenDaysAgoStart());
		view.setViewName("warehouse/product/listProduct");
		return view;
	}

	/**
	 * 产品查询
	 * 
	 * @param request
	 * @param sortorder
	 * @param sortname
	 * @param page
	 * @param pagesize
	 * @param userLoginName
	 * @param keyword
	 * @param createdTimeStart
	 * @param createdTimeEnd
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getListProductData", method = RequestMethod.POST)
	public String getListProductData(HttpServletRequest request,
			String sortorder, String sortname, Integer page, Integer pagesize,
			String userLoginName, String keyword, String createdTimeStart,
			String createdTimeEnd) {
		Pagination pagination = new Pagination();
		pagination.curPage = page;
		pagination.pageSize = pagesize;
		pagination.sortName = sortname;
		pagination.sortOrder = sortorder;
		if (StringUtil.isNotNull(createdTimeStart)
				&& createdTimeStart.contains(",")) {
			createdTimeStart = createdTimeStart.substring(
					createdTimeStart.lastIndexOf(",") + 1,
					createdTimeStart.length());
		}
		Product param = new Product();
		// 客户帐号
		if (StringUtil.isNotNull(userLoginName)) {
			Long userIdOfCustomer = userService
					.findUserIdByLoginName(userLoginName);
			param.setUserIdOfCustomer(userIdOfCustomer);
			logger.info("userIdOfCustomer:" + userIdOfCustomer);
		}
		param.setSku(keyword);
		param.setProductName(keyword);
		// 更多参数
		Map<String, String> moreParam = new HashMap<String, String>();
		moreParam.put("createdTimeStart", createdTimeStart);
		moreParam.put("createdTimeEnd", createdTimeEnd);

		pagination = productService.findProduct(param, moreParam, pagination);
		Map map = new HashMap();
		map.put("Rows", pagination.rows);
		map.put("Total", pagination.total);
		return GsonUtil.toJson(map);
	}

	/**
	 * 根据产品Id删除产品
	 * 
	 * @param request
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteProductById", method = RequestMethod.POST)
	public String deleteProductById(HttpServletRequest request, Long id) {
		logger.info("产品ID:" + id);
		Map<String, String> map = productService.deleteProductById(id);
		return GsonUtil.toJson(map);
	}
	
	@ResponseBody
	@RequestMapping(value = "/deleteProductByIds", method = RequestMethod.POST)
	public String deleteProductByIds(HttpServletRequest request, String ids) {
		Map<String, String> map = new HashMap<String, String>();
		String[] id = ids.split(",");
		for (int i = 0; i < id.length; i++) {
			Long pId = Long.parseLong(id[i]);
			map = productService.deleteProductById(pId);
		}
		return GsonUtil.toJson(map);
	}
	/**
	 * 更新界面
	 * 
	 * @param request
	 * @param response
	 * @return 显示选中跟新产品
	 */
	@RequestMapping(value = "/getProductById", method = RequestMethod.GET)
	public ModelAndView getProductById(HttpServletRequest request,
			HttpServletResponse response,Long id) {
		ModelAndView view = new ModelAndView();
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		Product product = productService.getProductById(id);
		ProductType productType = productTypeService.getProductTypeById(product.getProductTypeId());
		User user = userService.getUserById(product.getUserIdOfCustomer());
		view.addObject("product",product);
		view.addObject("productType",productType);
		view.addObject("user",user);
		view.setViewName("warehouse/product/updateProduct");
		return view;
	}

	/**
	 * 更新产品
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateProductById", method = RequestMethod.POST)
	public String updateProductById(HttpServletRequest request,Long id,String productName, String productTypeName,
			String userIdOfCustomer, String isNeedBatchNo, String sku,
			String warehouseSku, String model, Double volume,
			Double customsWeight, String currency, Double customsValue,
			String taxCode, String origin, String remark) {
		Long productTypeId = productTypeService
				.getProductTypeIdByName(productTypeName);
		Long userId = userService.findUserIdByLoginName(userIdOfCustomer);
		Long lastUpdateTime = System.currentTimeMillis();
		Product product = new Product();
		product.setId(id);
		product.setProductName(productName);
		product.setProductTypeId(productTypeId);
		product.setUserIdOfCustomer(userId);
		product.setIsNeedBatchNo(isNeedBatchNo);
		product.setSku(sku);
		product.setWarehouseSku(warehouseSku);
		product.setModel(model);
		product.setVolume(volume);
		product.setCustomsWeight(customsWeight);
		product.setCurrency(currency);
		product.setCustomsValue(customsValue);
		product.setTaxCode(taxCode);
		product.setOrigin(origin);
		product.setRemark(remark);
		product.setLastUpdateTime(lastUpdateTime);
		Map<String, String> map = productService.updateProductById(product);
		return GsonUtil.toJson(map);
	}

	/**
	 * 添加产品界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/addProduct", method = RequestMethod.GET)
	public ModelAndView addProduct(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView view = new ModelAndView();
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		view.setViewName("warehouse/product/addProduct");
		return view;
	}

	/**
	 * 添加新产品
	 * 
	 * @param request
	 * @param productName
	 * @param productTypeName
	 * @param userIdOfCustomer
	 * @param isNeedBatchNo
	 * @param sku
	 * @param warehouseSku
	 * @param model
	 * @param volume
	 * @param customsWeight
	 * @param currency
	 * @param customsValue
	 * @param taxCode
	 * @param origin
	 * @param remark
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/saveAddProduct", method = RequestMethod.POST)
	public String saveAddProduct(HttpServletRequest request,
			String productName, String productTypeName,
			String userIdOfCustomer, String isNeedBatchNo, String sku,
			String warehouseSku, String model, Double volume,
			Double customsWeight, String currency, Double customsValue,
			String taxCode, String origin, String remark) {
		Long productTypeId = productTypeService
				.getProductTypeIdByName(productTypeName);
		Long userId = userService.findUserIdByLoginName(userIdOfCustomer);
		Map<String, String> map = productService.saveAddProduct(productName,
				productTypeId, userId, isNeedBatchNo, sku, warehouseSku, model,
				volume, customsWeight, currency, customsValue, taxCode, origin,
				remark);
		return GsonUtil.toJson(map);
	}
}
