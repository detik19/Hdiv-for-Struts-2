/*
 * Copyright 2005-2008 hdiv.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hdiv.interceptor;


import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.TextUtils;

/**
 * Interceptor to visualize the errors produced in the editable fields detected by
 * HDIV.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 * @see com.opensymphony.xwork2.interceptor.Interceptor
 */
public class EditableValidatorInterceptor implements Interceptor {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(EditableValidatorInterceptor.class);

	/**
	 * The request attributes key under HDIV should store errors produced in the
	 * editable fields.
	 */
	public static final String EDITABLE_PARAMETER_ERROR = "org.hdiv.action.EDITABLE_PARAMETER_ERROR";

	/**
	 * Property that contains the error message to be shown by Struts when the value
	 * of the editable parameter is not valid.
	 */
	private static final String HDIV_EDITABLE_ERROR = "hdiv.editable.error";

	/**
	 * Property that contains the error message to be shown by Struts when the value
	 * of the editable password parameter is not valid.
	 */
	private static final String HDIV_EDITABLE_PASSWORD_ERROR = "hdiv.editable.password.error";

	private static final String DEFAULT_MESSAGE = "no.message.found";


	/**
	 * Called after an interceptor is created, but before any requests are processed
	 * using intercept , giving the Interceptor a chance to initialize any needed
	 * resources.
	 */
	public void init() {

	}

	/**
	 * Allows the Interceptor to do some processing on the request before and/or
	 * after the rest of the processing of the request by the ActionInvocation or to
	 * short-circuit the processing and just return a String return code.
	 */
	public String intercept(ActionInvocation invocation) throws Exception {

		ActionContext ac = invocation.getInvocationContext();

		HttpServletRequest request = ServletActionContext.getRequest();
		Object action = invocation.getAction();

		this.getEditableParametersErrors(request, action, ac.getLocale());

		return invocation.invoke();
	}

	/**
	 * Called to let an interceptor clean up any resources it has allocated.
	 */
	public void destroy() {
	}

	/**
	 * Generate the errors detected by HDIV during the validation process of the
	 * editable parameters.
	 * 
	 * @param request The servlet request we are processing
	 * @param action action object
	 * @param locale
	 */
	public void getEditableParametersErrors(HttpServletRequest request, Object action, Locale locale) {

		Hashtable unauthorizedEditableParameters = (Hashtable) request.getAttribute(EDITABLE_PARAMETER_ERROR);

		if ((unauthorizedEditableParameters != null) && (unauthorizedEditableParameters.size() > 0)) {

			for (Iterator it = unauthorizedEditableParameters.keySet().iterator(); it.hasNext();) {

				String currentParameter = (String) it.next();
				String[] currentUnauthorizedValues = (String[]) unauthorizedEditableParameters.get(currentParameter);

				String errMsg = "";
				if ((currentUnauthorizedValues.length == 1)
						&& (currentUnauthorizedValues[0].equals(HDIV_EDITABLE_PASSWORD_ERROR))) {

					errMsg = getTextMessage(HDIV_EDITABLE_PASSWORD_ERROR, null, locale);					

				} else {
					String printedValue = this.createMessageError(currentUnauthorizedValues);
					errMsg = getTextMessage(HDIV_EDITABLE_ERROR, new Object[] { printedValue }, locale);				
				}
				
				if (action instanceof ValidationAware) {						
					((ValidationAware) action).addFieldError(currentParameter, errMsg);						
				} else {
					log.info(errMsg);
				}
			}
		}
	}

	/**
	 * It creates the message error from the values <code>values</code>.
	 * 
	 * @param values values with not allowed characters
	 * @return message error to show
	 */
	public String createMessageError(String[] values) {

		StringBuffer printedValue = new StringBuffer();

		for (int i = 0; i < values.length; i++) {

			if (i > 0) {
				printedValue.append(", ");
			}

			if (values[i].length() > 20) {
				printedValue.append(TextUtils.htmlEncode(values[i]).substring(0, 20) + "...");
			} else {
				printedValue.append(TextUtils.htmlEncode(values[i]));
			}

			if (printedValue.length() > 20) {
				break;
			}
		}

		return printedValue.toString();
	}

	/**
	 * Try to resolve the message.
	 * 
	 * @param messageKey the code to lookup up, such as 'calculator.noRateSet'
	 * @param o Array of arguments that will be filled in for params within the
	 *            message (params look like "{0}", "{1,date}", "{2,time}" within a
	 *            message), or null if none.
	 * @param locale locale
	 * @return The resolved message
	 */
	private String getTextMessage(String messageKey, Object[] args, Locale locale) {

		if (args == null || args.length == 0) {
			return LocalizedTextUtil.findText(this.getClass(), messageKey, locale);
		} else {
			return LocalizedTextUtil.findText(this.getClass(), messageKey, locale, DEFAULT_MESSAGE, args);
		}
	}

}
