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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Anchor;
import org.apache.struts2.views.annotations.StrutsTag;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.util.HDIVUtil;
import org.hdiv.util.RequestUtilsHDIV;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * A tag that creates a HTML &lt;a href='' /&gt; that when clicked calls a URL remote XMLHttpRequest call via the dojo
 * framework.<p/>
 *
 * <!-- START SNIPPET: ajaxJavadoc -->
 * <B>THE FOLLOWING IS ONLY VALID WHEN AJAX IS CONFIGURED</B>
 * <ul>
 *      <li>href</li>
 *      <li>errorText</li>
 *      <li>listenTopics</li>
 *      <li>notifyTopics</li>
 *      <li>executeScripts</li>
 *      <li>loadingText</li>
 *      <li>listenTopics</li>
 *      <li>handler</li>
 *      <li>formId</li>
 *      <li>formFilter</li>
 *      <li>targets</li>
 *      <li>showErrorTransportText</li>
 *      <li>targets</li>
 *      <li>indicator</li>
 * </ul>
 * 'resultDivId' Deprecated. Use targets.<p/>
 * 'targets' is a list of element ids whose content will be updated with the
 * text returned from request.<p/>
 * 'errorText' is the text that will be displayed when there is an error making the request.<p/>
 * 'onLoadJS' Deprecated. Use 'notifyTopics'.<p/>
 * 'preInvokeJS' Deprecated. Use 'notifyTopics'.<p/>
 * 'executeScripts' if set to true will execute javascript sections in the returned text.<p/>
 * 'loadingText' is the text that will be displayed on the 'targets' elements while making the
 * request.<p/>
 * 'handler' is the name of the function that will take care of making the AJAX request. Dojo's widget
 * and dom node are passed as parameters).<p/>
 * 'formId' is the id of the html form whose fields will be seralized and passed as parameters
 * in the request.<p/>
 * 'formFilter' is the name of a function which will be used to filter the fields that will be
 * seralized. This function takes as a parameter the element and returns true if the element
 * should be included.<p/>
 * 'listenTopics' comma separated list of topics names, that will trigger a request
 * 'indicator' element to be shown while the request executing
 * 'showErrorTransportText': whether errors should be displayed (on 'targets')</p>
 * 'showLoadingText' show loading text on targets</p>
 * 'notifyTopics' comma separated list of topics names, that will be published. Three parameters are passed:<p/>
 * <ul>
 *      <li>data: html or json object when type='load' or type='error'</li>
 *      <li>type: 'before' before the request is made, 'load' when the request succeeds, or 'error' when it fails</li>
 *      <li>request: request javascript object, when type='load' or type='error'</li>
 * </ul>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example1 -->
 * &lt;s:a id="link1" theme="ajax" href="/DoIt.action" errorText="An error ocurred" loadingText="Loading..."&gt;
 *     &lt;img border="none" src="&lt;%=request.getContextPath()%&gt;/images/delete.gif"/&gt;
 *     &lt;s:param name="id" value="1"/&gt;
 * &lt;/s:a&gt;
 * <!-- END SNIPPET: example1 -->
 * </pre>
 *
 * </p>
 *
 * <!-- START SNIPPET: exampledescription1 -->
 *
 * Results in
 *
 * <!-- END SNIPPET: exampledescription1 -->
 *
 * </p>
 *
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * &lt;a dojoType="BindAnchor" executeScripts="true" id="link1" href="/DoIt.action?id=1" errorText="An error ocurred"&gt;&lt;/a&gt;
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 * </p>
 *
 * <!-- START SNIPPET: exampledescription2 -->
 *
 * Here is an example that uses the beforeLoading. This example is in altSyntax=true:
 *
 * <!-- END SNIPPET: exampledescription2 -->
 *
 * </p>
 *
 * <pre>
 * <!-- START SNIPPET: example3 -->
 * &lt;s:a id="test" theme="ajax" href="/simpeResult.action" beforeLoading="confirm('Are you sure?')"&gt;
 *  A
 * &lt;/s:a&gt;
 * <!-- END SNIPPET: example3 -->
 * </pre>
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
@StrutsTag(name="a", tldTagClass="org.hdiv.views.jsp.ui.AnchorTagHDIV", description="Render a HTML href element that when clicked can optionally call a URL via remote XMLHttpRequest and updates its targets")
public class AnchorHDIV extends Anchor {
	
    public static final String OPEN_TEMPLATE = "a";
    public static final String TEMPLATE = "a-close";
    public static final String COMPONENT_NAME = AnchorHDIV.class.getName();
    
    private static final String DATA_COMPOSER = "dataComposer";


    public AnchorHDIV(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public void evaluateExtraParams() {
    	
    	super.evaluateExtraParams();
    	
    	String href = (String) parameters.get("href");
    	
		String hdivParameter = this.getHDIVParameter();
		if (href.indexOf(hdivParameter) == -1) {
						
			if (RequestUtilsHDIV.hasActionOrServletExtension(href, actionMapper)) {				
				href = RequestUtilsHDIV.addHDIVParameterIfNecessary(request, href, true);
				addParameter("href", href);
			}
		}				
    }
    
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }    
    
	/**
	 * @return Returns the HDIV state parameter.
	 */
	public String getHDIVParameter() {
		return (String) HDIVUtil.getHttpSession().getAttribute("HDIVParameter");
	}      
    
	/**
	 * @return Returns data composer from request
	 */
    public IDataComposer getDataComposer() {
    	return (IDataComposer) this.request.getAttribute(DATA_COMPOSER);
    }
    
}
