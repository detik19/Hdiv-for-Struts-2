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

import org.apache.struts2.components.UpDownSelect;
import org.apache.struts2.views.annotations.StrutsTag;
import org.hdiv.dataComposer.IDataComposer;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Create a Select component with buttons to move the elements in the select component
 * up and down. When the containing form is submited, its elements will be submitted in
 * the order they are arranged (top to bottom).
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;!-- Example 1: simple example --&gt;
 * &lt;s:updownselect
 * list="#{'england':'England', 'america':'America', 'germany':'Germany'}"
 * name="prioritisedFavouriteCountries"
 * headerKey="-1"
 * headerValue="--- Please Order Them Accordingly ---"
 * emptyOption="true" /&gt;
 *
 * &lt;!-- Example 2: more complex example --&gt;
 * &lt;s:updownselect
 * list="defaultFavouriteCartoonCharacters"
 * name="prioritisedFavouriteCartoonCharacters"
 * headerKey="-1"
 * headerValue="--- Please Order ---"
 * emptyOption="true"
 * allowMoveUp="true"
 * allowMoveDown="true"
 * allowSelectAll="true"
 * moveUpLabel="Move Up"
 * moveDownLabel="Move Down"
 * selectAllLabel="Select All" /&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @s.tag name="updownselect" tld-body-content="JSP" tld-tag-class="org.apache.struts2.views.jsp.ui.UpDownSelectTag"
 * description="Render a up down select element"
 * 
 *  @author Gorka Vicente
 *  @since HDIV 2.0.4
 */
@StrutsTag(name="updownselect", tldTagClass="org.hdiv.views.jsp.ui.UpDownSelectTagHDIV", 
        description="Create a Select component with buttons to move the elements in the select component up and down")
public class UpDownSelectHDIV extends UpDownSelect {

    final public static String TEMPLATE = "updownselectHDIV";
    
    private static final String DATA_COMPOSER = "dataComposer";

    public String getDefaultTemplate() {
        return TEMPLATE;
    }

    public UpDownSelectHDIV(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

	/**
	 * @return Returns data composer from request
	 */
    public IDataComposer getDataComposer() {
    	return (IDataComposer) this.request.getAttribute(DATA_COMPOSER);
    }
  
}
