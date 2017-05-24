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
package org.hdiv.views.freemarker.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.freemarker.tags.SelectModel;
import org.hdiv.components.SelectHDIV;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see SelectHDIV
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
public class SelectModelHDIV extends SelectModel {
	
    public SelectModelHDIV(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected Component getBean() {
        return new SelectHDIV(stack, req, res);
    }
}