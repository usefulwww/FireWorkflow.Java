package org.fireflow.jsf.tabbedpane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.custom.tabbedpane.HtmlPanelTab;
import org.apache.myfaces.custom.tabbedpane.HtmlPanelTabbedPane;
import org.apache.myfaces.custom.tabbedpane.HtmlTabbedPaneRenderer;
import org.apache.myfaces.renderkit.html.util.AddResource;
import org.apache.myfaces.renderkit.html.util.AddResourceFactory;
import org.apache.myfaces.shared_tomahawk.renderkit.RendererUtils;
import org.apache.myfaces.shared_tomahawk.renderkit.html.HTML;
import org.apache.myfaces.shared_tomahawk.renderkit.html.HtmlRendererUtils;
import org.apache.myfaces.shared_tomahawk.renderkit.html.util.FormInfo;
import org.apache.myfaces.shared_tomahawk.renderkit.html.util.JavascriptUtils;
import org.fireflow.jsf.FireflowJsfResourceLoader;

public class HtmlTabbedPaneRendererExtension extends HtmlTabbedPaneRenderer {
	private static final String DISABLED_HEADER_CELL_CLASS = "myFaces_panelTabbedPane_disabledHeaderCell";
    private static final String HEADER_ROW_CLASS = "myFaces_pannelTabbedPane_HeaderRow";
    private static final String ACTIVE_HEADER_CELL_CLASS = "myFaces_panelTabbedPane_activeHeaderCell";
    private static final String INACTIVE_HEADER_CELL_CLASS = "myFaces_panelTabbedPane_inactiveHeaderCell";
    private static final String EMPTY_HEADER_CELL_CLASS = "myFaces_panelTabbedPane_emptyHeaderCell";
    private static final String SUB_HEADER_ROW_CLASS = "myFaces_pannelTabbedPane_subHeaderRow";
    private static final String SUB_HEADER_CELL_CLASS = "myFaces_panelTabbedPane_subHeaderCell";
    private static final String SUB_HEADER_CELL_CLASS_ACTIVE = "myFaces_panelTabbedPane_subHeaderCell_active";
    private static final String SUB_HEADER_CELL_CLASS_INACTIVE = "myFaces_panelTabbedPane_subHeaderCell_inactive";
    private static final String SUB_HEADER_CELL_CLASS_FIRST = "myFaces_panelTabbedPane_subHeaderCell_first";
    private static final String SUB_HEADER_CELL_CLASS_LAST = "myFaces_panelTabbedPane_subHeaderCell_last";
    private static final String CONTENT_ROW_CLASS = "myFaces_panelTabbedPane_contentRow";
    private static final String TAB_PANE_CLASS = "myFaces_panelTabbedPane_pane";
    private static final String DEFAULT_BG_COLOR = "white";

    private static final String AUTO_FORM_SUFFIX = ".autoform";
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException
    {
        AddResource addResource = AddResourceFactory.getInstance(facesContext);
    	
        addResource.addStyleSheet(facesContext,AddResource.HEADER_BEGIN,
        		FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
        		FireflowJsfResourceLoader.Resource_Path+"=tabbedpane/resource/css/tabbedPane.css");
    	
        RendererUtils.checkParamValidity(facesContext, uiComponent, HtmlPanelTabbedPane.class);
        HtmlPanelTabbedPane tabbedPane = (HtmlPanelTabbedPane)uiComponent;
        if (tabbedPane.getBgcolor() == null)
        {
            tabbedPane.setBgcolor(DEFAULT_BG_COLOR);
        }


//        addResource.addStyleSheet(facesContext,AddResource.HEADER_BEGIN,
//                                  HtmlTabbedPaneRenderer.class, "defaultStyles.css");

        if( tabbedPane.isClientSide() ){
        	addResource.addJavaScriptAtPosition(facesContext, AddResource.HEADER_BEGIN,
            		FireflowJsfResourceLoader.Resource_Servlet_Name+"?"+
            		FireflowJsfResourceLoader.Resource_Path+"=tabbedpane/resource/js/myDynamicTabs.js");
                
                addResource.addInlineStyleAtPosition(facesContext,AddResource.HEADER_BEGIN,
                                                     '#'+getTableStylableId(tabbedPane,facesContext)+" ."+ACTIVE_HEADER_CELL_CLASS+" input,\n" +
                                                     '#'+getTableStylableId(tabbedPane,facesContext)+" ."+TAB_PANE_CLASS+",\n" +
                                                     '#'+getTableStylableId(tabbedPane,facesContext)+" ."+SUB_HEADER_CELL_CLASS+"{\n"+
                                                     "background-color:" + tabbedPane.getBgcolor()+";\n"+
                                                     "}\n");
        }


        ResponseWriter writer = facesContext.getResponseWriter();

        HtmlRendererUtils.writePrettyLineSeparator(facesContext);

        int selectedIndex = tabbedPane.getSelectedIndex();

        

        FormInfo parentFormInfo = RendererUtils.findNestingForm(tabbedPane, facesContext);


        List children = tabbedPane.getChildren();

        if( tabbedPane.isClientSide() ){
//        	selectedIndex=0;//ʼ��Ϊ��һ��
        	
            List headerIDs = new ArrayList();
            List tabIDs = new ArrayList();
            for (int i = 0, len = children.size(); i < len; i++)
            {
                UIComponent child = getUIComponent((UIComponent)children.get(i));
                if (child instanceof HtmlPanelTab && child.isRendered()){
                    HtmlPanelTab tab = (HtmlPanelTab) child;
                    tabIDs.add( child.getClientId(facesContext) );
                    if( ! isDisabled(facesContext, tab) )
                        headerIDs.add( getHeaderCellID(tab, facesContext) );
                }
            }

            HtmlRendererUtils.writePrettyLineSeparator(facesContext);
            writer.startElement(HTML.SCRIPT_ELEM, tabbedPane);
            writer.write('\n');

            writer.write( getHeaderCellsIDsVar(tabbedPane,facesContext)+"= new Array(" );
            for(Iterator ids=headerIDs.iterator(); ids.hasNext();){
                String id = (String)ids.next();
                writer.write('"'+JavascriptUtils.encodeString( id )+'"');
                if( ids.hasNext() )
                    writer.write(',');
            }
            writer.write( ");\n" ); // end Array

            writer.write( getTabsIDsVar(tabbedPane,facesContext)+"= new Array(" );
            for(Iterator ids=tabIDs.iterator(); ids.hasNext();){
                String id = (String)ids.next();
                writer.write('"'+JavascriptUtils.encodeString( id )+'"');
                if( ids.hasNext() )
                    writer.write(',');
            }
            writer.write( ");\n" ); // end Array

            writer.endElement(HTML.SCRIPT_ELEM);
            HtmlRendererUtils.writePrettyLineSeparator(facesContext);

            String submitFieldIDAndName = getTabIndexSubmitFieldIDAndName(tabbedPane, facesContext);
            writer.startElement(HTML.INPUT_ELEM, tabbedPane);
            writer.writeAttribute(HTML.ID_ATTR, submitFieldIDAndName, null);
            writer.writeAttribute(HTML.NAME_ATTR, submitFieldIDAndName, null);
            writer.writeAttribute(HTML.STYLE_ATTR, "display:none", null);
            writer.endElement(HTML.INPUT_ELEM);
        }

        writeTableStart(writer, facesContext, tabbedPane);
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        
        if (parentFormInfo == null && !tabbedPane.isClientSide())
        {
            writeFormStart(writer, facesContext, tabbedPane);
        }        
        writer.startElement(HTML.TR_ELEM, tabbedPane);
        writer.writeAttribute(HTML.CLASS_ATTR, HEADER_ROW_CLASS, null);

        //Tab headers
        int tabIdx = 0;
        int visibleTabCount = 0;
        int visibleTabSelectedIdx = -1;
        String cellWidth=null;//
        if (children.size()<=3){//��tabҳС�ڵ���3�������£�����ÿ��tabҳ�Ŀ��
        	cellWidth=100/(children.size()+1)+"%";
        }
        for (int i = 0, len = children.size(); i < len; i++)
        {
            UIComponent child = getUIComponent((UIComponent)children.get(i));
            if (child instanceof HtmlPanelTab)
            {
                if (child.isRendered())
                {
                    writeHeaderCell(writer,
                                    facesContext,
                                    tabbedPane,
                                    (HtmlPanelTab)child,
                                    tabIdx,
                                    visibleTabCount,
                                    tabIdx == selectedIndex,
                                    isDisabled(facesContext, (HtmlPanelTab)child),cellWidth);
                    if (tabIdx == selectedIndex)
                    {
                        visibleTabSelectedIdx = visibleTabCount;
                    }
                    visibleTabCount++;
                }
                tabIdx++;
            }
        }

        //Empty tab cell on the right for better look
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        HtmlRendererUtils.writePrettyIndent(facesContext);
        writer.startElement(HTML.TD_ELEM, tabbedPane);
        writer.writeAttribute(HTML.CLASS_ATTR, EMPTY_HEADER_CELL_CLASS, null);
        writer.write("&#160;");
        writer.endElement(HTML.TD_ELEM);
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        writer.endElement(HTML.TR_ELEM);
        
        
        if (parentFormInfo == null && !tabbedPane.isClientSide())
        {
            writeFormEnd(writer, facesContext);
        }
        
        
        //Sub header cells
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        writer.startElement(HTML.TR_ELEM, tabbedPane);
        writer.writeAttribute(HTML.CLASS_ATTR, SUB_HEADER_ROW_CLASS, null);
        writeSubHeaderCells(writer, facesContext, tabbedPane, visibleTabCount, visibleTabSelectedIdx);
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        writer.endElement(HTML.TR_ELEM);

        //Tabs
        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        writer.startElement(HTML.TR_ELEM, tabbedPane);
        writer.writeAttribute(HTML.CLASS_ATTR, CONTENT_ROW_CLASS, null);
        writer.startElement(HTML.TD_ELEM, tabbedPane);
        writer.writeAttribute(HTML.COLSPAN_ATTR, Integer.toString(visibleTabCount + 1), null);
        String tabContentStyleClass = tabbedPane.getTabContentStyleClass();
        writer.writeAttribute(HTML.CLASS_ATTR, TAB_PANE_CLASS+(tabContentStyleClass==null ? "" : " "+tabContentStyleClass), null);

        writeTabsContents(writer, facesContext, tabbedPane, selectedIndex);

        writer.endElement(HTML.TD_ELEM);
        writer.endElement(HTML.TR_ELEM);

        HtmlRendererUtils.writePrettyLineSeparator(facesContext);
        writer.endElement(HTML.TABLE_ELEM);


    }
    
	protected void writeHeaderCell(ResponseWriter writer,
			FacesContext facesContext, HtmlPanelTabbedPane tabbedPane,
			HtmlPanelTab tab, int tabIndex, int visibleTabIndex,
			boolean active, boolean disabled,String width) throws IOException {
		HtmlRendererUtils.writePrettyLineSeparator(facesContext);
		HtmlRendererUtils.writePrettyIndent(facesContext);
		

		writer.startElement(HTML.TD_ELEM, tabbedPane);
		writer.writeAttribute(HTML.ID_ATTR, getHeaderCellID(tab, facesContext),
				null);
		if (width!=null && !width.equals("")){
			writer.writeAttribute(HTML.WIDTH_ATTR, width, null);
		}


		if (disabled) {
			String disabledClass = tabbedPane.getDisabledTabStyleClass();
			writer.writeAttribute(HTML.CLASS_ATTR, DISABLED_HEADER_CELL_CLASS
					+ (disabledClass == null ? "" : ' ' + disabledClass), null);
		} else {
			if (active) {
				writer.writeAttribute(HTML.CLASS_ATTR,
						getActiveHeaderClasses(tabbedPane), null);
			} else {
				writer.writeAttribute(HTML.CLASS_ATTR,
						getInactiveHeaderClasses(tabbedPane), null);
			}
		}

		String label = tab.getLabel();
		if (label == null || label.length() == 0) {
			label = "Tab " + tabIndex;
		}
		
		String accesskey = (String)tab.getAttributes().get("accesskey");
		String tabindex = (String)tab.getAttributes().get("tabindex");
		String defaultFocusedElement = (String)tab.getAttributes().get("defaultFocusedElement");


		if (disabled) {
			writer.startElement(HTML.LABEL_ELEM, tabbedPane);
			// writer.writeAttribute(HTML.NAME_ATTR,
			// tabbedPane.getClientId(facesContext) + "." + tabIndex, null); //
			// Usefull ?
			writer.writeText(label, null);
			//writer.writeAttribute(HTML.TABINDEX_ATTR, "-1", null);
			writer.endElement(HTML.LABEL_ELEM);
		} else {
			// Button
			writer.startElement(HTML.INPUT_ELEM, tabbedPane);
			writer.writeAttribute(HTML.TYPE_ATTR, "submit", null);
			writer.writeAttribute(HTML.ID_ATTR, tab.getId()+"_HEARDER_INPUT", null);
			writer.writeAttribute(HTML.NAME_ATTR, tabbedPane
					.getClientId(facesContext)
					+ "." + tabIndex, null);
			writer.writeAttribute(HTML.VALUE_ATTR, label, null);
			
			if (tabbedPane.isClientSide()) {
				String activeUserClass = tabbedPane.getActiveTabStyleClass();
				String inactiveUserClass = tabbedPane
						.getInactiveTabStyleClass();
				String activeSubStyleUserClass = tabbedPane
						.getActiveSubStyleClass();
				String inactiveSubStyleUserClass = tabbedPane
						.getInactiveSubStyleClass();
//				System.out.println("----activeUserClass is "+activeUserClass);
//				System.out.println("--inactiveUserClass is "+inactiveUserClass);
				writer
						.writeAttribute(
								HTML.ONCLICK_ATTR,
								"return my_showPanelTab("
										+ tabIndex
										+ ",'"
										+ getTabIndexSubmitFieldIDAndName(
												tabbedPane, facesContext)
										+ "',"
										+ '\''
										+ getHeaderCellID(tab, facesContext)
										+ "','"
										+ tab.getClientId(facesContext)
										+ "',"
										+ getHeaderCellsIDsVar(tabbedPane,
												facesContext)
										+ ','
										+ getTabsIDsVar(tabbedPane,
												facesContext)
										+ ','
										+ (activeUserClass == null ? "null"
												: '\'' + activeUserClass + '\'')
										+ ','
										+ (inactiveUserClass == null ? "null"
												: '\'' + inactiveUserClass + '\'')
										+ ','
										+ (activeSubStyleUserClass == null ? "null"
												: '\'' + activeSubStyleUserClass + '\'')
										+ ','
										+ (inactiveSubStyleUserClass == null ? "null"
												: '\'' + inactiveSubStyleUserClass + '\'')
										+','
										+ (defaultFocusedElement==null?"null":'\''+defaultFocusedElement+'\'')
										+");", null);

			}
			if (accesskey!=null){
				writer.writeAttribute(HTML.ACCESSKEY_ATTR, accesskey, null);
			}
			//System.out.println("--Inside tabbedPaneRendererextionsion: tabindex="+tabindex);
			if (tabindex!=null){
				writer.writeAttribute(HTML.TABINDEX_ATTR, tabindex, null);				
			}
			writer
				.writeAttribute(HTML.ONFOCUS_ATTR,"JavaScript:if(event.altKey){this.click();};",null);
			
			writer.endElement(HTML.INPUT_ELEM);
		}
		writer.endElement(HTML.TD_ELEM);
	}
    private UIComponent getUIComponent(UIComponent uiComponent)
    {
        /* todo: handle forms other than UIForm */
        if (uiComponent instanceof UIForm || uiComponent instanceof UINamingContainer)
        {
            List children = uiComponent.getChildren();
            for (int i = 0, len = children.size(); i < len; i++)
            {
                uiComponent = getUIComponent((UIComponent)children.get(i));
            }
        }
        return uiComponent;
    }
}
