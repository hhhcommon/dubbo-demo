package com.coe.message.entity;

public class MessageResponseWithBLOBs extends MessageResponse {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column message_response.response_header
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    private String responseHeader;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column message_response.response_body
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    private String responseBody;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column message_response.response_header
     *
     * @return the value of message_response.response_header
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public String getResponseHeader() {
        return responseHeader;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column message_response.response_header
     *
     * @param responseHeader the value for message_response.response_header
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public void setResponseHeader(String responseHeader) {
        this.responseHeader = responseHeader;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column message_response.response_body
     *
     * @return the value of message_response.response_body
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column message_response.response_body
     *
     * @param responseBody the value for message_response.response_body
     *
     * @mbggenerated Mon Jan 09 13:39:09 CST 2017
     */
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}