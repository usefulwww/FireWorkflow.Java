package org.fireflow.jsf.datatable;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.renderkit.html.ext.HtmlLinkRenderer;
import org.apache.myfaces.shared_tomahawk.renderkit.html.HTML;

public class RowActionRenderer extends HtmlLinkRenderer {
	// protected void renderOutputLinkEnd(FacesContext facesContext,
	// UIComponent component) throws IOException {
	// ResponseWriter writer = facesContext.getResponseWriter();
	// // force separate end tag
	// writer.writeText("", null);
	//
	// writer.endElement(HTML.ANCHOR_ELEM);
	// }
	protected void renderCommandLinkStart(FacesContext facesContext,
			UIComponent component, String clientId, Object value, String style,
			String styleClass) throws IOException {
		super.renderCommandLinkStart(facesContext, component, clientId, value,
				style, styleClass);
		ResponseWriter writer = facesContext.getResponseWriter();

		writer.writeAttribute(HTML.NAME_ATTR, "RowActionAnchor", null);
	}
}
