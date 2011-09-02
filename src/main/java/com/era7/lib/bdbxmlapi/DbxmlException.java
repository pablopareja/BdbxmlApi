/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.era7.lib.bdbxmlapi;

/**
 *
 * @author pablo
 */
public class DbxmlException extends Exception{


    public static final String UPDATE_ATTEMPTED_IN_READ_QUERY = "Update attempted in an only-read query";
    public static final String UPDATE_QUERY_EXPECTED = "An update query was expected.";
    public static final String CONTAINER_DOES_NOT_EXIST = "El container especificado no existe! :(";

    public DbxmlException(String value){
        super(value);
    }
}
