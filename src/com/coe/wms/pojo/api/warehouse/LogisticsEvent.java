package com.coe.wms.pojo.api.warehouse;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

public class LogisticsEvent implements Serializable {

	private static final long serialVersionUID = -2366387117121513855L;
	@XmlElement
	private EventHeader eventHeader;
	@XmlElement
	private EventBody eventBody;

	public EventHeader getEventHeader() {
		return eventHeader;
	}

	public void setEventHeader(EventHeader eventHeader) {
		this.eventHeader = eventHeader;
	}

	public EventBody getEventBody() {
		return eventBody;
	}

	public void setEventBody(EventBody eventBody) {
		this.eventBody = eventBody;
	}
}
