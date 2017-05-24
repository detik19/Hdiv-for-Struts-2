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

import javax.servlet.http.HttpServletRequest;

import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.exception.HDIVException;
import org.hdiv.util.HDIVUtil;

/**
 * It validates client requests by comsuming an object of type IState and
 * validating all the entry data, besides replacing relative values by its real
 * values.
 * 
 * @author Roberto Velasco
 * @author Gorka Vicente
 * @since HDIV 2.0
 */
public class ValidatorHelperRequest extends AbstractValidatorHelper {

	/**
	 * Data composer
	 */
	private IDataComposer dataComposer;

	/**
	 * Initialization of the objects needed for the validation process.
	 * 
	 * @param request HTTP servlet request
	 * @throws HDIVException if there is an initialization error.
	 */
	public void init(HttpServletRequest request) {

		try {
			super.init(request);
			this.dataComposer = (IDataComposer) super.getBeanFactory().getBean(HDIVUtil.getDataComposerName());
			request.setAttribute("dataComposer", this.dataComposer);

		} catch (Exception e) {
			String errorMessage = HDIVUtil.getMessage("helper.init");
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * It is called in the pre-processing stage of each user request.
	 */
	public void startPage() {
		this.dataComposer.startPage();
	}

	/**
	 * Handle the storing of HDIV's state, which is done after action
	 * invocation.
	 * 
	 * @param request http request
	 * @throws Exception if there is an error in storing process.
	 */
	public void endPage() {

		if (super.getRequestWrapper().getSession(false) != null) {
			this.dataComposer.endPage();
		}
	}

}