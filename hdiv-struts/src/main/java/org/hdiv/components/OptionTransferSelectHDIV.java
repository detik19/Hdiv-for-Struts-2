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

import org.apache.struts2.components.OptionTransferSelect;
import org.apache.struts2.views.annotations.StrutsTag;
import org.hdiv.dataComposer.IDataComposer;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Create a option transfer select component which is basically two &lt;select ...&gt;
 * tag with buttons in the middle of them allowing options in each of the
 * &lt;select ...&gt; to be moved between themselves. Will auto-select all its
 * elements upon its containing form submision.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/>
 *
 *
 * <!-- START SNIPPET: notice -->
 *
 * NOTE: The id and doubleId need not be supplied as they will generated provided
 * that the optiontransferselect tag is being used in a form tag. The generated id
 * and doubleId will be &lt;form_id&gt;_&lt;optiontransferselect_doubleName&gt; and
 * &lt;form_id&gt;_&lt;optiontransferselect_doubleName&gt; respectively.
 *
 * <!-- END SNIPPET: notice -->
 *
 * <p/>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;-- minimum configuration --&gt;
 * &lt;s:optiontransferselect
 *      label="Favourite Cartoons Characters"
 *      name="leftSideCartoonCharacters"
 *      list="{'Popeye', 'He-Man', 'Spiderman'}"
 *      doubleName="rightSideCartoonCharacters"
 *      doubleList="{'Superman', 'Mickey Mouse', 'Donald Duck'}"
 *  /&gt;
 *
 *  &lt;-- possible configuration --&gt;
 *  &lt;s:optiontransferselect
 *      label="Favourite Cartoons Characters"
 *      name="leftSideCartoonCharacters"
 *      leftTitle="Left Title"
 *      rightTitle="Right Title"
 *      list="{'Popeye', 'He-Man', 'Spiderman'}"
 *      multiple="true"
 *      headerKey="headerKey"
 *      headerValue="--- Please Select ---"
 *      emptyOption="true"
 *      doubleList="{'Superman', 'Mickey Mouse', 'Donald Duck'}"
 *      doubleName="rightSideCartoonCharacters"
 *      doubleHeaderKey="doubleHeaderKey"
 *      doubleHeaderValue="--- Please Select ---"
 *      doubleEmptyOption="true"
 *      doubleMultiple="true"
 *  /&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
@StrutsTag(name="optiontransferselect", tldTagClass="org.hdiv.views.jsp.ui.OptionTransferSelectTagHDIV", description="Renders an input form")
public class OptionTransferSelectHDIV extends OptionTransferSelect {

    private static final String TEMPLATE = "optiontransferselectHDIV";
    
    private static final String DATA_COMPOSER = "dataComposer";

    public OptionTransferSelectHDIV(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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
