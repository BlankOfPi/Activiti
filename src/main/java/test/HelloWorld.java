package test;

import java.util.Date;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

public class HelloWorld {
	private static ProcessEngine processEngine;
	
	@Before
	public void Initialization() {
		processEngine = ProcessEngines.getDefaultProcessEngine();
	}
	
	/**
	 * 部署流程定义(操作数据表：act_re_deployment、act_re_procdef、act_ge_bytearray)
	 */
	@Test
	public void deploy() {
		// 获得一个部署构建器对象，用于加载流程定义文件（test1.bpmn,test.png）完成流程定义的部署
		DeploymentBuilder builder = processEngine.getRepositoryService().createDeployment();
		// 加载流程定义文件
		builder.addClasspathResource("ActivitiDemo.bpmn");
		builder.addClasspathResource("ActivitiDemo.png");
		// 部署流程定义
		Deployment deployment = builder.deploy();
		System.out.println(deployment.getId());
	}


	/** 启动流程实例 **/

	@Test
	public void startProcessInstance() {

		// 流程定义的key	
		String processDefinitionKey = "ActivitiDemo";

		ProcessInstance pi = processEngine.getRuntimeService()// 与正在执行 的流程实例和执行对象相关的Service

				.startProcessInstanceByKey(processDefinitionKey); // 使用流程定义的key启动流程实例,key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动

		System.out.println("流程实例ID:" + pi.getId());

		System.out.println("流程定义ID:" + pi.getProcessDefinitionId());

	}

	/** 查询当前人的个人任务 */

	@Test
	public void findMyPersonalTask() {

		String assignee = "李四";
		List<Task> list = processEngine.getTaskService()// 与正在执行的任务管理相关的Service

				.createTaskQuery()// 创建任务查询

				.taskAssignee(assignee)// 指定个人任查询，指定办理人

				.list();

		if (list != null && list.size() > 0) {

			for (Task task : list) {

				System.out.println("任务ID:" + task.getId());

				System.out.println("任务名称:" + task.getName());

				System.out.println("任务的创建时间:" + task.getCreateTime());

				System.out.println("任务的办理人:" + task.getAssignee());

				System.out.println("流程实例ID:" + task.getProcessInstanceId());

				System.out.println("执行对象ID:" + task.getExecutionId());

				System.out.println("流程定义ID:" + task.getProcessDefinitionId());

				System.out.println("############################################");

			}

		}

	}
	
	/**
    * 历史活动查询
    */
   @Test
   public void historyActInstanceList(){	  
       List<HistoricActivityInstance>  list=processEngine.getHistoryService() // 历史相关Service
           .createHistoricActivityInstanceQuery() // 创建历史活动实例查询
           .processInstanceId("22501") // 执行流程实例id
           .finished()
           .list();
       for(HistoricActivityInstance hai:list){
           System.out.println("活动ID:"+hai.getId());
           System.out.println("流程实例ID:"+hai.getProcessInstanceId());
           System.out.println("活动名称："+hai.getActivityName());
           System.out.println("办理人："+hai.getAssignee());
           System.out.println("开始时间："+hai.getStartTime());
           System.out.println("结束时间："+hai.getEndTime());
           System.out.println("=================================");
       }
   }
   
   /**
    * 历史任务查询
    */
   @Test
   public void historyTaskList(){
       List<HistoricTaskInstance> list=processEngine.getHistoryService() // 历史相关Service
           .createHistoricTaskInstanceQuery() // 创建历史任务实例查询
           .processInstanceId("22501") // 用流程实例id查询
           .finished() // 查询已经完成的任务
           .list(); 
       for(HistoricTaskInstance hti:list){
           System.out.println("任务ID:"+hti.getId());
           System.out.println("流程实例ID:"+hti.getProcessInstanceId());
           System.out.println("任务名称："+hti.getName());
           System.out.println("办理人："+hti.getAssignee());
           System.out.println("开始时间："+hti.getStartTime());
           System.out.println("结束时间："+hti.getEndTime());
           System.out.println("=================================");
       }
   }
   
   /**
    * 查询流程状态（正在执行 or 已经执行结束）
    */
   @Test
   public void processState(){   	
       ProcessInstance pi=processEngine.getRuntimeService() // 获取运行时Service
           .createProcessInstanceQuery() // 创建流程实例查询
           .processInstanceId("22501") // 用流程实例id查询
           .singleResult();
       if(pi!=null){
           System.out.println("流程正在执行！");
       }else{
           System.out.println("流程已经执行结束！");
       }
   }
	

	/**
	 * 设置流程变量数据
	 */
	@Test
	public void setVariableValues() {	
		TaskService taskService = processEngine.getTaskService(); // 任务Service
		String taskId = "22505";
		taskService.setVariable(taskId, "days", 7);
		taskService.setVariable(taskId, "date", new Date());
		taskService.setVariable(taskId, "reason", "约会");
		User user = new User();
		user.setId(1);
		user.setUsername("张小乙");
		user.setPhone_number("15555555555");
		taskService.setVariable(taskId, "user", user); // 存序列化对象
	}

	/**
	 * 获取流程变量数据
	 */
	@Test
	public void getVariableValues() {		
		TaskService taskService = processEngine.getTaskService(); // 任务Service
		String taskId = "27502";
		Integer days = (Integer) taskService.getVariable(taskId, "days");
		Date date = (Date) taskService.getVariable(taskId, "date");
		String reason = (String) taskService.getVariable(taskId, "reason");
		User student = (User) taskService.getVariable(taskId, "user");
		System.out.println("请假天数：" + days);
		System.out.println("请假日期：" + date);
		System.out.println("请假原因：" + reason);
		System.out.println("请假对象：" + student.getId() + "," + student.getUsername());
	}

	
	/** 完成我的任务 */

	@Test

	public void completeMyPersonalTask() {

		// 任务ID

		String taskId = "22505";		
		processEngine.getTaskService()// 与正在执行的任务管理相关的Service

				.complete(taskId);

		System.out.println("完成任务：任务ID:" + taskId);

	}

}