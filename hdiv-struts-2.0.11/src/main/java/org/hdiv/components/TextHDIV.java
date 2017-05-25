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

import org.apache.struts2.components.Param;
import org.apache.struts2.components.Text;
import org.apache.struts2.views.annotations.StrutsTag;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * Render a I18n text message.
 *
 * <p/>
 *
 * The message must be in a resource bundle
 * with the same name as the action that it is associated with. In practice
 * this means that you should create a properties file in the same package
 * as your Java class with the same name as your class, but with .properties
 * extension.
 *
 * <p/>
 *
 * If the named message is not found, then the body of the tag will be used as default message.
 * If no body is used, then the name of the message will be used.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 *
 *
 * <!-- START SNIPPET: params -->
 *
 * <ul>
 *      <li>name* (String) - the i18n message key</li>
 * </ul>
 *
 * <!-- END SNIPPET: params -->
 *
 * <p/>
 *
 * Example:
 * <pre>
 * <!-- START SNIPPET: exdescription -->
 *
 * Accessing messages from a given bundle (the i18n Shop example bundle in the first example) and using bundle defined through the framework in the second example.</p>
 *
 * <!-- END SNIPPET: exdescription -->
 * </pre>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;!-- First Example --&gt;
 * &lt;s:i18n name="struts.action.test.i18n.Shop"&gt;
 *     &lt;s:text name="main.title"/&gt;
 * &lt;/s:i18n&gt;
 *
 * &lt;!-- Second Example --&gt;
 * &lt;s:text name="main.title" /&gt;
 *
 * &lt;!-- Third Examlpe --&gt;
 * &lt;s:text name="i18n.label.greetings"&gt;
 *    &lt;s:param &gt;Mr Smith&lt;/s:param&gt;
 * &lt;/s:text&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 *
 * <pre>
 * <!-- START SNIPPET: i18nExample -->
 *
 * &lt;-- Fourth Example --&gt;
 * &lt;s:text name="some.key" /&gt;
 *
 * &lt;-- Fifth Example --&gt;
 * &lt;s:text name="some.invalid.key" &gt;
 *    The Default Message That Will Be Displayed
 * &lt;/s:text&gt;
 *
 * <!-- END SNIPPET: i18nExample -->
 * </pre>
 *
 * @see Param
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
@StrutsTag(name="text", tldTagClass="org.hdiv.views.jsp.TextTagHDIV", description="Render a I18n text message")
public class TextHDIV extends Text {

    public TextHDIV(ValueStack stack) {
        super(stack);
    }

}
