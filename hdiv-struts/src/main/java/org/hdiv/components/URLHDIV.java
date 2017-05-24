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
package org.hdiv.components;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsException;
import org.apache.struts2.components.Param;
import org.apache.struts2.components.URL;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.portlet.context.PortletActionContext;
import org.apache.struts2.portlet.util.PortletUrlHelper;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.util.UrlHelper;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.RequestUtilsHDIV;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * <p>This tag is used to create a URL.</p>
 *
 * <p>You can use the "param" tag inside the body to provide
 * additional request parameters.</p>
 *
 * <b>NOTE:</b>
 * <p>By default request parameters will be separated using escaped ampersands (i.e., &amp;amp;).
 * This is necessary for XHTML compliance, however, when using the URL generated by this tag
 * with the &lt;s:property&gt; tag, the <b>escapeAmp</b> attribute should be used to disable
 * ampersand escaping.</p>
 *
 * <b>NOTE:</b>
 * <p>When includeParams is 'all' or 'get', the parameter defined in param tag will take
 * precedence and will not be overriden if they exists in the parameter submitted. For
 * example, in Example 3 below, if there is a id parameter in the url where the page this
 * tag is included like http://<host>:<port>/<context>/editUser.action?id=3333&name=John
 * the generated url will be http://<host>:<port>/context>/editUser.action?id=22&name=John
 * cause the parameter defined in the param tag will take precedence.</p>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *      <li>action (String) - (value or action choose either one, if both exist value takes precedence) action's name (alias) <li>
 *      <li>value (String) - (value or action choose either one, if both exist value takes precedence) the url itself</li>
 *      <li>scheme (String) - http scheme (http, https) defaults to the scheme this request is in</li>
 *      <li>namespace - action's namespace</li>
 *      <li>method (String) - action's method name, defaults to 'execute'</li>
 *      <li>encode (Boolean) - url encode the generated url. Defaults to 'true'.</li>
 *      <li>includeParams (String) - The includeParams attribute may have the value 'none', 'get' or 'all'. Defaults to 'get'.
 *                                   none - include no parameters in the URL
 *                                   get  - include only GET parameters in the URL (default)
 *                                   all  - include both GET and POST parameters in the URL
 *      </li>
 *      <li>includeContext (Boolean) - Specifies whether to include the web app context path. Defaults to 'true'.</li>
 *      <li>escapeAmp (Boolean) - Specifies whether to escape ampersand (&amp;) to (&amp;amp;) or not. Defaults to 'true'.</li>
 *      <li>portletMode (String) - The resulting portlet mode.</li>
 *      <li>windowState (String) - The resulting portlet window state.</li>
 *      <li>portletUrlType (String) - Specifies if this should be a portlet render or action URL.</li>
 *      <li>forceAddSchemeHostAndPort (Boolean) - Specifies whether to force the addition of scheme, host and port or not.</li>
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <p/> <b>Examples</b>
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;-- Example 1 --&gt;
 * &lt;s:url value="editGadget.action"&gt;
 *     &lt;s:param name="id" value="%{selected}" /&gt;
 * &lt;/s:url&gt;
 *
 * &lt;-- Example 2 --&gt;
 * &lt;s:url action="editGadget"&gt;
 *     &lt;s:param name="id" value="%{selected}" /&gt;
 * &lt;/s:url&gt;
 *
 * &lt;-- Example 3--&gt;
 * &lt;s:url includeParams="get"  &gt;
 *     &lt;s:param name="id" value="%{'22'}" /&gt;
 * &lt;/s:url&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * @see Param
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
@StrutsTag(name = "url", tldTagClass = "org.hdiv.views.jsp.URLTagHDIV", description = "This tag is used to create a URL")
public class URLHDIV extends URL {

	private static final Log LOG = LogFactory.getLog(URLHDIV.class);

	private HttpServletRequest req;

	private HttpServletResponse res;

	private static final String DATA_COMPOSER = "dataComposer";

	private static final String JSP_EXTENSION = ".jsp";


	/**
	 * Constructor
	 * 
	 * @param stack OGNL value stack.
	 * @param req http request
	 * @param res http response
	 */
	public URLHDIV(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		super(stack, req, res);
		this.req = req;
		this.res = res;
	}

	/**
	 * Callback for the end tag of this component. Should the body be evaluated
	 * again? <p/> <b>NOTE:</b> will pop component stack.
	 * 
	 * @param writer the output writer.
	 * @param body the rendered body.
	 * @return true if the body should be evaluated again
	 */
	public boolean end(Writer writer, String body) {

		String scheme = req.getScheme();

		if (this.scheme != null) {
			scheme = this.scheme;
		}

		String result;
		if (value == null && action != null) {

			if (Dispatcher.getInstance().isPortletSupportActive()
					&& PortletActionContext.isPortletRequest()) {
				result = PortletUrlHelper.buildUrl(action, namespace, method, parameters, portletUrlType, portletMode, windowState);				
			} else {
                result = determineActionURL(action, namespace, method, req, res, parameters, scheme, includeContext, encode, forceAddSchemeHostAndPort, escapeAmp);
			}

			// add HDIV parameter to result
			result = RequestUtilsHDIV.composeURL(req, result);

		} else {

			if (Dispatcher.getInstance().isPortletSupportActive()
					&& PortletActionContext.isPortletRequest()) {
				result = PortletUrlHelper.buildResourceUrl(value, parameters);
			} else {
				String _value = value;

				// We don't include the request parameters cause they would have been
				// prioritised before this [in start(Writer) method]
				if (_value != null && _value.indexOf("?") > 0) {
					_value = _value.substring(0, _value.indexOf("?"));
				}
				result = UrlHelper.buildUrl(_value, req, res, parameters, scheme, includeContext, encode, forceAddSchemeHostAndPort, escapeAmp);				
			}

			if (RequestUtilsHDIV.hasActionOrServletExtension(result, super.actionMapper)) {
				result = RequestUtilsHDIV.addHDIVParameterIfNecessary(req, result);
			}
		}
		
		if (anchor != null && anchor.length() > 0) {
			result += '#' + anchor;
		}

		String id = getId();

		if (id != null) {
			getStack().getContext().put(id, result);

			// add to the request and page scopes as well
			req.setAttribute(id, result);
		} else {
			try {
				writer.write(result);
			} catch (IOException e) {
				throw new StrutsException("IOError: " + e.getMessage(), e);
			}
		}
		// callback for the end tag of this component and the body should be
		// evaluated again
		return super.end(writer, body, true);
	}

	/**
	 * @return Returns HDIV's data composer.
	 */
	public IDataComposer getDataComposer() {
		return (IDataComposer) this.req.getAttribute(DATA_COMPOSER);
	}

	/**
	 * Merge request parameters into current parameters. If a parameter is already
	 * present, than the request parameter in the current request and value atrribute
	 * will not override its value. The priority is as follows:-
	 * <ul>
	 * <li>parameter from the current request (least priority)</li>
	 * <li>parameter form the value attribute (more priority)</li>
	 * <li>parameter from the param tag (most priority)</li>
	 * </ul>
	 * 
	 * @param value the value attribute (url to be generated by this component)
	 * @param parameters component parameters
	 * @param contextParameters request parameters
	 */
	protected void mergeRequestParameters(String value, Map parameters, Map contextParameters) {

		Map mergedParams = new LinkedHashMap(contextParameters);

		String hdivParameter = this.getDataComposer().getHDIVParameter();

		if (mergedParams.containsKey(hdivParameter)) {
			mergedParams.remove(hdivParameter);
		}

		// Merge contextParameters (from current request) with parameters specified
		// in value attribute
		// eg. value="someAction.action?id=someId&venue=someVenue"
		// where the parameters specified in value attribute takes priority.

		if (value != null && value.trim().length() > 0 && value.indexOf("?") > 0) {
			mergedParams = new LinkedHashMap();

			String queryString = value.substring(value.indexOf("?") + 1);

			mergedParams = UrlHelper.parseQueryString(queryString);
			for (Iterator iterator = contextParameters.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry entry = (Map.Entry) iterator.next();
				Object key = entry.getKey();

				if (!mergedParams.containsKey(key)) {
					mergedParams.put(key, entry.getValue());
				}
			}
		}

		// Merge parameters specified in value attribute
		// eg. value="someAction.action?id=someId&venue=someVenue"
		// with parameters specified though param tag
		// eg. <param name="id" value="%{'someId'}" />
		// where parameters specified through param tag takes priority.

		for (Iterator iterator = mergedParams.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			Object key = entry.getKey();

			if (!parameters.containsKey(key)) {
				parameters.put(key, entry.getValue());
			}
		}
	}
}