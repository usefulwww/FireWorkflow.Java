package org.fireflow.example.goods_deliver_process.mbeans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.fireflow.BasicManagedBean;
import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.example.goods_deliver_process.persistence.TradeInfo;
import org.fireflow.example.goods_deliver_process.persistence.TradeInfoDAO;
import org.fireflow.kernel.KernelException;
import org.fireflow.security.persistence.User;
import org.fireflow.security.util.SecurityUtilities;
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.annotation.SelectItems;
import org.operamasks.faces.component.form.impl.UICombo;


@ManagedBean( scope = ManagedBeanScope.REQUEST)
public class PaymentBean extends BasicManagedBean{

	@Bind
	@SelectItems
	private static List goods = new ArrayList();
	
	static {
		SelectItem selectItem = new SelectItem();

		selectItem.setValue("TCL 电视机");
		selectItem.setLabel("TCL 电视机");
		goods.add(selectItem);

		selectItem = new SelectItem();
		selectItem.setValue("长虹 电视机");
		selectItem.setLabel("长虹 电视机");
		goods.add(selectItem);

		selectItem = new SelectItem();
		selectItem.setValue("万和 热水器");
		selectItem.setLabel("万和 热水器");
		goods.add(selectItem);

		selectItem = new SelectItem();
		selectItem.setValue("方太 抽油烟机");
		selectItem.setLabel("方太 抽油烟机");
		goods.add(selectItem);

		selectItem = new SelectItem();
		selectItem.setValue("海尔 洗衣机");
		selectItem.setLabel("海尔 洗衣机");
		goods.add(selectItem);
	}
	
	@Bind
	TradeInfo paymentInfo = new TradeInfo();
	
	@ManagedProperty("#{TradeInfoDAO}")
	TradeInfoDAO tradeInfoDao = null;
	
	@Bind(id = "goodsName")
	UICombo goods_comboBox;

	@Action(immediate = true)
	private void goodsName_onselect() {
		String goodsName = goods_comboBox.getSubmittedValue().toString();
		int unitPrice=1399;
		if ("TCL 电视机".equals(goodsName)){
			unitPrice=1399;
		}else if ("长虹 电视机".equals(goodsName)){
			unitPrice=1378;
		}else if ("万和 热水器".equals(goodsName)){
			unitPrice=899;
		}else if ("方太 抽油烟机".equals(goodsName)){
			unitPrice=2350;
		}else{
			unitPrice=1620;
		}
		paymentInfo.setUnitPrice(unitPrice);
	}

	@Action(immediate = true)
	private void quantity_onchange(){
		paymentInfo.setAmount(paymentInfo.getQuantity()*paymentInfo.getUnitPrice());
	}
	
	@Bind
	private String response;

	@Action
	private void save() {
		User currentUser = SecurityUtilities.getCurrentUser();
		
		// 一、执行业务业务操作，保存业务数据
		tradeInfoDao.save(paymentInfo);

		// 二、开始执行流程操作
		IWorkflowSession workflowSession = workflowRuntimeContext
				.getWorkflowSession();
		try {
			// 1、创建流程实例
			IProcessInstance procInst = workflowSession
					.createProcessInstance("Goods_Deliver_Process",currentUser==null?"":currentUser.getLoginid());
			// 2、设置流程变量/业务属性字段
			procInst.setProcessInstanceVariable("sn", paymentInfo.getSn());// 设置交易顺序号
			procInst.setProcessInstanceVariable("goodsName", paymentInfo
					.getGoodsName());// 货物名称
			procInst.setProcessInstanceVariable("quantity", paymentInfo
					.getQuantity());// 数量
			procInst.setProcessInstanceVariable("mobile", paymentInfo
					.getCustomerMobile());// 客户电话
			procInst.setProcessInstanceVariable("customerName", paymentInfo
					.getCustomerName());

			// 3、启动流程实例,run()方法启动实例并创建第一个环节实例、分派任务
			procInst.run();

			// 4、根据业务情况，收银环节应该立即对该环节的workitem执行complete操作
			/*
			 * 在org.fireflow.example.workflowextension.CurrentUserAssignmentHandler中完成
			 * 该工作更为合理. 2009-05-03
			 */
			/*
			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpSession httpSession = (HttpSession) facesContext
					.getExternalContext().getSession(true);
			User currentUser = (User) httpSession.getAttribute("CURRENT_USER");
			List workitems = workflowSession.findMyTodoWorkItems(currentUser
					.getId(), procInst.getId());
			if (workitems != null && workitems.size() > 0) {
				IWorkItem wi = (IWorkItem) workitems.get(0);
				wi.complete();
			}
			*/

		} catch (EngineException e) {
			response = e.getMessage();
			e.printStackTrace();
		} catch (KernelException e) {
			response = e.getMessage();
			e.printStackTrace();
		}

		paymentInfo = new TradeInfo();

	}
}
