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
package org.hdiv.config.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.filter.RequestWrapper;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Class containing multipart request configuration and methods initialized from
 * Spring Factory.
 * 
 * @author Gorka Vicente
 */
public class Struts2MultipartConfig extends AbstractMultipartConfig {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(Struts2MultipartConfig.class);


	/**
	 * Parses the input stream and partitions the parsed items into a set of form
	 * fields and a set of file items.
	 * 
	 * @param request The multipart request wrapper.
	 * @param servletContext Our ServletContext object
	 * @throws FileUploadException if an unrecoverable error occurs.
	 * @throws DiskFileUpload.SizeLimitExceededException if size limit exceeded
	 */
	public void handleMultipartRequest(RequestWrapper request, ServletContext servletContext)
			throws FileUploadException, DiskFileUpload.SizeLimitExceededException, MaxUploadSizeExceededException {

		DiskFileItemFactory fac = new DiskFileItemFactory();
		// Make sure that the data is written to file
		fac.setSizeThreshold(0);

		String repositoryPath = this.getRepositoryPath(servletContext);
		if (repositoryPath != null) {
			fac.setRepository(new File(repositoryPath));
		}

		// Parse the request
		try {			
			ServletFileUpload upload = new ServletFileUpload(fac);
			upload.setSizeMax(this.getSizeMax());
			List items = upload.parseRequest(createRequestContext(request));
			
			for (int i = 0; i < items.size(); i++) {
				
				FileItem item = (FileItem) items.get(i);
							
				if (log.isDebugEnabled())
					log.debug("Found item " + item.getFieldName());
				if (item.isFormField()) {
					if (log.isDebugEnabled())
						log.debug("Item is a normal form field");
					this.addTextParameter(request, item);
				} else {
					if (log.isDebugEnabled())
						log.debug("Item is a file upload");
					
                    // Skip file uploads that don't have a file name - meaning that no file was selected.
                    if (item.getName() == null || item.getName().trim().length() < 1) {
                        log.debug("No file has been uploaded for the field: " + item.getFieldName());
                        continue;
                    }					
					this.addFileParameter(request, item);
				}
			}
		} catch (FileUploadException e) {			
			log.error(e);
			throw e;
		}
	}

	/**
	 * Adds a regular text parameter to the set of text parameters for this request.
	 * Handles the case of multiple values for the same parameter by using an array
	 * for the parameter value.
	 * 
	 * @param request The request in which the parameter was specified.
	 * @param item The file item for the parameter to add.
	 */
	public void addTextParameter(RequestWrapper request, FileItem item) {

		String name = item.getFieldName();
		String value = null;
		boolean haveValue = false;
		String encoding = request.getCharacterEncoding();

		if (encoding != null) {
			try {
				value = item.getString(encoding);
				haveValue = true;
			} catch (Exception e) {
				// Handled below, since haveValue is false.
			}
		}
		if (!haveValue) {
			value = item.getString();
		}

		String[] oldArray = (String[]) request.getParameterValues(name);
		String[] newArray;

		if (oldArray != null) {
			newArray = new String[oldArray.length + 1];
			System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
			newArray[oldArray.length] = value;
		} else {
			newArray = new String[] { value };
		}

		request.addParameter(name, newArray);
	}

	/**
	 * Creates a RequestContext needed by Jakarta Commons Upload.
	 * 
	 * @param req the request.
	 * @return a new request context.
	 */
	private RequestContext createRequestContext(final HttpServletRequest req) {

		return new RequestContext() {

			public String getCharacterEncoding() {
				return req.getCharacterEncoding();
			}

			public String getContentType() {
				return req.getContentType();
			}

			public int getContentLength() {
				return req.getContentLength();
			}

			public InputStream getInputStream() throws IOException {
				return req.getInputStream();
			}
		};
	}
}
