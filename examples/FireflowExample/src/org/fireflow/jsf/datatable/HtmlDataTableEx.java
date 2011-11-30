package org.fireflow.jsf.datatable;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

import org.apache.myfaces.component.html.ext.HtmlDataTable;

public class HtmlDataTableEx extends HtmlDataTable {
	private String frameDivStyle = "";
	private Integer pageSize = new Integer(20);
	private Boolean defaultDataTable = false;


	
	public HtmlDataTableEx(){
		super();
		this.setHeaderClass("dataTableHeader");
		this.setFooterClass("dataTableFooter");
		this.setRowClasses("dataTableRow1,dataTableRow2");
		this.setOnkeydown("scrollRow(this);");
		this.setBorder(0);
		this.setWidth("100%");
		this.setBgcolor("#EEEEEE");
		this.setCellspacing("1");
//		this.setForceId(true);
		this.setFirst(0);
		this.setRows(0);
		
		this.setRowOnClick("selectRow(this);");
        setRendererType("cn.hnisi.jsfext.datatable.HtmlTableRendererEx");
	}
	
	public void setOnkeydown(String jsEventHandler){
		String oldEventHandler = this.getOnkeydown();
		if (oldEventHandler!=null && !oldEventHandler.endsWith(";")){
			oldEventHandler=oldEventHandler+";";
		}
		if (oldEventHandler==null){
			super.setOnkeydown(jsEventHandler);
		}else{
			super.setOnkeydown(oldEventHandler+jsEventHandler);
		}
	}
	
	
	public void setRowOnClick(String jsEventHandler){
		String oldEventHandler = this.getRowOnClick();
		if (oldEventHandler!=null && !oldEventHandler.endsWith(";")){
			oldEventHandler=oldEventHandler+";";
		}
		if (oldEventHandler==null){
			super.setRowOnClick(jsEventHandler);
		}else{
			super.setRowOnClick(oldEventHandler+jsEventHandler);
		}
	}
	

//	public void setForceId(boolean b){
//		super.setForceId(true);
//	}

	public String getFrameDivStyle() {
		return frameDivStyle;
	}

	public void setFrameDivStyle(String frameDivStyle) {
		this.frameDivStyle = frameDivStyle;
	}
    public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	
    public Boolean isDefaultDataTable() {
		return defaultDataTable;
	}

	public void setDefaultDataTable(Boolean defaultDataTable) {
		this.defaultDataTable = defaultDataTable;
	}

	public Object saveState(FacesContext context)
    {
        Object values[] = new Object[4];
        values[0] = super.saveState(context);
        values[1] = frameDivStyle;
        values[2] = this.pageSize;
        values[3] = this.defaultDataTable;
        return ((Object) (values));
    }

    public void restoreState(FacesContext context, Object state)
    {
        Object values[] = (Object[])state;
        super.restoreState(context, values[0]);
        frameDivStyle = (String)values[1];
        pageSize = (Integer)values[2];
        this.defaultDataTable = (Boolean)values[3];
    }	
	
}
