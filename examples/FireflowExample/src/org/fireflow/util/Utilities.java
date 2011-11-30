package org.fireflow.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.fireflow.security.persistence.Department;
import org.fireflow.security.persistence.DepartmentDAO;
import org.springframework.beans.factory.InitializingBean;

public class Utilities implements InitializingBean{
	DepartmentDAO departmentDAO = null;
	Map states = new HashMap();
	
	public Utilities(){
		states.put(new Integer(0), "Initialized");
		states.put(new Integer(1), "Running");
		states.put(new Integer(7), "Completed");
		states.put(new Integer(9), "Canceled");
	}
	
	List departmentSelectItems = new ArrayList();

	public List getDepartmentSelectItems() {
		return departmentSelectItems;
	}

	public void setDepartmentSelectItems(List departmentSelectItems) {
		this.departmentSelectItems = departmentSelectItems;
	}

	
	public DepartmentDAO getDepartmentDAO() {
		return departmentDAO;
	}

	public void setDepartmentDAO(DepartmentDAO departmentDAO) {
		this.departmentDAO = departmentDAO;
	}

	public void afterPropertiesSet() throws Exception {
		List<Department> departments = departmentDAO.findAll();
		for (int i=0;i<departments.size();i++){
			Department dept = departments.get(i);
			SelectItem selectItem = new SelectItem();
			selectItem.setValue(dept.getCode());
			selectItem.setLabel(dept.getName());
			departmentSelectItems.add(selectItem);
		}
	}
	
	
	public Map getWorkflowElementInstanceStates(){
		return states;
	}

	public static boolean isEmpty(String s){
		if (s==null || s.trim().equals("")){
			return true;
		}
		return false;
	}
}
