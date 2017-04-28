package com.thinkgem.jeesite.modules.oa.service;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.oa.util.ActUtils;
/**
 *
 * @Company:
 * @Description:
 * @fileName:ActTaskService.java
 * @author:wen.zhang   <br>
 * @date 2016年3月27日 下午12:09:04
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 * @JDK version used:jdk1.7.0_02
 * @Update date： 2016年3月27日 下午12:09:04
 * @Update author：
*/
@Service
@Transactional(readOnly = true)
public class ActTaskService extends ActTaskBaseService {


    /**
     * 获取已签收的任务
     *
     * @param userId     用户id
     * @param procDefKey 流程定义标识
     * @param beginDate  开始时间
     * @param endDate    结束手贱
     * @return
     */
    public List<Task> todoList(String userId, String procDefKey, Date beginDate, Date endDate) {


        // =============== 已经签收的任务  ===============
        TaskQuery todoTaskQuery = taskService.createTaskQuery().taskAssignee(userId).active()
                .includeProcessVariables().orderByTaskCreateTime().desc();

        // 设置查询条件
        if (StringUtils.isNotBlank(procDefKey)) {
            todoTaskQuery.processDefinitionKey(procDefKey);
        }
        if (beginDate != null) {
            todoTaskQuery.taskCreatedAfter(beginDate);
        }
        if (endDate != null) {
            todoTaskQuery.taskCreatedBefore(endDate);
        }

        // 查询列表
        List<Task> todoList = todoTaskQuery.list();
        return todoList;

    }


    /**
     * 等待签收的任务
     *
     * @param userId
     * @param procDefKey
     * @param beginDate
     * @param endDate
     * @return
     */
    public List<Task> toClaimList(String userId, String procDefKey, Date beginDate, Date endDate) {

        // =============== 等待签收的任务  ===============
        TaskQuery toClaimQuery = taskService.createTaskQuery().taskCandidateUser(userId)
                .includeProcessVariables().active().orderByTaskCreateTime().desc();


        // 设置查询条件
        if (StringUtils.isNotBlank(procDefKey)) {
            toClaimQuery.processDefinitionKey(procDefKey);
        }
        if (beginDate != null) {
            toClaimQuery.taskCreatedAfter(beginDate);
        }
        if (endDate != null) {
            toClaimQuery.taskCreatedBefore(endDate);
        }
        // 查询列表
        List<Task> toClaimList = toClaimQuery.list();
        return toClaimList;

    }


    /**
     * 获取已办任务
     *
     * @param userId
     * @param procDefKey
     * @param beginDate
     * @param endDate
     * @return
     */
    public List<HistoricTaskInstance> historicList(String userId, String procDefKey, Date beginDate, Date endDate) {

        HistoricTaskInstanceQuery histTaskQuery = historyService.createHistoricTaskInstanceQuery().taskAssignee(userId).finished()
                .includeProcessVariables().orderByHistoricTaskInstanceEndTime().desc();

        // 设置查询条件
        if (StringUtils.isNotBlank(procDefKey)) {
            histTaskQuery.processDefinitionKey(procDefKey);
        }
        if (beginDate != null) {
            histTaskQuery.taskCompletedAfter(beginDate);
        }
        if (endDate != null) {
            histTaskQuery.taskCompletedBefore(endDate);
        }
        // 查询列表
        List<HistoricTaskInstance> histList = histTaskQuery.list(); //.listPage()

        return histList;
    }


    /**
     * 获取流转历史列表
     *
     * @param procInsId 流程实例
     * @param startAct  开始活动节点名称
     * @param endAct    结束活动节点名称
     * @return
     */
    public List<HistoricActivityInstance> histoicFlowList(String procInsId, String startAct, String endAct) {
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(procInsId)
                .orderByHistoricActivityInstanceStartTime().asc().orderByHistoricActivityInstanceEndTime().asc().list();

        boolean start = false;
        Map<String, Integer> actMap = Maps.newHashMap();

        for (int i = 0; i < list.size(); i++) {

            HistoricActivityInstance histIns = list.get(i);

            // 过滤开始节点前的节点
            if (StringUtils.isNotBlank(startAct) && startAct.equals(histIns.getActivityId())) {
                start = true;
            }
            if (StringUtils.isNotBlank(startAct) && !start) {
                continue;
            }

            // 只显示开始节点和结束节点，并且执行人不为空的任务
            if (StringUtils.isNotBlank(histIns.getAssignee())
                    || "startEvent".equals(histIns.getActivityType())
                    || "endEvent".equals(histIns.getActivityType())) {

                // 给节点增加一个序号
                Integer actNum = actMap.get(histIns.getActivityId());
                if (actNum == null) {
                    actMap.put(histIns.getActivityId(), actMap.size());
                }

                // 获取流程发起人名称
                if ("startEvent".equals(histIns.getActivityType())) {
                    List<HistoricProcessInstance> il = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).orderByProcessInstanceStartTime().asc().list();
//					List<HistoricIdentityLink> il = historyService.getHistoricIdentityLinksForProcessInstance(procInsId);
                    if (il.size() > 0) {
                        if (StringUtils.isNotBlank(il.get(0).getStartUserId())) {
                            il.get(0).getStartUserId();
                        }
                    }
                }
                // 获取任务执行人名称
                if (StringUtils.isNotEmpty(histIns.getAssignee())) {
                    histIns.getAssignee();
                }
                // 获取意见评论内容
                if (StringUtils.isNotBlank(histIns.getTaskId())) {
                    List<Comment> commentList = taskService.getTaskComments(histIns.getTaskId());
                    if (commentList.size() > 0) {
                        commentList.get(0).getFullMessage();
                    }
                }
            }

            // 过滤结束节点后的节点
            if (StringUtils.isNotBlank(endAct) && endAct.equals(histIns.getActivityId())) {
                boolean bl = false;
                Integer actNum = actMap.get(histIns.getActivityId());
                // 该活动节点，后续节点是否在结束节点之前，在后续节点中是否存在
                for (int j = i + 1; j < list.size(); j++) {
                    HistoricActivityInstance hi = list.get(j);
                    Integer actNumA = actMap.get(hi.getActivityId());
                    if ((actNumA != null && actNumA < actNum) || StringUtils.equals(hi.getActivityId(), histIns.getActivityId())) {
                        bl = true;
                    }
                }
                if (!bl) {
                    break;
                }
            }
        }
        return list;
    }


    /**
     * 获取流程实例对象
     *
     * @param procInsId
     * @return
     */
    @Transactional(readOnly = false)
    public ProcessInstance getProcIns(String procInsId) {
        return runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
    }

    /**
     * 启动流程
     *
     * @param procDefKey    流程定义KEY
     * @param businessTable 业务表表名
     * @param businessId    业务表编号
     * @return 流程实例ID
     */
    @Transactional(readOnly = false)
    public String startProcess(String procDefKey, String businessTable, String businessId) {
        return startProcess(procDefKey, businessTable, businessId, "");
    }

    /**
     * 启动流程
     *
     * @param procDefKey    流程定义KEY
     * @param businessTable 业务表表名
     * @param businessId    业务表编号
     * @param title         流程标题，显示在待办任务标题
     * @return 流程实例ID
     */
    @Transactional(readOnly = false)
    public String startProcess(String procDefKey, String businessTable, String businessId, String title) {
        Map<String, Object> vars = Maps.newHashMap();
        return startProcess(procDefKey, businessTable, businessId, title, vars);
    }

    /**
     * 启动流程
     *
     * @param procDefKey    流程定义KEY
     * @param businessTable 业务表表名
     * @param businessId    业务表编号
     * @param title         流程标题，显示在待办任务标题
     * @param vars          流程变量
     * @return 流程实例ID
     */
    @Transactional(readOnly = false)
    public String startProcess(String procDefKey, String businessTable, String businessId, String title, Map<String, Object> vars) {
        String userId = "";

        // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(userId);

        // 设置流程变量
        if (vars == null) {
            vars = Maps.newHashMap();
        }

        // 设置流程标题
        if (StringUtils.isNotBlank(title)) {
            vars.put("title", title);
        }

        // 启动流程
        ProcessInstance procIns = runtimeService.startProcessInstanceByKey(procDefKey, businessTable + ":" + businessId, vars);

        return procIns.getId();
    }


    /**
     * 获取任务
     *
     * @param taskId 任务ID
     */
    public Task getTask(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    /**
     * 删除任务
     *
     * @param taskId       任务ID
     * @param deleteReason 删除原因
     */
    public void deleteTask(String taskId, String deleteReason) {
        taskService.deleteTask(taskId, deleteReason);
    }

    /**
     * 签收任务
     *
     * @param taskId 任务ID
     * @param userId 签收用户ID（用户登录名）
     */
    @Transactional(readOnly = false)
    public void claim(String taskId, String userId) {
        taskService.claim(taskId, userId);
    }

    /**
     * 提交任务, 并保存意见
     *
     * @param taskId    任务ID
     * @param procInsId 流程实例ID，如果为空，则不保存任务提交意见
     * @param comment   任务提交意见的内容
     * @param vars      任务变量
     */
    @Transactional(readOnly = false)
    public void complete(String taskId, String procInsId, String comment, Map<String, Object> vars) {
        complete(taskId, procInsId, comment, "", vars);
    }

    /**
     * 提交任务, 并保存意见
     *
     * @param taskId    任务ID
     * @param procInsId 流程实例ID，如果为空，则不保存任务提交意见
     * @param comment   任务提交意见的内容
     * @param title     流程标题，显示在待办任务标题
     * @param vars      任务变量
     */
    @Transactional(readOnly = false)
    public void complete(String taskId, String procInsId, String comment, String title, Map<String, Object> vars) {
        // 添加意见
        if (StringUtils.isNotBlank(procInsId) && StringUtils.isNotBlank(comment)) {
            taskService.addComment(taskId, procInsId, comment);
        }

        // 设置流程变量
        if (vars == null) {
            vars = Maps.newHashMap();
        }

        // 设置流程标题
        if (StringUtils.isNotBlank(title)) {
            vars.put("title", title);
        }

        // 提交任务
        taskService.complete(taskId, vars);
    }

    /**
     * 完成第一个任务
     *
     * @param procInsId
     */
    public void completeFirstTask(String procInsId) {
        completeFirstTask(procInsId, null, null, null);
    }

    /**
     * 完成第一个任务
     *
     * @param procInsId
     * @param comment
     * @param title
     * @param vars
     */
    public void completeFirstTask(String procInsId, String comment, String title, Map<String, Object> vars) {
        String userId = "";
        Task task = taskService.createTaskQuery().taskAssignee(userId).processInstanceId(procInsId).active().singleResult();
        if (task != null) {
            complete(task.getId(), procInsId, comment, title, vars);
        }
    }

    /**
     * 读取带跟踪的图片
     *
     * @param executionId 环节ID
     * @return 封装了各种节点信息
     */
    public InputStream tracePhoto(String processDefinitionId, String executionId) {
        // ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(executionId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        List<String> activeActivityIds = Lists.newArrayList();
        if (runtimeService.createExecutionQuery().executionId(executionId).count() > 0) {
            activeActivityIds = runtimeService.getActiveActivityIds(executionId);
        }

        // 不使用spring请使用下面的两行代码
        // ProcessEngineImpl defaultProcessEngine = (ProcessEngineImpl)ProcessEngines.getDefaultProcessEngine();
        // Context.setProcessEngineConfiguration(defaultProcessEngine.getProcessEngineConfiguration());

        // 使用spring注入引擎请使用下面的这行代码
        Context.setProcessEngineConfiguration(processEngine.getProcessEngineConfiguration());

        return null;
    }

    /**
     * 流程跟踪图信息
     *
     * @param processInstanceId 流程实例ID
     * @return 封装了各种节点信息
     */
    public List<Map<String, Object>> traceProcess(String processInstanceId) throws Exception {
        Execution execution = runtimeService.createExecutionQuery().executionId(processInstanceId).singleResult();//执行实例
        Object property = PropertyUtils.getProperty(execution, "activityId");
        String activityId = "";
        if (property != null) {
            activityId = property.toString();
        }
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .singleResult();
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        List<ActivityImpl> activitiList = processDefinition.getActivities();//获得当前任务的所有节点

        List<Map<String, Object>> activityInfos = new ArrayList<Map<String, Object>>();
        for (ActivityImpl activity : activitiList) {

            boolean currentActiviti = false;
            String id = activity.getId();

            // 当前节点
            if (id.equals(activityId)) {
                currentActiviti = true;
            }

            Map<String, Object> activityImageInfo = packageSingleActivitiInfo(activity, processInstance, currentActiviti);

            activityInfos.add(activityImageInfo);
        }

        return activityInfos;
    }

    /**
     * 封装输出信息，包括：当前节点的X、Y坐标、变量信息、任务类型、任务描述
     *
     * @param activity
     * @param processInstance
     * @param currentActiviti
     * @return
     */
    private Map<String, Object> packageSingleActivitiInfo(ActivityImpl activity, ProcessInstance processInstance,
                                                          boolean currentActiviti) throws Exception {
        Map<String, Object> vars = new HashMap<String, Object>();
        Map<String, Object> activityInfo = new HashMap<String, Object>();
        activityInfo.put("currentActiviti", currentActiviti);
        setPosition(activity, activityInfo);
        setWidthAndHeight(activity, activityInfo);

        Map<String, Object> properties = activity.getProperties();
        vars.put("节点名称", properties.get("name"));
        vars.put("任务类型", ActUtils.parseToZhType(properties.get("type").toString()));

        ActivityBehavior activityBehavior = activity.getActivityBehavior();
        logger.debug("activityBehavior={}", activityBehavior);
        if (activityBehavior instanceof UserTaskActivityBehavior) {

            Task currentTask = null;

            // 当前节点的task
            if (currentActiviti) {
                currentTask = getCurrentTaskInfo(processInstance);
            }

            // 当前任务的分配角色
            UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
            TaskDefinition taskDefinition = userTaskActivityBehavior.getTaskDefinition();
            Set<Expression> candidateGroupIdExpressions = taskDefinition.getCandidateGroupIdExpressions();
            if (!candidateGroupIdExpressions.isEmpty()) {

                // 任务的处理角色
                setTaskGroup(vars, candidateGroupIdExpressions);

                // 当前处理人
                if (currentTask != null) {
                    setCurrentTaskAssignee(vars, currentTask);
                }
            }
        }

        vars.put("节点说明", properties.get("documentation"));

        String description = activity.getProcessDefinition().getDescription();
        vars.put("描述", description);

        logger.debug("trace variables: {}", vars);
        activityInfo.put("vars", vars);
        return activityInfo;
    }

    /**
     * 设置任务组
     *
     * @param vars
     * @param candidateGroupIdExpressions
     */
    private void setTaskGroup(Map<String, Object> vars, Set<Expression> candidateGroupIdExpressions) {
        String roles = "";
        for (Expression expression : candidateGroupIdExpressions) {
            String expressionText = expression.getExpressionText();
            String roleName = identityService.createGroupQuery().groupId(expressionText).singleResult().getName();
            roles += roleName;
        }
        vars.put("任务所属角色", roles);
    }

    /**
     * 设置当前处理人信息
     *
     * @param vars
     * @param currentTask
     */
    private void setCurrentTaskAssignee(Map<String, Object> vars, Task currentTask) {
        String assignee = currentTask.getAssignee();
        if (assignee != null) {
            org.activiti.engine.identity.User assigneeUser = identityService.createUserQuery().userId(assignee).singleResult();
            String userInfo = assigneeUser.getFirstName() + " " + assigneeUser.getLastName();
            vars.put("当前处理人", userInfo);
        }
    }

    /**
     * 获取当前节点信息
     *
     * @param processInstance
     * @return
     */
    private Task getCurrentTaskInfo(ProcessInstance processInstance) {
        Task currentTask = null;
        try {
            String activitiId = (String) PropertyUtils.getProperty(processInstance, "activityId");
            logger.debug("current activity id: {}", activitiId);

            currentTask = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey(activitiId)
                    .singleResult();
            logger.debug("current task for processInstance: {}", ToStringBuilder.reflectionToString(currentTask));

        } catch (Exception e) {
            logger.error("can not get property activityId from processInstance: {}", processInstance);
        }
        return currentTask;
    }

    /**
     * 设置宽度、高度属性
     *
     * @param activity
     * @param activityInfo
     */
    private void setWidthAndHeight(ActivityImpl activity, Map<String, Object> activityInfo) {
        activityInfo.put("width", activity.getWidth());
        activityInfo.put("height", activity.getHeight());
    }

    /**
     * 设置坐标位置
     *
     * @param activity
     * @param activityInfo
     */
    private void setPosition(ActivityImpl activity, Map<String, Object> activityInfo) {
        activityInfo.put("x", activity.getX());
        activityInfo.put("y", activity.getY());
    }


}
