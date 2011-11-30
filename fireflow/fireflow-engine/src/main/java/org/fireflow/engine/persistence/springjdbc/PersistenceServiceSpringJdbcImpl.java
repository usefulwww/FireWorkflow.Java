/**
 * Copyright 2009-2010 wmj2003,hanzaihua All rights reserved. This program is
 * free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation。
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.engine.persistence.springjdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.engine.impl.ProcessInstanceTrace;
import org.fireflow.engine.impl.ProcessInstanceVar;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.impl.WorkItem;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.impl.Token;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;

/**
 * 配置方式如下： <bean id="persistenceService" class=
 * "org.fireflow.engine.persistence.springjdbc.PersistenceServiceSpringJdbcImpl">
 * <br/>
 * <property name="dataSource" ref="dataSource" /><br/>
 * <property name="lobHandler" ref="defaltLobHandler" /><br/>
 * </bean><br/>
 * <bean id="oracleLobHandler"
 * class="org.springframework.jdbc.support.lob.OracleLobHandler"
 * lazy-init="true"><br/>
 * <property name="nativeJdbcExtractor" ref="nativeJdbcExtractor" /><br/>
 * </bean><br/>
 * <bean id="defaltLobHandler"
 * class="org.springframework.jdbc.support.lob.DefaultLobHandler"
 * lazy-init="true"><br/>
 * </bean><br/>
 * @author wmj2003
 */
public class PersistenceServiceSpringJdbcImpl extends JdbcDaoSupport implements IPersistenceService
{
//	private static Log log = LogFactory.getLog(PersistenceServiceSpringJdbcImpl.class);

	protected RuntimeContext rtCtx = null;

	private boolean show_sql = false;

	public void setRuntimeContext(RuntimeContext ctx)
	{
		this.rtCtx = ctx;
	}

	public RuntimeContext getRuntimeContext()
	{
		return this.rtCtx;
	}

	public static java.sql.Timestamp getSqlDateTime(final java.util.Date date)
	{
		if (date == null)
		{
			return null;
		}
		else
		{
			return new java.sql.Timestamp(date.getTime());
		}
	}		

	/**
	 * 流程实例 Save processInstance
	 * @param processInstance
	 */
	public void saveOrUpdateProcessInstance(IProcessInstance processInstance)
	{
		// 首先判断流程实例ID是否为null，如果为null那么create，否则就update
		// 同时还要判断流程实例中的变量值，如果存在就更新如果不存在就插入

		String processInstanceId = null;
		if (processInstance.getId() == null || processInstance.getId().trim().equals(""))
		{
			// 向数据库中插入数据
			StringBuffer sql = new StringBuffer();
			sql.append("INSERT ");
			sql.append("INTO    T_FF_RT_PROCESSINSTANCE ");
			sql.append("        ( ");
			sql.append("                id                       , ");
			sql.append("                process_id               , ");
			sql.append("                version                  , ");
			sql.append("                name                     , ");
			sql.append("                display_name             , ");

			sql.append("                state                    , ");
			sql.append("                suspended                , ");
			sql.append("                creator_id               , ");
			sql.append("                created_time             , ");
			sql.append("                started_time             , ");

			sql.append("                expired_time             , ");
			sql.append("                end_time                 , ");
			sql.append("                parent_processinstance_id, ");
			sql.append("                parent_taskinstance_id     ");
			sql.append("        ) ");
			sql.append("        VALUES ");
			sql.append("        ( ");
			sql.append("                ?, ");
			sql.append("                ?, ");
			sql.append("                ?, ");
			sql.append("                ?, ");
			sql.append("                ?, ");

			sql.append("                ?, ");
			sql.append("                ?, ");
			sql.append("                ?, ");
			sql.append("                ?, ");
			sql.append("                ?, ");

			sql.append("                ?, ");
			sql.append("                ?, ");
			sql.append("                ?, ");
			sql.append("                ? ");
			sql.append("        )");

			if (show_sql)
			{
				System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
			}

			processInstanceId = java.util.UUID.randomUUID().toString().replace("-", "");
			((ProcessInstance) processInstance).setId(processInstanceId); // 给流程实例赋值
			super.getJdbcTemplate().update(
					sql.toString(),
					new Object[] { processInstanceId, processInstance.getProcessId(), processInstance.getVersion(),
							processInstance.getName(), processInstance.getDisplayName(),

							processInstance.getState(), processInstance.isSuspended() == true ? 1 : 0,
							processInstance.getCreatorId(), getSqlDateTime(processInstance.getCreatedTime()),
							getSqlDateTime(processInstance.getStartedTime()),

							getSqlDateTime(processInstance.getExpiredTime()), getSqlDateTime(processInstance.getEndTime()),
							processInstance.getParentProcessInstanceId(), processInstance.getParentTaskInstanceId() },
					new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
							Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP,
							Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR });
		}
		else
		{
			// 更新数据
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE T_FF_RT_PROCESSINSTANCE ");
			sql.append("SET     process_id                = ?, ");
			sql.append("        version                   = ?, ");
			sql.append("        name                      = ?, ");
			sql.append("        display_name              = ?, ");
			sql.append("        state                     = ?, ");

			sql.append("        suspended                 = ?, ");
			sql.append("        creator_id                = ?, ");
			sql.append("        created_time              = ?, ");
			sql.append("        started_time              = ?, ");
			sql.append("        expired_time              = ?, ");

			sql.append("        end_time                  = ?, ");
			sql.append("        parent_processinstance_id = ?, ");
			sql.append("        parent_taskinstance_id    = ?  ");
			sql.append("WHERE   ID                        =?");

			if (show_sql)
			{
				System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
			}

			processInstanceId = processInstance.getId();

			super.getJdbcTemplate().update(
					sql.toString(),
					new Object[] { processInstance.getProcessId(), processInstance.getVersion(),
							processInstance.getName(), processInstance.getDisplayName(), processInstance.getState(),

							processInstance.isSuspended() == true ? 1 : 0, processInstance.getCreatorId(),
							getSqlDateTime(processInstance.getCreatedTime()), getSqlDateTime(processInstance.getStartedTime()),
							getSqlDateTime(processInstance.getExpiredTime()),

							getSqlDateTime(processInstance.getEndTime()), processInstance.getParentProcessInstanceId(),
							processInstance.getParentTaskInstanceId(), processInstance.getId()

					},
					new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER,
							Types.INTEGER, Types.VARCHAR, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP,

							Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR });
		}

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#saveTaskInstance(
	 * org.fireflow.engine.ITaskInstance)
	 */
	public void saveOrUpdateTaskInstance(ITaskInstance taskInstance)
	{

		// TODO wmj2003 taskinstance中的biz_type从何而来？既没有set也没有get，那么只能够不操作这个字段
		// can_be_withdrawn 是不是也从来没有被用到过？
		String taskInstanceId = null;
		if (taskInstance.getId() == null || taskInstance.getId().trim().equals(""))
		{

			StringBuffer sql = new StringBuffer();
			sql.append("INSERT ");
			sql.append("INTO    t_ff_rt_taskinstance ");
			sql.append("        ( ");
			sql.append("                id                 , ");
			sql.append("                biz_type           , ");
			sql.append("                task_id            , ");
			sql.append("                activity_id        , ");
			sql.append("                name               , ");

			sql.append("                display_name       , ");
			sql.append("                state              , ");
			sql.append("                suspended          , ");
			sql.append("                task_type          , ");
			sql.append("                created_time       , ");

			sql.append("                started_time       , ");
			sql.append("                expired_time       , ");
			sql.append("                end_time           , ");
			sql.append("                assignment_strategy, ");
			sql.append("                processinstance_id , ");

			sql.append("                process_id         , ");
			sql.append("                version            , ");
			sql.append("                target_activity_id   , ");
			sql.append("                from_activity_id   , ");
			sql.append("                step_number        , ");

			sql.append("                can_be_withdrawn ");
			sql.append("        ) ");
			sql.append("        VALUES ");
			sql.append("        ( ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,?  )");

			if (show_sql)
			{
				System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
			}

			taskInstanceId = java.util.UUID.randomUUID().toString().replace("-", "");

			((TaskInstance) taskInstance).setId(taskInstanceId);// 给taskinstance设置id
			super.getJdbcTemplate().update(
					sql.toString(),
					new Object[] {
							taskInstanceId,
							taskInstance.getClass().getName(),// TODO
																// biz_type由中TaskInstance.hbm.xml文件中的discriminator属性指定，这里为具体类名
							taskInstance.getTaskId(), taskInstance.getActivityId(), taskInstance.getName(),

							taskInstance.getDisplayName(), taskInstance.getState(),
							taskInstance.isSuspended() == true ? 1 : 0, taskInstance.getTaskType(),
							getSqlDateTime(taskInstance.getCreatedTime()),

							getSqlDateTime(taskInstance.getStartedTime()), getSqlDateTime(taskInstance.getExpiredTime()),
							getSqlDateTime(taskInstance.getEndTime()), taskInstance.getAssignmentStrategy(),
							taskInstance.getProcessInstanceId(),

							taskInstance.getProcessId(), taskInstance.getVersion(), taskInstance.getTargetActivityId(),
							((TaskInstance) taskInstance).getFromActivityId(), taskInstance.getStepNumber(),

							((TaskInstance) taskInstance).getCanBeWithdrawn() == true ? 1 : 0 },
					new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,

					Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.TIMESTAMP,

					Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR,

					Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.INTEGER,

					Types.INTEGER

					});
		}
		else
		{
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE t_ff_rt_taskinstance ");
			sql.append("SET  ");
			sql.append("        biz_type            = ?, ");
			sql.append("        task_id             = ?, ");
			sql.append("        activity_id         = ?, ");
			sql.append("        name                = ?, ");

			sql.append("        display_name        = ?, ");
			sql.append("        state               = ?, ");
			sql.append("        suspended           = ?, ");
			sql.append("        task_type           = ?, ");
			sql.append("        created_time        = ?, ");

			sql.append("        started_time        = ?, ");
			sql.append("       	expired_time        = ?, ");
			sql.append("        end_time            = ?, ");
			sql.append("        assignment_strategy = ?, ");
			sql.append("        processinstance_id  = ?, ");

			sql.append("        process_id          = ?, ");
			sql.append("        version             = ?, ");
			sql.append("        target_activity_id  = ?, ");
			sql.append("        from_activity_id    = ?, ");
			sql.append("        step_number         = ?, ");

			sql.append("        can_be_withdrawn    = ? ");

			sql.append("WHERE   ID                  = ? ");

			if (show_sql)
			{
				System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
			}

			super.getJdbcTemplate().update(
					sql.toString(),
					new Object[] {
							taskInstance.getClass().getName(),// TODO
																// biz_type由中TaskInstance.hbm.xml文件中的discriminator属性指定，这里为具体类名
							taskInstance.getTaskId(), taskInstance.getActivityId(), taskInstance.getName(),

							taskInstance.getDisplayName(), taskInstance.getState(),
							taskInstance.isSuspended() == true ? 1 : 0, taskInstance.getTaskType(),
							getSqlDateTime(taskInstance.getCreatedTime()),

							getSqlDateTime(taskInstance.getStartedTime()), getSqlDateTime(taskInstance.getExpiredTime()),
							getSqlDateTime(taskInstance.getEndTime()), taskInstance.getAssignmentStrategy(),
							taskInstance.getProcessInstanceId(),

							taskInstance.getProcessId(), taskInstance.getVersion(), taskInstance.getTargetActivityId(),
							((TaskInstance) taskInstance).getFromActivityId(), taskInstance.getStepNumber(),

							((TaskInstance) taskInstance).getCanBeWithdrawn() == true ? 1 : 0, taskInstance.getId() },
					new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,

					Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.TIMESTAMP,

					Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR,

					Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.INTEGER,

					Types.INTEGER, Types.VARCHAR

					});
		}

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#saveOrUpdateWorkItem
	 * (org.fireflow.engine.IWorkItem)
	 */
	public void saveOrUpdateWorkItem(IWorkItem workitem)
	{
		String workItemId = null;
		if (workitem.getId() == null || workitem.getId().trim().equals(""))
		{
			StringBuffer sql = new StringBuffer();
			sql.append("INSERT ");
			sql.append("INTO    t_ff_rt_workitem ");
			sql.append("        ( ");
			sql.append("                id          , ");
			sql.append("                state       , ");
			sql.append("                created_time, ");
			sql.append("                claimed_time, ");
			sql.append("                end_time    , ");

			sql.append("                actor_id    , ");
			sql.append("                taskinstance_id, ");
			sql.append("                comments ");
			sql.append("        ) ");
			sql.append("        VALUES ");
			sql.append("        ( ?,?,?,?,? ,?,?,?)");

			if (show_sql)
			{
				System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
			}

			workItemId = java.util.UUID.randomUUID().toString().replace("-", "");
			((WorkItem) workitem).setId(workItemId);
			super.getJdbcTemplate().update(
					sql.toString(),
					new Object[] { workItemId, workitem.getState(), getSqlDateTime(workitem.getCreatedTime()),
							getSqlDateTime(workitem.getClaimedTime()), getSqlDateTime(workitem.getEndTime()),

							workitem.getActorId(), workitem.getTaskInstance().getId(), workitem.getComments() },
					new int[] { Types.VARCHAR, Types.INTEGER, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP,

					Types.VARCHAR, Types.VARCHAR, Types.VARCHAR

					});

		}
		else
		{
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE t_ff_rt_workitem ");
			sql.append("SET     state           = ?, ");
			sql.append("        created_time    = ?, ");
			sql.append("        claimed_time    = ?, ");
			sql.append("        end_time        = ?, ");
			sql.append("        actor_id        = ?, ");
			sql.append("        taskinstance_id = ?, ");
			sql.append("        comments = ? ");
			sql.append("WHERE   ID              = ? ");

			if (show_sql)
			{
				System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
			}

			super.getJdbcTemplate().update(
					sql.toString(),
					new Object[] { workitem.getState(), getSqlDateTime(workitem.getCreatedTime()),
							getSqlDateTime(workitem.getClaimedTime()), getSqlDateTime(workitem.getEndTime()),

							workitem.getActorId(), workitem.getTaskInstance().getId(), workitem.getComments(),
							workitem.getId() }, new int[] { Types.INTEGER, Types.TIMESTAMP, Types.TIMESTAMP, Types.TIMESTAMP,

					Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR

					});
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#saveOrUpdateToken
	 * (org.fireflow.kernel.IToken)
	 */
	public void saveOrUpdateToken(IToken token)
	{
		String tokenId = null;
		if (token.getId() == null || token.getId().trim().equals(""))
		{
			StringBuffer sql = new StringBuffer();
			sql.append("INSERT ");
			sql.append("INTO    t_ff_rt_token ");
			sql.append("        ( ");
			sql.append("                id                , ");
			sql.append("                alive             , ");
			sql.append("                value             , ");
			sql.append("                node_id           , ");
			sql.append("                processinstance_id, ");

			sql.append("                step_number       , ");
			sql.append("                from_activity_id ");
			sql.append("        ) ");
			sql.append("        VALUES ");
			sql.append("        ( ?,?,?,?,? ,?,? )");

			if (show_sql)
			{
				System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
			}

			tokenId = java.util.UUID.randomUUID().toString().replace("-", "");
			((Token) token).setId(tokenId);
			super.getJdbcTemplate().update(
					sql.toString(),
					new Object[] { tokenId, token.isAlive() == true ? 1 : 0, token.getValue(), token.getNodeId(),
							token.getProcessInstanceId(),

							token.getStepNumber(), token.getFromActivityId() },
					new int[] { Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR,

					Types.INTEGER, Types.VARCHAR

					});
		}
		else
		{
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE t_ff_rt_token   ");
			sql.append("SET     alive				= ?, ");
			sql.append("        value   			= ?, ");
			sql.append("        node_id    			= ?, ");
			sql.append("        processinstance_id	= ?, ");
			sql.append("        step_number        	= ?, ");

			sql.append("        from_activity_id 	= ?  ");
			sql.append("WHERE   ID = ? ");

			if (show_sql)
			{
				System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
			}

			super.getJdbcTemplate().update(
					sql.toString(),
					new Object[] {

					token.isAlive() == true ? 1 : 0, token.getValue(), token.getNodeId(), token.getProcessInstanceId(),
							token.getStepNumber(),

							token.getFromActivityId(), token.getId() },
					new int[] { Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.INTEGER,

					Types.VARCHAR, Types.VARCHAR

					});
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#getAliveTokenCountForNode
	 * (java.lang.String, java.lang.String)
	 */
	public Integer getAliveTokenCountForNode(final String processInstanceId, final String nodeId)
	{

		int result = super.getJdbcTemplate().queryForInt(
				"select count(*) from T_FF_RT_TOKEN where alive=1 and processinstance_id=? and node_id =?",
				new Object[] { processInstanceId, nodeId });
		return new Integer(result);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * getCompletedTaskInstanceCountForTask(java.lang.String, java.lang.String)
	 */
	public Integer getCompletedTaskInstanceCountForTask(final String processInstanceId, final String taskId)
	{
		int result = super.getJdbcTemplate().queryForInt(
				"select count(*) from T_FF_RT_TASKINSTANCE where state=" + ITaskInstance.COMPLETED
						+ " and task_id=? and processinstance_id=? ", new Object[] { taskId, processInstanceId });
		return new Integer(result);

	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * getAliveTaskInstanceCountForActivity(java.lang.String, java.lang.String)
	 */
	public Integer getAliveTaskInstanceCountForActivity(final String processInstanceId, final String activityId)
	{
		int result = super.getJdbcTemplate().queryForInt(
				"select count(*) from T_FF_RT_TASKINSTANCE where " + " (state=" + ITaskInstance.INITIALIZED
						+ " or state=" + ITaskInstance.RUNNING + ")" + " and activity_id=? and processinstance_id=? ",
				new Object[] { activityId, processInstanceId });
		return new Integer(result);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findTaskInstancesForProcessInstance(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<ITaskInstance> findTaskInstancesForProcessInstance(final java.lang.String processInstanceId,
			final String activityId)
	{
		String orderBy = " order by created_time ";
		StringBuffer sb = new StringBuffer("");
		sb.append(" select * from t_ff_rt_taskinstance ");
		sb.append(" where processinstance_id=? ");
		if (activityId != null && !activityId.trim().equals(""))
		{
			sb.append("  and activity_id=? ");
			return super.getJdbcTemplate().query(sb.toString()+orderBy, new Object[] { processInstanceId, activityId },
					new TaskInstanceRowMapper());
		}
		else
		{
			return super.getJdbcTemplate().query(sb.toString()+orderBy, new Object[] { processInstanceId },
					new TaskInstanceRowMapper());
		}

	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findTaskInstancesForProcessInstanceByStepNumber(java.lang.String,
	 * java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<ITaskInstance> findTaskInstancesForProcessInstanceByStepNumber(final String processInstanceId,
			final Integer stepNumber)
	{
		String orderBy =  " order by created_time ";
		StringBuffer sb = new StringBuffer("");
		sb.append(" select * from t_ff_rt_taskinstance ");
		sb.append(" where processinstance_id=? ");
		if (stepNumber != null)
		{
			sb.append("  and step_number=? ");
			return super.getJdbcTemplate().query(sb.toString()+orderBy, new Object[] { processInstanceId, stepNumber },
					new TaskInstanceRowMapper());
		}
		else
		{
			return super.getJdbcTemplate().query(sb.toString()+orderBy, new Object[] { processInstanceId },
					new TaskInstanceRowMapper());
		}

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#lockTaskInstance(
	 * java.lang.String)
	 */
	public void lockTaskInstance(String taskInstanceId)
	{
		// 这里使用的是sqlserver 的代码 ,这段代码不要删除！
		 String sql =
		 "select * from t_ff_rt_taskinstance with (updlock, rowlock) where id=? ";
		// 以下是oracle中的语句
//		String sql = "select * from t_ff_rt_taskinstance where id=? for update ";
		super.getJdbcTemplate().queryForObject(sql, new Object[] { taskInstanceId }, new TaskInstanceRowMapper());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#findTokenById(java
	 * .lang.String)
	 */
	public IToken findTokenById(String id)
	{
		String sql = "select * from t_ff_rt_token where id=? ";
		return (IToken) super.getJdbcTemplate().queryForObject(sql, new Object[] { id }, new TokenRowMapper());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#deleteTokensForNodes
	 * (java.lang.String, java.util.List)
	 */
	public void deleteTokensForNodes(final String processInstanceId, final List<String> nodeIdsList)
	{

		super.getJdbcTemplate().batchUpdate("delete from t_ff_rt_token where processinstance_id = ? and node_id=? ",
				new BatchPreparedStatementSetter()
				{
					public void setValues(PreparedStatement ps, int i) throws SQLException
					{
						ps.setString(1, processInstanceId);
						ps.setString(2, nodeIdsList.get(i));
					}

					public int getBatchSize()
					{
						return nodeIdsList.size();
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#deleteTokensForNode
	 * (java.lang.String, java.lang.String)
	 */
	public void deleteTokensForNode(final String processInstanceId, final String nodeId)
	{
		String sql = "delete from t_ff_rt_token where processinstance_id = ? and node_id=? ";
		super.getJdbcTemplate().update(sql, new Object[] { processInstanceId, nodeId });
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#deleteToken(org.fireflow
	 * .kernel.IToken)
	 */
	public void deleteToken(IToken token)
	{
		String sql = "delete from t_ff_rt_token where id=? ";
		super.getJdbcTemplate().update(sql, new Object[] { token.getId() });
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findTokensForProcessInstance(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IToken> findTokensForProcessInstance(final String processInstanceId, final String nodeId)
	{
		String sql = "select * from t_ff_rt_token where processinstance_id=? ";
		if (nodeId != null && !nodeId.trim().equals(""))
		{
			sql += " and node_id=? ";
			return super.getJdbcTemplate().query(sql, new Object[] { processInstanceId, nodeId }, new TokenRowMapper());
		}
		else
		{
			return super.getJdbcTemplate().query(sql, new Object[] { processInstanceId }, new TokenRowMapper());
		}

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#findWorkItemById(
	 * java.lang.String)
	 */
	public IWorkItem findWorkItemById(String id)
	{
		String workItemSql = " select * from t_ff_rt_workitem where id=? ";

		WorkItem workItem = (WorkItem) super.getJdbcTemplate().queryForObject(workItemSql, new Object[] { id },
				new WorkItemRowMapper());
		String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";
		TaskInstance taskInstance = (TaskInstance) super.getJdbcTemplate().queryForObject(taskInstanceSql,
				new Object[] { workItem.getTaskInstanceId() }, new TaskInstanceRowMapper());
		workItem.setTaskInstance(taskInstance);
		return workItem;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#findAliveTaskInstanceById
	 * (java.lang.String)
	 */
	public ITaskInstance findAliveTaskInstanceById(final String id)
	{
		String sql = "select * from t_ff_rt_taskinstance where id=? and  (state=" + ITaskInstance.INITIALIZED
				+ " or state=" + ITaskInstance.RUNNING + " )";
		return (TaskInstance) super.getJdbcTemplate().queryForObject(sql, new Object[] { id },
				new TaskInstanceRowMapper());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#findTaskInstanceById
	 * (java.lang.String)
	 */
	public ITaskInstance findTaskInstanceById(String id)
	{
		String sql = "select * from t_ff_rt_taskinstance where id=? ";
		return (TaskInstance) super.getJdbcTemplate().queryForObject(sql, new Object[] { id },
				new TaskInstanceRowMapper());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#abortTaskInstance
	 * (org.fireflow.engine.impl.TaskInstance)
	 */
	public void abortTaskInstance(final TaskInstance taskInstance)
	{
		String sql = "update t_ff_rt_taskinstance set state=? ,end_time=? where id=? and (state=0 or state=1)";
		super.getJdbcTemplate().update(
				sql,
				new Object[] { ITaskInstance.CANCELED, getSqlDateTime(rtCtx.getCalendarService().getSysDate()),
						taskInstance.getId() });

		// 将与之关联的WorkItem取消掉
		String workItemSql = " update t_ff_rt_workitem set state=" + IWorkItem.CANCELED + ",end_time=?  "
				+ " where taskinstance_id =? ";
		super.getJdbcTemplate().update(workItemSql,
				new Object[] { rtCtx.getCalendarService().getSysDate(), taskInstance.getId() });

		taskInstance.setState(ITaskInstance.CANCELED);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * getAliveWorkItemCountForTaskInstance(java.lang.String)
	 */
	public Integer getAliveWorkItemCountForTaskInstance(final String taskInstanceId)
	{
		String sql = "select count(*) from t_ff_rt_workitem where taskinstance_id=? and (state=0 or state=1 or state=3)";
		return super.getJdbcTemplate().queryForInt(sql, new Object[] { taskInstanceId });
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findCompletedWorkItemsForTaskInstance(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IWorkItem> findCompletedWorkItemsForTaskInstance(final String taskInstanceId)
	{
		String sql = " select * from t_ff_rt_workitem where taskinstance_id=? and state=" + IWorkItem.COMPLETED;

		List<IWorkItem> l = (List<IWorkItem>) super.getJdbcTemplate().query(sql, new Object[] { taskInstanceId },
				new WorkItemRowMapper());
		String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";
		TaskInstance taskInstance = (TaskInstance) super.getJdbcTemplate().queryForObject(taskInstanceSql,
				new Object[] { taskInstanceId }, new TaskInstanceRowMapper());
		if (l == null)
		{
			return null;
		}
		else
		{
			for (IWorkItem workItem : l)
			{
				((WorkItem) workItem).setTaskInstance(taskInstance);
			}
			return l;
		}

	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findWorkItemsForTaskInstance(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IWorkItem> findWorkItemsForTaskInstance(final String taskInstanceId)
	{
		String sql = " select * from t_ff_rt_workitem where taskinstance_id=? ";

		List<IWorkItem> l = (List<IWorkItem>) super.getJdbcTemplate().query(sql, new Object[] { taskInstanceId },
				new WorkItemRowMapper());
		String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";
		TaskInstance taskInstance = (TaskInstance) super.getJdbcTemplate().queryForObject(taskInstanceSql,
				new Object[] { taskInstanceId }, new TaskInstanceRowMapper());
		if (l == null)
		{
			return null;
		}
		else
		{
			for (IWorkItem workItem : l)
			{
				((WorkItem) workItem).setTaskInstance(taskInstance);
			}
			return l;
		}

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#findWorkItemsForTask
	 * (java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IWorkItem> findWorkItemsForTask(final String taskid)
	{
		String sql = " select a.* from t_ff_rt_workitem a,t_ff_rt_taskinstance b where a.taskinstance_id=b.id and b.task_id=? ";
		List<IWorkItem> l = (List<IWorkItem>) super.getJdbcTemplate().query(sql, new Object[] { taskid },
				new WorkItemRowMapper());
		if (l == null)
		{
			return null;
		}
		else
		{
			String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

			for (IWorkItem workItem : l)
			{
				TaskInstance taskInstance = (TaskInstance) super.getJdbcTemplate().queryForObject(taskInstanceSql,
						new Object[] { ((WorkItem) workItem).getTaskInstanceId() }, new TaskInstanceRowMapper());

				((WorkItem) workItem).setTaskInstance(taskInstance);
			}
			return l;
		}
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findProcessInstancesByProcessId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IProcessInstance> findProcessInstancesByProcessId(final String processId)
	{

		String sql = "select * from t_ff_rt_processinstance where process_id=? order by created_time ";
		List<IProcessInstance> l = new ArrayList<IProcessInstance>();
		l = (List<IProcessInstance>) super.getJdbcTemplate().query(sql, new Object[] { processId },
				new ProcessInstanceRowMapper());
		if (l == null || l.size() < 1)
		{
			return null;
		}
		// 变量不需要同时查询出来，在第一次使用的时候查询，2009-11-1，非也
		//
		// for(IProcessInstance iProcessInstance:l){
		// iProcessInstance.setProcessInstanceVariables(getVarMap(iProcessInstance.getId()));
		// }

		return l;

	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findProcessInstancesByProcessIdAndVersion(java.lang.String,
	 * java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<IProcessInstance> findProcessInstancesByProcessIdAndVersion(final String processId,
			final Integer version)
	{

		String sql = "select * from t_ff_rt_processinstance where process_id=? and version=? order by created_time ";
		List<IProcessInstance> l = new ArrayList<IProcessInstance>();
		l = (List<IProcessInstance>) super.getJdbcTemplate().query(sql, new Object[] { processId, version },
				new ProcessInstanceRowMapper());
		if (l == null || l.size() < 1)
		{
			return null;
		}

		// 变量不需要同时查询出来，在第一次使用的时候查询，2009-11-1，非也
		// for(IProcessInstance iProcessInstance:l){
		// iProcessInstance.setProcessInstanceVariables(getVarMap(iProcessInstance.getId()));
		// }

		return l;

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#findProcessInstanceById
	 * (java.lang.String)
	 */
	public IProcessInstance findProcessInstanceById(String id)
	{
		String sql = "select * from t_ff_rt_processinstance where id=?  ";

		IProcessInstance iProcessInstance = (IProcessInstance) super.getJdbcTemplate().queryForObject(sql,
				new Object[] { id }, new ProcessInstanceRowMapper());

		// 变量不需要同时查询出来，在第一次使用的时候查询，2009-11-1，非也
		// iProcessInstance.setProcessInstanceVariables(getVarMap(iProcessInstance.getId()));

		return iProcessInstance;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findAliveProcessInstanceById(java.lang.String)
	 */
	public IProcessInstance findAliveProcessInstanceById(final String id)
	{

		String sql = "select * from t_ff_rt_processinstance where id=? and ( state=" + IProcessInstance.INITIALIZED
				+ " or state=" + IProcessInstance.RUNNING + ")";

		IProcessInstance iProcessInstance = (IProcessInstance) super.getJdbcTemplate().queryForObject(sql,
				new Object[] { id }, new ProcessInstanceRowMapper());

		// 变量不需要同时查询出来，在第一次使用的时候查询，2009-11-1，非也
		// iProcessInstance.setProcessInstanceVariables(getVarMap(iProcessInstance.getId()));
		return iProcessInstance;
	}

	private LobHandler lobHandler; // 用来操作lob大字段

	public LobHandler getLobHandler()
	{
		return lobHandler;
	}

	public void setLobHandler(LobHandler lobHandler)
	{
		this.lobHandler = lobHandler;
	}

	// <bean id="lobHandler"
	// class="org.springframework.jdbc.support.lob.DefaultLobHandler"
	// lazy-init="true"/>

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * saveOrUpdateWorkflowDefinition
	 * (org.fireflow.engine.definition.WorkflowDefinition)
	 */
	public void saveOrUpdateWorkflowDefinition(final WorkflowDefinition workflowDef)
	{

		if (workflowDef.getId() == null || workflowDef.getId().equals(""))
		{
			Integer latestVersion = findTheLatestVersionNumberIgnoreState(workflowDef.getProcessId());
			if (latestVersion != null)
			{
				workflowDef.setVersion(new Integer(latestVersion.intValue() + 1));
			}
			else
			{
				workflowDef.setVersion(new Integer(1));
			}
		}

		// this.getHibernateTemplate().saveOrUpdate(workflowDef);

		if (workflowDef.getId() == null)
		{
			StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO t_ff_df_workflowdef(");
			sql.append("id,definition_type,process_id,name,display_name,");
			sql.append("description,version,state,upload_user,upload_time,");
			sql.append("publish_user,publish_time,process_content )");
			sql.append(" VALUES(?,?,?,?,?, ?,?,?,?,?, ?,?,?)");

			if (show_sql)
			{
				System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
			}

			super.getJdbcTemplate().execute(sql.toString(),
					new AbstractLobCreatingPreparedStatementCallback(this.lobHandler)
					{
						protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException
						{
							ps.setString(1, java.util.UUID.randomUUID().toString().replace("-", ""));
							ps.setString(2, workflowDef.getDefinitionType());
							ps.setString(3, workflowDef.getProcessId());
							ps.setString(4, workflowDef.getName());
							ps.setString(5, workflowDef.getDisplayName());

							ps.setString(6, workflowDef.getDescription());
							ps.setInt(7, workflowDef.getVersion());
							ps.setInt(8, workflowDef.getState() == true ? 1 : 0);
							ps.setString(9, workflowDef.getUploadUser());
							ps.setTimestamp(10, getSqlDateTime(workflowDef.getUploadTime()));

							ps.setString(11, workflowDef.getPublishUser());
							ps.setTimestamp(12, getSqlDateTime(workflowDef.getPublishTime()));
							lobCreator.setClobAsString(ps, 13, workflowDef.getProcessContent());
						}
					});

		}
		else
		{
			StringBuffer sql = new StringBuffer();
			sql.append(" update t_ff_df_workflowdef ");
			sql.append("set definition_type=?,process_id=?,name=?,display_name=?,");
			sql.append("description=?,version=?,state=?,upload_user=?,upload_time=?,");
			sql.append("publish_user=?,publish_time=?,process_content=? ");
			sql.append(" where id=? ");

			if (show_sql)
			{
				System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
			}

			super.getJdbcTemplate().execute(sql.toString(),
					new AbstractLobCreatingPreparedStatementCallback(this.lobHandler)
					{
						protected void setValues(PreparedStatement ps, LobCreator lobCreator) throws SQLException
						{

							ps.setString(1, workflowDef.getDefinitionType());
							ps.setString(2, workflowDef.getProcessId());
							ps.setString(3, workflowDef.getName());
							ps.setString(4, workflowDef.getDisplayName());

							ps.setString(5, workflowDef.getDescription());
							ps.setInt(6, workflowDef.getVersion());
							ps.setInt(7, workflowDef.getState() == true ? 1 : 0);
							ps.setString(8, workflowDef.getUploadUser());
							ps.setTimestamp(9, getSqlDateTime(workflowDef.getUploadTime()));

							ps.setString(10, workflowDef.getPublishUser());
							ps.setTimestamp(11, getSqlDateTime(workflowDef.getPublishTime()));
							lobCreator.setClobAsString(ps, 12, workflowDef.getProcessContent());

							ps.setString(13, workflowDef.getId());
						}
					});
		}

	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findTheLatestVersionNumber(java.lang.String)
	 */
	public Integer findTheLatestVersionNumber(final String processId)
	{
		String sql = " select max(version) from t_ff_df_workflowdef where process_id=? and state=1 ";
		return super.getJdbcTemplate().queryForInt(sql, new Object[] { processId });

	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findTheLatestVersionNumberIgnoreState(java.lang.String)
	 */
	public Integer findTheLatestVersionNumberIgnoreState(final String processId)
	{
		String sql = " select max(version) from t_ff_df_workflowdef where process_id=? ";
		return super.getJdbcTemplate().queryForInt(sql, new Object[] { processId });
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findWorkflowDefinitionById(java.lang.String)
	 */
	public WorkflowDefinition findWorkflowDefinitionById(String id)
	{
		String sql = " select * from t_ff_df_workflowdef where id=? ";

		return (WorkflowDefinition) super.getJdbcTemplate().queryForObject(sql, new Object[] { id }, new RowMapper()
		{
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException
			{

				WorkflowDefinition workFlowDefinition = new WorkflowDefinition();
				workFlowDefinition.setId(rs.getString("id"));
				workFlowDefinition.setDefinitionType(rs.getString("definition_type"));
				workFlowDefinition.setProcessId(rs.getString("process_id"));
				workFlowDefinition.setName(rs.getString("name"));
				workFlowDefinition.setDisplayName(rs.getString("display_name"));

				workFlowDefinition.setDescription(rs.getString("description"));
				workFlowDefinition.setVersion(rs.getInt("version"));
				workFlowDefinition.setState(rs.getInt("state") == 1 ? true : false);
				workFlowDefinition.setUploadUser(rs.getString("upload_user"));
				workFlowDefinition.setUploadTime(rs.getTimestamp("upload_time"));

				workFlowDefinition.setPublishUser(rs.getString("publish_user"));
				workFlowDefinition.setPublishTime(rs.getTimestamp("publish_time"));
				// 读取blob大字段
				workFlowDefinition.setProcessContent(lobHandler.getClobAsString(rs, "process_content"));
				return workFlowDefinition;
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findWorkflowDefinitionByProcessIdAndVersionNumber(java.lang.String, int)
	 */
	public WorkflowDefinition findWorkflowDefinitionByProcessIdAndVersionNumber(final String processId,
			final int version)
	{

		String sql = " select * from t_ff_df_workflowdef where process_id=? and version=? ";

		if (show_sql)
		{
			System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
		}

		return (WorkflowDefinition) super.getJdbcTemplate().queryForObject(sql, new Object[] { processId, version },
				new RowMapper()
				{
					public Object mapRow(ResultSet rs, int rowNum) throws SQLException
					{

						WorkflowDefinition workFlowDefinition = new WorkflowDefinition();
						workFlowDefinition.setId(rs.getString("id"));
						workFlowDefinition.setDefinitionType(rs.getString("definition_type"));
						workFlowDefinition.setProcessId(rs.getString("process_id"));
						workFlowDefinition.setName(rs.getString("name"));
						workFlowDefinition.setDisplayName(rs.getString("display_name"));

						workFlowDefinition.setDescription(rs.getString("description"));
						workFlowDefinition.setVersion(rs.getInt("version"));
						workFlowDefinition.setState(rs.getInt("state") == 1 ? true : false);
						workFlowDefinition.setUploadUser(rs.getString("upload_user"));
						workFlowDefinition.setUploadTime(rs.getTimestamp("upload_time"));

						workFlowDefinition.setPublishUser(rs.getString("publish_user"));
						workFlowDefinition.setPublishTime(rs.getTimestamp("publish_time"));
						// 读取blob大字段
						workFlowDefinition.setProcessContent(lobHandler.getClobAsString(rs, "process_content"));
						return workFlowDefinition;
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findTheLatestVersionOfWorkflowDefinitionByProcessId(java.lang.String)
	 */
	public WorkflowDefinition findTheLatestVersionOfWorkflowDefinitionByProcessId(String processId)
	{
		Integer latestVersion = this.findTheLatestVersionNumber(processId);
		return this.findWorkflowDefinitionByProcessIdAndVersionNumber(processId, latestVersion);
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findWorkflowDefinitionsByProcessId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<WorkflowDefinition> findWorkflowDefinitionsByProcessId(final String processId)
	{
		String sql = " select * from t_ff_df_workflowdef where process_id=?  ";

		if (show_sql)
		{
			System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
		}

		return super.getJdbcTemplate().query(sql, new Object[] { processId }, new RowMapper()
		{
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException
			{

				WorkflowDefinition workFlowDefinition = new WorkflowDefinition();
				workFlowDefinition.setId(rs.getString("id"));
				workFlowDefinition.setDefinitionType(rs.getString("definition_type"));
				workFlowDefinition.setProcessId(rs.getString("process_id"));
				workFlowDefinition.setName(rs.getString("name"));
				workFlowDefinition.setDisplayName(rs.getString("display_name"));

				workFlowDefinition.setDescription(rs.getString("description"));
				workFlowDefinition.setVersion(rs.getInt("version"));
				workFlowDefinition.setState(rs.getInt("state") == 1 ? true : false);
				workFlowDefinition.setUploadUser(rs.getString("upload_user"));
				workFlowDefinition.setUploadTime(rs.getTimestamp("upload_time"));

				workFlowDefinition.setPublishUser(rs.getString("publish_user"));
				workFlowDefinition.setPublishTime(rs.getTimestamp("publish_time"));
				// 读取blob大字段
				workFlowDefinition.setProcessContent(lobHandler.getClobAsString(rs, "process_content"));
				return workFlowDefinition;
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * findAllTheLatestVersionsOfWorkflowDefinition()
	 */
	@SuppressWarnings("unchecked")
	public List<WorkflowDefinition> findAllTheLatestVersionsOfWorkflowDefinition()
	{

		String sql = " select distinct process_id from t_ff_df_workflowdef ";
		List<String> l = super.getJdbcTemplate().queryForList(sql);
		if (l == null || l.size() < 1)
		{
			return null;
		}
		List<WorkflowDefinition> _result = new ArrayList<WorkflowDefinition>();
		for (String str : l)
		{
			WorkflowDefinition wfDef = findTheLatestVersionOfWorkflowDefinitionByProcessId(str);
			_result.add(wfDef);
		}
		return _result;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#findTodoWorkItems
	 * (java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IWorkItem> findTodoWorkItems(final String actorId)
	{
		if (actorId == null || actorId.trim().equals(""))
		{
			throw new NullPointerException("工单操作员（actorId）不能为空！");
		}

		String workItemSql = " select * from t_ff_rt_workitem where  (state=" + IWorkItem.INITIALIZED + " or state="
				+ IWorkItem.RUNNING + " ) and actor_id=?  ";

		List<IWorkItem> l = (List<IWorkItem>) super.getJdbcTemplate().query(workItemSql, new Object[] { actorId },
				new WorkItemRowMapper());
		if (l == null)
		{
			return null;
		}
		else
		{
			String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

			for (IWorkItem workItem : l)
			{
				TaskInstance taskInstance = (TaskInstance) super.getJdbcTemplate().queryForObject(taskInstanceSql,
						new Object[] { ((WorkItem) workItem).getTaskInstanceId() }, new TaskInstanceRowMapper());

				((WorkItem) workItem).setTaskInstance(taskInstance);
			}
			return l;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#findTodoWorkItems
	 * (java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IWorkItem> findTodoWorkItems(final String actorId, final String processInstanceId)
	{

		if (actorId == null || actorId.trim().equals(""))
		{
			throw new NullPointerException("工单操作员（actorId）不能为空！");
		}
		if (processInstanceId == null || processInstanceId.trim().equals(""))
		{
			throw new NullPointerException("流程实例ID（processInstanceId）不能为空！");
		}

		String workItemSql = " select a.* from t_ff_rt_workitem a,t_ff_rt_taskinstance b where a.taskinstance_id=b.id and (a.state="
				+ IWorkItem.INITIALIZED
				+ " or a.state="
				+ IWorkItem.RUNNING
				+ " ) and actor_id=? and processinstance_id=?  ";

		List<IWorkItem> l = (List<IWorkItem>) super.getJdbcTemplate().query(workItemSql,
				new Object[] { actorId, processInstanceId }, new WorkItemRowMapper());
		if (l == null)
		{
			return null;
		}
		else
		{
			String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

			for (IWorkItem workItem : l)
			{
				TaskInstance taskInstance = (TaskInstance) super.getJdbcTemplate().queryForObject(taskInstanceSql,
						new Object[] { ((WorkItem) workItem).getTaskInstanceId() }, new TaskInstanceRowMapper());

				((WorkItem) workItem).setTaskInstance(taskInstance);
			}
			return l;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#findTodoWorkItems
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IWorkItem> findTodoWorkItems(final String actorId, final String processId, final String taskId)
	{

		if (actorId == null || actorId.trim().equals(""))
		{
			throw new NullPointerException("工单操作员（actorId）不能为空！");
		}
		if (processId == null || processId.trim().equals(""))
		{
			throw new NullPointerException("流程ID（processId）不能为空！");
		}
		if (taskId == null || taskId.trim().equals(""))
		{
			throw new NullPointerException("任务ID（taskId）不能为空！");
		}

		String workItemSql = " select a.* from t_ff_rt_workitem a,t_ff_rt_taskinstance b where a.taskinstance_id=b.id and (a.state="
				+ IWorkItem.INITIALIZED
				+ " or a.state="
				+ IWorkItem.RUNNING
				+ " ) and actor_id=? and process_id=? and task_id=?  ";

		List<IWorkItem> l = (List<IWorkItem>) super.getJdbcTemplate().query(workItemSql,
				new Object[] { actorId, processId, taskId }, new WorkItemRowMapper());
		if (l == null)
		{
			return null;
		}
		else
		{
			String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

			for (IWorkItem workItem : l)
			{
				TaskInstance taskInstance = (TaskInstance) super.getJdbcTemplate().queryForObject(taskInstanceSql,
						new Object[] { ((WorkItem) workItem).getTaskInstanceId() }, new TaskInstanceRowMapper());

				((WorkItem) workItem).setTaskInstance(taskInstance);
			}
			return l;
		}

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#findHaveDoneWorkItems
	 * (java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IWorkItem> findHaveDoneWorkItems(final String actorId)
	{
		if (actorId == null || actorId.trim().equals(""))
		{
			throw new NullPointerException("工单操作员（actorId）不能为空！");
		}

		String workItemSql = " select * from t_ff_rt_workitem where (state=" + IWorkItem.COMPLETED + " or state="
				+ IWorkItem.CANCELED + " ) and actor_id=? ";

		List<IWorkItem> l = (List<IWorkItem>) super.getJdbcTemplate().query(workItemSql, new Object[] { actorId },
				new int[] { Types.VARCHAR }, new WorkItemRowMapper());
		if (l == null)
		{
			return null;
		}
		else
		{
			String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

			for (IWorkItem workItem : l)
			{
				TaskInstance taskInstance = (TaskInstance) super.getJdbcTemplate().queryForObject(taskInstanceSql,
						new Object[] { ((WorkItem) workItem).getTaskInstanceId() }, new TaskInstanceRowMapper());

				((WorkItem) workItem).setTaskInstance(taskInstance);
			}
			return l;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#findHaveDoneWorkItems
	 * (java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IWorkItem> findHaveDoneWorkItems(final String actorId, final String processInstanceId)
	{

		if (actorId == null || actorId.trim().equals(""))
		{
			throw new NullPointerException("工单操作员（actorId）不能为空！");
		}
		if (processInstanceId == null || processInstanceId.trim().equals(""))
		{
			throw new NullPointerException("流程实例ID（processInstanceId）不能为空！");
		}

		String workItemSql = " select a.* from t_ff_rt_workitem a,t_ff_rt_taskinstance b where a.taskinstance_id=b.id and (a.state="
				+ IWorkItem.COMPLETED
				+ " or a.state="
				+ IWorkItem.CANCELED
				+ " ) and actor_id=? and processinstance_id=?  ";

		List<IWorkItem> l = (List<IWorkItem>) super.getJdbcTemplate().query(workItemSql,
				new Object[] { actorId, processInstanceId }, new WorkItemRowMapper());
		if (l == null)
		{
			return null;
		}
		else
		{
			String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

			for (IWorkItem workItem : l)
			{
				TaskInstance taskInstance = (TaskInstance) super.getJdbcTemplate().queryForObject(taskInstanceSql,
						new Object[] { ((WorkItem) workItem).getTaskInstanceId() }, new TaskInstanceRowMapper());

				((WorkItem) workItem).setTaskInstance(taskInstance);
			}
			return l;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#findHaveDoneWorkItems
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IWorkItem> findHaveDoneWorkItems(final String actorId, final String processId, final String taskId)
	{

		if (actorId == null || actorId.trim().equals(""))
		{
			throw new NullPointerException("工单操作员（actorId）不能为空！");
		}
		if (processId == null || processId.trim().equals(""))
		{
			throw new NullPointerException("流程ID（processId）不能为空！");
		}
		if (taskId == null || taskId.trim().equals(""))
		{
			throw new NullPointerException("任务ID（taskId）不能为空！");
		}

		String workItemSql = " select a.* from t_ff_rt_workitem a,t_ff_rt_taskinstance b where a.taskinstance_id=b.id and (a.state="
				+ IWorkItem.COMPLETED
				+ " or a.state="
				+ IWorkItem.CANCELED
				+ " ) and actor_id=? and process_id=? and task_id=?  ";

		List<IWorkItem> l = (List<IWorkItem>) super.getJdbcTemplate().query(workItemSql,
				new Object[] { actorId, processId, taskId }, new WorkItemRowMapper());
		if (l == null)
		{
			return null;
		}
		else
		{
			String taskInstanceSql = "select * from t_ff_rt_taskinstance where id=? ";

			for (IWorkItem workItem : l)
			{
				TaskInstance taskInstance = (TaskInstance) super.getJdbcTemplate().queryForObject(taskInstanceSql,
						new Object[] { ((WorkItem) workItem).getTaskInstanceId() }, new TaskInstanceRowMapper());

				((WorkItem) workItem).setTaskInstance(taskInstance);
			}
			return l;
		}
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * deleteWorkItemsInInitializedState(java.lang.String)
	 */
	public void deleteWorkItemsInInitializedState(final String taskInstanceId)
	{
		String sql = " delete from t_ff_rt_workitem where taskinstance_id=? and  state=" + IWorkItem.INITIALIZED;
		super.getJdbcTemplate().update(sql, new Object[] { taskInstanceId }, new int[] { Types.VARCHAR });
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * getAliveProcessInstanceCountForParentTaskInstance(java.lang.String)
	 */
	public Integer getAliveProcessInstanceCountForParentTaskInstance(final String taskInstanceId)
	{
		String sql = " select count(*) from t_ff_rt_processinstance where parent_taskinstance_id=? and" + " (state="
				+ IProcessInstance.INITIALIZED + " or state=" + IProcessInstance.RUNNING + ")";
		return super.getJdbcTemplate().queryForInt(sql, new Object[] { taskInstanceId });
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#suspendProcessInstance
	 * (org.fireflow.engine.impl.ProcessInstance)
	 */
	public void suspendProcessInstance(final ProcessInstance processInstance)
	{
		String sql = " update t_ff_rt_processinstance set suspended=1 where id=? ";
		super.getJdbcTemplate().update(sql, new Object[] { processInstance.getId() });

		// 挂起对应的TaskInstance
		String sql2 = " update t_ff_rt_taskinstance set suspended=1 where processinstance_id=? ";
		super.getJdbcTemplate().update(sql2, new Object[] { processInstance.getId() });

		processInstance.setSuspended(true);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#restoreProcessInstance
	 * (org.fireflow.engine.impl.ProcessInstance)
	 */
	public void restoreProcessInstance(final ProcessInstance processInstance)
	{
		String sql = " update t_ff_rt_processinstance set suspended=0 where id=? ";
		super.getJdbcTemplate().update(sql, new Object[] { processInstance.getId() });

		// 恢复对应的TaskInstance
		String sql2 = " update t_ff_rt_taskinstance set suspended=0 where processinstance_id=? ";
		super.getJdbcTemplate().update(sql2, new Object[] { processInstance.getId() });

		processInstance.setSuspended(false);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#abortProcessInstance
	 * (org.fireflow.engine.impl.ProcessInstance)
	 */
	public void abortProcessInstance(final ProcessInstance processInstance)
	{
		// 更新流程状态，设置为canceled
		Date now = rtCtx.getCalendarService().getSysDate();
		String processSql = " update t_ff_rt_processinstance set state=" + IProcessInstance.CANCELED
				+ ",end_time=? where id=? ";
		super.getJdbcTemplate().update(processSql, new Object[] { getSqlDateTime(now), processInstance.getId() });

		// 更新所有的任务实例状态为canceled
		String taskSql = " update t_ff_rt_taskinstance set state=" + ITaskInstance.CANCELED
				+ ",end_time=?,can_be_withdrawn=0 " + "  where processinstance_id=? and (state=0 or state=1)";
		super.getJdbcTemplate().update(taskSql, new Object[] { getSqlDateTime(now), processInstance.getId() });
		// 更新所有工作项的状态为canceled
		String workItemSql = " update t_ff_rt_workitem set state="
				+ IWorkItem.CANCELED
				+ ",end_time=?  "
				+ " where taskinstance_id in (select a.id  from t_ff_rt_taskinstance a,t_ff_rt_workitem b where a.id=b.taskinstance_id and a.processinstance_id=? ) and (state=0 or state=1) ";
		super.getJdbcTemplate().update(workItemSql, new Object[] { getSqlDateTime(now), processInstance.getId() });
		// 删除所有的token
		String tokenSql = " delete from t_ff_rt_token where processinstance_id=?  ";
		super.getJdbcTemplate().update(tokenSql, new Object[] { processInstance.getId() });

		// 数据库操作成功后，更新对象的状态
		processInstance.setState(IProcessInstance.CANCELED);

	}

	/*
	 * (non-Javadoc)
	 * @seeorg.fireflow.engine.persistence.IPersistenceService#
	 * saveOrUpdateProcessInstanceTrace
	 * (org.fireflow.engine.impl.ProcessInstanceTrace)
	 */
	public void saveOrUpdateProcessInstanceTrace(ProcessInstanceTrace processInstanceTrace)
	{
		String processInstanceTraceId = null;
		if (processInstanceTrace.getId() == null)
		{
			StringBuffer sql = new StringBuffer();
			sql.append("INSERT ");
			sql.append("INTO    t_ff_hist_trace ");
			sql.append("        ( ");
			sql.append("                id                , ");
			sql.append("                processinstance_id, ");
			sql.append("                step_number       , ");
			sql.append("                minor_number      , ");
			sql.append("                type              , ");

			sql.append("                edge_id           , ");
			sql.append("                from_node_id      , ");
			sql.append("                to_node_id ");
			sql.append("        ) ");
			sql.append("        VALUES ");
			sql.append("        ( ");
			sql.append("                ? , ");
			sql.append("                ? , ");
			sql.append("                ? , ");
			sql.append("                ? , ");
			sql.append("                ? , ");

			sql.append("                ? , ");
			sql.append("                ? , ");
			sql.append("                ? ");
			sql.append("        )");

			processInstanceTraceId = java.util.UUID.randomUUID().toString().replace("-", "");
			processInstanceTrace.setId(processInstanceTraceId);
			super.getJdbcTemplate().update(
					sql.toString(),
					new Object[] { processInstanceTraceId, processInstanceTrace.getProcessInstanceId(),
							processInstanceTrace.getStepNumber(), processInstanceTrace.getMinorNumber(),
							processInstanceTrace.getType(),

							processInstanceTrace.getEdgeId(), processInstanceTrace.getFromNodeId(),
							processInstanceTrace.getToNodeId() },
					new int[] { Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.VARCHAR,

					Types.VARCHAR, Types.VARCHAR, Types.VARCHAR

					});
		}
		else
		{
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE t_ff_hist_trace ");
			sql.append("SET     processinstance_id = ?, ");
			sql.append("        step_number        = ?, ");
			sql.append("        minor_number       = ?, ");
			sql.append("        type               = ?, ");
			sql.append("        edge_id            = ?, ");

			sql.append("        from_node_id       = ?, ");
			sql.append("        to_node_id         = ?  ");

			sql.append("WHERE   ID                 = ?  ");

			super.getJdbcTemplate().update(
					sql.toString(),
					new Object[] {

					processInstanceTrace.getProcessInstanceId(), processInstanceTrace.getStepNumber(),
							processInstanceTrace.getMinorNumber(), processInstanceTrace.getType(),
							processInstanceTrace.getEdgeId(),

							processInstanceTrace.getFromNodeId(), processInstanceTrace.getToNodeId(),
							processInstanceTrace.getId()

					}, new int[] { Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR,

					Types.VARCHAR, Types.VARCHAR, Types.VARCHAR

					});
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.fireflow.engine.persistence.IPersistenceService#findProcessInstanceTraces
	 * (java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<ProcessInstanceTrace> findProcessInstanceTraces(final String processInstanceId)
	{
		String sql = " select * from t_ff_hist_trace where processinstance_id=? order by step_number,minor_number ";

		return super.getJdbcTemplate().query(sql, new Object[] { processInstanceId },
				new ProcessInstanceTraceRowMapper());
	}

	@SuppressWarnings("unchecked")
	public List<ProcessInstanceVar> findProcessInstanceVariable(final String processInstanceId)
	{

		String varSql = "select * from t_ff_rt_procinst_var where processinstance_id=? ";
		List<ProcessInstanceVar> list = (List<ProcessInstanceVar>) super.getJdbcTemplate().query(varSql,
				new Object[] { processInstanceId }, new ProcessInstanceVarRowMapper());

		return list;
	}

	@SuppressWarnings("unchecked")
	public ProcessInstanceVar findProcessInstanceVariable(String processInstanceId, String name)
	{
		String varSql = "select * from t_ff_rt_procinst_var where processinstance_id=? and name=?";
		List<ProcessInstanceVar> list = (List<ProcessInstanceVar>) super.getJdbcTemplate().query(varSql,
				new Object[] { processInstanceId, name }, new ProcessInstanceVarRowMapper());

		if (list == null || list.size() == 0)
		{
			return null;
		}
		else
		{
			return list.get(0);
		}
	}

	public void updateProcessInstanceVariable(ProcessInstanceVar var)
	{
		String sql = "update t_ff_rt_procinst_var set value=? where processinstance_id=? and name=? ";
		if (show_sql)
		{
			System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
		}
		super.getJdbcTemplate().update(sql,
				new Object[] { object2String(var.getValue()), var.getProcessInstanceId(), var.getName() });
	}

	public void saveProcessInstanceVariable(ProcessInstanceVar var)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT ");
		sql.append("INTO    t_ff_rt_procinst_var ");
		sql.append("        ( ");
		sql.append("                processInstance_id                , ");
		sql.append("                name             , ");
		sql.append("                value             ");
		sql.append("        ) ");
		sql.append("        VALUES ");
		sql.append("        ( ?,?,? )");

		if (show_sql)
		{
			System.out.println("FIREWORKFLOW_JDCB:" + sql.toString());
		}

		super.getJdbcTemplate().update(sql.toString(),
				new Object[] { var.getProcessInstanceId(), var.getName(), object2String(var.getValue()) },
				new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR

				});
	}

	private String object2String(Object arg1)
	{
		if (arg1 == null)
		{
			return null;
		}
		String type = arg1.getClass().getName();
		if (type.equals(String.class.getName()))
		{
			return type + "#" + arg1;
		}
		else if (type.equals(Integer.class.getName()) || type.equals(Long.class.getName()))
		{
			return type + "#" + arg1;
		}
		else if (type.equals(Float.class.getName()) || type.equals(Double.class.getName()))
		{
			return type + "#" + arg1;
		}
		else if (type.equals(Boolean.class.getName()))
		{
			return type + "#" + arg1;
		}
		else if (type.equals(java.util.Date.class.getName()))
		{
			return type + "#" + ((Date) arg1).getTime();
		}
		else
		{
			throw new RuntimeException("Fireflow不支持数据类型" + type);
		}
	}

	public boolean isShow_sql()
	{
		return show_sql;
	}

	public void setShow_sql(boolean show_sql)
	{
		this.show_sql = show_sql;
	}

	public List<IWorkItem> findHaveDoneWorkItems(String actorId, String publishUser, int pageSize, int pageNumber)
			throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<IProcessInstance> findProcessInstanceListByCreatorId(String creatorId, String publishUser,
			int pageSize, int pageNumber) throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<IProcessInstance> findProcessInstanceListByPublishUser(String publishUser, int pageSize, int pageNumber)
			throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<IWorkItem> findTodoWorkItems(String actorId, String publishUser, int pageSize, int pageNumber)
			throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getHaveDoneWorkItemsCount(String actorId, String publishUser) throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getProcessInstanceCountByCreatorId(String creatorId, String publishUser) throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getProcessInstanceCountByPublishUser(String publishUser) throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getTodoWorkItemsCount(String actorId, String publishUser) throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

}