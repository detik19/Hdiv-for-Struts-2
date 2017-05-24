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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.config.HDIVConfig;
import org.hdiv.config.multipart.IMultipartConfig;
import org.hdiv.util.Constants;
import org.hdiv.util.HDIVUtil;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * An unique filter exists within HDIV. This filter has two responsabilities:
 * initialize and validate. In fact, the actual validation is not implemented in
 * this class, it is delegated to ValidatorHelper.
 * 
 * @author Roberto Velasco
 * @author Gorka Vicente
 * @see org.hdiv.filter.ValidatorHelperThreadLocal
 */
public class ValidatorFilter extends OncePerRequestFilter {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(ValidatorFilter.class);

	/**
	 * HDIV configuration object
	 */
	private HDIVConfig hdivConfig;

	/**
	 * Creates a new ValidatorFilter object.
	 */
	public ValidatorFilter() {

	}

	/**
	 * Called by the web container to indicate to a filter that it is being
	 * placed into service.
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	protected void initFilterBean() throws ServletException {

		HDIVUtil.initFactory();
		this.hdivConfig = (HDIVConfig) HDIVUtil.getApplication().getBean("config");
	}

	/**
	 * Called by the container each time a request/response pair is passed
	 * through the chain due to a client request for a resource at the end of
	 * the chain.
	 * 
	 * @param servletRequest request
	 * @param servletResponse response
	 * @param filterChain filter chain
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		this.initHDIV(request);		
		ResponseWrapper responseWrapper = new ResponseWrapper(response);
		try {			
			IValidationHelper validationHelper = (IValidationHelper) HDIVUtil.getApplication().getBean("validatorHelper");
			validationHelper.setHdivConfig(hdivConfig);
			validationHelper.init(request);

			boolean legal = false;
			boolean isSizeLimitExceeded = false;

			if (this.isMultipartContent(request.getContentType())) {

				validationHelper.getRequestWrapper().setMultipart(true);
				try {
					IMultipartConfig multipartConfig = (IMultipartConfig) HDIVUtil.getApplication().getBean("multipartConfig");
					multipartConfig.handleMultipartRequest(validationHelper.getRequestWrapper(), super
							.getServletContext());

				} catch (DiskFileUpload.SizeLimitExceededException e) {
					validationHelper.getRequestWrapper().setAttribute(IMultipartConfig.FILEUPLOAD_EXCEPTION, e);
					isSizeLimitExceeded = true;
					legal = true;
				} catch (MaxUploadSizeExceededException e) {
					isSizeLimitExceeded = true;
					legal = true;
					validationHelper.getRequestWrapper().setAttribute(IMultipartConfig.FILEUPLOAD_EXCEPTION, e);
				} catch (FileUploadException e) {
					isSizeLimitExceeded = true;
					legal = true;
					if (e.getCause() == null)
						e = new FileUploadException(e.getMessage());
					validationHelper.getRequestWrapper().setAttribute(IMultipartConfig.FILEUPLOAD_EXCEPTION, e);
				}
			}

			if (!isSizeLimitExceeded) {
				legal = validationHelper.validate();
			}

			if (legal) {
				processRequest(validationHelper, responseWrapper, filterChain);
			} else {
				response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + hdivConfig.getErrorPage()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + hdivConfig.getErrorPage()));
		} finally {
			HDIVUtil.resetLocalData();
		}
	}
	
	/**
	 * Initialize HDIV HTTP session
	 * 
	 * @param request HTTP request
	 */
	public void initHDIV(HttpServletRequest request) {		
		HDIVUtil.setHttpSession(request.getSession());
	}

	/**
	 * Utility method that determines whether the request contains multipart
	 * content.
	 * 
	 * @param contentType content type
	 * @return <code>true</code> if the request is multipart.
	 *         <code>false</code> otherwise.
	 */
	public boolean isMultipartContent(String contentType) {
		return ((contentType != null) && (contentType.indexOf("multipart/form-data") != -1));
	}

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 * 
	 * @param validationHelper validation helper
	 * @param responseWrapper response wrapper
	 * @param filterChain filter chain
	 * @throws Exception if there is an error in request process.
	 */
	protected void processRequest(IValidationHelper validationHelper, ResponseWrapper responseWrapper,
			FilterChain filterChain) throws Exception {

		validationHelper.startPage();
		filterChain.doFilter(validationHelper.getRequestWrapper(), responseWrapper);
		validationHelper.endPage();
	}

}
