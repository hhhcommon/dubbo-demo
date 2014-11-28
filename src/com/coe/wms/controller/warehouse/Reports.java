package com.coe.wms.controller.warehouse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.coe.wms.controller.Application;
import com.coe.wms.model.user.User;
import com.coe.wms.model.warehouse.report.Report;
import com.coe.wms.model.warehouse.report.ReportType.ReportTypeCode;
import com.coe.wms.service.inventory.IItemInventoryService;
import com.coe.wms.service.report.IReportService;
import com.coe.wms.service.storage.IStorageService;
import com.coe.wms.service.user.IUserService;
import com.coe.wms.util.Constant;
import com.coe.wms.util.DateUtil;
import com.coe.wms.util.GsonUtil;
import com.coe.wms.util.Pagination;
import com.coe.wms.util.SessionConstant;
import com.coe.wms.util.StringUtil;

@Controller("report")
@RequestMapping("/warehouse/report")
public class Reports {

	private static final Logger logger = Logger.getLogger(Reports.class);

	@Resource(name = "storageService")
	private IStorageService storageService;

	@Resource(name = "reportService")
	private IReportService reportService;

	@Resource(name = "itemInventoryService")
	private IItemInventoryService itemInventoryService;

	@Resource(name = "userService")
	private IUserService userService;

	/**
	 * 自定义报表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/diyReport", method = RequestMethod.GET)
	public ModelAndView diyReport(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView view = new ModelAndView();
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute(SessionConstant.USER_ID);
		User user = userService.getUserById(userId);
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		view.addObject("warehouseList", storageService.findAllWarehouse(user.getDefaultWarehouseId()));
		view.addObject("reportTypeList", reportService.findAllReportType());
		view.addObject("todayStart", DateUtil.getTodayStart());
		view.setViewName("warehouse/report/diyReport");
		return view;
	}

	/**
	 * 下载报表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/listReport", method = RequestMethod.GET)
	public ModelAndView listReport(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView view = new ModelAndView();
		HttpSession session = request.getSession();
		Long userId = (Long) session.getAttribute(SessionConstant.USER_ID);
		User user = userService.getUserById(userId);
		view.addObject(Application.getBaseUrlName(), Application.getBaseUrl());
		view.addObject("warehouseList", storageService.findAllWarehouse(user.getDefaultWarehouseId()));
		view.addObject("sevenDaysAgoStart", DateUtil.getSevenDaysAgoStart());
		view.addObject("reportTypeList", reportService.findAllReportType());
		view.setViewName("warehouse/report/listReport");
		return view;
	}

	/**
	 * 获取报表数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/getListReportData")
	public String getListReportData(HttpServletRequest request, String sortorder, String sortname, int page, int pagesize, String userLoginName, Long warehouseId, String reportType, String reportName, String createdTimeStart, String createdTimeEnd)
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
		Report param = new Report();
		if (StringUtil.isNotNull(userLoginName)) {
			Long userIdOfCustomer = userService.findUserIdByLoginName(userLoginName);
			param.setUserIdOfCustomer(userIdOfCustomer);
		}
		param.setWarehouseId(warehouseId);
		param.setReportName(reportName);
		param.setReportType(reportType);
		// 更多参数
		Map<String, String> moreParam = new HashMap<String, String>();
		moreParam.put("createdTimeStart", createdTimeStart);
		moreParam.put("createdTimeEnd", createdTimeEnd);
		pagination = reportService.getListReportData(param, moreParam, pagination);
		Map map = new HashMap();
		map.put("Rows", pagination.rows);
		map.put("Total", pagination.total);
		return GsonUtil.toJson(map);
	}

	/**
	 * 下载报表文件
	 * 
	 * @param response
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "downloadReport", method = RequestMethod.GET)
	public void downloadReport(HttpServletResponse response, Long reportId) throws IOException {
		OutputStream os = response.getOutputStream();
		try {
			Report report = reportService.getReportById(reportId);
			String filePathAndName = report.getFilePath();
			int a = filePathAndName.lastIndexOf("\\");
			int b = filePathAndName.lastIndexOf("/");
			String fileName = filePathAndName.substring((a > b ? a : b) + 1, filePathAndName.length());
			response.reset();
			response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
			response.setContentType("application/octet-stream; charset=utf-8");
			File file = new File(filePathAndName);
			os.write(FileUtils.readFileToByteArray(file));
			os.flush();
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}

	/**
	 * 获取报表数据
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/saveDiyReport")
	public String saveDiyReport(HttpServletRequest request, String userLoginName, Long warehouseId, String reportType, String reportName, String inWarehouseTimeStart, String inWarehouseTimeEnd, String outWarehouseTimeStart, String outWarehouseTimeEnd)
			throws IOException {
		HttpSession session = request.getSession();
		// 当前操作员
		Long userIdOfOperator = (Long) session.getAttribute(SessionConstant.USER_ID);
		// 检查参数
		Map<String, String> map = reportService.checkAddReport(userLoginName, warehouseId, reportType, reportName, inWarehouseTimeStart, inWarehouseTimeEnd, outWarehouseTimeStart, outWarehouseTimeEnd, userIdOfOperator);
		if (StringUtil.isEqual(map.get(Constant.STATUS), Constant.FAIL)) {
			return GsonUtil.toJson(map);
		}
		Long userIdOfCustomer = userService.findUserIdByLoginName(userLoginName);
		if (StringUtil.isEqual(reportType, ReportTypeCode.IN_WAREHOUSE_REPORT)) {
			map = reportService.addInWarehouseReport(userIdOfCustomer, warehouseId, reportName, inWarehouseTimeStart, inWarehouseTimeEnd);
		}
		if (StringUtil.isEqual(reportType, ReportTypeCode.OUT_WAREHOUSE_REPORT)) {
			map = reportService.addOutWarehouseReport(userIdOfCustomer, warehouseId, reportName, outWarehouseTimeStart, outWarehouseTimeEnd);
		}
		return GsonUtil.toJson(map);
	}
}
