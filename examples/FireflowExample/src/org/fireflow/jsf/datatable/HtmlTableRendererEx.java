package org.fireflow.jsf.datatable;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.component.html.ext.HtmlDataTable;
import org.apache.myfaces.renderkit.html.ext.HtmlTableRenderer;
import org.apache.myfaces.renderkit.html.util.AddResource;
import org.apache.myfaces.renderkit.html.util.AddResourceFactory;
import org.apache.myfaces.shared_tomahawk.renderkit.RendererUtils;
import org.apache.myfaces.shared_tomahawk.renderkit.html.HTML;
import org.apache.myfaces.shared_tomahawk.renderkit.html.HtmlRendererUtils;
import org.fireflow.jsf.FireflowJsfResourceLoader;
//import org.apache.myfaces.shared_tomahawk.renderkit.html.HtmlTableRendererBase.Styles;

public class HtmlTableRendererEx extends HtmlTableRenderer {
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException
    {
        AddResource addResource = AddResourceFactory.getInstance(facesContext);
    	
//        addResource.addStyleSheet(facesContext, AddResource.HEADER_BEGIN, HtmlTableRendererEx.class, "css/dataTable.css");
//        addResource.addJavaScriptAtPosition(facesContext, AddResource.HEADER_BEGIN, HtmlTableRendererEx.class, "js/dataTable.js");
        addResource.addStyleSheet(facesContext,AddResource.HEADER_BEGIN,
        		FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
        		FireflowJsfResourceLoader.Resource_Path+"=datatable/resource/css/dataTable.css");    	
        addResource.addJavaScriptAtPosition(facesContext, AddResource.HEADER_BEGIN, 
        		FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
        		FireflowJsfResourceLoader.Resource_Path+"=datatable/resource/js/dataTable.js");
        
    	super.encodeBegin(facesContext, uiComponent);
    }
    

    
    protected void beforeTable(FacesContext facesContext, UIData uiData) throws IOException
    {
    	HtmlDataTableEx dataTableEx = (HtmlDataTableEx)uiData;
        ResponseWriter writer = facesContext.getResponseWriter();
        writer.startElement(HTML.DIV_ELEM, uiData); 
        if ( dataTableEx.getFrameDivStyle()!=null){
        	writer.writeAttribute(HTML.STYLE_ATTR, dataTableEx.getFrameDivStyle(), null);
        }
    }
    
    protected void afterTable(FacesContext facesContext, UIData uiData) throws IOException{
    	HtmlDataTableEx dataTableEx = (HtmlDataTableEx)uiData;
        ResponseWriter writer = facesContext.getResponseWriter();
        writer.endElement(HTML.DIV_ELEM); 
        Boolean isDefaultDataTable = dataTableEx.isDefaultDataTable();
        if (isDefaultDataTable){
        	writer.write("<script>defaultDataTableId='"+uiData.getClientId(facesContext)+"';bodyOnLoadEventRouter.addListener(selectTheFirstRow);</script>");
        }
    }
    
    protected void renderColumnHeaderRow(FacesContext facesContext, ResponseWriter writer, UIComponent component,
            String headerStyleClass) throws IOException
    {
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);

        writer.startElement(HTML.TR_ELEM, component);
        writer.writeAttribute(HTML.STYLE_ATTR, "position:relative;top:expression(this.offsetParent.scrollTop-2);", null);
        int columnIndex = 0;
        int newspaperColumns = getNewspaperColumns(component);
        for(int nc = 0; nc < newspaperColumns; nc++)
        {
            for (Iterator it = getChildren(component).iterator(); it.hasNext();)
            {
                UIComponent uiComponent = (UIComponent) it.next();
                if (uiComponent.isRendered())
                {
                    if (component instanceof UIData && uiComponent instanceof UIColumn)
                        beforeColumnHeaderOrFooter(facesContext, (UIData)component, true, columnIndex);
                
                	renderColumnChildHeaderOrFooterRow(facesContext, writer, uiComponent, headerStyleClass, true);
                    
                    if (component instanceof UIData && uiComponent instanceof UIColumn)
                        afterColumnHeaderOrFooter(facesContext, (UIData)component, true, columnIndex);
                }
                columnIndex += 1;
            }

            if (hasNewspaperTableSpacer(component))
            {
                // draw the spacer facet
                if(nc < newspaperColumns - 1) renderSpacerCell(facesContext, writer, component);
            }
        }
        writer.endElement(HTML.TR_ELEM);    	
//        renderColumnHeaderOrFooterRow(facesContext, writer, component, headerStyleClass, true);
    }    

//    protected void renderRowStart(
//            FacesContext facesContext,
//            ResponseWriter writer,
//            UIData uiData,
//            Styles styles, int rowStyleIndex) throws IOException
//        {
//    		
//            writer.startElement(HTML.TR_ELEM, uiData);
//            
//            renderRowStyle(facesContext, writer, uiData, styles, rowStyleIndex);
//            
//            Object rowId = uiData.getAttributes().get(org.apache.myfaces.shared_tomahawk.renderkit.JSFAttr.ROW_ID);
//
//            if (rowId != null)
//            {
//                writer.writeAttribute(HTML.ID_ATTR, rowId.toString(), null);
//            }
//        }
    protected void renderRowStyle(FacesContext facesContext, ResponseWriter writer, UIData uiData, Styles styles, int rowStyleIndex) throws IOException
    {
    	HtmlDataTableEx tableEx = (HtmlDataTableEx)uiData;
    	int pageSize = tableEx.getPageSize()==null?20:tableEx.getPageSize().intValue();
    	if (pageSize<=0){pageSize=20;}
    	int currentPage = 0;

    	currentPage = rowStyleIndex/pageSize+1;

    	if (rowStyleIndex>=pageSize){
    		writer.writeAttribute(HTML.STYLE_ATTR,"display:none",null);
    	}
    	writer.writeAttribute("pageIndex",currentPage,null);
        if(styles.hasRowStyle()) {
            String rowStyle = styles.getRowStyle(rowStyleIndex);
            writer.writeAttribute(HTML.CLASS_ATTR, rowStyle, null);
        }
    }    
}
