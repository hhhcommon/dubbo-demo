package com.coe.message.entity;

import java.util.ArrayList;
import java.util.List;

public class MessageResponseExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public MessageResponseExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andMessageIdIsNull() {
            addCriterion("message_id is null");
            return (Criteria) this;
        }

        public Criteria andMessageIdIsNotNull() {
            addCriterion("message_id is not null");
            return (Criteria) this;
        }

        public Criteria andMessageIdEqualTo(Long value) {
            addCriterion("message_id =", value, "messageId");
            return (Criteria) this;
        }

        public Criteria andMessageIdNotEqualTo(Long value) {
            addCriterion("message_id <>", value, "messageId");
            return (Criteria) this;
        }

        public Criteria andMessageIdGreaterThan(Long value) {
            addCriterion("message_id >", value, "messageId");
            return (Criteria) this;
        }

        public Criteria andMessageIdGreaterThanOrEqualTo(Long value) {
            addCriterion("message_id >=", value, "messageId");
            return (Criteria) this;
        }

        public Criteria andMessageIdLessThan(Long value) {
            addCriterion("message_id <", value, "messageId");
            return (Criteria) this;
        }

        public Criteria andMessageIdLessThanOrEqualTo(Long value) {
            addCriterion("message_id <=", value, "messageId");
            return (Criteria) this;
        }

        public Criteria andMessageIdIn(List<Long> values) {
            addCriterion("message_id in", values, "messageId");
            return (Criteria) this;
        }

        public Criteria andMessageIdNotIn(List<Long> values) {
            addCriterion("message_id not in", values, "messageId");
            return (Criteria) this;
        }

        public Criteria andMessageIdBetween(Long value1, Long value2) {
            addCriterion("message_id between", value1, value2, "messageId");
            return (Criteria) this;
        }

        public Criteria andMessageIdNotBetween(Long value1, Long value2) {
            addCriterion("message_id not between", value1, value2, "messageId");
            return (Criteria) this;
        }

        public Criteria andHttpStatusIsNull() {
            addCriterion("http_status is null");
            return (Criteria) this;
        }

        public Criteria andHttpStatusIsNotNull() {
            addCriterion("http_status is not null");
            return (Criteria) this;
        }

        public Criteria andHttpStatusEqualTo(Integer value) {
            addCriterion("http_status =", value, "httpStatus");
            return (Criteria) this;
        }

        public Criteria andHttpStatusNotEqualTo(Integer value) {
            addCriterion("http_status <>", value, "httpStatus");
            return (Criteria) this;
        }

        public Criteria andHttpStatusGreaterThan(Integer value) {
            addCriterion("http_status >", value, "httpStatus");
            return (Criteria) this;
        }

        public Criteria andHttpStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("http_status >=", value, "httpStatus");
            return (Criteria) this;
        }

        public Criteria andHttpStatusLessThan(Integer value) {
            addCriterion("http_status <", value, "httpStatus");
            return (Criteria) this;
        }

        public Criteria andHttpStatusLessThanOrEqualTo(Integer value) {
            addCriterion("http_status <=", value, "httpStatus");
            return (Criteria) this;
        }

        public Criteria andHttpStatusIn(List<Integer> values) {
            addCriterion("http_status in", values, "httpStatus");
            return (Criteria) this;
        }

        public Criteria andHttpStatusNotIn(List<Integer> values) {
            addCriterion("http_status not in", values, "httpStatus");
            return (Criteria) this;
        }

        public Criteria andHttpStatusBetween(Integer value1, Integer value2) {
            addCriterion("http_status between", value1, value2, "httpStatus");
            return (Criteria) this;
        }

        public Criteria andHttpStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("http_status not between", value1, value2, "httpStatus");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgIsNull() {
            addCriterion("http_status_msg is null");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgIsNotNull() {
            addCriterion("http_status_msg is not null");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgEqualTo(String value) {
            addCriterion("http_status_msg =", value, "httpStatusMsg");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgNotEqualTo(String value) {
            addCriterion("http_status_msg <>", value, "httpStatusMsg");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgGreaterThan(String value) {
            addCriterion("http_status_msg >", value, "httpStatusMsg");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgGreaterThanOrEqualTo(String value) {
            addCriterion("http_status_msg >=", value, "httpStatusMsg");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgLessThan(String value) {
            addCriterion("http_status_msg <", value, "httpStatusMsg");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgLessThanOrEqualTo(String value) {
            addCriterion("http_status_msg <=", value, "httpStatusMsg");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgLike(String value) {
            addCriterion("http_status_msg like", value, "httpStatusMsg");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgNotLike(String value) {
            addCriterion("http_status_msg not like", value, "httpStatusMsg");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgIn(List<String> values) {
            addCriterion("http_status_msg in", values, "httpStatusMsg");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgNotIn(List<String> values) {
            addCriterion("http_status_msg not in", values, "httpStatusMsg");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgBetween(String value1, String value2) {
            addCriterion("http_status_msg between", value1, value2, "httpStatusMsg");
            return (Criteria) this;
        }

        public Criteria andHttpStatusMsgNotBetween(String value1, String value2) {
            addCriterion("http_status_msg not between", value1, value2, "httpStatusMsg");
            return (Criteria) this;
        }

        public Criteria andSendBeginTimeIsNull() {
            addCriterion("send_begin_time is null");
            return (Criteria) this;
        }

        public Criteria andSendBeginTimeIsNotNull() {
            addCriterion("send_begin_time is not null");
            return (Criteria) this;
        }

        public Criteria andSendBeginTimeEqualTo(Long value) {
            addCriterion("send_begin_time =", value, "sendBeginTime");
            return (Criteria) this;
        }

        public Criteria andSendBeginTimeNotEqualTo(Long value) {
            addCriterion("send_begin_time <>", value, "sendBeginTime");
            return (Criteria) this;
        }

        public Criteria andSendBeginTimeGreaterThan(Long value) {
            addCriterion("send_begin_time >", value, "sendBeginTime");
            return (Criteria) this;
        }

        public Criteria andSendBeginTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("send_begin_time >=", value, "sendBeginTime");
            return (Criteria) this;
        }

        public Criteria andSendBeginTimeLessThan(Long value) {
            addCriterion("send_begin_time <", value, "sendBeginTime");
            return (Criteria) this;
        }

        public Criteria andSendBeginTimeLessThanOrEqualTo(Long value) {
            addCriterion("send_begin_time <=", value, "sendBeginTime");
            return (Criteria) this;
        }

        public Criteria andSendBeginTimeIn(List<Long> values) {
            addCriterion("send_begin_time in", values, "sendBeginTime");
            return (Criteria) this;
        }

        public Criteria andSendBeginTimeNotIn(List<Long> values) {
            addCriterion("send_begin_time not in", values, "sendBeginTime");
            return (Criteria) this;
        }

        public Criteria andSendBeginTimeBetween(Long value1, Long value2) {
            addCriterion("send_begin_time between", value1, value2, "sendBeginTime");
            return (Criteria) this;
        }

        public Criteria andSendBeginTimeNotBetween(Long value1, Long value2) {
            addCriterion("send_begin_time not between", value1, value2, "sendBeginTime");
            return (Criteria) this;
        }

        public Criteria andSendEndTimeIsNull() {
            addCriterion("send_end_time is null");
            return (Criteria) this;
        }

        public Criteria andSendEndTimeIsNotNull() {
            addCriterion("send_end_time is not null");
            return (Criteria) this;
        }

        public Criteria andSendEndTimeEqualTo(Long value) {
            addCriterion("send_end_time =", value, "sendEndTime");
            return (Criteria) this;
        }

        public Criteria andSendEndTimeNotEqualTo(Long value) {
            addCriterion("send_end_time <>", value, "sendEndTime");
            return (Criteria) this;
        }

        public Criteria andSendEndTimeGreaterThan(Long value) {
            addCriterion("send_end_time >", value, "sendEndTime");
            return (Criteria) this;
        }

        public Criteria andSendEndTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("send_end_time >=", value, "sendEndTime");
            return (Criteria) this;
        }

        public Criteria andSendEndTimeLessThan(Long value) {
            addCriterion("send_end_time <", value, "sendEndTime");
            return (Criteria) this;
        }

        public Criteria andSendEndTimeLessThanOrEqualTo(Long value) {
            addCriterion("send_end_time <=", value, "sendEndTime");
            return (Criteria) this;
        }

        public Criteria andSendEndTimeIn(List<Long> values) {
            addCriterion("send_end_time in", values, "sendEndTime");
            return (Criteria) this;
        }

        public Criteria andSendEndTimeNotIn(List<Long> values) {
            addCriterion("send_end_time not in", values, "sendEndTime");
            return (Criteria) this;
        }

        public Criteria andSendEndTimeBetween(Long value1, Long value2) {
            addCriterion("send_end_time between", value1, value2, "sendEndTime");
            return (Criteria) this;
        }

        public Criteria andSendEndTimeNotBetween(Long value1, Long value2) {
            addCriterion("send_end_time not between", value1, value2, "sendEndTime");
            return (Criteria) this;
        }

        public Criteria andUsedTimeIsNull() {
            addCriterion("used_time is null");
            return (Criteria) this;
        }

        public Criteria andUsedTimeIsNotNull() {
            addCriterion("used_time is not null");
            return (Criteria) this;
        }

        public Criteria andUsedTimeEqualTo(Long value) {
            addCriterion("used_time =", value, "usedTime");
            return (Criteria) this;
        }

        public Criteria andUsedTimeNotEqualTo(Long value) {
            addCriterion("used_time <>", value, "usedTime");
            return (Criteria) this;
        }

        public Criteria andUsedTimeGreaterThan(Long value) {
            addCriterion("used_time >", value, "usedTime");
            return (Criteria) this;
        }

        public Criteria andUsedTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("used_time >=", value, "usedTime");
            return (Criteria) this;
        }

        public Criteria andUsedTimeLessThan(Long value) {
            addCriterion("used_time <", value, "usedTime");
            return (Criteria) this;
        }

        public Criteria andUsedTimeLessThanOrEqualTo(Long value) {
            addCriterion("used_time <=", value, "usedTime");
            return (Criteria) this;
        }

        public Criteria andUsedTimeIn(List<Long> values) {
            addCriterion("used_time in", values, "usedTime");
            return (Criteria) this;
        }

        public Criteria andUsedTimeNotIn(List<Long> values) {
            addCriterion("used_time not in", values, "usedTime");
            return (Criteria) this;
        }

        public Criteria andUsedTimeBetween(Long value1, Long value2) {
            addCriterion("used_time between", value1, value2, "usedTime");
            return (Criteria) this;
        }

        public Criteria andUsedTimeNotBetween(Long value1, Long value2) {
            addCriterion("used_time not between", value1, value2, "usedTime");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusDescIsNull() {
            addCriterion("status_desc is null");
            return (Criteria) this;
        }

        public Criteria andStatusDescIsNotNull() {
            addCriterion("status_desc is not null");
            return (Criteria) this;
        }

        public Criteria andStatusDescEqualTo(String value) {
            addCriterion("status_desc =", value, "statusDesc");
            return (Criteria) this;
        }

        public Criteria andStatusDescNotEqualTo(String value) {
            addCriterion("status_desc <>", value, "statusDesc");
            return (Criteria) this;
        }

        public Criteria andStatusDescGreaterThan(String value) {
            addCriterion("status_desc >", value, "statusDesc");
            return (Criteria) this;
        }

        public Criteria andStatusDescGreaterThanOrEqualTo(String value) {
            addCriterion("status_desc >=", value, "statusDesc");
            return (Criteria) this;
        }

        public Criteria andStatusDescLessThan(String value) {
            addCriterion("status_desc <", value, "statusDesc");
            return (Criteria) this;
        }

        public Criteria andStatusDescLessThanOrEqualTo(String value) {
            addCriterion("status_desc <=", value, "statusDesc");
            return (Criteria) this;
        }

        public Criteria andStatusDescLike(String value) {
            addCriterion("status_desc like", value, "statusDesc");
            return (Criteria) this;
        }

        public Criteria andStatusDescNotLike(String value) {
            addCriterion("status_desc not like", value, "statusDesc");
            return (Criteria) this;
        }

        public Criteria andStatusDescIn(List<String> values) {
            addCriterion("status_desc in", values, "statusDesc");
            return (Criteria) this;
        }

        public Criteria andStatusDescNotIn(List<String> values) {
            addCriterion("status_desc not in", values, "statusDesc");
            return (Criteria) this;
        }

        public Criteria andStatusDescBetween(String value1, String value2) {
            addCriterion("status_desc between", value1, value2, "statusDesc");
            return (Criteria) this;
        }

        public Criteria andStatusDescNotBetween(String value1, String value2) {
            addCriterion("status_desc not between", value1, value2, "statusDesc");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIsNull() {
            addCriterion("created_time is null");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIsNotNull() {
            addCriterion("created_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeEqualTo(Long value) {
            addCriterion("created_time =", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotEqualTo(Long value) {
            addCriterion("created_time <>", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeGreaterThan(Long value) {
            addCriterion("created_time >", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("created_time >=", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeLessThan(Long value) {
            addCriterion("created_time <", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeLessThanOrEqualTo(Long value) {
            addCriterion("created_time <=", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIn(List<Long> values) {
            addCriterion("created_time in", values, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotIn(List<Long> values) {
            addCriterion("created_time not in", values, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeBetween(Long value1, Long value2) {
            addCriterion("created_time between", value1, value2, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotBetween(Long value1, Long value2) {
            addCriterion("created_time not between", value1, value2, "createdTime");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table message_response
     *
     * @mbggenerated do_not_delete_during_merge Mon Jan 09 13:39:09 CST 2017
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table message_response
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}