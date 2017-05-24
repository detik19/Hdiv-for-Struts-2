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
import org.apache.struts2.components.DateTimePicker;
import org.apache.struts2.views.annotations.StrutsTag;
import org.hdiv.dataComposer.IDataComposer;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>
 * Renders a date/time picker in a dropdown container.
 * </p>
 * <p>
 * A stand-alone DateTimePicker widget that makes it easy to select a date/time, or increment by week, month,
 * and/or year.
 * </p>
 *
 * <p>
 * It is possible to customize the user-visible formatting with either the
 * 'formatLength' (long, short, medium or full) or 'displayFormat' attributes. By defaulty current
 * locale will be used.</p>
 * </p>
 * 
 * Syntax supported by 'displayFormat' is (http://www.unicode.org/reports/tr35/tr35-4.html#Date_Format_Patterns):-
 * <table border="1">
 *   <tr>
 *      <td>Format</td>
 *      <td>Description</td>
 *   </tr>
 *   <tr>
 *      <td>d</td>
 *      <td>Day of the month</td>
 *   </tr>
 *   <tr>
 *      <td>D</td>
 *      <td>Day of year</td>
 *   </tr>
 *   <tr>
 *      <td>M</td>
 *      <td>Month - Use one or two for the numerical month, three for the abbreviation, or four for the full name, or 5 for the narrow name.</td>
 *   </tr>
 *   <tr>
 *      <td>h</td>
 *      <td>Hour [1-12].</td>
 *   </tr>
 *   <tr>
 *      <td>H</td>
 *      <td>Hour [0-23].</td>
 *   </tr>
 *   <tr>
 *      <td>m</td>
 *      <td>Minute. Use one or two for zero padding.</td>
 *   </tr>
 *   <tr>
 *      <td>s</td>
 *      <td>Second. Use one or two for zero padding.</td>
 *   </tr>
 * </table>
 * 
 * <p>
 * The value sent to the server is
 * typically a locale-independent value in a hidden field as defined by the name
 * attribute. RFC3339 representation is the format used.
 * </p>
 *
 * <p/>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: expl1 -->
 *
 * Example 1:
 *     &lt;s:datetimepicker name="order.date" label="Order Date" /&gt;
 * Example 2:
 *     &lt;s:datetimepicker name="delivery.date" label="Delivery Date" format="yyyy-MM-dd"  /&gt;
 *
 * <!-- END SNIPPET: expl1 -->
 * </pre>
 * <p/>
 *
 * <!-- START SNIPPET: expldesc2 -->
 *
 * The css could be changed by using the following :-
 *
 * <!-- END SNIPPET: expldesc2 -->
 *
 * <pre>
 * <!-- START SNIPPET: expl2 -->
 *
 * &lt;s:datetimepicker name="birthday" label="Birthday" templateCss="...." /&gt;
 *
 * <!-- END SNIPPET: expl2 -->
 * </pre>
 *
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
@StrutsTag(name="datetimepicker", tldTagClass="org.hdiv.views.jsp.ui.DateTimePickerTag", description="Render datetimepicker")
public class DateTimePickerHDIV extends DateTimePicker {

    final public static String TEMPLATE = "datetimepickerHDIV";
    
    private static final String DATA_COMPOSER = "dataComposer";

    public DateTimePickerHDIV(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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
