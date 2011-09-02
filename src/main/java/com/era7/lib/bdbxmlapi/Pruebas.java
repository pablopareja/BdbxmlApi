/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.era7.lib.bdbxmlapi;

import com.era7.lib.era7xmlapi.model.XMLElement;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlTransaction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class Pruebas {

    public static void main(String[] args){
        try {

            /*EnvironmentConfig config = new EnvironmentConfig();
            config.setCacheSize(50 * 1024 * 1024); // 50MB
            config.setAllowCreate(true);
            config.setInitializeCache(true);
            config.setTransactional(true);
            config.setInitializeLocking(true);
            config.setInitializeLogging(true);
            config.setErrorStream(System.err);
            Environment dbEnv_ = new Environment(new File("C:/era7/ppareja/Domains/GD/cosas/environments/gd_environment/"), config);

             */
            /*
            BdbxmlManager manager = new BdbxmlManager();

            XmlTransaction txn = manager.createTransaction();


            //XmlResults results = manager.executeQuery("doc('dbxml:/gdhvn.dbxml/AreaSample.xml')/area", txn, false);

            //XMLElement elem = new XMLElement(results.next());

            //System.out.println(elem.toString());*/
            

        } catch (Exception ex) {
            Logger.getLogger(Pruebas.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}
