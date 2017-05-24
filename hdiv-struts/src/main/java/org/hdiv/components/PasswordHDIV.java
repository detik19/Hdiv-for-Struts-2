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

import org.apache.struts2.components.Password;
import org.apache.struts2.views.annotations.StrutsTag;
import org.hdiv.dataComposer.IDataComposer;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render an HTML input tag of type password.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <p/>
 * <!-- START SNIPPET: exdescription -->
 * In this example, a password control is displayed. For the label, we are calling ActionSupport's getText() to
 * retrieve password label from a resource bundle.<p/>
 * <!-- END SNIPPET: exdescription -->
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:password label="%{text('password')}" name="password" size="10" maxlength="15" /&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * author Gorka Vicente
 * @since HDIV 2.0.4
 */
@StrutsTag(name="password", tldTagClass="org.hdiv.views.jsp.ui.PasswordTagHDIV", description="Render an HTML input tag of type password")
public class PasswordHDIV extends Password {
	
    final public static String TEMPLATE = "passwordHDIV";
    
    private static final String DATA_COMPOSER = "dataComposer";

    public PasswordHDIV(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
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
    
}
