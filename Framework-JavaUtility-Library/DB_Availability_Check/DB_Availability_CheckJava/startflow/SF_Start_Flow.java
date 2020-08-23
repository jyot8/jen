package startflow;

import com.ibm.broker.config.proxy.BrokerConnectionParameters;
import com.ibm.broker.config.proxy.BrokerProxy;
import com.ibm.broker.config.proxy.ExecutionGroupProxy;
import com.ibm.broker.config.proxy.LocalBrokerConnectionParameters;
import com.ibm.broker.config.proxy.MQBrokerConnectionParameters;
import com.ibm.broker.config.proxy.MessageFlowProxy;
import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbExecutionGroup;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbMessageFlow;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class SF_Start_Flow extends MbJavaComputeNode {
	private String msgflowName,host,qmgr  = null ;
	private Integer port = 0 ;
	
	public void onInitialize() throws MbException{
		 
		 /* host = (String) getUserDefinedAttribute("HOSTx");
		 port = (Integer) getUserDefinedAttribute("PORTx");
	     qmgr = (String) getUserDefinedAttribute("QMGRx"); */
	     msgflowName = (String) getUserDefinedAttribute("FLOWNAME");
			
		} 
	
	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();

		// create new empty message
		MbMessage outMessage = new MbMessage();
		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly,
				outMessage);
		
		//BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(host,port,qmgr);
		BrokerConnectionParameters bcp = new LocalBrokerConnectionParameters(getBroker().getName());
		BrokerProxy b = null ;
		
		MbExecutionGroup egp = getExecutionGroup();
		String exgName = egp.getName() ;
		
		MbMessageFlow msgflow = getMessageFlow();
		//msgflowName = msgflow.getName();
		String appName = msgflow.getApplicationName();

		try {
			
			copyMessageHeaders(inMessage, outMessage);
			if (bcp != null) {
				b = BrokerProxy.getInstance(bcp) ;
				Thread.sleep(3000);

				if (b != null && exgName != null) {
					ExecutionGroupProxy egproxy = b.getExecutionGroupByName(exgName);
					if (egproxy != null && appName != null && msgflowName != null){
					    MessageFlowProxy mfp = egproxy.getApplicationByName(appName).getMessageFlowByName(msgflowName);
					   //Checks if the the flow is running ??
					    if (mfp != null){
						    if((mfp.isRunning())!= true){
						    mfp.start();
						    }
					    }
					}
					else {
						System.err.println("No such ExecutionGroupProxy "+egproxy);
					}

				} else {
					System.err.println("No such BrokerProxy "+b);
				}
			}
			else {
				System.err.println("BrokerConnectionParameters is NULL for MASTERFLOW_OPS_LEG_DETAIL.msgflow");
			}
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);
	}

	public void copyMessageHeaders(MbMessage inMessage, MbMessage outMessage)
			throws MbException {
		MbElement outRoot = outMessage.getRootElement();

		// iterate though the headers starting with the first child of the root
		// element
		MbElement header = inMessage.getRootElement().getFirstChild();
		while (header != null && header.getNextSibling() != null) // stop before
																	// the last
																	// child
																	// (body)
		{
			// copy the header and add it to the out message
			outRoot.addAsLastChild(header.copy());
			// move along to next header
			header = header.getNextSibling();
		}
	}

}
