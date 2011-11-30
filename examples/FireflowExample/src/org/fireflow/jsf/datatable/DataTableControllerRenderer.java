package org.fireflow.jsf.datatable;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

import org.apache.myfaces.shared_tomahawk.renderkit.RendererUtils;
import org.apache.myfaces.shared_tomahawk.renderkit.html.HTML;
import org.apache.myfaces.shared_tomahawk.renderkit.html.HtmlRenderer;
import org.apache.myfaces.shared_tomahawk.util._ComponentUtils;
import org.fireflow.jsf.FireflowJsfResourceLoader;

public class DataTableControllerRenderer extends HtmlRenderer {
	public void encodeEnd(FacesContext facesContext, UIComponent component)
			throws IOException {

		RendererUtils.checkParamValidity(facesContext, component,
				DataTableController.class);

		HtmlDataTableEx parent = (HtmlDataTableEx)component.getParent();
		String tableId = getComponentId(facesContext,parent);
		
		int rowCount = parent.getRowCount();

		
		DataTableController controller = (DataTableController) component;
		int pageSize = parent.getPageSize()==null?20:parent.getPageSize().intValue();
		int pages = 0;
		if ((rowCount % pageSize)==0){
			pages = rowCount/pageSize;
		}else{
			pages = rowCount/pageSize+1;
		}
			
		String ctxPath = facesContext.getExternalContext().getRequestContextPath();
        //first page control
        ResponseWriter writer = facesContext.getResponseWriter();
        writer.startElement(HTML.IMG_ELEM,component);//
        writer.writeAttribute(HTML.ID_ATTR, "firstPageControl_4_"+tableId, null);
        writer.writeAttribute(HTML.SRC_ATTR, ctxPath+"/"+FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
        		FireflowJsfResourceLoader.Resource_Path+"=datatable/resource/images/page-first-disabled.gif", null);
        writer.writeAttribute(HTML.ALIGN_ATTR, "absmiddle", null);
        writer.writeAttribute(HTML.ONCLICK_ATTR, "firstPage('"+tableId+"');", null);
        writer.writeAttribute("imageFolder", ctxPath+"/"+FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
        		FireflowJsfResourceLoader.Resource_Path+"=datatable/resource/images/", null);
        writer.writeAttribute(HTML.ONMOUSEOVER_ATTR, "controlOnMouseOver(this)", null);
        writer.writeAttribute(HTML.ONMOUSEOUT_ATTR, "controlOnMouseOut(this)", null);
        writer.endElement(HTML.IMG_ELEM);
        
        //pervious page control
        writer.startElement(HTML.IMG_ELEM,component);//
        writer.writeAttribute(HTML.ID_ATTR, "prePageControl_4_"+tableId, null);
        writer.writeAttribute(HTML.SRC_ATTR, ctxPath+"/"+FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
        		FireflowJsfResourceLoader.Resource_Path+"=datatable/resource/images/page-prev-disabled.gif", null);
        writer.writeAttribute(HTML.ALIGN_ATTR, "absmiddle", null);
        writer.writeAttribute(HTML.ONCLICK_ATTR, "prePage('"+tableId+"');", null);
        writer.writeAttribute("imageFolder", ctxPath+"/"+FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
        		FireflowJsfResourceLoader.Resource_Path+"=datatable/resource/images/", null);
        writer.writeAttribute(HTML.ONMOUSEOVER_ATTR, "controlOnMouseOver(this)", null);
        writer.writeAttribute(HTML.ONMOUSEOUT_ATTR, "controlOnMouseOut(this)", null);        
        writer.endElement(HTML.IMG_ELEM);
        
        writer.startElement(HTML.IMG_ELEM,component);//
        writer.writeAttribute(HTML.SRC_ATTR, ctxPath+"/"+FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
        		FireflowJsfResourceLoader.Resource_Path+"=datatable/resource/images/grid-blue-split.gif", null);
        writer.writeAttribute(HTML.ALIGN_ATTR, "absmiddle", null);
        writer.endElement(HTML.IMG_ELEM);
        
       //页数，记录数等信息。。。
        writer.write("第<input value=\"1\" size=\"2\" id=\"currentPage_4_"+tableId+"\" totalPage=\""+pages+"\"/>页");
        writer.write("共"+pages+"页,"+rowCount+"条记录");
        
        writer.startElement(HTML.IMG_ELEM,component);//
        writer.writeAttribute(HTML.SRC_ATTR, ctxPath+"/"+FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
        		FireflowJsfResourceLoader.Resource_Path+"=datatable/resource/images/grid-blue-split.gif", null);
        writer.writeAttribute(HTML.ALIGN_ATTR, "absmiddle", null);
        writer.endElement(HTML.IMG_ELEM);
        
        //next page control
        writer.startElement(HTML.IMG_ELEM,component);//
        writer.writeAttribute(HTML.ID_ATTR, "nextPageControl_4_"+tableId, null);
        writer.writeAttribute(HTML.SRC_ATTR, ctxPath+"/"+FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
        		FireflowJsfResourceLoader.Resource_Path+"=datatable/resource/images/page-next.gif", null);
        writer.writeAttribute(HTML.ALIGN_ATTR, "absmiddle", null);
        writer.writeAttribute(HTML.ONCLICK_ATTR, "nextPage('"+tableId+"');", null);
        writer.writeAttribute("imageFolder", ctxPath+"/"+FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
        		FireflowJsfResourceLoader.Resource_Path+"=datatable/resource/images/", null);
        writer.writeAttribute(HTML.ONMOUSEOVER_ATTR, "controlOnMouseOver(this)", null);
        writer.writeAttribute(HTML.ONMOUSEOUT_ATTR, "controlOnMouseOut(this)", null);        
        writer.endElement(HTML.IMG_ELEM);
        
        //next page control
        writer.startElement(HTML.IMG_ELEM,component);//
        writer.writeAttribute(HTML.ID_ATTR, "lastPageControl_4_"+tableId, null);
        writer.writeAttribute(HTML.SRC_ATTR, ctxPath+"/"+FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
        		FireflowJsfResourceLoader.Resource_Path+"=datatable/resource/images/page-last.gif", null);
        writer.writeAttribute(HTML.ALIGN_ATTR, "absmiddle", null);
        writer.writeAttribute(HTML.ONCLICK_ATTR, "lastPage('"+tableId+"');", null);
        writer.writeAttribute("imageFolder", ctxPath+"/"+FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
        		FireflowJsfResourceLoader.Resource_Path+"=datatable/resource/images/", null);
        writer.writeAttribute(HTML.ONMOUSEOVER_ATTR, "controlOnMouseOver(this)", null);
        writer.writeAttribute(HTML.ONMOUSEOUT_ATTR, "controlOnMouseOut(this)", null);        
        writer.endElement(HTML.IMG_ELEM);		
        
        
	}
	
	public String getComponentId(FacesContext facesContext,UIComponent component){
		return component.getClientId(facesContext);
		/*
		Object ifForceId = component.getAttributes().get("forceId");
		String componentId = null;
		if (ifForceId==null){
	        ValueBinding vb = component.getValueBinding("forceId");
	        ifForceId = vb != null ? _ComponentUtils.getStringValue(facesContext, vb) : null;				
		}

		if (ifForceId==null || ifForceId.toString().toLowerCase()=="false"){
			componentId = component.getClientId(facesContext);
		}
		else{
			componentId = component.getId();
		}
		
		return componentId;
		*/
	}
}
