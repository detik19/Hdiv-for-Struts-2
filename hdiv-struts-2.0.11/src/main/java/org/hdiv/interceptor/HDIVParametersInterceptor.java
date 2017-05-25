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
package org.hdiv.interceptor;

import org.hdiv.util.HDIVUtil;

import com.opensymphony.xwork2.interceptor.ParametersInterceptor;


/**
 * <!-- START SNIPPET: description -->
 * This interceptor sets all parameters on the value stack.
 * <p/>
 * 
 * @see com.opensymphony.xwork2.interceptor.ParametersInterceptor
 * @author Gorka Vicente
 * @since HDIV 2.0.4
 */
public class HDIVParametersInterceptor extends ParametersInterceptor {

    
    protected boolean isExcluded(String paramName) {
    	
    	if (super.isExcluded(paramName)) {
    		return true;    		    		
    	}
    	
    	return paramName.equals(this.getHDIVParameter());
    } 

	/**
	 * @return Returns the HDIV state parameter.
	 */
	public String getHDIVParameter() {
		
		try {
			return (String) HDIVUtil.getHttpSession().getAttribute("HDIVParameter");			
		} catch (Exception e) {
			return null;
		}
	}    

}
