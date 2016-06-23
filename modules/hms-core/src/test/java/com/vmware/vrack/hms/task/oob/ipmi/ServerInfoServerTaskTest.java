package com.vmware.vrack.hms.task.oob.ipmi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hms.ipmiservice.IpmiTaskConnectorFactory;
import com.vmware.vrack.hms.task.ipmi.IpmiTaskConnector;
import com.vmware.vrack.hp.ilo.boardservice.BoardService_iLO;
import com.vmware.vrack.intel.rmm.boardservice.BoardService_S2600GZ;

/**
* Test of ServerInfoServerTask functions
* @author Yagnesh Chawda
*
*/
@Ignore
public class ServerInfoServerTaskTest
{
	private static Logger logger = Logger.getLogger(ServerInfoServerTaskTest.class);
	
	
	@After
	public void destroyCachedConnections()
	{
		ServerNode node = new ServerNode("N1", "10.28.197.202", "root", "root123");
		try
{
	        IpmiTaskConnector connector = IpmiTaskConnectorFactory.getIpmiTaskConnector(node.getServiceObject(), 3, false, null, null);
	        IpmiTaskConnectorFactory.destroyConnector(connector, node.getServiceObject(), true);
}
catch (Exception e)
{
	logger.debug("Error in Cleanup" + e);
}
		
	}
	
	@Test(expected = HmsException.class)
	public void test_executeTask_ValidHost_NoBoardMapping() throws Exception
	{
		logger.info("TS: ServerInfoServerTaskTest: test_executeTask_ValidHost_NoBoardMapping");
		TaskResponse taskResponse = new TaskResponse(new ServerNode("N1", "10.28.197.202", "root", "root123"));
		
		ServerInfoServerTask serverInfoServerTask = new ServerInfoServerTask(taskResponse);
		
		/*IBoardService boardService = new BoardService_S2600GZ();
		BoardServiceProvider.addBoardService(taskResponse.getNode().getServiceObject(), boardService, true);*/
		serverInfoServerTask.executeTask();
	}
	
	@Test(expected = HmsException.class)
	public void test_executeTask_InvalidHost_ValidBoardMapping() throws Exception
	{
		logger.info("TS: ServerInfoServerTaskTest: test_executeTask_InvalidHost_ValidBoardMapping");
		TaskResponse taskResponse = new TaskResponse(new ServerNode("N1", "10.28.197.202", "root", "root123"));
		
		ServerInfoServerTask serverInfoServerTask = new ServerInfoServerTask(taskResponse);
		
		IBoardService boardService = new BoardService_S2600GZ();
		BoardServiceProvider.addBoardService(taskResponse.getNode().getServiceObject(), boardService, true);
		
		serverInfoServerTask.executeTask();
	}
	
	
	@Test(expected = HmsException.class)
	public void test_executeTask_InvalidHostCreds() throws Exception
	{
		logger.info("TS: ServerInfoServerTaskTest: test_executeTask_InvalidHostCreds");
		TaskResponse taskResponse = new TaskResponse(new ServerNode("N1", "10.28.197.202", "root", "root123"));
		
		ServerInfoServerTask serverInfoServerTask = new ServerInfoServerTask(taskResponse);
		
		IBoardService boardService = new BoardService_S2600GZ();
		BoardServiceProvider.addBoardService(taskResponse.getNode().getServiceObject(), boardService, true);
		
		serverInfoServerTask.executeTask();
	}
	
	@Test
	public void test_executeTask_ValidHost_ValidBoardMapping() throws Exception
	{
		logger.info("TS: ServerInfoServerTaskTest: test_executeTask_ValidHost_ValidBoardMapping");
		TaskResponse taskResponse = new TaskResponse(new ServerNode("N1", "10.28.197.202", "root", "root123"));
		
		ServerInfoServerTask serverInfoServerTask = new ServerInfoServerTask(taskResponse);
		
		IBoardService boardService = new BoardService_S2600GZ();
		BoardServiceProvider.addBoardService(taskResponse.getNode().getServiceObject(), boardService, true);
		
		serverInfoServerTask.executeTask();
		ServerNode node = (ServerNode) taskResponse.getNode();
		
		assertNotNull(node.getBoardVendor());
		assertNotNull(node.getBoardProductName());
	}
	
	@Ignore
	@Test
	public void test_executeTask_ValidHost_InvalidBoardMapping() throws Exception
	{
		logger.info("TS: ServerInfoServerTaskTest: test_executeTask_ValidHost_InvalidBoardMapping");
		TaskResponse taskResponse = new TaskResponse(new ServerNode("N1", "10.28.197.202", "root", "root123"));
		ServerInfoServerTask serverInfoServerTask = new ServerInfoServerTask(taskResponse);
		
		IBoardService boardService = new BoardService_iLO();
		BoardServiceProvider.addBoardService(taskResponse.getNode().getServiceObject(), boardService, true);
		
		serverInfoServerTask.executeTask();
		ServerNode node = (ServerNode) taskResponse.getNode();
		System.out.println(node.getOobMacAddress());
		assertNull(node.getBoardProductName());
	}
	
	
	@Test(expected = NullPointerException.class)
	public void test_executeTask_InvalidTaskResponse() throws Exception
	{
		logger.info("TS: ServerInfoServerTaskTest: test_executeTask_InvalidTaskResponse");
		TaskResponse taskResponse = null;
		ServerNode sNode = new ServerNode("N1", "10.28.197.202", "root", "root123");

		ServerInfoServerTask serverInfoServerTask = new ServerInfoServerTask(taskResponse);
		
		IBoardService boardService = new BoardService_S2600GZ();
		BoardServiceProvider.addBoardService(sNode.getServiceObject(), boardService, true);
		
		serverInfoServerTask.executeTask();
	}
	
}
