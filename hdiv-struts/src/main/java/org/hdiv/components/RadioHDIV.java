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

import org.apache.struts2.components.Radio;
import org.apache.struts2.views.annotations.StrutsTag;
import org.hdiv.dataComposer.IDataComposer;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render a radio button input field.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 * <p/>
 * <!-- START SNIPPET: exdescription -->
 * In this example, a radio control is displayed with a list of genders. The gender list is built from attribute
 * id=genders. The framework calls getGenders() which will return a Map. For examples using listKey and listValue attributes,
 * see the section select tag. The default selected one will be determined (in this case) by the getMale() method
 * in the action class which should retun a value similar to the key of the getGenters() map if that particular
 * gender is to be selected.<p/>
 * <!-- END SNIPPET: exdescription -->
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;s:action name="GenderMap" id="genders"/&gt;
 * &lt;s:radio label="Gender" name="male" list="#genders.genders"/&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
@StrutsTag(name="radio", tldTagClass="org.hdiv.views.jsp.ui.RadioTagHDIV", description="Renders a radio button input field")
public class RadioHDIV extends Radio {
	
    final public static String TEMPLATE = "radiomapHDIV";
    
    private static final String DATA_COMPOSER = "dataComposer";

    public RadioHDIV(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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
