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
package org.hdiv.filter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.dataValidator.IDataValidator;
import org.hdiv.dataValidator.IValidationResult;
import org.hdiv.exception.HDIVException;
import org.hdiv.logs.Logger;
import org.hdiv.session.ISession;
import org.hdiv.state.IPage;
import org.hdiv.state.IParameter;
import org.hdiv.state.IState;
import org.hdiv.state.State;
import org.hdiv.state.StateUtil;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * It validates client requests by comsuming an object of type IState and
 * validating all the entry data, besides replacing relative values by its real
 * values.
 * 
 * @author Roberto Velasco
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public abstract class AbstractValidatorHelper implements IValidationHelper, BeanFactoryAware {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(AbstractValidatorHelper.class);

	/**
	 * The root interface for accessing a Spring bean container.
	 * 
	 * @see org.springframework.beans.factory.BeanFactory
	 */
	private BeanFactory beanFactory;

	/**
	 * Target name
	 */
	private String target;

	/**
	 * Target name without context path name
	 */
	private String targetWithoutContextPath;

	/**
	 * Data validator
	 */
	private IDataValidator dataValidator;

	/**
	 * A wrapper for HTTP servlet request.
	 */
	private RequestWrapper requestWrapper;

	/**
	 * HDIV configuration object.
	 */
	private HDIVConfig hdivConfig;

	/**
	 * Logger to print the possible attacks detected by HDIV.
	 */
	private Logger logger;

	/**
	 * State that represents all the request or form data.
	 */
	private IState state;

	/**
	 * Utility methods for state
	 */
	private StateUtil stateUtil;

	/**
	 * Name of the parameter that HDIV will include in the requests or/and forms
	 * which contains the state identifier in the memory strategy or the state
	 * itself in the Encoded or Hash strategies.
	 */
	private String hdivParameter;

	private HttpServletRequest request; // only for testing

	/**
	 * Initialization of the objects needed for the validation process.
	 * 
	 * @param request HTTP servlet request
	 * @throws HDIVException if there is an initialization error.
	 */
	public void init(HttpServletRequest request) {

		try {
			this.target = HDIVUtil.actionName(request);
			this.dataValidator = HDIVUtil.getDataValidator();

			this.requestWrapper = new RequestWrapper(request);
			this.requestWrapper.setConfidentiality(this.hdivConfig.getConfidentiality());
			this.requestWrapper.setCookiesConfidentiality(this.hdivConfig.isCookiesConfidentialityActivated());

			this.hdivParameter = (String) HDIVUtil.getHttpSession().getAttribute("HDIVParameter");
			this.targetWithoutContextPath = this.target.substring(this.requestWrapper.getContextPath().length());

			this.logger.init(request, HDIVUtil.getHttpSession());

		} catch (Exception e) {
			String errorMessage = HDIVUtil.getMessage("helper.init");
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Testing objects initilization.
	 */
	public void initTesting() {

		this.requestWrapper = new RequestWrapper(request);
		this.requestWrapper.setConfidentiality(this.hdivConfig.getConfidentiality());
		this.requestWrapper.setCookiesConfidentiality(this.hdivConfig.isCookiesConfidentialityActivated());

		this.state = new State();
		this.hdivParameter = (String) this.beanFactory.getBean("hdivParameter");
		this.dataValidator = (IDataValidator) this.beanFactory.getBean("dataValidator");
		this.targetWithoutContextPath = (String) this.beanFactory.getBean("targetName");
		this.logger.init(request, HDIVUtil.getHttpSession());
	}

	/**
	 * Checks if the values of the parameters received in the request
	 * <code>request</code> are valid. These values are valid if and only if
	 * the noneditable parameters haven´t been modified.<br>
	 * Validation process is as follows.<br>
	 * 1. If the action to which the request is directed is an init page, then
	 * it is a valid request.<br>
	 * 2. if the cookies received in the request are not found in the user
	 * session, the validation is incorrect.<br>
	 * 3. if the state recover process has produced an error, incorrect
	 * validation.<br>
	 * 4. If the action received in the request is different to the action of
	 * the recovered state, incorrect validation.<br>
	 * 5. If not, all the parameter values are checked and if all the received
	 * values are valid then the request is valid. <br>
	 * 5.1. If it is an init parameter or a HDIV parameter then it is a valid
	 * parameter.<br>
	 * 5.2. If the received parameter is not in the state:<br>
	 * 5.2.1. If it has been defined by the user as a no validation required
	 * parameter, then it is a valid parameter.<br>
	 * 5.2.2. otherwise, it is a no valid request.<br>
	 * 5.3. If the parameter is editable, if validations have been defined
	 * values are checked.<br>
	 * 5.4. If it is a noneditable parameter, all the received values are
	 * checked.
	 * 
	 * @return True If all the parameter values of the request
	 *         <code>request</code> pass the the HDIV validation. False,
	 *         otherwise.
	 * @throws HDIVException If the request doesn't pass the HDIV validation an
	 *             exception is thrown explaining the cause of the error.
	 */
	public boolean validate() {

		if (this.hdivConfig.isStartPage(this.targetWithoutContextPath)) {
			return (this.validateStartPageParameters());
		}

		if (this.hdivConfig.isCookiesIntegrityActivated()) {
			if (!this.validateRequestCookies(this.requestWrapper.getCookies())) {
				return false;
			}
		}

		// restore state from request or from memory
		if (!this.restoreState(this.requestWrapper)) {
			return false;
		}

		if (!this.isTheSameAction()) {
			return false;
		}

		if (!this.allRequiredParametersReceived(this.requestWrapper)) {
			return false;
		}

		Hashtable unauthorizedEditableParameters = new Hashtable();
		Enumeration parameters = this.requestWrapper.getParameterNames();
		while (parameters.hasMoreElements()) {

			String parameter = (String) parameters.nextElement();

			// check if the HDIV validation must be applied to the parameter
			if (!this.hdivConfig.needValidation(parameter, this.hdivParameter)) {

				if (log.isDebugEnabled() && !parameter.equals(this.hdivParameter)) {
					log.debug("parameter " + parameter + " doesn't need validation");
				}
				continue;
			}

			// if the parameter requires no validation it is considered a
			// valid parameter
			if (this.isUserDefinedNonValidationParameter(parameter)) {
				continue;
			}			
			
			IParameter stateParameter = this.state.getParameter(parameter);
			if (stateParameter == null) {

				// If the parameter is not defined in the state, it is an error.
				// With this verification we guarantee that no extra parameters are
				// added.
				this.logger.log(HDIVErrorCodes.PARAMETER_NOT_EXISTS, this.target, parameter, null);
				return false;
			}

			// At this point we are processing a noneditable parameter
			String[] values = this.requestWrapper.getParameterValues(parameter);

			// check if the parameter is editable
			if (stateParameter.isEditable()) {

				if (hdivConfig.existValidations() && (stateParameter.getEditableDataType() != null)) {
					this.validateEditableParameter(parameter, values, stateParameter.getEditableDataType(),
							unauthorizedEditableParameters);
				}
				continue;
			}

			try {
				if (!this.validateParameterValues(stateParameter, parameter, values)) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				String errorMessage = HDIVUtil.getMessage("validation.error", e.getMessage());
				throw new HDIVException(errorMessage, e);
			}
		}

		if (unauthorizedEditableParameters.size() > 0) {
			this.requestWrapper.getRequest().setAttribute(HDIVErrorCodes.EDITABLE_PARAMETER_ERROR,
					unauthorizedEditableParameters);
		}

		return true;
	}

	/**
	 * Checks if the action received in the request is the same as the one
	 * stored in the HDIV state.
	 * 
	 * @return True if the actions are the same. False otherwise.
	 */
	public boolean isTheSameAction() {

		if (this.state.getAction().equalsIgnoreCase(target)) {
			return true;
		}

		if (target.endsWith("/")) {
			String actionSlash = this.state.getAction() + "/";
			if (actionSlash.equalsIgnoreCase(target)) {
				return true;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("target:" + target);
			log.debug("state action:" + this.state.getAction());
		}

		this.logger.log(HDIVErrorCodes.ACTION_ERROR, this.target, null, null);
		return false;
	}

	/**
	 * It validates the parameters of an init page because our application can
	 * receive requests that require validation but don't have any HDIV state.
	 * So, despite being init pages, editable data validation must be done.
	 * 
	 * @return True if the values of the editable parameters pass the
	 *         validations defined in hdiv-config.xml. False otherwise.
	 * @since HDIV 1.1.2
	 */
	public boolean validateStartPageParameters() {

		if (hdivConfig.existValidations()) {

			Enumeration parameters = this.requestWrapper.getParameterNames();
			while (parameters.hasMoreElements()) {

				String parameter = (String) parameters.nextElement();
				String[] values = this.requestWrapper.getParameterValues(parameter);

				if (!hdivConfig.areEditableParameterValuesValid(this.targetWithoutContextPath, parameter, values, "")) {
					this.logger.log(HDIVErrorCodes.EDITABLE_PARAMETER_ERROR, this.target, parameter, values.toString());
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the cookies received in the request are correct. For that, it
	 * checks if they are in the user session.
	 * 
	 * @param requestCookies cookies received in the request
	 * @return True if all the cookies received in the request are correct. They
	 *         must have been previously stored in the user session by HDIV to
	 *         be correct. False otherwise.
	 * @since HDIV 1.1
	 */
	public boolean validateRequestCookies(Cookie[] requestCookies) {

		if ((requestCookies == null) || (requestCookies.length == 0)) {
			return true;
		}

		ISession sessionHDIV = HDIVUtil.getSession();
		Hashtable sessionCookies = (Hashtable) sessionHDIV.getWebSession().getAttribute(Constants.HDIV_COOKIES_KEY);

		if (sessionCookies == null) {
			return true;
		}

		boolean cookiesConfidentiality = Boolean.TRUE.equals(this.hdivConfig.getConfidentiality())
				&& this.hdivConfig.isCookiesConfidentialityActivated();

		for (int i = 0; i < requestCookies.length; i++) {

			boolean found = false;
			if (requestCookies[i].getName().equals(Constants.JSESSIONID)) {
				continue;
			}

			if (sessionCookies.containsKey(requestCookies[i].getName())) {

				SavedCookie savedCookie = (SavedCookie) sessionCookies.get(requestCookies[i].getName());
				if (savedCookie.equals(requestCookies[i], cookiesConfidentiality)) {

					found = true;
					if (cookiesConfidentiality) {
						if (savedCookie.getValue() != null) {
							requestCookies[i].setValue(savedCookie.getValue());
						}
					}
				}
			}

			if (!found) {
				this.logger.log(HDIVErrorCodes.COOKIE_INCORRECT, this.target, "cookie:" + requestCookies[i].getName(),
						requestCookies[i].getValue());
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the values <code>values</code> are valid for the editable
	 * parameter <code>parameter</code>. This validation is defined by the
	 * user in the hdiv-validations.xml file of Spring. If the values are not
	 * valid, an error message with the parameter and the received values will
	 * be log.
	 * 
	 * @param parameter parameter name
	 * @param values parameter's values
	 * @param dataType editable data type
	 * @param unauthorizedParameters Unauthorized editable parameters
	 * @since HDIV 1.1
	 */
	public void validateEditableParameter(String parameter, String[] values, String dataType,
			Hashtable unauthorizedParameters) {

		boolean isValid = hdivConfig.areEditableParameterValuesValid(this.targetWithoutContextPath, parameter, values, dataType);
		if (!isValid) {

			StringBuffer unauthorizedValues = new StringBuffer(values[0]);

			for (int i = 1; i < values.length; i++) {
				unauthorizedValues.append("," + values[i]);
			}

			if (dataType.equals("password")) {
				String[] passwordError = { "hdiv.editable.password.error" };
				unauthorizedParameters.put(parameter, passwordError);
			} else {
				unauthorizedParameters.put(parameter, values);
			}

			this.logger.log(HDIVErrorCodes.EDITABLE_VALIDATION_ERROR, this.target, parameter, unauthorizedValues
					.toString());
		}
	}

	/**
	 * Check if all required parameters are received in <code>request</code>.
	 * 
	 * @param request HTTP request
	 * @return True if all required parameters are received. False in otherwise.
	 */
	private boolean allRequiredParametersReceived(HttpServletRequest request) {

		Hashtable receivedParameters = new Hashtable(this.state.getRequiredParams());

		String currentParameter = null;
		Enumeration requestParameters = request.getParameterNames();
		while (requestParameters.hasMoreElements()) {

			currentParameter = (String) requestParameters.nextElement();
			if (receivedParameters.containsKey(currentParameter)) {
				receivedParameters.remove(currentParameter);
			}

			// If multiple parameters are received, it is possible to pass this
			// verification without checking all the request parameters.
			if (receivedParameters.size() == 0) {
				return true;
			}
		}

		if (receivedParameters.size() > 0) {
			this.logger.log(HDIVErrorCodes.REQUIRED_PARAMETERS, this.target, receivedParameters.keySet().toString(),
					null);
			return false;
		}

		return true;
	}

	/**
	 * Checks if the parameter <code>parameter</code> is defined by the user
	 * as a no required validation parameter for the action
	 * <code>this.target</code>.
	 * 
	 * @param parameter parameter name
	 * @return True If it is parameter that needs no validation. False
	 *         otherwise.
	 */
	private boolean isUserDefinedNonValidationParameter(String parameter) {

		if (this.hdivConfig.isParameterWithoutValidation(this.targetWithoutContextPath, parameter)) {

			if (log.isDebugEnabled()) {
				log.debug("parameter " + parameter + " doesn't need validation. It is user defined parameter.");
			}
			return true;
		}
		return false;
	}

	/**
	 * Restore state from session or <code>request</code> with
	 * <code>request</code> identifier. Strategy defined by the user
	 * determines the way the state is restored.
	 * 
	 * @param request HTTP request
	 * @return True if restored state is valid. False in otherwise.
	 * @throws HDIVException if there is an error restoring state from request
	 *             or session.
	 */
	private boolean restoreState(HttpServletRequest request) {

		// checks if the parameter HDIV parameter exists in the parameters of
		// the request
		String requestState = request.getParameter(this.hdivParameter);

		if (requestState == null) {
			this.logger.log(HDIVErrorCodes.HDIV_PARAMETER_NOT_EXISTS, this.target, this.hdivParameter, null);
			return false;
		}

		try {
			if (stateUtil.isMemoryStrategy(requestState)) {

				if (!this.validateHDIVSuffix(requestState)) {
					this.logger.log(HDIVErrorCodes.HDIV_PARAMETER_INCORRECT_VALUE, this.target, this.hdivParameter,
							requestState);
					return false;
				}
			}

			this.state = stateUtil.restoreState(requestState);
			this.dataValidator.setState(this.state);
			return true;

		} catch (HDIVException e) {

			String strategy = (String) this.beanFactory.getBean("strategy");
			if (!strategy.equalsIgnoreCase("memory")) {
				requestState = null;
			}

			this.logger.log(e.getMessage(), this.target, this.hdivParameter, requestState);
			return false;
		}
	}

	/**
	 * Checks if the suffix added in the memory version to all requests in the
	 * HDIV parameter is the same as the one stored in session, which is the
	 * original suffix. So any request using the memory version should keep the
	 * suffix unchanged.
	 * 
	 * @param value value received in the HDIV parameter
	 * @return True if the received value of the suffix is valid. False
	 *         otherwise.
	 */
	public boolean validateHDIVSuffix(String value) {

		int firstSeparator = value.indexOf("-");
		int lastSeparator = value.lastIndexOf("-");

		if (firstSeparator >= lastSeparator) {
			return false;
		}

		try {
			// read hdiv's suffix from request
			String requestSuffix = value.substring(lastSeparator + 1);

			// read suffix from page stored in session			
			String pageId = value.substring(0, firstSeparator);
			IPage currentPage = HDIVUtil.getSession().getPage(pageId);

			return currentPage.getRandomToken().equals(requestSuffix);

		} catch (Exception e) {
			//String errorMessage = HDIVUtil.getMessage("validation.error", e.getMessage());
			String errorMessage = "error de prueba";
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Checks if all the received parameter <code>parameter</code> values are
	 * valid, that is, are expected values. Received value number is checked and
	 * then these values are validated.
	 * 
	 * @param stateParameter parameter stored in state
	 * @param parameter Parameter to validate
	 * @param values parameter <code>parameter</code> values
	 * @return True if the validation is correct. False otherwise.
	 * @throws HDIVException if there is an error in parameter validation
	 *             process.
	 */
	private boolean validateParameterValues(IParameter stateParameter, String parameter, String[] values) {

		try {
			// Only for required parameters must be checked if the number of received
			// values is the same as number of values in the state. If this wasn't
			// taken into account, this verification will be done for every parameter, 
			// including for example, a multiple combo where hardly ever are all its 
			// values received.
			if (stateParameter.isActionParam()) {

				if (values.length != stateParameter.getValues().size()) {

					String valueMessage = (values.length > stateParameter.getValues().size()) ? 
										  "extra value" : "more values expected";
					this.logger.log(HDIVErrorCodes.VALUE_LENGTH_INCORRECT, this.target, parameter, valueMessage);
					return false;
				}
			}

			if (this.hasRepeatedOrInvalidValues(parameter, values, stateParameter.getValues())) {
				return false;
			}

			// At this point, we know that the number of received values is the same
			// as the number of values sent to the client. Now we have to check if
			// the received values are all tha ones stored in the state.
			return this.validateReceivedValuesInState(parameter, values);

		} catch (Exception e) {
			String errorMessage = HDIVUtil.getMessage("validation.error", e.getMessage());
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Checks if repeated or no valid values have been received for the
	 * parameter <code>parameter</code>.
	 * 
	 * @param parameter parameter name
	 * @param values Parameter <code>parameter</code> values
	 * @param stateValues values stored in state for <code>parameter</code>
	 * @return True If repeated or no valid values have been received for the
	 *         parameter <code>parameter</code>.
	 */
	private boolean hasRepeatedOrInvalidValues(String parameter, String[] values, List stateValues) {

		List tempStateValues = new ArrayList();
		tempStateValues.addAll(stateValues);

		if (Boolean.TRUE.equals(this.hdivConfig.getConfidentiality())) {
			return this.hasConfidentialIncorrectValues(parameter, values, stateValues.size());
		} else {
			return this.hasNonConfidentialIncorrectValues(parameter, values, tempStateValues);
		}
	}

	/**
	 * Checks if repeated values have been received for the parameter
	 * <code>parameter</code>.
	 * 
	 * @param parameter parameter name
	 * @param values Parameter <code>parameter</code> values
	 * @param size number of values received for <code>parameter</code>
	 * @return True If repeated values have been received for the parameter
	 *         <code>parameter</code>.
	 */
	private boolean hasConfidentialIncorrectValues(String parameter, String[] values, int size) {

		Hashtable receivedValues = new Hashtable();

		for (int i = 0; i < values.length; i++) {

			if (!this.isInRange(parameter, values[i], size)) {
				return true;
			}

			if (receivedValues.containsKey(values[i])) {
				this.logger.log(HDIVErrorCodes.REPEATED_VALUES, this.target, parameter, values[i]);
				return true;
			}

			receivedValues.put(values[i], values[i]);
		}
		return false;
	}

	/**
	 * Checks if repeated or no valid values have been received for the
	 * parameter <code>parameter</code>.
	 * 
	 * @param parameter parameter name
	 * @param values Parameter <code>parameter</code> values
	 * @param tempStateValues values stored in state for <code>parameter</code>
	 * @return True If repeated or no valid values have been received for the
	 *         parameter <code>parameter</code>.
	 */
	private boolean hasNonConfidentialIncorrectValues(String parameter, String[] values, List tempStateValues) {

		Hashtable receivedValues = new Hashtable();

		for (int i = 0; i < values.length; i++) {

			boolean exists = false;
			for (int j = 0; j < tempStateValues.size() && !exists; j++) {

				String tempValue = (String) tempStateValues.get(j);

				if (tempValue.equalsIgnoreCase(values[i])) {
					tempStateValues.remove(j);
					exists = true;
				}
			}

			if (!exists) {

				if (receivedValues.containsKey(values[i])) {
					this.logger.log(HDIVErrorCodes.REPEATED_VALUES, this.target, parameter, values[i]);
					return true;
				}
				this.logger.log(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT, this.target, parameter, values[i]);
				return true;
			}

			receivedValues.put(values[i], values[i]);
		}
		return false;
	}

    /**
     * Checks if the confidential value received in <code>value</code> is a
     * value lower than the number or values received for the parameter
     * <code>parameter</code>. 
     * 
     * @param parameter parameter
     * @param value value
     * @param valuesNumber number of values received for <code>parameter</code>
     * @return True if <code>value</code> is correct. False otherwise. 
     * @since HDIV 2.0
     */
	private boolean isInRange(String parameter, String value, int valuesNumber) {

		Pattern p = Pattern.compile("[0-9]+");
		Matcher m = p.matcher(value);

		if (!m.matches() || (Integer.valueOf(value).intValue() >= valuesNumber)) {
			this.logger.log(HDIVErrorCodes.CONFIDENTIAL_VALUE_INCORRECT, this.target, parameter, value);
			return false;
		}
		return true;
	}

	/**
	 * Checks that values <code>values</code> for the <code>parameter</code>
	 * are valid.
	 * 
	 * @param parameter Parameter to validate
	 * @param values Parameter <code>parameter</code> values.
	 * @return True If the <code>values</code> validation is correct. False
	 *         otherwise.
	 * @throws Exception if there is an internal error.
	 */
	private boolean validateReceivedValuesInState(String parameter, String[] values) throws Exception {

		int size = values.length;
		String[] originalValues = new String[size];

		IValidationResult result = null;
		for (int i = 0; i < size; i++) {

			result = this.dataValidator.validate(values[i], this.targetWithoutContextPath, parameter);

			if (!result.getLegal()) {
				this.logger.log(HDIVErrorCodes.PARAMETER_VALUE_INCORRECT, this.target, parameter, values[i]);
				return false;
			} else {
				originalValues[i] = (String) result.getResult();
			}
		}

		this.requestWrapper.addParameter(parameter, originalValues);
		return true;
	}

	/**
	 * Callback that supplies the owning factory to a bean instance. Invoked
	 * after population of normal bean properties but before an init callback
	 * like InitializingBean's afterPropertiesSet or a custom init-method.
	 * 
	 * @param beanFactory owning BeanFactory (may not be null). The bean can
	 *            immediately call methods on the factory.
	 */
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return Returns the beanFactory.
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @return Returns the data validator.
	 */
	public IDataValidator getDataValidator() {
		return dataValidator;
	}

	/**
	 * @param dataValidator The data validator to set.
	 */
	public void setDataValidator(IDataValidator dataValidator) {
		this.dataValidator = dataValidator;
	}

	/**
	 * @return Returns the request.
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * @param request The request to set.
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * @return Returns the target.
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target The target to set.
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @param targetWithoutContextPath The target without context path to set.
	 */
	public void setTargetWithoutContextPath(String targetWithoutContextPath) {
		this.targetWithoutContextPath = targetWithoutContextPath;
	}

	/**
	 * @return Returns the uwrapper for HTTP servlet request.
	 */
	public RequestWrapper getRequestWrapper() {
		return requestWrapper;
	}

	/**
	 * @return Returns the user logger.
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @param logger The user logger to set.
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @return Returns the utility methods for state.
	 */
	public StateUtil getStateUtil() {
		return stateUtil;
	}

	/**
	 * @param stateUtil The state utility to set.
	 */
	public void setStateUtil(StateUtil stateUtil) {
		this.stateUtil = stateUtil;
	}

	/**
	 * @return Returns the HDIV configuration object.
	 */
	public HDIVConfig getHdivConfig() {
		return hdivConfig;
	}

	/**
	 * @param hdivConfig The HDIV configuration object to set.
	 */
	public void setHdivConfig(HDIVConfig hdivConfig) {
		this.hdivConfig = hdivConfig;
	}

	/**
	 * @param request The request to set. Only for testing
	 */
	public void setRequestWrapper(RequestWrapper requestWrapper) {
		this.requestWrapper = requestWrapper;
	}

	/**
	 * It is called in the pre-processing stage of each user request.
	 */
	public abstract void startPage();

	/**
	 * Handle the storing of HDIV's state, which is done after action
	 * invocation.
	 * 
	 * @param request http request
	 * @throws Exception if there is an error in storing process.
	 */
	public abstract void endPage();
	
}