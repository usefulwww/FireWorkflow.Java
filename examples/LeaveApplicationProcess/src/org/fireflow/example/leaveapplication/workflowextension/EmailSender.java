package org.fireflow.example.leaveapplication.workflowextension;

import java.util.Date;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.taskinstance.IApplicationHandler;
import org.fireflow.example.leaveapplication.data.EmailMock;
import org.fireflow.example.leaveapplication.data.EmailMockDAO;
import org.fireflow.example.leaveapplication.data.LeaveApplicationInfo;
import org.fireflow.example.leaveapplication.data.LeaveApplicationInfoDAO;

/**
 * 模拟向邮件系统发送邮件
 * @author app
 *
 */
public class EmailSender implements IApplicationHandler {
	LeaveApplicationInfoDAO leaveApplicationInfoDAO = null;
	EmailMockDAO emailMockDAO = null;
		
	public LeaveApplicationInfoDAO getLeaveApplicationInfoDAO() {
		return leaveApplicationInfoDAO;
	}

	public void setLeaveApplicationInfoDAO(
			LeaveApplicationInfoDAO leaveApplicationInfoDAO) {
		this.leaveApplicationInfoDAO = leaveApplicationInfoDAO;
	}


	public EmailMockDAO getEmailMockDAO() {
		return emailMockDAO;
	}

	public void setEmailMockDAO(EmailMockDAO emailMockDAO) {
		this.emailMockDAO = emailMockDAO;
	}

	@Override
	public void execute(ITaskInstance arg0) {
		IProcessInstance processInstance = ((TaskInstance)arg0).getAliveProcessInstance();
		String sn = (String)processInstance.getProcessInstanceVariable("sn");
		LeaveApplicationInfo leaveApplicationInfo = (LeaveApplicationInfo)leaveApplicationInfoDAO.findBySn(sn);
		Boolean approvalFlag = (Boolean)processInstance.getProcessInstanceVariable("approvalFlag");
		
		EmailMock email = new EmailMock();
		email.setUserId(leaveApplicationInfo.getApplicantId());
		email.setContent("您的请假申请：从"+leaveApplicationInfo.getFromDate()+"到"
				+leaveApplicationInfo.getToDate()+"被"+(approvalFlag?"批准":"拒绝。"));
		email.setCreateTime(new Date());
		emailMockDAO.save(email);
	}

}
