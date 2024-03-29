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
package org.hdiv.session;

import javax.servlet.http.HttpSession;

import org.hdiv.cipher.ICipherHTTP;
import org.hdiv.exception.HDIVException;
import org.hdiv.state.IPage;
import org.hdiv.state.IState;

/**
 * A custom wrapper for http session request that returns a wrapped http
 * session.
 * 
 * @author Roberto Velasco
 */
public interface ISession {

	/**
	 * It adds a new page to the user session. To do this it adds a new page
	 * identifier to the cache and if it has reached the maximun size allowed,
	 * the oldest page is deleted from the session and from the cache itself.
	 * 
	 * @param pageId Page identifier
	 * @param page Page with all the information about states
	 */
	public void addPage(String pageId, IPage page);

	/**
	 * Deletes from session the data related to the finished flows. This means a
	 * memory consumption optimization because useless objects of type
	 * <code>IPage</code> are deleted.
	 * 
	 * @param conversationId finished flow identifier
	 * @since HDIV 2.0.3
	 */
	public void removeEndedPages(String conversationId);

	/**
	 * Obtains the state identifier <code>stateId</code> related to the page
	 * identifier <code>pageId</code>.
	 * 
	 * @return State identifier <code>stateId</code> throws HDIVException If
	 *         the state doesn't exist a new HDIV exception is thrown.
	 */
	public IState getState(String pageId, String stateId);

	/**
	 * Obtains the hash of the state identifier <code>stateId</code> related
	 * to page identifier <code>pageId</code>.
	 * 
	 * @return Hash of the state identifier <code>stateId</code>
	 * @throws HDIVException If the state doesn't exist a new HDIV exception is
	 *             thrown.
	 */
	public String getStateHash(String pageId, String stateId);

	/**
	 * Obtains from the user session the page identifier where the current
	 * request or form is
	 * 
	 * @return Returns the pageId.
	 */
	public String getPageId();
	
	/**
	 * Returns the page with id <code>pageId</code>.
	 * @param pageId page id
	 * @return Returns the page with id <code>pageId</code>.
	 * @since HDIV 2.0.4
	 */
	public IPage getPage(String pageId);	

	/**
	 * @return Returns the HTTP session.
	 */
	public HttpSession getWebSession();

	/**
	 * Initializes the data cipher.
	 * 
	 * @return Returns the data cipher initialized with the symmetric key
	 * @throws HDIVException If the state doesn't exist a new HDIV exception is
	 *             thrown.
	 */
	public ICipherHTTP getEncryptCipher();

	/**
	 * Inilitializes the data decrypter.
	 * 
	 * @return Returns the data decrypter initialized with the symmetric key.
	 * @throws HDIVException if there is an error in cipher initialization.
	 */
	public ICipherHTTP getDecryptCipher();

}