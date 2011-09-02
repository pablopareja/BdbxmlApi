/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.era7.lib.bdbxmlapi;

import com.era7.lib.era7xmlapi.model.XMLElement;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlValue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes
 */
public class DbxmlQueryVar {

    //----------------AVAILABLE TYPES----------------
    public static final int STRING_TYPE = 0;
    public static final int INT_TYPE = 1;
    public static final int FLOAT_TYPE = 2;
    public static final int XML_ELEMENT_TYPE = 3;
    
    /**
     * String, int, boolean....
     */
    protected int type;
    /**
     * Variable name
     */
    protected String name;

    /**
     * Variable value in XmlValue format
     */
    protected XmlValue xmlValue = null;

    /**
     *
     * @param varName Name of the variable
     * @param value Value of the variable (only String)
     */
    public DbxmlQueryVar(String varName,String value){
        try {
            name = varName;
            type = STRING_TYPE;
            xmlValue = new XmlValue(value);            
            
        } catch (XmlException ex) {
            Logger.getLogger(DbxmlQueryVar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     *
     * @param varName Name of the variable
     * @param value Value of the variable (only int)
     */
    public DbxmlQueryVar(String varName,int value){
        try {
            name = varName;
            type = INT_TYPE;
            xmlValue = new XmlValue(value);
        } catch (XmlException ex) {
            Logger.getLogger(DbxmlQueryVar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     *
     * @param varName Name of the variable
     * @param value Value of the variable (only float)
     */
    public DbxmlQueryVar(String varName,float value){
        try {
            name = varName;
            type = FLOAT_TYPE;
            xmlValue = new XmlValue(value);
        } catch (XmlException ex) {
            Logger.getLogger(DbxmlQueryVar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     *
     * @param varName Name of the variable
     * @param value Value of the variable (only XMLElement)
     */
    public DbxmlQueryVar(String varName,XMLElement element){
        try {
            name = varName;
            type = XML_ELEMENT_TYPE;
            XmlDocument doc = BdbxmlManager.getManager().getXmlManager().createDocument();
            doc.setContent(element.toString());
            xmlValue = new XmlValue(doc);
        } catch (Exception ex) {
            Logger.getLogger(DbxmlQueryVar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return Name of the variable
     */
    public String getName(){    return name;}
    /**
     *
     * @return Type of the var value
     */
    public int getType(){   return type;}
    /**
     * Sets the value of the var to a new String value
     * The instance must have been constructed with a String value, an exception will be thrown otherwise.
     * @param value
     * @throws XmlException, DbxmlQueryVarException
     */
    public void setString(String value) throws XmlException, DbxmlQueryVarException
    {
        if(type != STRING_TYPE){
            throw new DbxmlQueryVarException(DbxmlQueryVarException.WRONG_VAR_TYPE_SETTER);
        }else{
            xmlValue = new XmlValue(value);
        }
    }
    /**
     * Sets the value of the var to a new int value
     * The instance must have been constructed with a int value, an exception will be thrown otherwise.
     * @param value
     * @throws XmlException, DbxmlQueryVarException
     */
    public void setInt(int value) throws XmlException, DbxmlQueryVarException{
        if(type != INT_TYPE){
            throw new DbxmlQueryVarException(DbxmlQueryVarException.WRONG_VAR_TYPE_SETTER);
        }else{
            xmlValue = new XmlValue(value);
        }
    }
    /**
     * Sets the value of the var to a new float value
     * The instance must have been constructed with a float value, an exception will be thrown otherwise.
     * @param value
     * @throws XmlException, DbxmlQueryVarException
     */
    public void setFloat(float value) throws XmlException, DbxmlQueryVarException{
        if(type != FLOAT_TYPE){
            throw new DbxmlQueryVarException(DbxmlQueryVarException.WRONG_VAR_TYPE_SETTER);
        }else{
            xmlValue = new XmlValue(value);
        }
    }
    /**
     * Sets the value of the var to a new XMLElement value
     * The instance must have been constructed with a float value, an exception will be thrown otherwise.
     * @param value
     * @throws XmlException, DbxmlQueryVarException
     */
    public void setXMLElement(XMLElement value) throws XmlException, DbxmlQueryVarException{
        if(type != XML_ELEMENT_TYPE){
            throw new DbxmlQueryVarException(DbxmlQueryVarException.WRONG_VAR_TYPE_SETTER);
        }else{
            try {
                XmlDocument doc = BdbxmlManager.getManager().getXmlManager().createDocument();
                doc.setContent(value.toString());
                xmlValue = new XmlValue(doc);
            } catch (Exception ex) {
                Logger.getLogger(DbxmlQueryVar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     *
     * @return The value of the var in XmlValue format
     */
    public XmlValue getXmlValue(){  return xmlValue;}

}
