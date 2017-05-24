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

import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hdiv.cipher.ICipherHTTP;
import org.hdiv.cipher.Key;
import org.hdiv.exception.HDIVException;
import org.hdiv.state.IPage;
import org.hdiv.state.IState;
import org.hdiv.util.HDIVErrorCodes;
import org.hdiv.util.HDIVUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * A custom wrapper for http session request that returns a wrapped http
 * session.
 * 
 * @author Roberto Velasco
 */
public class SessionHDIV implements ISession, BeanFactoryAware {

	/**
	 * Commons Logging instance.
	 */
	private static final Log log = LogFactory.getLog(SessionHDIV.class);

	/**
	 * The root interface for accessing a Spring bean container.
	 * 
	 * @see org.springframework.beans.factory.BeanFactory
	 */
	private BeanFactory beanFactory;

	/**
	 * HTTP session
	 */
	private HttpSession webSession;

	private String cipherName;

	private String cacheName;

	private String requestName;

	private String keyName;

	/**
	 * SessionHDIV initialization with HTTP session.
	 */
	public void init() {
		this.webSession = HDIVUtil.getHttpSession();
	}

	/**
	 * Testing initialization
	 */
	public void initTesting() {
		IStateCache cache = (IStateCache) this.beanFactory.getBean(this.cacheName);
		this.webSession.setAttribute(this.cacheName, cache);
	}

	/**
	 * Obtains from the user session the page identifier where the current
	 * request or form is
	 * 
	 * @return Returns the pageId.
	 */
	public String getPageId() {

		Integer requestId = (Integer) this.webSession.getAttribute(this.requestName);
		if (requestId == null) {
			Integer initialPageId = this.generateInitialPageId();
			this.webSession.setAttribute(this.requestName, initialPageId);
			return initialPageId.toString();

		} else {
			int result = requestId.intValue();
			result++;
			this.webSession.setAttribute(this.requestName, new Integer(result));
			return result + "";
		}
	}
	
	/**
	 * Returns the page with id <code>pageId</code>.
	 * @param pageId page id
	 * @return Returns the page with id <code>pageId</code>.
	 * @since HDIV 2.0.4
	 */
	public IPage getPage(String pageId) {
		try {
			return (IPage) this.webSession.getAttribute(pageId);
			
		} catch (Exception e) {
			throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT);
		}
	}	

	/**
	 * Generates a random number for the first page identifier
	 * 
	 * @return init page identifier
	 * @since HDIV 1.1
	 */
	public Integer generateInitialPageId() {

		Random r = new Random();
		int i = r.nextInt(20);
		if (i == 0) {
			i = 1;
		}

		return new Integer(i);
	}

	/**
	 * It adds a new page to the user session. To do this it adds a new page
	 * identifier to the cache and if it has reached the maximun size allowed,
	 * the oldest page is deleted from the session and from the cache itself.
	 * 
	 * @param pageId Page identifier
	 * @param page Page with all the information about states
	 */
	public void addPage(String pageId, IPage page) {

		IStateCache cache = (IStateCache) this.webSession.getAttribute(this.cacheName);

		page.setName(pageId);
		String removedPageId = cache.addPage(pageId);

		// if it returns a page identifier it is because the cache has reached
		// the maximun size and therefore we must delete the page which has been
		// stored for the longest time
		if (removedPageId != null) {
			this.webSession.removeAttribute(removedPageId);
		}

		// we update page identifier cache in session
		this.webSession.setAttribute(this.cacheName, cache);

		// we add a new page in session
		this.webSession.setAttribute(page.getName(), page);
	}

	 /**
	  * Deletes from session the data related to the finished flows. This means 
	  * a memory consumption optimization because useless objects of type 
	  * <code>IPage</code> are deleted.
	  * 
	  * @param conversationId finished flow identifier
	  * @since HDIV 2.0.3
	  */	
	public void removeEndedPages(String conversationId) {

		IStateCache cache = (IStateCache) this.webSession.getAttribute(this.cacheName);
		if (log.isDebugEnabled()) {
			log.debug("Cache pages before finished pages are deleted:" + cache.toString());
		}

		List pageIds = cache.getPageIds();
		String pageId = null;
		IPage currentPage = null;

		for (int i = 0; i < pageIds.size(); i++) {

			pageId = (String) pageIds.get(i);
			currentPage = (IPage) this.webSession.getAttribute(pageId);
			if ((currentPage != null) && (currentPage.getFlowId() != null)) {
				
				String pageFlowId = currentPage.getFlowId();	
				if (pageFlowId.indexOf("_k") > 0) {
					pageFlowId = pageFlowId.substring(0, pageFlowId.indexOf("_k"));
				}

				if (conversationId.equalsIgnoreCase(pageFlowId)) {

					this.webSession.removeAttribute(pageId);
					String removedId = (String) pageIds.remove(i);
					i--;

					if (removedId != null) {
						if (log.isDebugEnabled()) {
							log.debug("Page with id " + removedId + " have been removed");
						}
					}
				}
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("Cache pages after finished pages are deleted:" + cache.toString());
		}
	}

	/**
	 * Obtains the state identifier <code>stateId</code> related to the page
	 * identifier <code>pageId</code>.
	 * 
	 * @return State identifier <code>stateId</code> throws HDIVException If
	 *         the state doesn't exist a new HDIV exception is thrown.
	 */
	public IState getState(String pageId, String stateId) {

		try {
			IPage currentPage = (IPage) this.webSession.getAttribute(pageId);
			return currentPage.getState(stateId);

		} catch (Exception e) {
			throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT);
		}
	}

	/**
	 * Obtains the hash of the state identifier <code>stateId</code> related
	 * to page identifier <code>pageId</code>.
	 * 
	 * @return Hash of the state identifier <code>stateId</code>
	 * @throws HDIVException If the state doesn't exist a new HDIV exception is
	 *             thrown.
	 */
	public String getStateHash(String pageId, String stateId) {

		try {
			IPage currentPage = (IPage) this.webSession.getAttribute(pageId);
			return currentPage.getStateHash(stateId);

		} catch (Exception e) {
			throw new HDIVException(HDIVErrorCodes.PAGE_ID_INCORRECT);
		}
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
	 * Initializes the data cipher.
	 * 
	 * @return Returns the data cipher initialized with the symmetric key
	 * @throws HDIVException If the state doesn't exist a new HDIV exception is
	 *             thrown.
	 */
	public ICipherHTTP getEncryptCipher() {
		try {
			Key key = (Key) this.webSession.getAttribute(this.keyName);
			ICipherHTTP cipher = (ICipherHTTP) this.beanFactory.getBean(this.cipherName);
			cipher.initEncryptMode(key);
			return cipher;

		} catch (Exception e) {
			String errorMessage = HDIVUtil.getMessage("encrypt.message");
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * Inilitializes the data decrypter.
	 * 
	 * @return Returns the data decrypter initialized with the symmetric key.
	 * @throws HDIVException if there is an error in cipher initialization.
	 */
	public ICipherHTTP getDecryptCipher() {
		try {
			Key key = (Key) this.webSession.getAttribute(this.keyName);
			ICipherHTTP cipher = (ICipherHTTP) this.beanFactory.getBean(this.cipherName);
			cipher.initDecryptMode(key);
			return cipher;

		} catch (Exception e) {
			String errorMessage = HDIVUtil.getMessage("decrypt.message");
			throw new HDIVException(errorMessage, e);
		}
	}

	/**
	 * @return Returns the HTTP session.
	 */
	public HttpSession getWebSession() {
		return webSession;
	}

	/**
	 * @param webSession The webSession to set.
	 */
	public void setWebSession(HttpSession webSession) {
		this.webSession = webSession;
	}

	/**
	 * @return Returns the cacheName.
	 */
	public String getCacheName() {
		return cacheName;
	}

	/**
	 * @param cacheName The cacheName to set.
	 */
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	/**
	 * @return Returns the cipherName.
	 */
	public String getCipherName() {
		return cipherName;
	}

	/**
	 * @param cipherName The cipherName to set.
	 */
	public void setCipherName(String cipherName) {
		this.cipherName = cipherName;
	}

	/**
	 * @return Returns the requestName.
	 */
	public String getRequestName() {
		return requestName;
	}

	/**
	 * @param requestName The requestName to set.
	 */
	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	/**
	 * @return Returns the keyName.
	 */
	public String getKeyName() {
		return keyName;
	}

	/**
	 * @param keyName The keyName to set.
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

}