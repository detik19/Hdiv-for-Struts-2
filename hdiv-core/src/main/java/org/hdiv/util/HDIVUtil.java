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
package org.hdiv.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.application.IApplication;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.dataValidator.IDataValidator;
import org.hdiv.exception.HDIVException;
import org.hdiv.session.ISession;
import org.hdiv.util.RandomGuidUidGenerator;
import org.hdiv.util.UidGenerator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Class containing utility methods for access HDIV components: IDataComposer,
 * IDataValidator, IApplication, ISession.
 * <p>
 * This class is initialized from a Listener and a Filter.
 * </p>
 * 
 * @author Roberto Velasco
 * @author Gorka Vicente
 */
public class HDIVUtil {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(HDIVUtil.class);

	/**
	 * The ServletContext that the request runs in
	 */
	private static ServletContext servletContext;

	/**
	 * Servlet context wrapper
	 */
	private static IApplication application = null;

	/**
	 * HTTP session thread local
	 */
	private static ThreadLocal httpSession = new ThreadLocal();

	/**
	 * HTTP session wrapper (ISession) thread local
	 */
	private static ThreadLocal session = new ThreadLocal();

	/**
	 * The root interface for accessing a Spring bean container.
	 * 
	 * @see org.springframework.beans.factory.BeanFactory
	 */
	private static BeanFactory wac;

	/**
	 * Each request has a single request Data Validator
	 */
	private static ThreadLocal dataValidator = new ThreadLocal();

	/**
	 * Each request has a single request Data Composer
	 */
	private static ThreadLocal dataComposer = new ThreadLocal();

	/**
	 * User defined data composer name calculated from strategy.
	 */
	private static String dataComposerName;
	
	/**
	 * Generates random tokens to avoid Cross Site Request Forgery (CSRF) attacks.
	 */
	private static UidGenerator randomTokenGenerator = new RandomGuidUidGenerator();

	/**
	 * ThreadLocales is always guaranteed to be cleaned up when returning the
	 * thread to the server's pool.
	 */
	public static void resetLocalData() {

		dataComposer.set(null);
		dataValidator.set(null);
		httpSession.set(null);
		session.set(null);
	}

	/**
	 * Returns data composer object from <code>request</code>. If it is null,
	 * it returns data composer from thread local.
	 * 
	 * @param request HTTP servlet request
	 * @return Returns data composer object
	 * @since HDIV 2.0.3
	 */
	public static IDataComposer getDataComposer(HttpServletRequest request) {

		IDataComposer requestDataComposer = (IDataComposer) request.getAttribute("dataComposer");
		return (requestDataComposer != null) ? requestDataComposer : HDIVUtil.getDataComposer();
	}

	/**
	 * @return Returns data composer object
	 */
	public static IDataComposer getDataComposer() {

		if (dataComposer.get() == null) {

			// we create a data composer for the request
			IDataComposer newDataComposer = (IDataComposer) wac.getBean(dataComposerName);

			dataComposer.set(newDataComposer);
			return newDataComposer;
		} else {
			return (IDataComposer) dataComposer.get();
		}
	}

	/**
	 * @return Returns data validator object
	 */
	public static IDataValidator getDataValidator() {

		if (dataValidator.get() == null) {
			IDataValidator newDataValidator = (IDataValidator) wac.getBean("dataValidator");
			dataValidator.set(newDataValidator);
			return newDataValidator;
		} else {
			return (IDataValidator) dataValidator.get();
		}
	}

	/**
	 * @return Returns http session wrapper object
	 */
	public static ISession getSession() {

		if (session.get() == null) {
			ISession newSession = (ISession) wac.getBean("sessionHDIV");
			session.set(newSession);
			return newSession;
		} else {
			return (ISession) session.get();
		}
	}

	/**
	 * Only for testing.
	 * 
	 * @param sessionHDIV The session to set.
	 */
	public static void setSession(ISession sessionHDIV) {
		session.set(sessionHDIV);
	}

	/**
	 * @return Returns the servlet context that the request runs in.
	 */
	public static ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * @param servletContext the ServletContext that the request runs in.
	 */
	public static void setServletContext(ServletContext servletContext) {

		HDIVUtil.servletContext = servletContext;
	}

	/**
	 * Find the root WebApplicationContext for this web app, which is typically
	 * loaded via ContextLoaderListener or ContextLoaderServlet.
	 * 
	 * @see org.springframework.web.context.support.WebApplicationContextUtils#getRequiredWebApplicationContext(javax.servlet.ServletContext)
	 */
	public static void initFactory() {

		wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

		String strategy = (String) wac.getBean("strategy");

		if (strategy.equalsIgnoreCase("memory")) {
			dataComposerName = "dataComposerMemory";
		} else if (strategy.equalsIgnoreCase("cipher")) {
			dataComposerName = "dataComposerCipher";
		} else if (strategy.equalsIgnoreCase("hash")) {
			dataComposerName = "dataComposerHash";
		} else {
			String errorMessage = HDIVUtil.getMessage("strategy.error", strategy);
			throw new HDIVException(errorMessage);
		}
	}

	/**
	 * @return Returns the servlet context wrapper object.
	 */
	public static IApplication getApplication() {

		if (application == null) {
			wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			application = (IApplication) wac.getBean("application");
			return application;
		} else {
			return application;
		}
	}

	public static HttpSession getHttpSession() {
		return (HttpSession) httpSession.get();
	}

	public static void setHttpSession(HttpSession httpSession2) {
		httpSession.set(httpSession2);
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be
	 * found.
	 * 
	 * @param key the code to lookup up, such as 'calculator.noRateSet'
	 * @return The resolved message
	 */
	public static String getMessage(String key) {
		return HDIVUtil.getMessage(key, null);
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be
	 * found.
	 * 
	 * @param key the code to lookup up, such as 'calculator.noRateSet'
	 * @param o Array of arguments that will be filled in for params within the
	 *            message (params look like "{0}", "{1,date}", "{2,time}" within
	 *            a message), or null if none.
	 * @return The resolved message
	 */
	public static String getMessage(String key, String o) {
		return HDIVUtil.getMessage(key, o, Locale.getDefault());
	}

	/**
	 * Try to resolve the message. Treat as an error if the message can't be
	 * found.
	 * 
	 * @param key the code to lookup up, such as 'calculator.noRateSet'
	 * @param o Array of arguments that will be filled in for params within the
	 *            message (params look like "{0}", "{1,date}", "{2,time}" within
	 *            a message), or null if none.
	 * @param userLocale locale
	 * @return The resolved message
	 */
	public static String getMessage(String key, String o, Locale userLocale) {

		WebApplicationContext webac = (WebApplicationContext) wac;

		String resolvedMessage = webac.getMessage(key, new String[] { o }, userLocale);
		if (log.isDebugEnabled()) {
			log.debug(resolvedMessage);
		}
		return resolvedMessage;
	}

	/**
	 * Return the form action converted into an action mapping path. The value
	 * of the <code>action</code> property is manipulated as follows in
	 * computing the name of the requested mapping:
	 * <ul>
	 * <li>Any filename extension is removed (on the theory that extension
	 * mapping is being used to select the controller servlet).</li>
	 * <li>If the resulting value does not start with a slash, then a slash is
	 * prepended.</li>
	 * </ul>
	 * 
	 * @param url URL representing the current request
	 * @return the form action converted into an action mapping path.
	 */
	public static String getActionMappingName(String url) {

		String value = url;
		int question = url.indexOf("?");
		if (question >= 0) {
			value = value.substring(0, question);
		}

		int pound = value.indexOf("#");
		if (pound >= 0) {
			value = value.substring(0, pound);
		}

		// strip a servlet session ID from
		value = stripSession(value);

		int slash = value.lastIndexOf("/");
		int period = value.lastIndexOf(".");

		// struts-examples/dir/action.do
		if ((period >= 0) && (period > slash)) {
			value = value.substring(0, value.length());
		}

		return value.startsWith("/") ? value : ("/" + value);
	}

	/**
	 * Strips a servlet session ID from <tt>url</tt>. The session ID is
	 * encoded as a URL "path parameter" beginning with "jsessionid=". We thus
	 * remove anything we find between ";jsessionid=" (inclusive) and either EOS
	 * or a subsequent ';' (exclusive).
	 */
	public static String stripSession(String url) {

		StringBuffer u = new StringBuffer(url);
		int sessionStart;

		while ((sessionStart = u.toString().indexOf(";jsessionid=")) != -1) {

			int sessionEnd = u.toString().indexOf(";", sessionStart + 1);
			if (sessionEnd == -1) {
				sessionEnd = u.toString().indexOf("?", sessionStart + 1);
			}
			if (sessionEnd == -1) { // still
				sessionEnd = u.length();
			}
			u.delete(sessionStart, sessionEnd);
		}
		return u.toString();
	}

	/**
	 * Return the URL representing the current request.
	 * 
	 * @param request The servlet request we are processing
	 * @return URL representing the current request
	 * @exception Exception if a URL cannot be created
	 */
	public static String actionName(HttpServletRequest request) throws Exception {

		return requestURL(request).getFile();
	}

	/**
	 * Return the URL representing the current request. This is equivalent to
	 * <code>HttpServletRequest.getRequestURL()</code> in Servlet 2.3.
	 * 
	 * @param request The servlet request we are processing
	 * @return URL representing the current request
	 * @exception MalformedURLException if a URL cannot be created
	 */
	public static URL requestURL(HttpServletRequest request) throws MalformedURLException {

		StringBuffer url = new StringBuffer();
		String scheme = request.getScheme();
		int port = request.getServerPort();
		if (port < 0) {
			port = 80; // Work around java.net.URL bug
		}
		url.append(scheme);
		url.append("://");
		url.append(request.getServerName());
		if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
			url.append(':');
			url.append(port);
		}
		url.append(request.getRequestURI());
		return (new URL(url.toString()));
	}

	/**
	 * Function to protect meaningful characters of regular expressions
	 * (+,*,...)
	 * 
	 * @param par Parameter to encode
	 * 
	 * @return Returns par with protected characters
	 */
	public static String protectCharacters(String par) {

		par = par.replaceAll("\\+", "\\\\+");
		par = par.replaceAll("\\*", "\\\\*");
		par = par.replaceAll("\\?", "\\\\?");
		par = par.replaceAll("\\$", "\\\\\\$");
		par = par.replaceAll("\\^", "\\\\^");
		par = par.replaceAll("\\[", "\\\\[");
		par = par.replaceAll("\\(", "\\\\(");
		par = par.replaceAll("\\)", "\\\\)");
		par = par.replaceAll("\\|", "\\\\|");
		return par;
	}

	/**
	 * <p>
	 * It generates a new encoded values for the <code>url</code> parameters.
	 * </p>
	 * <p>
	 * The returned values guarantees the confidentiality in the encoded and
	 * memory strategies if confidentiality indicator defined by user is true.
	 * </p>
	 * 
	 * @param url request url
	 * @param questionIndex index of the first question occurrence in
	 *            <code>url</code> string
	 * @param charEncoding character encoding
	 * @return url with encoded values
	 */
	public static String composeAction(String url, int questionIndex, String charEncoding) {

		IDataComposer dataComposer = HDIVUtil.getDataComposer();
		String value = url;

		value = value.substring(questionIndex + 1);
		value = value.replaceAll("&amp;", "&");

		String token = null;
		String urlAction = HDIVUtil.getActionMappingName(url);

		StringTokenizer st = new StringTokenizer(value, "&");
		while (st.hasMoreTokens()) {
			token = st.nextToken();
			String param = token.substring(0, token.indexOf("="));
			String val = token.substring(token.indexOf("=") + 1);

			String encodedValue = dataComposer.compose(urlAction, param, val, false, true, charEncoding);
			value = value.replaceFirst(HDIVUtil.protectCharacters(token), param + "=" + encodedValue);
		}

		return url.substring(0, questionIndex + 1) + value;
	}

	/**
	 * Generates a random number between 0 (inclusive) and n (exclusive).
	 * 
	 * @param n the bound on the random number to be returned. Must be positive.
	 * @return Returns a pseudorandom, uniformly distributed int value between 0
	 *         (inclusive) and <code>n</code> (exclusive).
	 * @since HDIV 1.1
	 */
	public static String createRandomToken(int n) {

		Random r = new Random();
		int i = r.nextInt(n);
		if (i == 0) {
			i = 1;
		}

		return String.valueOf(i);
	}

	/**
	 * @return Returns the dataComposerName.
	 * @since HDIV 2.0
	 */
	public static String getDataComposerName() {
		return dataComposerName;
	}

	/**
	 * @return Returns random token generator.
	 * @since HDIV 2.0.4
	 */
	public static UidGenerator getRandomTokenGenerator() {
		return randomTokenGenerator;
	}	
	
}