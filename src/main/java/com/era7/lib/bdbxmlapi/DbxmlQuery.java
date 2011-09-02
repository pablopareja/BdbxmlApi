/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.era7.lib.bdbxmlapi;

import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlQueryContext;
import com.sleepycat.dbxml.XmlQueryExpression;
import java.util.ArrayList;

/**
 *
 * @author Pablo Pareja Tobes
 */
public abstract class DbxmlQuery {

    /**
     * String representing the query
     */
    protected String queryString = "";
    /**
     * Context for the query
     */
    protected XmlQueryContext xmlQueryContext = null;
    protected XmlQueryExpression xmlQueryExpression= null;
    /**
     * Array of vars used in this query
     */
    protected ArrayList<DbxmlQueryVar> queryVars = null;

    /**
     * 
     * @param query String representing the query to be executed
     * @param context Context within the query will be executed
     * @throws XmlException
     */
    public DbxmlQuery(String query, XmlQueryContext context) throws XmlException{

        queryString = query;
        queryVars = defineQueryVars();
        xmlQueryContext = context;

        updateXmlQueryContext();
    }

    /**
     * This method must be implemented in order to define the vars this query
     * will be using
     * @return An array of DbxmlQueryVar objects representing each of the vars
     * included in the query
     */
    abstract protected ArrayList<DbxmlQueryVar> defineQueryVars();


    public XmlQueryContext getXmlQueryContext() throws XmlException{
        //------updating vars values-------
        updateXmlQueryContext();
        return xmlQueryContext;
    }
    public String getQueryString(){ return queryString;}

    /**
     * Gets the var with name 'varName'
     * @param varName Name of the var
     * @return
     */
    public DbxmlQueryVar getVar(String varName){
        for(int i=0;i<queryVars.size();i++){
            DbxmlQueryVar temp = queryVars.get(i);
            if(temp.getName().equals(varName)){
                return temp;
            }
        }
        return null;
    }

    private void updateXmlQueryContext() throws XmlException{
        for(DbxmlQueryVar var : queryVars){
            xmlQueryContext.setVariableValue(var.getName(), var.getXmlValue());
        }
    }

    /**
     * Sets the XmlQueryExpression for this query
     * @param expression
     */
    public void setXmlQueryExpression(XmlQueryExpression expression){
        xmlQueryExpression = expression;
    }
    /**
     * Gets the XmlQueryExpression for this query
     * @return
     */
    public XmlQueryExpression getXmlQueryExpression(){
        return xmlQueryExpression;
    }

    public void deleteXmlQueryExpression(){
        xmlQueryExpression.delete();
    }

}
