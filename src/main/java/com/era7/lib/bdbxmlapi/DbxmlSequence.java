/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.era7.lib.bdbxmlapi;

import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Sequence;
import com.sleepycat.db.SequenceConfig;
import com.sleepycat.db.Transaction;
import java.nio.charset.Charset;

/**
 *
 * @author Pablo Pareja Tobes
 */
public class DbxmlSequence {


    protected Sequence sequence = null;
    protected SequenceConfig sequenceConfig = null;
    protected String name;

    /**
     * 
     * @param db
     * @param seqName
     * @param config
     */
    public DbxmlSequence(Database db,String seqName,SequenceConfig config) throws DatabaseException{
        
        name = seqName;
        DatabaseEntry entry = new DatabaseEntry(stringToByte(name));
        sequence = db.openSequence(null,entry, config);

    }

    public final long getNextVal(Transaction transaction) throws DatabaseException{
        return getNext(transaction,sequence);
    }

    private final static synchronized long getNext(Transaction transaction,Sequence seq) throws DatabaseException{
        return seq.get(transaction, 1);
    }

    /**
     * transalate from key to byte[]
     * @param str
     * @return
     */
    private final byte[] stringToByte(String str) {
        return str.getBytes(Charset.forName("UTF-8"));
    }

    public String getName(){
        return name;
    }

}
