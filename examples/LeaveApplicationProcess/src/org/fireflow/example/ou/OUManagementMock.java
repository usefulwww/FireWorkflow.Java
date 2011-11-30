package org.fireflow.example.ou;

import java.util.List;
import java.util.Vector;

/**
 * 模拟用户/组织机构管理系统
 * @author chennieyun
 *
 */
public class OUManagementMock {
	private static OUManagementMock ouMgr = null;
	
	private List commonEmployees = new Vector() ;//所有的用户
	private List departmentManagers = new Vector();//部门经理
	private List companyManagers = new Vector();//公司经理
	private List hrClerks = new Vector();//人力资源部职员

	
	private OUManagementMock(){
		init();
	}
	public static OUManagementMock getInstance(){
		if (ouMgr==null){
			ouMgr = new OUManagementMock();
		}
		return ouMgr;
	}
	
	/**
	 * 模拟系统登陆时用户校验
	 * @param id
	 * @param pwd
	 * @return 登陆成功则返回User对象，否则返回null
	 */
	public User checkUser(String id,String pwd){
		for (int i=0;i<this.commonEmployees.size();i++){
			User user = (User)this.commonEmployees.get(i);
			if (user.getId().equals(id.trim()) && user.getPassword().equals(pwd)){
				return user;
			}
		}
		return null;
	}
	
	/**
	 * 通过角色名称查询出该角色的所有用户
	 * @param roleName
	 * @return
	 */
	public List getAllUsersForRole(String roleName){
		if (roleName.equals("DepartmentManager")){
			return departmentManagers;
		}else if (roleName.equals("CompanyManager")){
			return companyManagers;
		}
		else if (roleName.equals("HRClerk")){
			return hrClerks;
		}
		return null;
	}
	
	public List getAllUsers(){
		return commonEmployees;
	}
	
	public User findUserById(String userId){
		for (int i=0;i<this.commonEmployees.size();i++){
			User u = (User)this.commonEmployees.get(i);
			if (u.getId().equals(userId)){
				return u;
			}
		}
		return null;
	}
	
	protected void init(){
		//初始化所有的用户和角色
		//部门1......
		User u = new User();
		u.setId("Zhang");
		u.setName("张三");
		u.setDepartmentCode("dept1");
		u.setDepartmentName("部门1");
		u.setPassword("123456");
		commonEmployees.add(u);
		
		u = new User();
		u.setId("Li");
		u.setName("李四");
		u.setDepartmentCode("dept1");
		u.setDepartmentName("部门1");		
		u.setPassword("123456");
		commonEmployees.add(u);

		u = new User();
		u.setId("DeptManager1");
		u.setName("部门经理1");
		u.setDepartmentCode("dept1");
		u.setDepartmentName("部门1");		
		u.setPassword("123456");
		commonEmployees.add(u);
		departmentManagers.add(u);
		
		//部门2......
		u = new User();
		u.setId("Wang");
		u.setName("王五");
		u.setDepartmentCode("dept2");
		u.setDepartmentName("部门2");
		u.setPassword("123456");
		commonEmployees.add(u);
		
		u = new User();
		u.setId("Zhao");
		u.setName("赵六");
		u.setDepartmentCode("dept2");
		u.setDepartmentName("部门2");		
		u.setPassword("123456");
		commonEmployees.add(u);

		u = new User();
		u.setId("DeptManager2");
		u.setName("部门经理2");
		u.setDepartmentCode("dept2");
		u.setDepartmentName("部门2");		
		u.setPassword("123456");
		commonEmployees.add(u);
		departmentManagers.add(u);		
		
		//人力资源部......
		u = new User();
		u.setId("hr1");
		u.setName("人力资源职员1");
		u.setDepartmentCode("deptHR");
		u.setDepartmentName("人力资源部");
		u.setPassword("123456");
		commonEmployees.add(u);
		hrClerks.add(u);
		
		u = new User();
		u.setId("hr2");
		u.setName("人力资源职员2");
		u.setDepartmentCode("deptHR");
		u.setDepartmentName("人力资源部");		
		u.setPassword("123456");
		commonEmployees.add(u);
		hrClerks.add(u);

		u = new User();
		u.setId("deptManagerHR");
		u.setName("人力资源部张经理");
		u.setDepartmentCode("deptHR");
		u.setDepartmentName("人力资源部");		
		u.setPassword("123456");
		commonEmployees.add(u);
		departmentManagers.add(u);			
		
		//公司经理
		u = new User();
		u.setId("ManagerChen");
		u.setName("陈总经理");
		u.setDepartmentCode("dept0");
		u.setDepartmentName("Fire workflow 公司");		
		u.setPassword("123456");
		commonEmployees.add(u);
		companyManagers.add(u);	
	}
}
