/*Author : Suvendu Paul
*Date : Tuesday,April 07,2015
/*
 *
 * Copyright (c) 2015 IBM All rights reserved.
 *
 * Created on 26.3.2015
 *
 * <pre>
 * Date $Date: 2015/11/11 08:01:00 $
 * CVS History:
 * $Log: SF_Decompress_Java_JavaCompute.java,v $
 * Revision 1.1  2015/11/11 08:01:00  suvendu.paul
 * Decompress JavaApp
 *
 * </pre>
 * 
 * @author $Author: suvendu.paul $
 * @version $Revision: 1.1 $
 *
 */
package decomp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbBLOB;
import com.ibm.broker.plugin.MbBroker;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbExecutionGroup;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbMessageFlow;
import com.ibm.broker.plugin.MbNode;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

import decomp.DC;
@SuppressWarnings("unused")
public class SF_Decompress_Java_JavaCompute extends MbJavaComputeNode {
	
	public void onInitialize() throws MbException{
	 String AppName = getMessageFlow().getApplicationName() ;
	 String lvl = (String) getUserDefinedAttribute("JAVA_LOG_LEVEL");
	 String dir = (String) getUserDefinedAttribute("JAVA_LOG_DIRECTORY");

	
	DC.INSTANCE.setDir(dir);
	DC.INSTANCE.setLvl(lvl);
	DC.INSTANCE.setAppName(AppName);
	DC.INSTANCE.createLogger();
		
	}
	
	private static final Logger LOG = Logger.getLogger(DC.LNAME);
	private static String fileName = null;
	private static final int GZ = 1024 * 128;
	private static final int BSZ = 1024 * 1024;
	
	
	
	

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		
		 // = MsgFlow.getName().toString();
		 
		 MbOutputTerminal Success = getOutputTerminal("out");
		    MbOutputTerminal Failure = getOutputTerminal("failure");
		    MbMessage inMessage = inAssembly.getMessage();
		    MbMessage outMessage = new MbMessage(inMessage);
		    
		   MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly,outMessage);
		    MbElement localenv = outAssembly.getLocalEnvironment().getRootElement();
		    MbElement localenv2 = localenv.getFirstElementByPath("/File/Name");
		    fileName = localenv2.getValueAsString();
		    
		    MbElement msgBody;
		    byte []msgByteStreamIn;  
		    byte []msgByteStreamOut; 
		    InflaterInputStream infInputStream = null;
		try {
			msgBody = outMessage.getRootElement().getLastChild();
		      msgBody.detach();  
		      msgByteStreamIn = msgBody.toBitstream("","","",0,0,0);
		      infInputStream = new GZIPInputStream(new ByteArrayInputStream(msgByteStreamIn));
		     // GZIPInputStream infInputStream = new GZIPInputStream(new ByteArrayInputStream(msgByteStreamIn),GZ);
		      ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(msgByteStreamIn.length);
		      
		     		      
		     // byte[] tempBuffer = new byte[BSZ];
		      byte[] tempBuffer = new byte[msgByteStreamIn.length];
		      //int gzipsize = msgByteStreamIn.length * 32;
		      //DecompressBuffer decombuf = new DecompressBuffer(gzipsize);
		      int numBytesRead = 0;
		      while (numBytesRead != -1) {
		      numBytesRead = infInputStream.read(tempBuffer, 0, msgByteStreamIn.length);
		      if (numBytesRead != -1) {
		          bytesOut.write(tempBuffer, 0, numBytesRead);
		        }
		      } 
		    		      
		      msgByteStreamOut = bytesOut.toByteArray();
		      infInputStream.close();
		      bytesOut.close();
		      //decombuf.close();
		      //decombuf.flush();

		      //msgBody = outMessage.getRootElement().createElementAsLastChildFromBitstream
		        //  (decombuf.getBuf(),MbBLOB.PARSER_NAME,"","","",0,0,0);
		      msgBody = outMessage.getRootElement().createElementAsLastChildFromBitstream
			          (msgByteStreamOut,MbBLOB.PARSER_NAME,"","","",0,0,0);
		      
		     // decombuf.flush();
		      //decombuf.close();
		      
		      	Success.propagate(outAssembly);
		      
		      //LOG.logp(Level.INFO,DC.LNAME, "END","--FlowName--" + flowname);
		      LOG.logp(Level.INFO,DC.LNAME, "END","--Decompressed Successfully--" + fileName);
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			LOG.logp(Level.SEVERE,DC.LNAME, "END","--Exception in Decompressing--" + fileName);
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}

		finally{
			
			outMessage.clearMessage();
		}

	}

}
