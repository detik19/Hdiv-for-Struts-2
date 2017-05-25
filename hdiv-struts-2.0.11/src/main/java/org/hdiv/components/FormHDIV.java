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

import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Form;
import org.apache.struts2.views.annotations.StrutsTag;
import org.hdiv.dataComposer.IDataComposer;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc --> <p/> Renders HTML an input form.<p/> <p/> The
 * remote form allows the form to be submitted without the page being refreshed. The
 * results from the form can be inserted into any HTML element on the page.<p/> <p/>
 * NOTE:<p/> The order / logic in determining the posting url of the generated HTML
 * form is as follows:-
 * <ol>
 * <li> If the action attribute is not specified, then the current request will be
 * used to determine the posting url </li>
 * <li> If the action is given, Struts will try to obtain an ActionConfig. This will
 * be successfull if the action attribute is a valid action alias defined struts.xml.
 * </li>
 * <li> If the action is given and is not an action alias defined in struts.xml,
 * Struts will used the action attribute as if it is the posting url, separting the
 * namespace from it and using UrlHelper to generate the final url. </li>
 * </ol>
 * <p/> <!-- END SNIPPET: javadoc --> <p/> <p/> <b>Examples</b> <p/>
 * 
 * <pre>
 *  &lt;!-- START SNIPPET: example --&gt;
 *  &lt;p/&gt;
 *  &lt;s:form ... /&gt;
 *  &lt;p/&gt;
 *  &lt;!-- END SNIPPET: example --&gt;
 * </pre>
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
@StrutsTag(name = "form", tldTagClass = "org.hdiv.views.jsp.ui.FormTagHDIV", description = "Renders an input form")
public class FormHDIV extends Form {

	public static final String OPEN_TEMPLATE = "formHDIV";

	public static final String TEMPLATE = "form-closeHDIV";

	private static final String DATA_COMPOSER = "dataComposer";
	

	public FormHDIV(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
		super(stack, request, response);
	}

	public String getDefaultOpenTemplate() {
		return OPEN_TEMPLATE;
	}

	protected String getDefaultTemplate() {
		return TEMPLATE;
	}

	/**
	 * @return Returns data composer from request
	 */
	public IDataComposer getDataComposer() {
		return (IDataComposer) this.request.getAttribute(DATA_COMPOSER);
	}
	
	public boolean end(Writer writer, String body) {
        		
        Map hdivParameters = new LinkedHashMap();
        
        this.addHDIVParameter(hdivParameters);                
        addParameter("hdivParameters", hdivParameters);
        
        return super.end(writer, body);
    }
	
	/**
	 * Adds HDIV state as a parameter.
	 * 
	 * @param hdivParameters
	 */
	protected void addHDIVParameter(Map hdivParameters) {
		
        String requestId = this.getDataComposer().endRequest();       
        
        if (requestId.length() > 0) {
        	hdivParameters.put(this.getDataComposer().getHDIVParameter(), requestId);        	
        }		
	}
	
}
