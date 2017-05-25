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

import org.apache.struts2.components.ComboBox;
import org.apache.struts2.views.annotations.StrutsTag;
import org.hdiv.dataComposer.IDataComposer;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * The combo box is basically an HTML INPUT of type text and HTML SELECT grouped together to give you a combo box
 * functionality. You can place text in the INPUT control by using the SELECT control or type it in directly in
 * the text field.<p/>
 *
 * In this example, the SELECT will be populated from id=year attribute. Counter is itself an Iterator. It will
 * span from first to last. The population is done via javascript, and requires that this tag be surrounded by a
 * &lt;form&gt;.<p/>
 *
 * Note that unlike the &lt;s:select/&gt; tag, there is no ability to define the individual &lt;option&gt; tags' id attribute
 * or content separately. Each of these is simply populated from the toString() method of the list item. Presumably
 * this is because the select box isn't intended to actually submit useful data, but to assist the user in filling
 * out the text field.<p/>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * JSP:
 * &lt;-- Example One --&gt;
 * &lt;s:bean name="struts.util.Counter" id="year"&gt;
 *   &lt;s:param name="first" value="text('firstBirthYear')"/&gt;
 *   &lt;s:param name="last" value="2000"/&gt;
 *
 *   &lt;s:combobox label="Birth year" size="6" maxlength="4" name="birthYear" list="#year"/&gt;
 * &lt;/s:bean&gt;
 *
 * &lt;-- Example Two --&gt;
 * <s:combobox
 *     label="My Favourite Fruit"
 *     name="myFavouriteFruit"
 *     list="{'apple','banana','grape','pear'}"
 *     headerKey="-1"
 *     headerValue="--- Please Select ---"
 *     emptyOption="true"
 *     value="banana" />
 *
 * &lt;-- Example Two --&gt;
 * <s:combobox
 *    label="My Favourite Color"
 *    name="myFavouriteColor"
 *    list="#{'red':'red','green':'green','blue':'blue'}"
 *    headerKey="-1"
 *    headerValue="--- Please Select ---"
 *    emptyOption="true"
 *    value="green" />
 *
 * Velocity:
 * #tag( ComboBox "label=Birth year" "size=6" "maxlength=4" "name=birthYear" "list=#year" )
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
@StrutsTag(name="combobox", tldTagClass="org.hdiv.views.jsp.ui.ComboBoxTagHDIV", description="Widget that fills a text box from a select")
public class ComboBoxHDIV extends ComboBox {
	
    final public static String TEMPLATE = "comboboxHDIV";

    private static final String DATA_COMPOSER = "dataComposer";
    
    public ComboBoxHDIV(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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
