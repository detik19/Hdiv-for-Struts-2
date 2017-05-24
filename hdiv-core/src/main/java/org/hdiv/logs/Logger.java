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
package org.hdiv.logs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Log that shows the attacks detected by HDIV. It includes type of attack and the
 * identity (application user) of the user. Since the way to obtain this user
 * may vary from application to application, an standard interface has been defined
 * to be implemented by each application.
 * <p>
 * Log format = type;target;parameter;value;userLocalIP;ip;userId
 * </p>
 * 
 * @author Roberto Velasco
 * @see org.hdiv.logs.IUserData
 */
public class Logger {

	/**
	 * Commons Logging instance.
	 */
	private static Log log = LogFactory.getLog(Logger.class);

	IUserData userData;

	HttpServletRequest request;

	HttpSession session;


	/**
	 * Logger is initialized with http request and user web session.
	 * 
	 * @param request HTTP request
	 * @param session HTTP session
	 */
	public void init(HttpServletRequest request, HttpSession session) {
		this.request = request;
		this.session = session;
	}

	/**
	 * Imprime el ataque formateado producido por el usuario siempre y cuando el
	 * nivel de logueo definido en la aplicación web sea como mínimo de tipo INFO.
	 * 
	 * @param type Error type
	 * @param target target name
	 * @param parameter parameter name
	 * @param value parameter value
	 */
	public void log(String type, String target, String parameter, String value) {
		
		String formatedData = this.format(type, target, parameter, value);		
		log.info(formatedData);
	}

	/**
	 * <p>
	 * String formateado con la información del ataque producido por el usuario. El formato del log es el siguiente:
	 * </p>
	 * <p>
	 * <code>[error type];[target];[parameter];[value];[user local IP adress];[IP adress of the client or the last proxy that sent the request];[userId]</code>
	 * </p>
	 * 
	 * @param type Error type
	 * @param target target name
	 * @param parameter parameter name
	 * @param value parameter value
	 * @return String formateado con la información del ataque producido por el usuario.
	 */
	public String format(String type, String target, String parameter, String value) {

		StringBuffer buffer = new StringBuffer();	
		buffer.append(type);
		buffer.append(";");
		buffer.append(target);
		buffer.append(";");
		buffer.append(parameter);
		buffer.append(";");
		buffer.append(value);
		buffer.append(";");
		buffer.append(this.getUserLocalIP());
		buffer.append(";");
		buffer.append(request.getRemoteAddr());
		buffer.append(";");
		buffer.append(this.userData.getUsername(this.request));
		
		return buffer.toString();
	}
	
	/**
	 * @return Returns the remote user IP address if behind the proxy.
	 */
	private String getUserLocalIP() {

		String ipAddress = null;

		if (request.getHeader("X-Forwarded-For") == null) {
			ipAddress = request.getRemoteAddr();
		} else {
			ipAddress = request.getHeader("X-Forwarded-For");
		}
		return ipAddress;
	}

	public IUserData getUserData() {
		return userData;
	}

	public void setUserData(IUserData userData) {
		this.userData = userData;
	}
}