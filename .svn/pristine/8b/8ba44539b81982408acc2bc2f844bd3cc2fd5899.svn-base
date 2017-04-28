package com.thinkgem.jeesite.modules.oa.util;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @Company:
 * @Description:Activiti工具类
 * @fileName:ActUtils.java
 * @author: wen.zhang   <br>
 * @date 2016年3月27日 上午11:19:09
 * @Copyright: Copyright (c) 2016
 * @version 1.0
 * @JDK version used:jdk1.7.0_02
 * @Update date： 2016年3月27日 上午11:19:09
 * @Update author：
*/
public class ActUtils {

    /**
     * 转换流程节点类型为中文说明
     * @param type 英文名称
     * @return 翻译后的中文名称
     */
    public static String parseToZhType(String type) {
        Map<String, String> types = new HashMap<String, String>();
        types.put("userTask", "用户任务");
        types.put("serviceTask", "系统任务");
        types.put("startEvent", "开始节点");
        types.put("endEvent", "结束节点");
        types.put("exclusiveGateway", "条件判断节点(系统自动根据条件处理)");
        types.put("inclusiveGateway", "并行处理任务");
        types.put("callActivity", "子流程");
        return types.get(type) == null ? type : types.get(type);
    }
}
