package stopflow;

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


public class SF_Stop_Flow extends MbJavaComputeNode {
	private String msgflowName,host,qmgr = null ;
	private Integer port = 0 ;
	
	

	/*public void onInitialize() throws MbException{
	 
	 host = (String) getUserDefinedAttribute("HOST");
	 port = (Integer) getUserDefinedAttribute("PORT");
    qmgr = (String) getUserDefinedAttribute("QMGR");
		
	} */
	
	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
        
		// create new empty message
		MbMessage outMessage = new MbMessage();
		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly,
				outMessage);
		//Use 
		//BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(host,port,qmgr);
		BrokerConnectionParameters bcp = new LocalBrokerConnectionParameters(getBroker().getName()) ;
		
		BrokerProxy b = null ;
		
		MbExecutionGroup egp = getExecutionGroup();
		String exgName = egp.getName() ;
		
		MbMessageFlow msgflow = getMessageFlow();
		
		msgflowName = msgflow.getName();
		String appName = msgflow.getApplicationName() ;
		try {
			// optionally copy message headers
			copyMessageHeaders(inMessage, outMessage);
			b = BrokerProxy.getInstance(bcp);
			Thread.sleep(1000);
			if (b != null && exgName != null) {
				ExecutionGroupProxy egproxy = b.getExecutionGroupByName(exgName);
				//egproxy.stop();
				if (egproxy != null && appName != null && msgflowName != null) {
				    MessageFlowProxy mfp = egproxy.getApplicationByName(appName).getMessageFlowByName(msgflowName);
				    if (mfp != null) {
				    mfp.stop();
				    } 
				} else {
					System.err.println("No such ExecutionGroupProxy "+egproxy);
				}
			} else {
				System.err.println("No such BrokerProxy "+b);
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
