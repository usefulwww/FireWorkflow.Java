/*
 * Copyright 2007-2009 非也
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

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */

package org.fireflow.engine.persistence.hibernate;


import org.fireflow.engine.persistence.IFireWorkflowHelperDao;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 *
 * @author 非也
 * @version 1.0
 * Created on Mar 11, 2009
 */
public class FireWorkflowHelperDao extends HibernateDaoSupport implements IFireWorkflowHelperDao{
    /**
     * 测试之前删除所有表中的数据
     * 不在需要，但是这个类还是需要的
     */
    public void clearAllTables(){
//        this.getHibernateTemplate().execute(new HibernateCallback(){
//
//            @SuppressWarnings("unchecked")
//			public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
//            	String deleteHql4ProcessInstanceVar = "delete from org.fireflow.engine.impl.ProcessInstanceVar ";
//            	
//                String selectHql4ProcessInstance = "from org.fireflow.engine.impl.ProcessInstance";
////                String deleteHql4ProcessInstance = "delete from org.fireflow.engine.impl.ProcessInstance";
//                String deleteHql4TaskInstance = "delete from org.fireflow.engine.impl.TaskInstance";
//                String deleteHql4WorkItem = "delete from org.fireflow.engine.impl.WorkItem";
//                String deleteHql4Token = "delete from org.fireflow.kernel.impl.Token";
//                
//                Query query4ProcessInstanceVar = arg0.createQuery(deleteHql4ProcessInstanceVar);
//                query4ProcessInstanceVar.executeUpdate();
//
//                Query query4ProcessInstance = arg0.createQuery(selectHql4ProcessInstance);
//                List processInstanceList = query4ProcessInstance.list();
//                for (int i=0;processInstanceList!=null && i<processInstanceList.size();i++){
//                    IProcessInstance procInst = (IProcessInstance)processInstanceList.get(i);
//                    arg0.delete(procInst);
//                }
////                Query query4ProcessInstance = arg0.createQuery(deleteHql4ProcessInstance);
////                query4ProcessInstance.executeUpdate();
//
//               Query query4WorkItem = arg0.createQuery(deleteHql4WorkItem);
//                query4WorkItem.executeUpdate();
//
//                Query query4TaskInstance = arg0.createQuery(deleteHql4TaskInstance);
//                query4TaskInstance.executeUpdate();
// 
//
//                Query query4Token = arg0.createQuery(deleteHql4Token);
//                query4Token.executeUpdate();
//
//                return null;
//            }
//
//        });
    }
}
