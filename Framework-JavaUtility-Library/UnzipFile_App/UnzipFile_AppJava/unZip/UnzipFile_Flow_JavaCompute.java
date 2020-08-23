//Author : SuvenduPaul
//Date : June12,2014
package unZip;

import java.io.*; 
import java.util.zip.ZipEntry; 
import java.util.zip.ZipInputStream; 
import com.ibm.broker.javacompute.MbJavaComputeNode; 
import com.ibm.broker.plugin.*; 
import com.ibm.security.cmp.OOBCertHash;

public class UnzipFile_Flow_JavaCompute extends MbJavaComputeNode { 

	  public void evaluate(MbMessageAssembly inAssembly) throws MbException { 
          MbOutputTerminal Out = getOutputTerminal("out"); 
          MbOutputTerminal Alternative = getOutputTerminal("alternate"); 
          MbOutputTerminal Failure = getOutputTerminal("failure"); 

          MbMessage inMessage = inAssembly.getMessage(); 

          MbMessage outMessage = new MbMessage(inMessage); 
          MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly,outMessage); 
          MbMessage gbEnv = outAssembly.getGlobalEnvironment();
          MbElement messageBody; 
          byte[] zipByteStreamIn; 
          
          byte[] msgBytes = null; 

          try { 
                 
                  messageBody = outMessage.getRootElement().getLastChild(); 
                  messageBody.detach(); 

                 
                  zipByteStreamIn = messageBody.toBitstream("", "", "", 0, 0, 0); 
                  int isiz = zipByteStreamIn.length * 34;
                 
                  ZipInputStream inputZipStream = new ZipInputStream(new ByteArrayInputStream(zipByteStreamIn)); 
                  ZipEntry inputZipEntry = null; 
                 
                  int file_num = 0; 
                  try { 
                          while ((inputZipEntry = inputZipStream.getNextEntry()) != null) 
                          { 
                                 
                        	       
                                  byte[] stringBuffer = new byte[32 * 1024]; 
                                  //String fileName = inputZipEntry.getName().replace('/', '_');
                                  String fileName = inputZipEntry.getName();
                                  int len2 ;
                                  len2 = fileName.length();
                                  int pos1;
                                  pos1 = fileName.indexOf('/');
                                  String resultFileName = fileName.substring(pos1+1, len2);

                                  int length; 
                                  UnzipBuffer unzipbuffer = new UnzipBuffer(isiz); 

                                  while ((length = inputZipStream.read(stringBuffer)) > 0) 
                                  { 
                                	  unzipbuffer.write(stringBuffer, 0, length);
                                  } 

                                  inputZipStream.closeEntry();
                                  
                                  file_num = file_num + 1; 
                                  
                                 // gbEnv.getRootElement().createElementAsFirstChild(fileName);
                                  //Out.propagate(outAssembly, true);
                                  
                                  MbElement outRoot = outAssembly.getMessage().getRootElement(); 
                      
                                  messageBody = outMessage.getRootElement().createElementAsLastChildFromBitstream(unzipbuffer.getBuf(),"NONE", "", "", "", 0, 0, 0);
                                  
                                  
                                 // MbElement outParser = messageBody.createElementAsLastChild(MbBLOB.PARSER_NAME);
                                  //MbElement FinalMsgBody = outParser.createElementAsFirstChild(MbElement.TYPE_NAME_VALUE,"BLOB", outParser);

                                  Out.propagate(outAssembly); 
                                  messageBody.getParent().getLastChild().delete();
                                  outAssembly.getGlobalEnvironment().getRootElement().getFirstChild().delete();
                                  
                                  
                               
                          } 
                          //inputZipStream.close();
                          
                  }  catch (IOException e1) { 
                          System.out.println("Error Reading Archive File"); 
                          e1.printStackTrace(); 
                  } 
                 
          } catch (MbException e) { 
                  throw e; 
          } finally { 
                   
                  outMessage.clearMessage(); 
          } 
  }         


public void copyMessageHeaders(MbMessage inMessage, MbMessage outMessage) 
throws MbException { 
MbElement outRoot = outMessage.getRootElement(); 

MbElement header = inMessage.getRootElement().getFirstChild();

//header.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, getName(), outMessage);




}
}               