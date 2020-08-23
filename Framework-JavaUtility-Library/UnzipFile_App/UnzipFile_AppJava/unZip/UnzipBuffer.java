/*
 *
 * Copyright (c) 2014 IBM All rights reserved.
 *
 * Created on Sep 30, 2014
 *
 * <pre>
 * Date $Date: 2014/10/14 06:05:43 $
 * CVS History:
 * $Log: UnzipBuffer.java,v $
 * Revision 1.1  2014/10/14 06:05:43  pertti.peramaa
 * UnzipBuffer
 *
 * </pre>
 * 
 * @author $Author: pertti.peramaa $
 * @version $Revision: 1.1 $
 *
 */
package unZip;

import java.io.ByteArrayOutputStream;

public class UnzipBuffer extends ByteArrayOutputStream {
	public UnzipBuffer() {
	}
	public UnzipBuffer(int size) {
		super(size);
	}
	public int getCount() {
		return count;
	}
	public byte[] getBuf() {
		return buf;
	}
}