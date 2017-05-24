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
package org.hdiv.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.velocity.components.CheckBoxDirective;
import org.hdiv.components.CheckboxHDIV;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see CheckboxHDIV
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
public class CheckBoxDirectiveHDIV extends CheckBoxDirective {
	
    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new CheckboxHDIV(stack, req, res);
    }

}
