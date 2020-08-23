//Custom Formatter Initial Version
//Author Suvendu Paul
//Date January 21,2015
/*
 *
 * Copyright (c) 2015 IBM All rights reserved.
 *
 * Created on 26.3.2015
 *
 * <pre>
 * Date $Date: 2015/11/11 08:01:00 $
 * CVS History:
 * $Log: Custformat.java,v $
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class Custformat extends Formatter {
	private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
	@Override
	public String format(LogRecord record) {

		 StringBuilder builder = new StringBuilder(1000);
		    
		    builder.append(df.format(new Date(record.getMillis()))).append(" - ");
	        builder.append("[").append(record.getLevel()).append(":");
	        builder.append("]");
	        builder.append(formatMessage(record));
	        builder.append(System.lineSeparator());
	        return builder.toString();
	}
    public String getHead(Handler h) {
        return super.getHead(h);
    }
 
    public String getTail(Handler h) {
        return super.getTail(h);

}
}