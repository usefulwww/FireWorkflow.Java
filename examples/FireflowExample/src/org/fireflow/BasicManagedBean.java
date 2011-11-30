package org.fireflow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

public class BasicManagedBean extends AbstractManagedBean {
	protected List bizDataList = null;
	protected String selectedObjectId = null;
	protected Object currentObject = null;
	protected Boolean isCreatOperation = Boolean.FALSE;
	
	
	
	public List getBizDataList() {
		return bizDataList;
	}

	public void setBizDataList(List bizDataList) {
		this.bizDataList = bizDataList;
	}

	public String getSelectedObjectId() {
		return selectedObjectId;
	}

	public void setSelectedObjectId(String selectedObjectId) {
		this.selectedObjectId = selectedObjectId;
	}

	public Object getCurrentObject() {
		return currentObject;
	}

	public void setCurrentObject(Object selectedObject) {
		this.currentObject = selectedObject;
	}
	
	

	public Boolean getIsCreatOperation() {
		return isCreatOperation;
	}

	public void setIsCreatOperation(Boolean isCreatOperation) {
		this.isCreatOperation = isCreatOperation;
	}

	@Override
	protected String executeBizOperQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String executeSaveBizData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String fireBizDataSelected() {
		Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		this.selectedObjectId = (String)requestMap.get("selectedObjectId");
		for (int i=0;bizDataList!=null && i<bizDataList.size();i++){
			Object obj = bizDataList.get(i);
			try {
				Method m = obj.getClass().getMethod("getId", null);
				String id = (String)m.invoke(obj, null);
				if (this.selectedObjectId!=null && this.selectedObjectId.equals(id)){
					this.currentObject = obj;
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(InvocationTargetException e){
				e.printStackTrace();
			}
			catch(IllegalAccessException e){
				e.printStackTrace();
			}
		}
		return null;
	}

	public String doSelectBizData() {
		// TODO Auto-generated method stub
		return null;
	}

}
