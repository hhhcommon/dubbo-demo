package com.coe.message.entity;

public class MessageRequestWithBLOBs extends MessageRequest {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column message_request.body
     *
     * @mbggenerated Fri Jan 06 14:22:57 CST 2017
     */
    private String body;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column message_request.header_params
     *
     * @mbggenerated Fri Jan 06 14:22:57 CST 2017
     */
    private String headerParams;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column message_request.body_params
     *
     * @mbggenerated Fri Jan 06 14:22:57 CST 2017
     */
    private String bodyParams;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column message_request.body
     *
     * @return the value of message_request.body
     *
     * @mbggenerated Fri Jan 06 14:22:57 CST 2017
     */
    public String getBody() {
        return body;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column message_request.body
     *
     * @param body the value for message_request.body
     *
     * @mbggenerated Fri Jan 06 14:22:57 CST 2017
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column message_request.header_params
     *
     * @return the value of message_request.header_params
     *
     * @mbggenerated Fri Jan 06 14:22:57 CST 2017
     */
    public String getHeaderParams() {
        return headerParams;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column message_request.header_params
     *
     * @param headerParams the value for message_request.header_params
     *
     * @mbggenerated Fri Jan 06 14:22:57 CST 2017
     */
    public void setHeaderParams(String headerParams) {
        this.headerParams = headerParams;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column message_request.body_params
     *
     * @return the value of message_request.body_params
     *
     * @mbggenerated Fri Jan 06 14:22:57 CST 2017
     */
    public String getBodyParams() {
        return bodyParams;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column message_request.body_params
     *
     * @param bodyParams the value for message_request.body_params
     *
     * @mbggenerated Fri Jan 06 14:22:57 CST 2017
     */
    public void setBodyParams(String bodyParams) {
        this.bodyParams = bodyParams;
    }
}