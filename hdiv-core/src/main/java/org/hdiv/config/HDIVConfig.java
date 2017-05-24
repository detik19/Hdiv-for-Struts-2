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
package org.hdiv.config;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hdiv.validator.IValidation;

/**
 * Class containing HDIV configuration initialized from Spring Factory.
 * 
 * @author Roberto Velasco
 * @author Gorka Vicente
 */
public class HDIVConfig {

	/**
	 * Map with the pages that will not be Treated by the HDIV filter. The init pages
	 * are initialized by the Spring factory.
	 */
	protected Hashtable startPages;

	/**
	 * Map with the parameters that will not be validated by the HDIV filter. The
	 * init parameters are initialized by the Spring factory.
	 */
	private Hashtable startParameters;

	/**
	 * Map of the validated actions that are init pages. It is necessary to create
	 * this map to improve the efficiency at checking init-pages.
	 * 
	 * @since HDIV 1.1.1
	 */
	private Hashtable matchedPages;

	/**
	 * Map of the validated parameters that are init parameters. It is necessary to
	 * create this map to improve the efficiency at checking init-parameters.
	 * 
	 * @since HDIV 1.1.1
	 */
	private Hashtable matchedParameters;

	/**
	 * Error page to which HDIV will redirect the request if it doesn't pass the HDIV
	 * validation.
	 */
	private String errorPage;

	/**
	 * Confidentiality indicator to know if information is accessible only for those
	 * who are authorized.
	 */
	private Boolean confidentiality;

	/**
	 * Parameters which HDIV validation will not be appied to.
	 */
	private Map paramsWithoutValidation;

	/**
	 * Validations for editable fields (text/textarea) defined by the user in the
	 * hdiv-validations.xml configuration file of Spring.
	 */
	private HDIVValidations validations;

	/**
	 * If <code>cookiesIntegrity</code> is true, cookie integrity will not be
	 * applied.
	 */
	private boolean cookiesIntegrity;

	/**
	 * If <code>cookiesConfidentiality</code> is true, cookie confidentiality will
	 * not be applied.
	 */
	private boolean cookiesConfidentiality;
	
	/**
	 * Extensions that we have to protect with HDIV's state.
	 * @since HDIV 2.0
	 */
	private Hashtable protectedURLPatterns;
	

	/**
	 * Checks if <code>parameter</code> is an init parameter, in which case it will
	 * not be treated by HDIV.
	 * 
	 * @param parameter Parameter name
	 * @return True if <code>parameter</code> is an init parameter. False
	 *         otherwise.
	 */
	public boolean isStartParameter(String parameter) {

		if (this.matchedParameters.containsKey(parameter)) {
			return true;
		}

		String value = this.checkValue(parameter, this.startParameters);
		if (value == null) {
			return false;
		}

		this.matchedParameters.put(parameter, value);
		return true;
	}

	/**
	 * Checks if <code>target</code> is an init action, in which case it will not
	 * be treated by HDIV.
	 * 
	 * @param target target name
	 * @return True if <code>target</code> is an init action. False otherwise.
	 */
	public boolean isStartPage(String target) {

		if (this.matchedPages.containsKey(target)) {
			return true;
		}

		String value = this.checkValue(target, this.startPages);
		if (value == null) {
			return false;
		}

		this.matchedPages.put(target, value);
		return true;
	}

	/**
	 * Checks if the parameter <code>parameter</code> is defined by the user as a
	 * no required validation parameter for the action <code>action</code>.
	 * 
	 * @param action action name
	 * @param parameter parameter name
	 * @return True if it is parameter that needs no validation. False otherwise.
	 */
	public boolean isParameterWithoutValidation(String action, String parameter) {

		Pattern p = null;
		Matcher m = null;

		String key = null;
		for (Iterator iter = this.paramsWithoutValidation.keySet().iterator(); iter.hasNext();) {

			key = (String) iter.next();

			p = Pattern.compile(key);
			m = p.matcher(action);
			
			if (m.matches()) {

				List parametersWithoutValidation = (List) this.paramsWithoutValidation.get(key);

				String definedParameter = null;
				for (int i = 0; i < parametersWithoutValidation.size(); i++) {

					definedParameter = (String) parametersWithoutValidation.get(i);

					p = Pattern.compile(definedParameter);
					m = p.matcher(parameter);

					if (m.matches()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if <code>value</code> is an init action or parameter, in which case
	 * it will not be treated by HDIV.
	 * 
	 * @param value target or parameter name
	 * @param startValues Map with start values
	 * @return True if <code>value</code> is an init action or parameter. False
	 *         otherwise.
	 * @since HDIV 1.1.1
	 */
	public String checkValue(String value, Map startValues) {

		String key = null;
		Pattern p = null;
		Matcher m = null;

		for (Iterator iter = startValues.keySet().iterator(); iter.hasNext();) {

			key = (String) iter.next();
			p = (Pattern) startValues.get(key);
			
			m = p.matcher(value);

			if (m.matches()) {
				return key;
			}
		}
		return null;
	}

	/**
	 * Checks if the HDIV validation must be applied to the parameter
	 * <code>parameter</code>
	 * 
	 * @param parameter Parameter name
	 * @param hdivParameter Name of the parameter that HDIV will include in the
	 *            requests or/and forms which contains the state identifier parameter
	 * @return True if <code>parameter</code> doesn't need HDIV validation.
	 */
	public boolean needValidation(String parameter, String hdivParameter) {

		if (this.isStartParameter(parameter) || (parameter.equals(hdivParameter))) {
			return false;
		}
		return true;
	}	

	public String getErrorPage() {
		return errorPage;
	}

	public void setErrorPage(String errorPage) {
		
		if (!errorPage.startsWith("/")) {
			errorPage = "/" + errorPage;
		}		
		this.errorPage = errorPage;
		this.startPages.put(errorPage, Pattern.compile(errorPage));
	}

	public Boolean getConfidentiality() {
		return confidentiality;
	}

	public void setConfidentiality(Boolean confidentiality) {
		this.confidentiality = confidentiality;
	}

	public Map getParamsWithoutValidation() {
		return paramsWithoutValidation;
	}

	public void setParamsWithoutValidation(Map paramsWithoutValidation) {
		this.paramsWithoutValidation = paramsWithoutValidation;
	}

	/**
	 * It creates a map from the list of start pages defined by the user.
	 * 
	 * @param userStartPages list of start pages defined by the user
	 */
	public void setUserStartPages(List userStartPages) {

		this.matchedPages = new Hashtable();
		this.startPages = new Hashtable();

		String currentPattern;
		for (int i = 0; i < userStartPages.size(); i++) {

			currentPattern = (String) userStartPages.get(i);
			this.startPages.put(currentPattern, Pattern.compile(currentPattern));
		}
	}

	/**
	 * It creates a map from the list of init parameters defined by the user.
	 * 
	 * @param userStartPages list of init parameters defined by the user
	 */
	public void setUserStartParameters(List userStartParameters) {

		this.matchedParameters = new Hashtable();
		this.startParameters = new Hashtable();

		String currentPattern;
		for (int i = 0; i < userStartParameters.size(); i++) {

			currentPattern = (String) userStartParameters.get(i);
			this.startParameters.put(currentPattern, Pattern.compile(currentPattern));
		}
	}

	/**
	 * @param validations The validations to set.
	 * @since HDIV 1.1
	 */
	public void setValidations(HDIVValidations validations) {
		this.validations = validations;
	}

	/**
	 * Checks if there are validations defined for editable fields
	 * 
	 * @return True if validations for editable fields have been defined. False
	 *         otherwise.
	 * @since HDIV 1.1
	 */
	public boolean existValidations() {

		return ((this.validations != null) && (this.validations.getUrls() != null) && (this.validations
				.getUrls().size() > 0));
	}

	/**
	 * <p>
	 * Checks if the values <code>values</code> are valid for the editable
	 * parameter <code>parameter</code>, using the validations defined in the
	 * hdiv-validations.xml configuration file of Spring.
	 * </p>
	 * <p>
	 * There are two types of validations:
	 * <li>accepted: the value is valid only if it passes the validation</li>
	 * <li>rejected: the value is rejected if doesn't pass the validation</li>
	 * </p>
	 * 
	 * @param target target name
	 * @param parameter parameter name
	 * @param values parameter's values
	 * @param dataType editable data type
	 * @return True if the values <code>values</code> are valid for the parameter
	 *         <code>parameter</code>.
	 * @since HDIV 1.1
	 */
	public boolean areEditableParameterValuesValid(String url, String parameter, String[] values,
			String dataType) {

		Pattern p = null;
		Matcher m = null;

		Iterator userDefinedURLs = this.validations.getUrls().keySet().iterator();
		while (userDefinedURLs.hasNext()) {

			String regExp = (String) userDefinedURLs.next();

			p = Pattern.compile(regExp);
			m = p.matcher(url);

			if (m.matches()) {

				List userDefinedValidations = (List) this.validations.getUrls().get(regExp);
				for (int i = 0; i < userDefinedValidations.size(); i++) {

					IValidation currentValidation = (IValidation) userDefinedValidations.get(i);

					if (!currentValidation.validate(parameter, values, dataType)) {
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * @return Returns true if cookies' confidentiality is activated.
	 */
	public boolean isCookiesConfidentialityActivated() {
		return (cookiesConfidentiality == false);
	}

	/**
	 * @param cookiesConfidentiality The cookiesConfidentiality to set.
	 */
	public void setCookiesConfidentiality(Boolean cookiesConfidentiality) {
		this.cookiesConfidentiality = cookiesConfidentiality.booleanValue();
	}

	/**
	 * @return Returns true if cookies' integrity is activated.
	 */
	public boolean isCookiesIntegrityActivated() {
		return (cookiesIntegrity == false);
	}

	/**
	 * @param cookiesIntegrity The cookiesIntegrity to set.
	 */
	public void setCookiesIntegrity(Boolean cookiesIntegrity) {
		this.cookiesIntegrity = cookiesIntegrity.booleanValue();
	}

	/**
	 * @param protectedExtensions The protected extensions to set.
	 * @since HDIV 2.0
	 */
	public void setProtectedExtensions(List protectedExtensions) {
		
		this.protectedURLPatterns = new Hashtable();
		
		String currentProtectedExtension;
		for (int i = 0; i < protectedExtensions.size(); i++) {

			currentProtectedExtension = (String) protectedExtensions.get(i);
			this.protectedURLPatterns.put(currentProtectedExtension, Pattern.compile(currentProtectedExtension));
		}
	}

	/**
	 * @return Returns the protected extensions.
	 * @since HDIV 2.0
	 */
	public Hashtable getProtectedURLPatterns() {
		return protectedURLPatterns;
	}

}
