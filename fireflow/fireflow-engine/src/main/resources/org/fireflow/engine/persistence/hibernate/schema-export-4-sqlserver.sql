alter table T_FF_RT_PROCINST_VAR drop constraint FKD79C420D7AF471D8;
alter table T_FF_RT_WORKITEM drop constraint FK4131554DE2527DDC;
drop table T_FF_DF_WORKFLOWDEF;
drop table T_FF_HIST_TRACE;
drop table T_FF_RT_PROCESSINSTANCE;
drop table T_FF_RT_PROCINST_VAR;
drop table T_FF_RT_TASKINSTANCE;
drop table T_FF_RT_TOKEN;
drop table T_FF_RT_WORKITEM;
create table T_FF_DF_WORKFLOWDEF (ID varchar(50) not null, definition_type varchar(50) not null, PROCESS_ID varchar(100) not null, NAME varchar(100) not null, DISPLAY_NAME varchar(128) null, DESCRIPTION varchar(1024) null, VERSION int not null, STATE tinyint not null, UPLOAD_USER varchar(50) null, UPLOAD_TIME datetime null, PUBLISH_USER varchar(50) null, PUBLISH_TIME datetime null, PROCESS_CONTENT text null, primary key (ID));
create table T_FF_HIST_TRACE (ID varchar(50) not null, PROCESSINSTANCE_ID varchar(50) not null, STEP_NUMBER int not null, MINOR_NUMBER int not null, TYPE varchar(15) not null, EDGE_ID varchar(100) null, FROM_NODE_ID varchar(100) not null, TO_NODE_ID varchar(100) not null, primary key (ID));
create table T_FF_RT_PROCESSINSTANCE (ID varchar(50) not null, PROCESS_ID varchar(100) not null, VERSION int not null, NAME varchar(100) null, DISPLAY_NAME varchar(128) null, STATE int not null, SUSPENDED tinyint not null, CREATOR_ID varchar(50) null, CREATED_TIME datetime null, STARTED_TIME datetime null, EXPIRED_TIME datetime null, END_TIME datetime null, PARENT_PROCESSINSTANCE_ID varchar(50) null, PARENT_TASKINSTANCE_ID varchar(50) null, primary key (ID));
create table T_FF_RT_PROCINST_VAR (PROCESSINSTANCE_ID varchar(50) not null, VALUE varchar(255) null, NAME varchar(255) not null, primary key (PROCESSINSTANCE_ID, NAME));
create table T_FF_RT_TASKINSTANCE (ID varchar(50) not null, BIZ_TYPE varchar(250) not null, TASK_ID varchar(300) not null, ACTIVITY_ID varchar(200) not null, NAME varchar(100) not null, DISPLAY_NAME varchar(128) null, STATE int not null, SUSPENDED tinyint not null, TASK_TYPE varchar(10) null, CREATED_TIME datetime not null, STARTED_TIME datetime null, EXPIRED_TIME datetime null, END_TIME datetime null, ASSIGNMENT_STRATEGY varchar(10) null, PROCESSINSTANCE_ID varchar(50) not null, PROCESS_ID varchar(100) not null, VERSION int not null, TARGET_ACTIVITY_ID varchar(100) null, FROM_ACTIVITY_ID varchar(600) null, STEP_NUMBER int not null, CAN_BE_WITHDRAWN tinyint not null, primary key (ID));
create table T_FF_RT_TOKEN (ID varchar(50) not null, ALIVE tinyint not null, VALUE int not null, NODE_ID varchar(200) not null, PROCESSINSTANCE_ID varchar(50) not null, STEP_NUMBER int not null, FROM_ACTIVITY_ID varchar(100) null, primary key (ID));
create table T_FF_RT_WORKITEM (ID varchar(50) not null, STATE int not null, CREATED_TIME datetime not null, CLAIMED_TIME datetime null, END_TIME datetime null, ACTOR_ID varchar(50) null, COMMENTS varchar(1024) null, TASKINSTANCE_ID varchar(50) not null, primary key (ID));
create index IDX_TRACE_PROCINSTID on T_FF_HIST_TRACE (PROCESSINSTANCE_ID);
alter table T_FF_RT_PROCINST_VAR add constraint FKD79C420D7AF471D8 foreign key (PROCESSINSTANCE_ID) references T_FF_RT_PROCESSINSTANCE;
alter table T_FF_RT_WORKITEM add constraint FK4131554DE2527DDC foreign key (TASKINSTANCE_ID) references T_FF_RT_TASKINSTANCE;
alter table T_FF_RT_PROCINST_VAR
   add constraint var_reference_process foreign key (processinstance_id)
      references T_FF_RT_PROCESSINSTANCE (id)
         on update cascade on delete cascade
go
