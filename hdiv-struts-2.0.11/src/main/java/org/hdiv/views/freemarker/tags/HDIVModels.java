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

import org.apache.struts2.views.freemarker.tags.ActionErrorModel;
import org.apache.struts2.views.freemarker.tags.ActionMessageModel;
import org.apache.struts2.views.freemarker.tags.ActionModel;
import org.apache.struts2.views.freemarker.tags.AutocompleterModel;
import org.apache.struts2.views.freemarker.tags.BeanModel;
import org.apache.struts2.views.freemarker.tags.ComponentModel;
import org.apache.struts2.views.freemarker.tags.DateModel;
import org.apache.struts2.views.freemarker.tags.DivModel;
import org.apache.struts2.views.freemarker.tags.ElseIfModel;
import org.apache.struts2.views.freemarker.tags.ElseModel;
import org.apache.struts2.views.freemarker.tags.FieldErrorModel;
import org.apache.struts2.views.freemarker.tags.HeadModel;
import org.apache.struts2.views.freemarker.tags.I18nModel;
import org.apache.struts2.views.freemarker.tags.IfModel;
import org.apache.struts2.views.freemarker.tags.IncludeModel;
import org.apache.struts2.views.freemarker.tags.IteratorModel;
import org.apache.struts2.views.freemarker.tags.LabelModel;
import org.apache.struts2.views.freemarker.tags.ParamModel;
import org.apache.struts2.views.freemarker.tags.PropertyModel;
import org.apache.struts2.views.freemarker.tags.PushModel;
import org.apache.struts2.views.freemarker.tags.ResetModel;
import org.apache.struts2.views.freemarker.tags.SetModel;
import org.apache.struts2.views.freemarker.tags.TabbedPanelModel;
import org.apache.struts2.views.freemarker.tags.TreeModel;
import org.apache.struts2.views.freemarker.tags.TreeNodeModel;
import org.apache.struts2.views.freemarker.tags.WebTableModel;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * Provides @s.tag access for various tags.
 *
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
public class HDIVModels {

	protected AnchorModelHDIV a;
	protected CheckboxListModelHDIV checkboxlist;
	protected CheckboxModelHDIV checkbox;	
	protected ComboBoxModelHDIV comboBox;	
	protected FileModelHDIV file;
	protected FormModelHDIV form;
	protected HiddenModelHDIV hidden;
	protected OptionTransferSelectModelHDIV optiontransferselect;
	protected PasswordModelHDIV password;
	protected RadioModelHDIV radio;
	protected SelectModelHDIV select;
	protected SubmitModelHDIV submit;	
	protected TextAreaModelHDIV textarea;
	protected TextFieldModelHDIV textfield;
	protected TokenModelHDIV token;
	protected UpDownSelectModelHDIV updownselect;	
	protected URLModelHDIV url;
	protected DateTimePickerModelHDIV dateTimePicker;
	protected InputTransferSelectModelHDIV inputtransferselect;
	protected TextModelHDIV text;
	protected DoubleSelectModelHDIV doubleselect;
	protected OptGroupModelHDIV optGroupModel;
    protected PropertyModelCustomHDIV propertyHDIV;
	
    protected ValueStack stack;
    protected HttpServletRequest req;
    protected HttpServletResponse res;

    protected ActionModel action;
    protected BeanModel bean;           
    protected ComponentModel component;
    protected DateModel date;    
    protected DivModel div;        
    
    protected HeadModel head;
        
    protected I18nModel i18n;
    protected IncludeModel include;
    protected LabelModel label;    
    protected PushModel push;
    protected ParamModel param;       
    protected SetModel set;
    protected ResetModel reset;
    protected TabbedPanelModel tabbedPanel;        
        
    protected WebTableModel table;
    protected PropertyModel property;
    protected IteratorModel iterator;
    protected ActionErrorModel actionerror;
    protected ActionMessageModel actionmessage;
    protected FieldErrorModel fielderror;    
    protected TreeModel treeModel;
    protected TreeNodeModel treenodeModel;        
    protected IfModel ifModel;
    protected ElseModel elseModel;
    protected ElseIfModel elseIfModel;
    protected AutocompleterModel autocompleterModel;    


    public HDIVModels(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        this.stack = stack;
        this.req = req;
        this.res = res;
    }

    public CheckboxListModelHDIV getCheckboxlist() {
        if (checkboxlist == null) {
            checkboxlist = new CheckboxListModelHDIV(stack, req, res);
        }

        return checkboxlist;
    }

    public CheckboxModelHDIV getCheckbox() {
        if (checkbox == null) {
            checkbox = new CheckboxModelHDIV(stack, req, res);
        }

        return checkbox;
    }

    public ComboBoxModelHDIV getComboBox() {
        if (comboBox == null) {
            comboBox = new ComboBoxModelHDIV(stack, req, res);
        }

        return comboBox;
    }

    public AutocompleterModel getAutocompleter() {
        if (autocompleterModel == null) {
            autocompleterModel = new AutocompleterModel(stack, req, res);
        }

        return autocompleterModel;
    }

    public ComponentModel getComponent() {
        if (component == null) {
            component = new ComponentModel(stack, req, res);
        }

        return component;
    }

    public DoubleSelectModelHDIV getDoubleselect() {
        if (doubleselect == null) {
            doubleselect = new DoubleSelectModelHDIV(stack, req, res);
        }

        return doubleselect;
    }

    public FileModelHDIV getFile() {
        if (file == null) {
            file = new FileModelHDIV(stack, req, res);
        }

        return file;
    }

    public FormModelHDIV getForm() {
        if (form == null) {
            form = new FormModelHDIV(stack, req, res);
        }

        return form;
    }

    public HeadModel getHead() {
        if (head == null) {
            head = new HeadModel(stack, req, res);
        }

        return head;
    }

    public HiddenModelHDIV getHidden() {
        if (hidden == null) {
            hidden = new HiddenModelHDIV(stack, req, res);
        }

        return hidden;
    }
    public LabelModel getLabel() {
        if (label == null) {
            label = new LabelModel(stack, req, res);
        }

        return label;
    }

    public PasswordModelHDIV getPassword() {
        if (password == null) {
            password = new PasswordModelHDIV(stack, req, res);
        }

        return password;
    }

    public RadioModelHDIV getRadio() {
        if (radio == null) {
            radio = new RadioModelHDIV(stack, req, res);
        }

        return radio;
    }

    public SelectModelHDIV getSelect() {
        if (select == null) {
            select = new SelectModelHDIV(stack, req, res);
        }

        return select;
    }

    public SubmitModelHDIV getSubmit() {
        if (submit == null) {
            submit = new SubmitModelHDIV(stack, req, res);
        }

        return submit;
    }

    public ResetModel getReset() {
        if (reset == null) {
            reset = new ResetModel(stack, req, res);
        }

        return reset;
    }

    public TextAreaModelHDIV getTextarea() {
        if (textarea == null) {
            textarea = new TextAreaModelHDIV(stack, req, res);
        }

        return textarea;
    }

    public TextFieldModelHDIV getTextfield() {
        if (textfield == null) {
            textfield = new TextFieldModelHDIV(stack, req, res);
        }

        return textfield;
    }

    public DateModel getDate() {
        if (date == null) {
            date = new DateModel(stack, req, res);
        }

        return date;
    }

    public DateTimePickerModelHDIV getDateTimePicker() {
        if (dateTimePicker == null) {
            dateTimePicker = new DateTimePickerModelHDIV(stack, req, res);
        }

        return dateTimePicker;
    }

    public TokenModelHDIV getToken() {
        if (token == null) {
            token = new TokenModelHDIV(stack, req, res);
        }

        return token;
    }

    public WebTableModel getTable() {
        if (table == null) {
            table = new WebTableModel(stack, req, res);
        }

        return table;
    }

    public URLModelHDIV getUrl() {
        if (url == null) {
            url = new URLModelHDIV(stack, req, res);
        }

        return url;
    }

    public IncludeModel getInclude() {
        if (include == null) {
            include = new IncludeModel(stack, req, res);
        }

        return include;
    }

    public ParamModel getParam() {
        if (param == null) {
            param = new ParamModel(stack, req, res);
        }

        return param;
    }

    public ActionModel getAction() {
        if (action == null) {
            action = new ActionModel(stack, req, res);
        }

        return action;
    }

    public AnchorModelHDIV getA() {
        if (a == null) {
            a = new AnchorModelHDIV(stack, req, res);
        }

        return a;
    }

    public AnchorModelHDIV getHref() {
        if (a == null) {
            a = new AnchorModelHDIV(stack, req, res);
        }

        return a;
    }

    public DivModel getDiv() {
        if (div == null) {
            div = new DivModel(stack, req, res);
        }

        return div;
    }

    public TextModelHDIV getText() {
        if (text == null) {
            text = new TextModelHDIV(stack, req, res);
        }

        return text;
    }

    public TabbedPanelModel getTabbedPanel() {
        if (tabbedPanel == null) {
            tabbedPanel = new TabbedPanelModel(stack, req, res);
        }

        return tabbedPanel;
    }

    public BeanModel getBean() {
        if (bean == null) {
            bean = new BeanModel(stack, req, res);
        }

        return bean;
    }

    public I18nModel getI18n() {
        if (i18n == null) {
            i18n = new I18nModel(stack, req, res);
        }

        return i18n;
    }

    public PushModel getPush() {
        if (push == null) {
            push = new PushModel(stack, req, res);
        }

        return push;
    }

    public SetModel getSet() {
        if (set == null) {
            set = new SetModel(stack, req, res);
        }

        return set;
    }

    public PropertyModel getProperty() {
        if (property == null) {
            property = new PropertyModel(stack, req, res);
        }

        return property;
    }

    public PropertyModelCustomHDIV getPropertyHDIV() {
        if (propertyHDIV == null) {
        	propertyHDIV = new PropertyModelCustomHDIV(stack, req, res);
        }

        return propertyHDIV;
    }    
    
    public IteratorModel getIterator() {
        if (iterator == null) {
            iterator = new IteratorModel(stack, req, res);
        }

        return iterator;
    }

    public ActionErrorModel getActionerror() {
        if (actionerror == null) {
            actionerror = new ActionErrorModel(stack, req, res);
        }

        return actionerror;
    }

    public ActionMessageModel getActionmessage() {
        if (actionmessage == null) {
            actionmessage = new ActionMessageModel(stack, req, res);
        }

        return actionmessage;
    }

    public FieldErrorModel getFielderror() {
        if (fielderror == null) {
            fielderror = new FieldErrorModel(stack, req, res);
        }

        return fielderror;
    }

    public OptionTransferSelectModelHDIV getOptiontransferselect() {
        if (optiontransferselect == null) {
            optiontransferselect = new OptionTransferSelectModelHDIV(stack, req, res);
        }
        return optiontransferselect;
    }

    public TreeModel getTree() {
        if (treeModel == null) {
            treeModel = new TreeModel(stack,req, res);
        }
        return treeModel;
    }

    public TreeNodeModel getTreenode() {
        if (treenodeModel == null) {
            treenodeModel = new TreeNodeModel(stack, req, res);
        }
        return treenodeModel;
    }

    public UpDownSelectModelHDIV getUpdownselect() {
        if (updownselect == null)  {
            updownselect = new UpDownSelectModelHDIV(stack, req, res);
        }
        return updownselect;
    }

    public OptGroupModelHDIV getOptgroup() {
        if (optGroupModel == null) {
            optGroupModel = new OptGroupModelHDIV(stack, req, res);
        }
        return optGroupModel;
    }

    public IfModel getIf() {
        if (ifModel == null) {
            ifModel = new IfModel(stack, req, res);
        }
        return ifModel;
    }

    public ElseModel getElse() {
        if (elseModel == null) {
            elseModel = new ElseModel(stack, req, res);
        }
        return elseModel;
    }

    public ElseIfModel getElseif() {
        if (elseIfModel == null) {
            elseIfModel = new ElseIfModel(stack, req, res);
        }
        return elseIfModel;
    }


    public InputTransferSelectModelHDIV getInputtransferselect() {
        if (inputtransferselect == null) {
            inputtransferselect = new InputTransferSelectModelHDIV(stack, req, res);
        }
        return inputtransferselect;
    }

}
