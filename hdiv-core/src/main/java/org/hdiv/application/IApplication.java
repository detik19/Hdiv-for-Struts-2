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
package org.hdiv.application;

import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * ServletContext Wrapper interface
 * 
 * @author Roberto Velasco
 */
public interface IApplication {

	public void setApplication(ServletContext servletContext);

	/**
	 * Return an instance, which may be shared or independent, of the given bean
	 * name. This method allows a Spring BeanFactory to be used as a replacement for
	 * the Singleton or Prototype design pattern.
	 * <p>
	 * Callers may retain references to returned objects in the case of Singleton
	 * beans.
	 * <p>
	 * Translates aliases back to the corresponding canonical bean name. Will ask the
	 * parent factory if the bean cannot be found in this factory instance.
	 * 
	 * @param name the name of the bean to return
	 * @return the instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no bean definition with the
	 *             specified name
	 * @throws BeansException if the bean could not be obtained
	 */
	public Object getBean(String name);

	/**
	 * Callback that supplies the owning factory to a bean instance. Invoked after
	 * population of normal bean properties but before an init callback like
	 * InitializingBean's afterPropertiesSet or a custom init-method.
	 * 
	 * @param beanFactory owning BeanFactory (may not be null). The bean can
	 *            immediately call methods on the factory.
	 */	
	public void setBeanFactory(BeanFactory beanFactory);

}
