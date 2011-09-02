/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.era7.lib.bdbxmlapi;

import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlQueryContext;

/**
 *
 * @author Pablo Pareja Tobes
 */
public class BasicQueryProvider {
    
    protected XmlManager xmlManager = null;
    protected int evaluationType = XmlQueryContext.Eager;

    /**
     *
     * @param manager
     */
    public BasicQueryProvider(XmlManager manager,boolean eagerEvaluation) throws Exception{

        xmlManager = manager;

        if(eagerEvaluation){
            evaluationType = XmlQueryContext.Eager;
        }else{
            evaluationType = XmlQueryContext.Lazy;
        }
    }

    protected XmlQueryContext getNewQueryContext() throws XmlException{
        return xmlManager.createQueryContext(evaluationType, XmlQueryContext.LiveValues);
    }

}
