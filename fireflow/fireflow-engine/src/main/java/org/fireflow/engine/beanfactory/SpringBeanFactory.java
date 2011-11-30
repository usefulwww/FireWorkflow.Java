/**
 * Copyright 2007-2008 非也
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.engine.beanfactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
/**
 * 用Spring 的IOC容器作为Fire Workflow 的BeanFactory
 * @author 非也，nychen2000@163.com
 */
public class SpringBeanFactory implements IBeanFactory,BeanFactoryAware {
    BeanFactory springBeanFactory = null;

    /**
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        return springBeanFactory.getBean(beanName);
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    public void setBeanFactory(BeanFactory arg0) throws BeansException {
        springBeanFactory = arg0;
    }
}
