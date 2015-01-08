/*
 * @(#)EventProcessorSpec.java 3.0, 17 Jun, 2013 12:02:54 PM
 * Copyright 2013 eGovernments Foundation. All rights reserved. 
 * eGovernments PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.egov.infstr.events.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "eg_event_processor_spec")
@NamedQuery(name="event_specByModuleAndCode", query="select EP from EventProcessorSpec EP where EP.module=:module and EP.eventCode=:eventCode")
public class EventProcessorSpec {

	private Integer id;
	private String module;
	private String eventCode;
	private String responseTemplate;

	@SequenceGenerator(name = "Event_Gen", sequenceName = "eg_event_processor_spec_seq" , allocationSize = 1)
	@GeneratedValue(generator = "Event_Gen", strategy=GenerationType.SEQUENCE)
	@Id
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "module")
	public String getModule() {
		return this.module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	@Column(name = "event_code")
	public String getEventCode() {
		return this.eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	@Column(name = "response_template")
	public String getResponseTemplate() {
		return this.responseTemplate;
	}

	public void setResponseTemplate(String responseTemplate) {
		this.responseTemplate = responseTemplate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EventProcessorSpec [id=").append(id)
				.append(", module=").append(module).append(", eventCode=")
				.append(eventCode).append(", responseTemplate=")
				.append(responseTemplate).append("]");
		return builder.toString();
	}

	

}
