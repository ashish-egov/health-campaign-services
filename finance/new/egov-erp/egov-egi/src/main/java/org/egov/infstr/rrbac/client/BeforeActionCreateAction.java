/*
 * @(#)BeforeActionCreateAction.java 3.0, 17 Jun, 2013 4:52:05 PM
 * Copyright 2013 eGovernments Foundation. All rights reserved. 
 * eGovernments PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.egov.infstr.rrbac.client;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.egov.exceptions.EGOVRuntimeException;
import org.egov.lib.rrbac.services.RbacService;
import org.egov.lib.rrbac.services.RbacServiceImpl;

public class BeforeActionCreateAction extends Action {
	private static final Logger LOG = LoggerFactory.getLogger(BeforeActionCreateAction.class);	
	private final RbacService rbacService = new RbacServiceImpl();
	
	@Override
	public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest req, final HttpServletResponse res) throws Exception {
		String target = "";
		try {
			final List entityList = this.rbacService.getEntityList();
			final List taskList = this.rbacService.getTaskList();
			Collections.sort(entityList);
			Collections.sort(taskList);
			req.getSession().setAttribute("entity", entityList);
			req.getSession().setAttribute("task", taskList);
			target = "success";

		} catch (final Exception ex) {
			target = "error";
			LOG.error("Exception Encountered!!!" + ex.getMessage());
			throw new EGOVRuntimeException("Exception occured -----> " + ex.getMessage());

		}
		return mapping.findForward(target);

	}
}
