package com.thinkgem.jeesite.modules.oa.service;

import org.activiti.engine.*;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.service.BaseService;
/**
 *
 * @Company:
 * @Description:
 * @fileName:ActTaskBaseService.java
 * @author:wen.zhang   <br>
 * @date 2016年3月27日 下午12:09:09
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 * @JDK version used:jdk1.7.0_02
 * @Update date： 2016年3月27日 下午12:09:09
 * @Update author：
*/
@Transactional(readOnly = true)
public abstract class ActTaskBaseService extends BaseService {

    @Autowired
    protected ProcessEngineFactoryBean processEngine;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected FormService formService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected IdentityService identityService;

}
