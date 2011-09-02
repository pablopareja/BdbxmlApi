/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.era7.lib.bdbxmlapi;

/**
 *
 * @author pablo
 */
public class DbxmlQueryVarException extends Exception {

    public static final String  WRONG_VAR_TYPE_SETTER = "Wrong setter used for the instance of DbxmlQueryVar";

    public DbxmlQueryVarException(String value){
        super(value);
    }

}
