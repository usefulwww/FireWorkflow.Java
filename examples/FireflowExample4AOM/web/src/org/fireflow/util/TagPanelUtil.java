/*$Id: TagPanelUtil.java,v 1.1 2009/05/22 09:41:16 libin Exp $
 *--------------------------------------
 * Apusic (Kingdee Middleware)
 *---------------------------------------
 * Copyright By Apusic ,All right Reserved
 * author   date   comment
 * chenhongxin  2008-4-14  Created
*/
package org.fireflow.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;

/**
 * TagPanel标签页工具类，实现将标签的图标地址转换成CSS文件
 * @author chenhongxin
 */
public class TagPanelUtil {
	/**
	 * css中图标的class的后缀
	 */
	public static final String PREFIX = "_tpcls";
	
	/**
	 * 获取图标的Css Class
	 * @param iconPath 图标的原始地址
	 * @return 图标的Css Class
	 */
	public static String getTabPanelIconCls(String iconPath) {
		if(iconPath == null) return "";
		int seperatorPos = iconPath.lastIndexOf('/');
		int dotPos = iconPath.lastIndexOf('.');
		
		if(seperatorPos == iconPath.length() - 1) return "";
		
		if(seperatorPos >= 0 && seperatorPos > dotPos) {
			return iconPath.substring(seperatorPos + 1) + PREFIX;
		} else if (seperatorPos >= 0 && seperatorPos < dotPos - 1) {
			return iconPath.substring(seperatorPos + 1, dotPos) + PREFIX;
		} else if (seperatorPos < 0 && dotPos > 0) {
			return iconPath.substring(0, dotPos) + PREFIX;
		} else {
			return "";
		}
	}
	
	/**
	 * 创建TagPanel标签页图标的Css文件
	 * @param icons 原始图标地址列表
	 * @param output 输出文件的物理路径
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void createTagPanelIconCss(List<String> icons, String output) throws FileNotFoundException, IOException {
		if(icons == null) return;
		
		StringBuilder sb = new StringBuilder();
		String basePath = TagPanelUtil.getBasePath();
		for (String icon : icons) {
			sb.append(".");
			sb.append(getTabPanelIconCls(icon));
			sb.append("{background-image: url(");
			sb.append(basePath);
			sb.append(icon);
			sb.append(")}\n");
		}
		
		
		FileOutputStream fos = new FileOutputStream(output);  
		Writer out = new OutputStreamWriter(fos, "utf-8");  
		out.write(sb.toString());  
		out.close();  
		fos.close(); 
	}
	
	/**
	 * 获取网站的BasePath
	 * 
	 * @return 网站的BasePath的字符串
	 */
	public static String getBasePath() {
		FacesContext context = FacesContext.getCurrentInstance();
		ExternalContext eContext = context.getExternalContext();
		ServletRequest req = (ServletRequest) eContext.getRequest();
		return (req.getScheme() + "://" + req.getServerName() + ":"
				+ req.getServerPort() + eContext.getRequestContextPath() + "/");
	}
}
