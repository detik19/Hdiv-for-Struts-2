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

import org.apache.struts2.components.Select;
import org.apache.struts2.views.annotations.StrutsTag;
import org.hdiv.dataComposer.IDataComposer;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Render an HTML input tag of type select.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;s:select label="Pets"
 *        name="petIds"
 *        list="petDao.pets"
 *        listKey="id"
 *        listValue="name"
 *        multiple="true"
 *        size="3"
 *        required="true"
 * /&gt;
 *
 * &lt;s:select label="Months"
 *        name="months"
 *        headerKey="-1" headerValue="Select Month"
 *        list="#{'01':'Jan', '02':'Feb', [...]}"
 *        value="selectedMonth"
 *        required="true"
 * /&gt;
 *
 * // The month id (01, 02, ...) returned by the getSelectedMonth() call
 * // against the stack will be auto-selected
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * <p/>
 *
 * <!-- START SNIPPET: exnote -->
 *
 * Note: For any of the tags that use lists (select probably being the most ubiquitous), which uses the OGNL list
 * notation (see the "months" example above), it should be noted that the map key created (in the months example,
 * the '01', '02', etc.) is typed. '1' is a char, '01' is a String, "1" is a String. This is important since if
 * the value returned by your "value" attribute is NOT the same type as the key in the "list" attribute, they
 * WILL NOT MATCH, even though their String values may be equivalent. If they don't match, nothing in your list
 * will be auto-selected.<p/>
 *
 * <!-- END SNIPPET: exnote -->
 *
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
@StrutsTag(name="select", tldTagClass="org.hdiv.views.jsp.ui.SelectTagDIV", description="Render a select element")
public class SelectHDIV extends Select {
    
	final public static String TEMPLATE = "selectHDIV";
	
    private static final String DATA_COMPOSER = "dataComposer";
   
    public SelectHDIV(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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
