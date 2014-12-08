package com.coe.wms.dao.warehouse.transport.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.coe.wms.dao.datasource.DataSource;
import com.coe.wms.dao.datasource.DataSourceCode;
import com.coe.wms.dao.warehouse.transport.IOrderAdditionalSfDao;
import com.coe.wms.model.warehouse.transport.OrderAdditionalSf;
import com.coe.wms.util.NumberUtil;
import com.coe.wms.util.Pagination;
import com.coe.wms.util.StringUtil;
import com.mysql.jdbc.Statement;

/**
 * 
 * @author Administrator
 */
@Repository("orderAdditionalSfDao")
public class OrderAdditionalSfDaoImpl implements IOrderAdditionalSfDao {

	Logger logger = Logger.getLogger(OrderAdditionalSfDaoImpl.class);

	@Resource(name = "jdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	/**
	 */
	@Override
	@DataSource(DataSourceCode.WMS)
	public long saveOrderAdditionalSf(final OrderAdditionalSf receiver) {
		final String sql = "insert into w_t_order_additional_sf (order_id,carrier_code,mail_no,sender_address,cust_id,pay_method,shipper_code,delivery_code,customer_order_id) values (?,?,?,?,?,?,?,?,?)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, receiver.getOrderId());
				ps.setString(2, receiver.getCarrierCode());
				ps.setString(3, receiver.getMailNo());
				ps.setString(4, receiver.getSenderAddress());
				ps.setString(5, receiver.getCustId());
				ps.setString(6, receiver.getPayMethod());
				ps.setString(7, receiver.getShipperCode());
				ps.setString(8, receiver.getDeliveryCode());
				ps.setString(9, receiver.getCustomerOrderId());
				return ps;
			}
		}, keyHolder);
		long id = keyHolder.getKey().longValue();
		return id;
	}

	/**
	 */
	@Override
	@DataSource(DataSourceCode.WMS)
	public int saveBatchOrderAdditionalSf(final List<OrderAdditionalSf> receiverList) {
		final String sql = "insert into w_t_order_additional_sf (order_id,carrier_code,mail_no,sender_address,cust_id,pay_method,shipper_code,delivery_code,customer_order_id) values (?,?,?,?,?,?,?,?,?)";
		int[] batchUpdateSize = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				OrderAdditionalSf receiver = receiverList.get(i);
				ps.setLong(1, receiver.getOrderId());
				ps.setString(2, receiver.getCarrierCode());
				ps.setString(3, receiver.getMailNo());
				ps.setString(4, receiver.getSenderAddress());
				ps.setString(5, receiver.getCustId());
				ps.setString(6, receiver.getPayMethod());
				ps.setString(7, receiver.getShipperCode());
				ps.setString(8, receiver.getDeliveryCode());
				ps.setString(9, receiver.getCustomerOrderId());
			}

			@Override
			public int getBatchSize() {
				return receiverList.size();
			}
		});
		return NumberUtil.sumArry(batchUpdateSize);
	}

	@Override
	@DataSource(DataSourceCode.WMS)
	public int saveBatchOrderAdditionalSfWithOrderId(final List<OrderAdditionalSf> receiverList, final Long orderId) {
		final String sql = "insert into w_t_order_additional_sf (order_id,carrier_code,mail_no,sender_address,cust_id,pay_method,shipper_code,delivery_code,customer_order_id) values (?,?,?,?,?,?,?,?,?)";
		int[] batchUpdateSize = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				OrderAdditionalSf receiver = receiverList.get(i);
				ps.setLong(1, orderId);
				ps.setString(2, receiver.getCarrierCode());
				ps.setString(3, receiver.getMailNo());
				ps.setString(4, receiver.getSenderAddress());
				ps.setString(5, receiver.getCustId());
				ps.setString(6, receiver.getPayMethod());
				ps.setString(7, receiver.getShipperCode());
				ps.setString(8, receiver.getDeliveryCode());
				ps.setString(9, receiver.getCustomerOrderId());
			}

			@Override
			public int getBatchSize() {
				return receiverList.size();
			}
		});
		return NumberUtil.sumArry(batchUpdateSize);
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * 查询出库订单收件人
	 * 
	 * 参数一律使用实体类加Map .
	 */
	@Override
	public List<OrderAdditionalSf> findOrderAdditionalSf(OrderAdditionalSf OrderAdditionalSf, Map<String, String> moreParam, Pagination page) {
		StringBuffer sb = new StringBuffer();
		sb.append("select id,order_id,carrier_code,mail_no,sender_address,cust_id,pay_method,shipper_code,delivery_code,order_id from w_t_order_additional_sf where 1=1 ");
		if (OrderAdditionalSf != null) {
			if (StringUtil.isNotNull(OrderAdditionalSf.getCarrierCode())) {
				sb.append(" and carrier_code = '" + OrderAdditionalSf.getCarrierCode() + "' ");
			}
			if (StringUtil.isNotNull(OrderAdditionalSf.getMailNo())) {
				sb.append(" and mail_no = '" + OrderAdditionalSf.getMailNo() + "' ");
			}
			if (OrderAdditionalSf.getId() != null) {
				sb.append(" and id = '" + OrderAdditionalSf.getId() + "' ");
			}
			if (OrderAdditionalSf.getOrderId() != null) {
				sb.append(" and order_id = " + OrderAdditionalSf.getOrderId());
			}
		}
		// 分页sql
		sb.append(page.generatePageSql());
		String sql = sb.toString();
		List<OrderAdditionalSf> OrderAdditionalSfList = jdbcTemplate.query(sql, ParameterizedBeanPropertyRowMapper.newInstance(OrderAdditionalSf.class));
		return OrderAdditionalSfList;
	}

	@Override
	public OrderAdditionalSf getOrderAdditionalSfByOrderId(Long orderId) {
		String sql = "select id,order_id,carrier_code,mail_no,sender_address,cust_id,pay_method,shipper_code,delivery_code,order_id from w_t_order_additional_sf where order_id = " + orderId;
		List<OrderAdditionalSf> OrderAdditionalSfList = jdbcTemplate.query(sql, ParameterizedBeanPropertyRowMapper.newInstance(OrderAdditionalSf.class));
		if (OrderAdditionalSfList != null && OrderAdditionalSfList.size() > 0) {
			return OrderAdditionalSfList.get(0);
		}
		return null;
	}
}