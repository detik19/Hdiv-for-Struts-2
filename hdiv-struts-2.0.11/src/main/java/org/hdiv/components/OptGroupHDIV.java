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
import org.apache.struts2.components.OptGroup;
import org.apache.struts2.views.annotations.StrutsTag;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Create a optgroup component which needs to resides within a select tag.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/>
 *
 * <!-- START SNIPPET: notice -->
 *
 * This component is to be used within a  Select component.
 *
 * <!-- END SNIPPET: notice -->
 *
 * <p/>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;s:select label="My Selection"
 *            name="mySelection"
 *            value="%{'POPEYE'}"
 *            list="%{#{'SUPERMAN':'Superman', 'SPIDERMAN':'spiderman'}}"&gt;
 *    &lt;s:optgroup label="Adult"
 *                 list="%{#{'SOUTH_PARK':'South Park'}}" /&gt;
 *    &lt;s:optgroup label="Japanese"
 *                 list="%{#{'POKEMON':'pokemon','DIGIMON':'digimon','SAILORMOON':'Sailormoon'}}" /&gt;
 * &lt;/s:select&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
@StrutsTag(name="optgroup", tldTagClass="org.hdiv.views.jsp.ui.OptGroupTagHDIV", description="Renders a Select Tag's OptGroup Tag")
public class OptGroupHDIV extends OptGroup {

    public static final String INTERNAL_LIST_UI_BEAN_LIST_PARAMETER_KEY = "optGroupInternalListUiBeanList";

    private static Log _log = LogFactory.getLog(OptGroupHDIV.class);


    public OptGroupHDIV(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
    	super(stack, req, res);
    }

}
