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

import org.apache.struts2.components.TabbedPanel;
import org.apache.struts2.components.TextArea;
import org.apache.struts2.views.annotations.StrutsTag;
import org.hdiv.dataComposer.IDataComposer;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render HTML textarea tag.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:textarea label="Comments" name="comments" cols="30" rows="8"/&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @see TabbedPanel
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
@StrutsTag(name="textarea", tldTagClass="org.hdiv.views.jsp.ui.TextareaTagHDIV", description="Render HTML textarea tag.")
public class TextAreaHDIV extends TextArea {
	
    final public static String TEMPLATE = "textareaHDIV";

    private static final String DATA_COMPOSER = "dataComposer";
    
    public TextAreaHDIV(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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
