package org.fireflow.engine.persistence.springjdbc;

import org.fireflow.engine.persistence.IFireWorkflowHelperDao;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class FireWorkflowHelperJdbcDao extends JdbcDaoSupport implements IFireWorkflowHelperDao{
    
	/**
     * 测试之前删除所有表中的数据
     * 不在需要，但是这个类还是需要的，用来获取session
     */
    public void clearAllTables(){
//    	
//    	String sql4DeleteProcInstVar = "delete from t_ff_rt_procinst_var";
//    	this.getJdbcTemplate().update(sql4DeleteProcInstVar);
//    	
//    	String sql4DeleteProcessInstance = "delete from t_ff_rt_processInstance";
//    	this.getJdbcTemplate().update(sql4DeleteProcessInstance);
//    	
//    	String sql4DeleteToken = "delete from t_ff_rt_token ";
//    	getJdbcTemplate().update(sql4DeleteToken);
//    	
//    	String sql4DeleteWorkItem = "delete from t_ff_rt_workitem";
//    	this.getJdbcTemplate().update(sql4DeleteWorkItem);
//    	
//    	String sql4DeleteTaskInstance = "delete from t_ff_rt_taskInstance";
//    	this.getJdbcTemplate().update(sql4DeleteTaskInstance);
//    	
//    	String sql4DeleteTrace = "delete from t_ff_hist_trace";
//    	this.getJdbcTemplate().update(sql4DeleteTrace);
//    	
//    	String sql4DeleteWorkflowDefinition = "delete from t_ff_df_workflowDef";
//    	this.getJdbcTemplate().update(sql4DeleteWorkflowDefinition);
//    	
    }
}
