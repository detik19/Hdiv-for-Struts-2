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
package org.hdiv.views;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.TagLibrary;
import org.apache.struts2.views.velocity.components.ActionDirective;
import org.apache.struts2.views.velocity.components.ActionErrorDirective;
import org.apache.struts2.views.velocity.components.ActionMessageDirective;
import org.apache.struts2.views.velocity.components.AutocompleterDirective;
import org.apache.struts2.views.velocity.components.BeanDirective;
import org.apache.struts2.views.velocity.components.ComponentDirective;
import org.apache.struts2.views.velocity.components.DateDirective;
import org.apache.struts2.views.velocity.components.DivDirective;
import org.apache.struts2.views.velocity.components.FieldErrorDirective;
import org.apache.struts2.views.velocity.components.HeadDirective;
import org.apache.struts2.views.velocity.components.I18nDirective;
import org.apache.struts2.views.velocity.components.IncludeDirective;
import org.apache.struts2.views.velocity.components.LabelDirective;
import org.apache.struts2.views.velocity.components.ParamDirective;
import org.apache.struts2.views.velocity.components.PropertyDirective;
import org.apache.struts2.views.velocity.components.PushDirective;
import org.apache.struts2.views.velocity.components.ResetDirective;
import org.apache.struts2.views.velocity.components.SetDirective;
import org.apache.struts2.views.velocity.components.TabbedPanelDirective;
import org.apache.struts2.views.velocity.components.TreeDirective;
import org.apache.struts2.views.velocity.components.TreeNodeDirective;
import org.apache.struts2.views.velocity.components.WebTableDirective;
import org.hdiv.views.freemarker.tags.HDIVModels;
import org.hdiv.views.velocity.components.AnchorDirectiveHDIV;
import org.hdiv.views.velocity.components.CheckBoxDirectiveHDIV;
import org.hdiv.views.velocity.components.CheckBoxListDirectiveHDIV;
import org.hdiv.views.velocity.components.ComboBoxDirectiveHDIV;
import org.hdiv.views.velocity.components.DateTimePickerDirectiveHDIV;
import org.hdiv.views.velocity.components.DoubleSelectDirectiveHDIV;
import org.hdiv.views.velocity.components.FileDirectiveHDIV;
import org.hdiv.views.velocity.components.FormDirectiveHDIV;
import org.hdiv.views.velocity.components.HiddenDirectiveHDIV;
import org.hdiv.views.velocity.components.OptionTransferSelectDirectiveHDIV;
import org.hdiv.views.velocity.components.PasswordDirectiveHDIV;
import org.hdiv.views.velocity.components.PropertyDirectiveCustomHDIV;
import org.hdiv.views.velocity.components.RadioDirectiveHDIV;
import org.hdiv.views.velocity.components.SelectDirectiveHDIV;
import org.hdiv.views.velocity.components.SubmitDirectiveHDIV;
import org.hdiv.views.velocity.components.TextAreaDirectiveHDIV;
import org.hdiv.views.velocity.components.TextDirectiveHDIV;
import org.hdiv.views.velocity.components.TextFieldDirectiveHDIV;
import org.hdiv.views.velocity.components.TokenDirectiveHDIV;
import org.hdiv.views.velocity.components.URLDirectiveHDIV;
import org.hdiv.views.velocity.components.UpDownSelectDirectiveHDIV;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * Provides Velocity and Freemarker implementation classes for a tag library.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
public class HDIVTagLibrary implements TagLibrary {

    /**
     * Gets a Java object that contains getters for the tag library's Freemarker models.  
     * Called once per Freemarker template processing.
     * 
     * @param stack The current value stack
     * @param req The HTTP request
     * @param res The HTTP response
     * @return The Java object containing the Freemarker model getter methods
     */
    public Object getFreemarkerModels(ValueStack stack,
    								  HttpServletRequest req, 
    								  HttpServletResponse res) {
    	
    	return new HDIVModels(stack, req, res);
    } 
	
   /**
    * Gets a list of Velocity directive classes for the tag library. Called once on
	* framework startup when initializing Velocity.
	* 
	* @return A list of Velocity directive classes
    */
   public List<Class> getVelocityDirectiveClasses() {
	   
        Class[] directives = new Class[] {
            ActionDirective.class,
            BeanDirective.class,
            CheckBoxDirectiveHDIV.class,
            CheckBoxListDirectiveHDIV.class,
            ComboBoxDirectiveHDIV.class,
            ComponentDirective.class,
            DateDirective.class,
            DateTimePickerDirectiveHDIV.class,
            DivDirective.class,
            AutocompleterDirective.class,
            DoubleSelectDirectiveHDIV.class,
            FileDirectiveHDIV.class,
            FormDirectiveHDIV.class,
            HeadDirective.class,
            HiddenDirectiveHDIV.class,
            AnchorDirectiveHDIV.class,
            I18nDirective.class,
            IncludeDirective.class,
            LabelDirective.class,
            ParamDirective.class,
            PasswordDirectiveHDIV.class,
            PushDirective.class,
            PropertyDirective.class,
            RadioDirectiveHDIV.class,
            SelectDirectiveHDIV.class,
            SetDirective.class,
            SubmitDirectiveHDIV.class,
            ResetDirective.class,
            TabbedPanelDirective.class,
            TextAreaDirectiveHDIV.class,
            TextDirectiveHDIV.class,
            TextFieldDirectiveHDIV.class,
            TokenDirectiveHDIV.class,
            TreeDirective.class,
            TreeNodeDirective.class,
            URLDirectiveHDIV.class,
            WebTableDirective.class,
            ActionErrorDirective.class,
            ActionMessageDirective.class,
            FieldErrorDirective.class,
            OptionTransferSelectDirectiveHDIV.class,
            UpDownSelectDirectiveHDIV.class,
            PropertyDirectiveCustomHDIV.class
        };
        return Arrays.asList(directives);
    }
    
}
