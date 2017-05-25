/*
 * Copyright 2004-2005 The Apache Software Foundation.
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
package org.hdiv.dispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.ServletRedirectResult;
import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.hdiv.util.RequestUtilsHDIV;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * <!-- START SNIPPET: description --> Calls the
 * {@link HttpServletResponse#sendRedirect(String) sendRedirect} method to the
 * location specified. The response is told to redirect the browser to the specified
 * location (a new request from the client). The consequence of doing this means that
 * the action (action instance, action errors, field errors, etc) that was just
 * executed is lost and no longer available. This is because actions are built on a
 * single-thread model. The only way to pass data is through the session or with web
 * parameters (url?name=value) which can be OGNL expressions. <!-- END SNIPPET:
 * description --> <p/> <b>This result type takes the following parameters:</b> <!--
 * START SNIPPET: params -->
 * <ul>
 * <li><b>location (default)</b> - the location to go to after execution.</li>
 * <li><b>parse</b> - true by default. If set to false, the location param will not
 * be parsed for Ognl expressions.</li>
 * </ul>
 * <p>
 * This result follows the same rules from {@link StrutsResultSupport}.
 * </p>
 * <!-- END SNIPPET: params --> <b>Example:</b>
 * 
 * <pre>
 *  &lt;!-- START SNIPPET: example --&gt;
 *   &lt;result name=&quot;success&quot; type=&quot;redirect&quot;&gt;
 *     &lt;param name=&quot;location&quot;&gt;foo.jsp&lt;/param&gt;
 *     &lt;param name=&quot;parse&quot;&gt;false&lt;/param&gt;
 *   &lt;/result&gt;
 *   &lt;!-- END SNIPPET: example --&gt;
 * </pre>
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public class HDIVServletRedirectResult extends ServletRedirectResult {

	private static final long serialVersionUID = -7962566677010511846L;

	private static final Log log = LogFactory.getLog(HDIVServletRedirectResult.class);


	public HDIVServletRedirectResult() {
		super();
	}

	public HDIVServletRedirectResult(String location) {
		super(location);
	}

	/**
	 * Redirects to the location specified by calling
	 * {@link HttpServletResponse#sendRedirect(String)}.
	 * 
	 * @param finalLocation the location to redirect to.
	 * @param invocation an encapsulation of the action execution state.
	 * @throws Exception if an error occurs when redirecting.
	 */
	protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {

		ActionContext ctx = invocation.getInvocationContext();
		HttpServletRequest request = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
		HttpServletResponse response = (HttpServletResponse) ctx.get(ServletActionContext.HTTP_RESPONSE);

		if (isPathUrl(finalLocation)) {

			if (!finalLocation.startsWith("/")) {
				ActionMapping mapping = actionMapper.getMapping(request, Dispatcher.getInstance().getConfigurationManager());
				String namespace = null;
				if (mapping != null) {
					namespace = mapping.getNamespace();
				}

				if ((namespace != null) && (namespace.length() > 0) && (!"/".equals(namespace))) {
					finalLocation = namespace + "/" + finalLocation;
				} else {
					finalLocation = "/" + finalLocation;
				}
			}

			// if the URL's are relative to the servlet context, append the servlet
			// context path
			if (prependServletContext && (request.getContextPath() != null)
					&& (request.getContextPath().length() > 0)) {
				finalLocation = request.getContextPath() + finalLocation;
			}
		}
		
		if (request.getSession(false) != null) {
			
			if (RequestUtilsHDIV.hasActionOrServletExtension(finalLocation, actionMapper)) {
				finalLocation = RequestUtilsHDIV.addHDIVParameterIfNecessary(request, finalLocation, false);
			}
		}
		
		if (isPathUrl(finalLocation)) {
			finalLocation = response.encodeRedirectURL(finalLocation);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Redirecting to finalLocation " + finalLocation);
		}

		response.sendRedirect(finalLocation);
	}

	private static boolean isPathUrl(String url) {
		// filter out "http:", "https:", "mailto:", "file:", "ftp:"
		// since the only valid places for : in URL's is before the path
		// specification
		// either before the port, or after the protocol
		return (url.indexOf(':') == -1);
	}

}
