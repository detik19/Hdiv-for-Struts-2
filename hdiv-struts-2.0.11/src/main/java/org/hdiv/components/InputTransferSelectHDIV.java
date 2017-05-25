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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.components.InputTransferSelect;
import org.apache.struts2.views.annotations.StrutsTag;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Create a input transfer select component which is basically an text input
 * and  &lt;select ...&gt; tag with buttons in the middle of them allowing text
 * to be added to the transfer select. Will auto-select all its
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
 * that the inputtransferselect tag is being used in a form tag. The generated id
 * and doubleId will be &lt;form_id&gt;_&lt;inputtransferselect_doubleName&gt; and
 * &lt;form_id&gt;_&lt;inputtransferselect_doubleName&gt; respectively.
 *
 * <!-- END SNIPPET: notice -->
 *
 * <p/>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;-- minimum configuration --&gt;
 * &lt;s:inputtransferselect
 *      label="Favourite Cartoons Characters"
 *      name="cartoons"
 *      list="{'Popeye', 'He-Man', 'Spiderman'}"
 *  /&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
@StrutsTag(name="inputtransferselect", tldTagClass="org.hdiv.views.jsp.ui.InputTransferSelectTagHDIV", description="Renders an input form")
public class InputTransferSelectHDIV extends InputTransferSelect {

    private static final Log _log = LogFactory.getLog(InputTransferSelectHDIV.class);

    private static final String TEMPLATE = "inputtransferselectHDIV";


    public InputTransferSelectHDIV(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

}
