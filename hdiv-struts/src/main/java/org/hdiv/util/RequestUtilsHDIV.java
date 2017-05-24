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

import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.dispatcher.mapper.HDIVActionMapper;
import org.hdiv.util.HDIVUtil;

/**
 * General purpose utility methods related to processing a servlet request.
 * 
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
public class RequestUtilsHDIV {
	
	/**
	 * Adds HDIV state as a parameter if <code>url</code> references our application.
	 * 
	 * @param request HTTP request
	 * @param url URL
	 * @param prependServletContext <tt>true</tt> to prepend the location with the
	 *            servlet context path, <tt>false</tt> otherwise
	 * @return <code>url</code> with the HDIV state added as a new parameter 
	 */
	public static String addHDIVParameterIfNecessary(HttpServletRequest request, String url,
			boolean prependServletContext) {

		if (RequestUtilsHDIV.isPathUrl(url)) {

			url = RequestUtilsHDIV.getContextRelativePath(request, url);

			if (prependServletContext && url.indexOf(request.getContextPath()) == -1) {
				url = request.getContextPath() + url;
			}
			url = RequestUtilsHDIV.composeURL(request, url);

		} else { // URL is absolute

			if (url.indexOf(request.getContextPath()) != -1) {

				url = url.substring(url.indexOf(request.getContextPath()));
				url = RequestUtilsHDIV.composeURL(request, url);
			}
		}
		return url;
	}

	/**
	 * Adds HDIV state as a parameter if <code>url</code> references our application.
	 * 
	 * @param request HTTP request
	 * @param url URL
	 * @return <code>url</code> with the HDIV state added as a new parameter
	 */
	public static String addHDIVParameterIfNecessary(HttpServletRequest request, String url) {

		return RequestUtilsHDIV.addHDIVParameterIfNecessary(request, url, true);
	}
	
	/**
	 * Checks if <code>url</code> contains any scheme.
	 * 
	 * @param url URL
	 * @return if the url <code>url</code> contains a scheme. False otherwise.
	 */
	private static boolean isPathUrl(String url) {
		return (url.indexOf(':') == -1);
	}

	/**
	 * It creates a new state to store all the parameters and values of the
	 * <code>request</code> and it generates a new encoded values for the
	 * <code>request</code> parameters and adds the HDIV parameter.
	 * 
	 * @param request HTTP request
	 * @param finalLocation the location to redirect to
	 * @return URL with encoded parameters and HDIV parameter
	 */
	public static String composeURL(HttpServletRequest request, String finalLocation) {

		String encodedURL = finalLocation;

		IDataComposer dataComposer = (IDataComposer) request.getAttribute("dataComposer");
		dataComposer.beginRequest(HDIVUtil.getActionMappingName(finalLocation));

		int question = finalLocation.indexOf("?");
		if (question > 0) {

			// generate a new encoded values for the url parameters
			encodedURL = RequestUtilsHDIV.composeAction(finalLocation, question, "UTF-8", dataComposer);
		}

		return RequestUtilsHDIV.addHDIVState(dataComposer, encodedURL, null);
	}
	
	/**
	 * <p>
	 * It generates a new encoded values for the <code>url</code> parameters.
	 * </p>
	 * <p>
	 * The returned values guarantees the confidentiality in the encoded and memory
	 * strategies if confidentiality indicator defined by user is true.
	 * </p>
	 * 
	 * @param url request url
	 * @param questionIndex index of the first question occurrence in <code>url</code> string
	 * @param charEncoding character encoding
	 * @return url with encoded values
	 */
	public static String composeAction(String url, int questionIndex, String charEncoding,
			IDataComposer dataComposer) {

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
	 * Adds the HDIV parameter, depending on the strategu defined by the user, to
	 * validate the request <code>encodedURL</code>.
	 * 
	 * @param dataComposer HDIV's data composer
	 * @param encodedURL URL encoded
	 * @param anchor anchor
	 * @return <code>url</code> with the HDIV state added as a new parameter
	 * @see org.hdiv.composer.IDataComposer
	 */
	public static String addHDIVState(IDataComposer dataComposer, String encodedURL, String anchor) {

		String hdivParameter = (String) HDIVUtil.getHttpSession().getAttribute("HDIVParameter");

		String separator = "";
		String requestId = dataComposer.endRequest();

		if ((requestId.length() <= 0) || (encodedURL.startsWith("javascript:"))) {
			return encodedURL;
		}

		// we ask if the link has an anchor. If so, we must remove it to be added
		// later on, once the hdivParameter has been added. this way we get a link
		// like this: ../action.do?parameters&hdivParameter=2-5#anchor
		boolean isAnchor = (anchor != null) && (!anchor.equals(""));
		if (isAnchor) {
			encodedURL = encodedURL.replaceFirst("#" + anchor, "");
		}

		// we check if the url contains parameters
		separator = (encodedURL.indexOf("?") > 0) ? "&" : "?";

		hdivParameter = separator + hdivParameter + "=" + requestId;

		return encodedURL + hdivParameter + ((isAnchor) ? "#" + anchor : "");
	}

	/**
	 * Checks if <code>path</code> has an action extension or a jsp page extension.
	 * 
	 * @param path path
	 * @param actionMapper action mapper
	 * @return True if <code>path</code> is an action or references a jsp page.
	 */
	public static boolean hasActionOrServletExtension(String path, ActionMapper actionMapper) {
		
		if (path.indexOf("?") > 0) {
			path = path.substring(0, path.indexOf("?"));
		}

		if (path.charAt(path.length() - 1) == '/') {
			return true;
		}

        int pound = path.indexOf("#");
        if (pound >= 0) {
            path = path.substring(0, pound);
        }  
        
        // strip a servlet session ID from
        path = HDIVUtil.stripSession(path);
		
		if (path.endsWith(".jsp")) {
			return true;
		}

		List extensions = null;
		if (actionMapper instanceof HDIVActionMapper) {
			HDIVActionMapper hdivActionMapper = (HDIVActionMapper) actionMapper;
			extensions = hdivActionMapper.getExtensions();
		}

		if (extensions != null) {
			Iterator it = extensions.iterator();
			while (it.hasNext()) {
				String extension = "." + (String) it.next();
				if (path.endsWith(extension)) {
					return true;
				}
			}
		}
		return (!path.startsWith("/")) && (path.indexOf(".") == -1);
	}

	public static boolean isContextRelativePath(ServletRequest request, String path) {

		String resourcePath = getContextRelativePath(request, path);
		RequestDispatcher rd = request.getRequestDispatcher(resourcePath);

		return (rd != null);
	}

	public static String getContextRelativePath(ServletRequest request, String relativePath) {

		String returnValue;

		if (relativePath.startsWith("/")) {
			returnValue = relativePath;
		} else if (!(request instanceof HttpServletRequest)) {
			returnValue = relativePath;
		} else {
			HttpServletRequest hrequest = (HttpServletRequest) request;
			String uri = (String) request.getAttribute("javax.servlet.include.servlet_path");

			if (uri == null) {
				uri = getServletPath(hrequest);
			}

			returnValue = uri.substring(0, uri.lastIndexOf('/')) + '/' + relativePath;
		}

		// .. is illegal in an absolute path according to the Servlet Spec and will
		// cause known problems on Orion application servers.
		if (returnValue.indexOf("..") != -1) {
			
			Stack stack = new Stack();
			StringTokenizer pathParts = new StringTokenizer(returnValue.replace('\\', '/'), "/");

			while (pathParts.hasMoreTokens()) {
				String part = pathParts.nextToken();

				if (!part.equals(".")) {
					if (part.equals("..")) {
						stack.pop();
					} else {
						stack.push(part);
					}
				}
			}

			StringBuffer flatPathBuffer = new StringBuffer();

			for (int i = 0; i < stack.size(); i++) {
				flatPathBuffer.append("/").append(stack.elementAt(i));
			}

			returnValue = flatPathBuffer.toString();
		}

		return returnValue;
	}

	/**
	 * Retrieves the current request servlet path. Deals with differences between
	 * servlet specs (2.2 vs 2.3+)
	 * 
	 * @param request the request
	 * @return the servlet path
	 */
	public static String getServletPath(HttpServletRequest request) {

		String servletPath = request.getServletPath();

		if (null != servletPath && !"".equals(servletPath)) {
			return servletPath;
		}

		String requestUri = request.getRequestURI();
		int startIndex = request.getContextPath().equals("") ? 0 : request.getContextPath()
				.length();
		int endIndex = request.getPathInfo() == null ? requestUri.length() : requestUri
				.lastIndexOf(request.getPathInfo());

		if (startIndex > endIndex) { // this should not happen
			endIndex = startIndex;
		}

		return requestUri.substring(startIndex, endIndex);
	}

}
