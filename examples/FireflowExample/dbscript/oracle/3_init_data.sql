prompt PL/SQL Developer import file
prompt Created on 2009年5月15日 by app
set feedback off
set define off
prompt Disabling triggers for T_SECU_DEPARTMENT...
alter table T_SECU_DEPARTMENT disable all triggers;
prompt Disabling triggers for T_SECU_RESOURCE...
alter table T_SECU_RESOURCE disable all triggers;
prompt Disabling triggers for T_SECU_ROLE...
alter table T_SECU_ROLE disable all triggers;
prompt Disabling triggers for T_SECU_ROLE_RESOURCE...
alter table T_SECU_ROLE_RESOURCE disable all triggers;
prompt Disabling triggers for T_SECU_USER...
alter table T_SECU_USER disable all triggers;
prompt Disabling triggers for T_SECU_USER_ROLE...
alter table T_SECU_USER_ROLE disable all triggers;
prompt Deleting T_SECU_USER_ROLE...
delete from T_SECU_USER_ROLE;
commit;
prompt Deleting T_SECU_USER...
delete from T_SECU_USER;
commit;
prompt Deleting T_SECU_ROLE_RESOURCE...
delete from T_SECU_ROLE_RESOURCE;
commit;
prompt Deleting T_SECU_ROLE...
delete from T_SECU_ROLE;
commit;
prompt Deleting T_SECU_RESOURCE...
delete from T_SECU_RESOURCE;
commit;
prompt Deleting T_SECU_DEPARTMENT...
delete from T_SECU_DEPARTMENT;
commit;
prompt Loading T_SECU_DEPARTMENT...
insert into T_SECU_DEPARTMENT (ID, CODE, NAME, PARENT_CODE, DESCRIPTION)
values ('A_SHOPPING_CENTER', 'A_SHOPPING_CENTER', '某商场', 'FireWorkflowExample', '某商场');
insert into T_SECU_DEPARTMENT (ID, CODE, NAME, PARENT_CODE, DESCRIPTION)
values ('A_BANK', 'A_BANK', '某银行', 'FireWorkflowExample', '某银行');
insert into T_SECU_DEPARTMENT (ID, CODE, NAME, PARENT_CODE, DESCRIPTION)
values ('FireWorkflowExample', 'FireWorkflowExample', 'FireWorkflowExample', 'FireWorkflowExample', 'FireWorkflowExample');
commit;
prompt 3 records loaded
prompt Loading T_SECU_RESOURCE...
prompt Table is empty
prompt Loading T_SECU_ROLE...
insert into T_SECU_ROLE (ID, CODE, NAME, DESCRIPTION, DEPARTMENT_CODE, DEPARTMENT_NAME)
values ('Cashier', 'Cashier', '收银岗', null, 'A_SHOPPING_CENTER', '某商场');
insert into T_SECU_ROLE (ID, CODE, NAME, DESCRIPTION, DEPARTMENT_CODE, DEPARTMENT_NAME)
values ('' || chr(9) || 'WarehouseKeeper', '' || chr(9) || 'WarehouseKeeper', '仓管岗', null, 'A_SHOPPING_CENTER', '某商场');
insert into T_SECU_ROLE (ID, CODE, NAME, DESCRIPTION, DEPARTMENT_CODE, DEPARTMENT_NAME)
values ('' || chr(9) || 'Deliveryman', '' || chr(9) || 'Deliveryman', '送货岗', null, 'A_SHOPPING_CENTER', '某商场');
insert into T_SECU_ROLE (ID, CODE, NAME, DESCRIPTION, DEPARTMENT_CODE, DEPARTMENT_NAME)
values ('Manager', 'Manager', '系统管理员', null, 'FireWorkflowExample', 'FireWorkflowExample');
insert into T_SECU_ROLE (ID, CODE, NAME, DESCRIPTION, DEPARTMENT_CODE, DEPARTMENT_NAME)
values ('Loanteller', 'Loanteller', '信贷员', null, 'A_BANK', '某银行');
insert into T_SECU_ROLE (ID, CODE, NAME, DESCRIPTION, DEPARTMENT_CODE, DEPARTMENT_NAME)
values ('RiskEvaluator', 'RiskEvaluator', '风险核查员', null, 'A_BANK', '某银行');
insert into T_SECU_ROLE (ID, CODE, NAME, DESCRIPTION, DEPARTMENT_CODE, DEPARTMENT_NAME)
values ('Approver', 'Approver', '贷款审批员', null, 'A_BANK', '某银行');
insert into T_SECU_ROLE (ID, CODE, NAME, DESCRIPTION, DEPARTMENT_CODE, DEPARTMENT_NAME)
values ('LendMoneyOfficer', 'LendMoneyOfficer', '放款操作员', null, 'A_BANK', '某银行');
commit;
prompt 8 records loaded
prompt Loading T_SECU_ROLE_RESOURCE...
prompt Table is empty
prompt Loading T_SECU_USER...
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('scott2', 'scott2', 'e10adc3949ba59abbe56e057f20f883e', 'scott2', 0, 0, null, null, null, '1', 'test', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('scott', 'scott', 'e10adc3949ba59abbe56e057f20f883e', 'scott', 0, 0, null, null, null, '1', 'test', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('Cashier1', 'Cashier1', 'e10adc3949ba59abbe56e057f20f883e', '收银员1', 0, 0, null, null, null, 'A_SHOPPING_CENTER', '某商场', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('WarehouseKeeper1', 'WarehouseKeeper1', 'e10adc3949ba59abbe56e057f20f883e', '仓管员1', 0, 0, null, null, null, 'A_SHOPPING_CENTER', '某商场', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('Cashier2', 'Cashier2', 'e10adc3949ba59abbe56e057f20f883e', '收银员2', 0, 0, null, null, null, 'A_SHOPPING_CENTER', '某商场', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('Deliveryman1', 'Deliveryman1', 'e10adc3949ba59abbe56e057f20f883e', '送货员1', 0, 0, null, null, null, 'A_SHOPPING_CENTER', '某商场', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('WarehouseKeeper2', 'WarehouseKeeper2', 'e10adc3949ba59abbe56e057f20f883e', '仓管员2', 0, 0, null, null, null, 'A_SHOPPING_CENTER', '某商场', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('Deliveryman2', 'Deliveryman2', 'e10adc3949ba59abbe56e057f20f883e', '送货员2', 0, 0, null, null, null, 'A_SHOPPING_CENTER', '某商场', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('Deliveryman3', 'Deliveryman3', 'e10adc3949ba59abbe56e057f20f883e', '送货员3', 0, 0, null, null, null, 'A_SHOPPING_CENTER', '某商场', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('Loanteller1', 'Loanteller1', 'e10adc3949ba59abbe56e057f20f883e', '信贷员1', 0, 0, null, null, null, 'A_BANK', '某银行', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('Loanteller2', 'Loanteller2', 'e10adc3949ba59abbe56e057f20f883e', '信贷员2', 0, 0, null, null, null, 'A_BANK', '某银行', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('RiskEvaluator1', 'RiskEvaluator1', 'e10adc3949ba59abbe56e057f20f883e', '风险审核员1', 0, 0, null, null, null, 'A_BANK', '某银行', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('RiskEvaluator2', 'RiskEvaluator2', 'e10adc3949ba59abbe56e057f20f883e', '风险审核员2', 0, 0, null, null, null, 'A_BANK', '某银行', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('Approver1', 'Approver1', 'e10adc3949ba59abbe56e057f20f883e', '贷款审批员1', 0, 0, null, null, null, 'A_BANK', '某银行', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('Approver2', 'Approver2', 'e10adc3949ba59abbe56e057f20f883e', '贷款审批员2', 0, 0, null, null, null, 'A_BANK', '某银行', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('Approver3', 'Approver3', 'e10adc3949ba59abbe56e057f20f883e', '贷款审批员3', 0, 0, null, null, null, 'A_BANK', '某银行', null);
insert into T_SECU_USER (ID, LOGINID, PASSWORD, NAME, DISABLED, ACCOUNT_LOCKED, ACCOUNT_EXPIRED_TIME, PASSWORD_EXPIRED_TIME, EMAIL, DEPARTMENT_CODE, DEPARTMENT_NAME, TITLE)
values ('LendMoneyOfficer1', 'LendMoneyOfficer1', 'e10adc3949ba59abbe56e057f20f883e', '放款操作员1', 0, 0, null, null, null, 'A_BANK', '某银行', null);
commit;
prompt 17 records loaded
prompt Loading T_SECU_USER_ROLE...
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('13', 'Approver1', 'Approver');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('14', 'Approver2', 'Approver');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('15', 'Approver3', 'Approver');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('1', 'Cashier1', 'Cashier');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('2', 'Cashier2', 'Cashier');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('6', 'Deliveryman1', 'Deliveryman');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('7', 'Deliveryman2', 'Deliveryman');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('8', 'Deliveryman3', 'Deliveryman');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('16', 'LendMoneyOfficer1', 'LendMoneyOfficer');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('9', 'Loanteller1', 'Loanteller');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('10', 'Loanteller2', 'Loanteller');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('11', 'RiskEvaluator1', 'RiskEvaluator');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('12', 'RiskEvaluator2', 'RiskEvaluator');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('3', 'WarehouseKeeper1', 'WarehouseKeeper');
insert into T_SECU_USER_ROLE (ID, LOGINID, ROLE_CODE)
values ('4', 'WarehouseKeeper2', 'WarehouseKeeper');
commit;
prompt 15 records loaded
prompt Enabling triggers for T_SECU_DEPARTMENT...
alter table T_SECU_DEPARTMENT enable all triggers;
prompt Enabling triggers for T_SECU_RESOURCE...
alter table T_SECU_RESOURCE enable all triggers;
prompt Enabling triggers for T_SECU_ROLE...
alter table T_SECU_ROLE enable all triggers;
prompt Enabling triggers for T_SECU_ROLE_RESOURCE...
alter table T_SECU_ROLE_RESOURCE enable all triggers;
prompt Enabling triggers for T_SECU_USER...
alter table T_SECU_USER enable all triggers;
prompt Enabling triggers for T_SECU_USER_ROLE...
alter table T_SECU_USER_ROLE enable all triggers;
set feedback on
set define on
prompt Done.
