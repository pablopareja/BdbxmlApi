/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.era7.lib.bdbxmlapitest;

import com.era7.lib.communication.model.BasicMonitor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.db.SequenceConfig;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlContainerConfig;
import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlManagerConfig;
import com.sleepycat.dbxml.XmlQueryExpression;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlTransaction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pablo Pareja Tobes
 */
public class Hola {

    //+++++++++++++++++++++++++++MAIN VARS++++++++++++++++++++++++++++++++++++++
    protected static EnvironmentConfig ENVIRONMENT_CONFIG = null;
    protected static Environment ENVIRONMENT = null;
    protected static XmlContainerConfig xmlContainerConfig = null;
    protected static XmlContainer xmlContainer = null;
    protected static XmlManagerConfig xmlManagerConfig = null;
    protected static SequenceConfig sequenceConfig = null;
    protected static Database sequenceDatabase = null;
    protected static DatabaseConfig sequenceDatabaseConfig = null;
    //protected static TransactionConfig transactionConfig = null;
    protected XmlManager xmlManager = null;

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /**
     * Environment's path  name
     */
    public static String ENVIRONMENT_PATH_NAME = "C:/era7/ppareja/Domains/GD/cosas/environments/gd_environment";
    /**
     * name of the Sequence DB
     */
    public static String SEQUENCE_DB_NAME = "sequenceDb";
    public static String SEQUENCE_DB_FILE_NAME = "sequenceDb";
    public static String CONTAINER_NAME = "gdhvn.dbxml";

    public static String MAIN_DOCUMENT_NAME = "Gdhvn.xml";
    public static String MAIN_DOCUMENT_INITIAL_CONTENT = "<gdhvn><areas></areas><tipos/><documentos/></gdhvn>";

    public static boolean USING_LOGS_DOCUMENT = false;
    public static String LOGS_DOCUMENT_NAME = "";
    public static String LOGS_DOCUMENT_INITIAL_CONTENT = "";

    public static boolean USING_ERROR_LOGS_DOCUMENT = false;
    public static String ERROR_LOGS_DOCUMENT_NAME = "";
    public static String ERROR_LOGS_DOCUMENT_INITIAL_CONTENT = "";

    //public static File CONTAINER_FILE = new File(ENVIRONMENT_PATH_NAME, CONTAINER_NAME);
    //public static File ENVIRONMENT_HOME_FILE = new File(ENVIRONMENT_PATH_NAME);
    public static String ENVIRONMENT_LOGS_PATH_NAME = ENVIRONMENT_PATH_NAME;
    /**
     *
     */
    public static int MAX_LOG_BUFFER_SIZE = 50 * 1024 * 1024;
    /**
     *
     */
    public static int MAX_CACHE_SIZE = 50 * 1024 * 1024;
    /**
     * max num of simultaneous active transactions
     */
    public static int MAX_ACTIVE_TXNS = 10000;


    //---------------------DEFAULT ENVIRONMENT CONFIGURATION VALUES---------------------------------
    public static boolean RUN_RECOVERY_BY_DEFAULT = true;
    public static int CACHE_SIZE_BY_DEFAULT = 50 * 1024 * 1024;
    public static int MAX_ACTIVE_TXNS_BY_DEFAULT = 5000;
    public static int LOG_BUFFER_SIZE_BY_DEFAULT =  50 * 1024 * 1024;
    public static boolean ENVIRONMENT_LOG_IN_MEMORY = false;
    public static boolean ENVIRONMENT_LOG_AUTO_REMOVE = true;
    public static int ENVIRONMENT_LOG_REGION_SIZE = 1024 * 1024;
    public static boolean ENVIRONMENT_TRANSACTIONAL = true;
    public static boolean ENVIRONMENT_THREADED = true;
    public static int ENVIRONMENT_TXN_MAX_ACTIVE = 5000;
    public static int DEFAULT_MAX_LOCKS = 2000;
    public static int DEFAULT_MAX_LOCKERS = 2000;
    public static int DEFAULT_MAX_LOCK_OBJECTS = 2000;
    //---------------------------------------------------------------------------------

    //--------------------SEQUENCE CONFIGURATION VALUES-------------------
    public static boolean ALLOW_CREATE_SEQUENCE_CONFIG = true;
    public static int SEQUENCE_CACHE_SIZE = 0;
    public static boolean DECREMENT_SEQUENCE_CONFIG = false;
    public static int SEQUENCE_MIN_VALUE = 0;
    public static int SEQUENCE_MAX_VALUE = 99999999;
    public static int SEQUENCE_INITIAL_VALUE = 0;
    public static boolean EXCLUSIVE_CREATE_SEQUENCE_CONFIG = false;
    public static boolean WRAP_SEQUENCE_CONFIG = false;
    //----------------------------------------------------------------------ç

    //--------------------XMLMANAGER CONFIGURATION VALUES-------------------
    public static boolean XML_MANAGER_LOG_ENABLED = false;
    public static int XML_MANAGER_LOG_LEVEL = XmlManager.LEVEL_ERROR;
    public static int XML_MANAGER_LOG_CATEGORY = XmlManager.CATEGORY_MANAGER;
    //----------------------------------------------------------------------

    //--------------------SEQUENCE DATABASE CONFIGURATION VALUES-------------------
    public static boolean ALLOW_CREATE_SEQUENCE_DATABASE_CONFIG = true;
    public static boolean TRANSACTIONAL_SEQUENCE_DATABASE_CONFIG = true;
    //----------------------------------------------------------------------

    public static void main(String args[]) throws DatabaseException, IOException{
        Hola hola = new Hola();
        Hola.closeSequenceDataBase();
        Hola.closeXmlContainer();
        hola.close();
        Hola.closeEnvironment();

    }

    /**
     *  Constructor de todas las cosas
     */
    protected Hola() throws DatabaseException, FileNotFoundException, IOException {

        init();

        //---------XmlManager----------------
        System.out.println("Creating the XmlManager.....");
        xmlManager = new XmlManager(ENVIRONMENT, xmlManagerConfig);
//        XmlManager.setLogLevel(XML_MANAGER_LOG_LEVEL, XML_MANAGER_LOG_ENABLED);
//        XmlManager.setLogCategory(XML_MANAGER_LOG_CATEGORY, XML_MANAGER_LOG_ENABLED);
        System.out.println("XmlManager created!");

        //---------Sequence Database------------------
        System.out.println("Creating/opening the sequence database....");
        sequenceDatabase = ENVIRONMENT.openDatabase(null, SEQUENCE_DB_NAME, SEQUENCE_DB_FILE_NAME, sequenceDatabaseConfig);
        System.out.println("Sequence database opened/created!!");

        //---------Container-----------------

        File containerFile = new File(ENVIRONMENT_PATH_NAME, CONTAINER_NAME);

        if (xmlManager.existsContainer(containerFile.getCanonicalPath()) == 0) {
            //Container does not exist
            System.out.println("Creando container...");
            XmlTransaction t1 = xmlManager.createTransaction();
            xmlContainer = xmlManager.createContainer(t1,containerFile.getCanonicalPath(), xmlContainerConfig);
            t1.commit();
            System.out.println("Container creado!!");
        }

        System.out.println("Opening the container: " + containerFile + ".....");
        XmlTransaction t = xmlManager.createTransaction();
        xmlContainer = xmlManager.openContainer(t,containerFile.getCanonicalPath(),xmlContainerConfig);
        t.commit();
        System.out.println("Container is transactional: " + xmlContainer.getContainerConfig().getTransactional());
        System.out.println("Container opened!");



        try {
            XmlDocument tempDoc = xmlContainer.getDocument(MAIN_DOCUMENT_NAME);
            System.out.println("tempDoc no es nulo: " + tempDoc == null);
        } catch (XmlException e) {
            if (e.getErrorCode() == XmlException.DOCUMENT_NOT_FOUND) {
                System.out.println("El documento " + MAIN_DOCUMENT_NAME + " no existe");
                System.out.println("Añadiendo documento....");
                xmlContainer.putDocument(MAIN_DOCUMENT_NAME,MAIN_DOCUMENT_INITIAL_CONTENT);
            }
        }

        //---------------------------LOGS DOCUMENT INITIALIZATION----------------------------------
        if(USING_LOGS_DOCUMENT){
            try {
                XmlDocument logsDoc = xmlContainer.getDocument(LOGS_DOCUMENT_NAME);
                System.out.println("logsDoc no es nulo: " + logsDoc == null);
            } catch (XmlException e) {
                if (e.getErrorCode() == XmlException.DOCUMENT_NOT_FOUND) {
                    System.out.println("El documento " + LOGS_DOCUMENT_NAME + " no existe");
                    System.out.println("Añadiendo documento....");
                    xmlContainer.putDocument(LOGS_DOCUMENT_NAME,LOGS_DOCUMENT_INITIAL_CONTENT);
                }
            }
        }
        //----------------------ERROR LOGS DOCUMENT INITIALIZATION----------------------------------
        if(USING_ERROR_LOGS_DOCUMENT){
            try {
                XmlDocument errorLogsDoc = xmlContainer.getDocument(ERROR_LOGS_DOCUMENT_NAME);
                System.out.println("errorLogsDoc no es nulo: " + errorLogsDoc == null);
            } catch (XmlException e) {
                if (e.getErrorCode() == XmlException.DOCUMENT_NOT_FOUND) {
                    System.out.println("El documento " + ERROR_LOGS_DOCUMENT_NAME + " no existe");
                    System.out.println("Añadiendo documento....");
                    xmlContainer.putDocument(ERROR_LOGS_DOCUMENT_NAME,ERROR_LOGS_DOCUMENT_INITIAL_CONTENT);
                }
            }
        }

    }

    protected static synchronized void init(){
        if(ENVIRONMENT_CONFIG == null){
            ENVIRONMENT_CONFIG = initEnvironmentConfiguration();
        }
        if(ENVIRONMENT == null){
            ENVIRONMENT = initEnvironment();
        }
        if(xmlContainerConfig == null){
            xmlContainerConfig = initXmlContainerConfiguration();
        }
        if(xmlManagerConfig == null){
            xmlManagerConfig = initXmlManagerConfiguration();
        }
        if(sequenceConfig == null){
            sequenceConfig = initSequenceConfiguration();
        }
        if(sequenceDatabaseConfig == null){
            sequenceDatabaseConfig = initSequenceDatabaseConfig();
        }

    }

    /*
     *  INIT ENVIRONMENT CONFIGURATION
     */
    protected static EnvironmentConfig initEnvironmentConfiguration() {

        System.out.println("Initializing environment configuration....");

        EnvironmentConfig environmentConfig = new EnvironmentConfig();

        environmentConfig.setErrorStream(System.err);
        // If the environment does not exists, create it
        environmentConfig.setAllowCreate(true);
        // Turn on the shared memory region
        environmentConfig.setInitializeCache(true);
        // Turn on the locking subsystem
        environmentConfig.setInitializeRegions(true);
        environmentConfig.setInitializeLocking(true);
        // Turn on the logging subsystem
        environmentConfig.setInitializeLogging(true);
        //environmentConfig.setThreaded(ENVIRONMENT_THREADED);
        //environmentConfig.setMaxMutexes(1000000);
        //environmentConfig.setMutexIncrement(1000000);
        //environmentConfig.setMaxLockers(DEFAULT_MAX_LOCKERS);
        //environmentConfig.setMaxLocks(DEFAULT_MAX_LOCKS);
        //environmentConfig.setMaxLockObjects(DEFAULT_MAX_LOCK_OBJECTS);

        //----------------------Cache configuration--------------------
        //environmentConfig.setCacheCount(1);
        environmentConfig.setCacheMax(MAX_CACHE_SIZE);
        environmentConfig.setCacheSize(CACHE_SIZE_BY_DEFAULT);
        //------------------------------------------------------------

        //----------------------Logging configuration--------------------
        //environmentConfig.setLogRegionSize(ENVIRONMENT_LOG_REGION_SIZE);
        //environmentConfig.setLogBufferSize(LOG_BUFFER_SIZE_BY_DEFAULT);
        environmentConfig.setLogDirectory(new File(ENVIRONMENT_LOGS_PATH_NAME));
        environmentConfig.setLogInMemory(ENVIRONMENT_LOG_IN_MEMORY);
        environmentConfig.setLogAutoRemove(ENVIRONMENT_LOG_AUTO_REMOVE);
        //------------------------------------------------------------

        //config.setC

        //----------------------Transactions configuration--------------------
        //Turn on the transactional subsystem
        environmentConfig.setTransactional(ENVIRONMENT_TRANSACTIONAL);
        environmentConfig.setTxnMaxActive(ENVIRONMENT_TXN_MAX_ACTIVE);
        environmentConfig.setTxnNoSync(false);
        environmentConfig.setTxnNotDurable(false);
        environmentConfig.setTxnSnapshot(true);
        environmentConfig.setTxnWriteNoSync(false);
        //----------------------------------------------------------------

        //-------------------Run recovery configuration-------------
        environmentConfig.setRunRecovery(RUN_RECOVERY_BY_DEFAULT);
        //----------------------------------------------------------

        System.out.println("EnvironmentConfig initialized!");

        return environmentConfig;
    }
    /*
     *  INIT ENVIRONMENT
     */

    protected static Environment initEnvironment() {

        Environment env = null;
        //---------Environment--------------
        System.out.println("Creating the environment....");
        try {
            env = new Environment(new File(ENVIRONMENT_PATH_NAME), ENVIRONMENT_CONFIG);
        } catch (DatabaseException ex) {
            Logger.getLogger(Hola.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Hola.class.getName()).log(Level.SEVERE, null, ex);
        }

        return env;
    }

    /*
     *  CONFIGURE CONTAINER
     */
    protected static XmlContainerConfig initXmlContainerConfiguration() {

        System.out.println("Initializing XmlContainer configuration....");

        XmlContainerConfig config = new XmlContainerConfig();

        config.setContainerType(XmlContainer.NodeContainer);
        config.setTransactional(ENVIRONMENT_TRANSACTIONAL);
        config.setIndexNodes(XmlContainerConfig.On);
        config.setAllowValidation(false);
        config.setStatisticsEnabled(XmlContainerConfig.Off);

        System.out.println("XmlContainer configuration initialized!");

        return config;
    }


    /*
     *  INIT XML MANAGER CONFIGURATION
     */
    protected static XmlManagerConfig initXmlManagerConfiguration() {

        System.out.println("Initializing XmlManager configuration....");

        XmlManagerConfig config = new XmlManagerConfig();

        config.setAdoptEnvironment(false);
        config.setAllowAutoOpen(true);
        config.setAllowExternalAccess(true);

        System.out.println("XmlManager configuration initialized!");

        return config;
    }

    /*
     *  INIT SEQUENCE CONFIGURATION
     */
    protected static SequenceConfig initSequenceConfiguration(){

        System.out.println("Initializing Sequence configuration....");

        SequenceConfig config = new SequenceConfig();

        config.setAllowCreate(ALLOW_CREATE_SEQUENCE_CONFIG);
        config.setCacheSize(SEQUENCE_CACHE_SIZE);
        config.setDecrement(DECREMENT_SEQUENCE_CONFIG);
        config.setRange(SEQUENCE_MIN_VALUE, SEQUENCE_MAX_VALUE);
        config.setInitialValue(SEQUENCE_INITIAL_VALUE);
        config.setExclusiveCreate(EXCLUSIVE_CREATE_SEQUENCE_CONFIG);
        config.setWrap(WRAP_SEQUENCE_CONFIG);

        System.out.println("Sequence configuration initialized!");

        return config;
    }

    /*
     *  INIT SEQUENCE DATABASE CONFIG
     */
    protected static DatabaseConfig initSequenceDatabaseConfig(){

        DatabaseConfig config = new DatabaseConfig();

        config.setAllowCreate(ALLOW_CREATE_SEQUENCE_DATABASE_CONFIG);
        config.setTransactional(TRANSACTIONAL_SEQUENCE_DATABASE_CONFIG);
        config.setType(DatabaseType.BTREE);

        return config;
    }
  


    /**
     * Closes all resources associated to this manager
     */
    public void close() {
        //xmlContainer.close();
        try {
            xmlManager.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public XmlManager getXmlManager() {
        return xmlManager;
    }
    public Database getDatabase(){
        return sequenceDatabase;
    }
    public SequenceConfig getSequenceConfig(){
        return sequenceConfig;
    }

    public synchronized static void closeSequenceDataBase()
    {
        if(sequenceDatabase != null){
            try {
                System.out.println("Closing the sequence database...");
                sequenceDatabase.close(false);
                System.out.println("Sequence database closed! :)");
            } catch (DatabaseException ex) {
                System.out.println("There was a problem closing the sequence database... :(");
                Logger.getLogger(Hola.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static synchronized void closeXmlContainer(){
        if(xmlContainer != null){
            try {
                System.out.println("Closing the XmlContainer....");
                xmlContainer.close();
                System.out.println("XmlContainer closed! :)");
            } catch (Exception ex) {
                System.out.println("There was a problem closing the XmlContainer... :(");
                Logger.getLogger(Hola.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public synchronized static void closeEnvironment()
    {
        if(ENVIRONMENT != null){
            try {
                System.out.println("Closing the environment....");
                ENVIRONMENT.close();
                System.out.println("Environment closed! :)");
            } catch (DatabaseException ex) {
                System.out.println("There was a problem closing the environment... :(");
                Logger.getLogger(Hola.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}