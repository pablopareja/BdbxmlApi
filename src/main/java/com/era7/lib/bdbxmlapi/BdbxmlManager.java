/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.lib.bdbxmlapi;

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
public class BdbxmlManager {

    private static BdbxmlManager singleton = null;

    //+++++++++++++++++++++++++++MAIN VARS++++++++++++++++++++++++++++++++++++++
    protected static EnvironmentConfig ENVIRONMENT_CONFIG = null;
    protected static Environment ENVIRONMENT = null;
    protected static XmlContainerConfig xmlContainerConfig = null;
    
    protected static XmlManagerConfig xmlManagerConfig = null;
    protected static SequenceConfig sequenceConfig = null;
    
    protected static DatabaseConfig sequenceDatabaseConfig = null;
    //protected static TransactionConfig transactionConfig = null;
    protected static XmlContainer XML_CONTAINER = null;
    protected static Database SEQUENCE_DATABASE = null;
    protected static XmlManager XML_MANAGER = null;
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /**
     * Environment's path  name
     */
    public static String ENVIRONMENT_PATH_NAME = "";
    /**
     * name of the Sequence DB
     */
    public static String SEQUENCE_DB_NAME = "";
    public static String SEQUENCE_DB_FILE_NAME = "";
    public static String CONTAINER_NAME = "";
    public static String MAIN_DOCUMENT_NAME = "";
    public static String MAIN_DOCUMENT_INITIAL_CONTENT = "";
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
    public static int LOG_BUFFER_SIZE_BY_DEFAULT = 50 * 1024 * 1024;
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
    //--------------------XMLCONTAINER CONFIGURATION VALUES-------------------
    public static boolean XML_CONTAINER_INDEX_NODES_AND_STATISTICS_ENABLED = true;
    public static boolean XML_CONTAINER_ALLOW_VALIDATION = false;
    //----------------------------------------------------------------------
    //--------------------SEQUENCE DATABASE CONFIGURATION VALUES-------------------
    public static boolean ALLOW_CREATE_SEQUENCE_DATABASE_CONFIG = true;
    public static boolean TRANSACTIONAL_SEQUENCE_DATABASE_CONFIG = true;
    //----------------------------------------------------------------------

    /**
     *  Constructor de todas las cosas
     */
    private BdbxmlManager() throws DatabaseException, FileNotFoundException, IOException, DbxmlException {

        init();

        initXmlManager();

        initSequenceDataBase();        

        //---------Container-----------------        

        File containerFile = new File(ENVIRONMENT_PATH_NAME, CONTAINER_NAME);

        if (XML_MANAGER.existsContainer(containerFile.getCanonicalPath()) != 0) {

            System.out.println("Opening the container: " + containerFile + ".....");
            XML_CONTAINER = XML_MANAGER.openContainer(containerFile.getCanonicalPath(), xmlContainerConfig);
            System.out.println("Container is transactional: " + XML_CONTAINER.getContainerConfig().getTransactional());
            System.out.println("Container opened!");


            try {
                XmlDocument tempDoc = XML_CONTAINER.getDocument(MAIN_DOCUMENT_NAME);
                System.out.println("tempDoc no es nulo: " + tempDoc == null);
            } catch (XmlException e) {
                if (e.getErrorCode() == XmlException.DOCUMENT_NOT_FOUND) {
                    System.out.println("El documento " + MAIN_DOCUMENT_NAME + " no existe");
                    System.out.println("Añadiendo documento....");
                    XML_CONTAINER.putDocument(MAIN_DOCUMENT_NAME, MAIN_DOCUMENT_INITIAL_CONTENT);
                }
            }

            //---------------------------LOGS DOCUMENT INITIALIZATION----------------------------------
            if (USING_LOGS_DOCUMENT) {
                try {
                    XmlDocument logsDoc = XML_CONTAINER.getDocument(LOGS_DOCUMENT_NAME);
                    System.out.println("logsDoc no es nulo: " + logsDoc == null);
                } catch (XmlException e) {
                    if (e.getErrorCode() == XmlException.DOCUMENT_NOT_FOUND) {
                        System.out.println("El documento " + LOGS_DOCUMENT_NAME + " no existe");
                        System.out.println("Añadiendo documento....");
                        XML_CONTAINER.putDocument(LOGS_DOCUMENT_NAME, LOGS_DOCUMENT_INITIAL_CONTENT);
                    }
                }
            }
            //----------------------ERROR LOGS DOCUMENT INITIALIZATION----------------------------------
            if (USING_ERROR_LOGS_DOCUMENT) {
                try {
                    XmlDocument errorLogsDoc = XML_CONTAINER.getDocument(ERROR_LOGS_DOCUMENT_NAME);
                    System.out.println("errorLogsDoc no es nulo: " + errorLogsDoc == null);
                } catch (XmlException e) {
                    if (e.getErrorCode() == XmlException.DOCUMENT_NOT_FOUND) {
                        System.out.println("El documento " + ERROR_LOGS_DOCUMENT_NAME + " no existe");
                        System.out.println("Añadiendo documento....");
                        XML_CONTAINER.putDocument(ERROR_LOGS_DOCUMENT_NAME, ERROR_LOGS_DOCUMENT_INITIAL_CONTENT);
                    }
                }

            }


        } else {
            throw new DbxmlException(DbxmlException.CONTAINER_DOES_NOT_EXIST);
        }

    }

    protected void initXmlManager() throws XmlException 
    {
        if(XML_MANAGER == null){
            //---------XmlManager----------------
            System.out.println("Creating the XmlManager.....");
            XML_MANAGER = new XmlManager(ENVIRONMENT, xmlManagerConfig);
            XmlManager.setLogLevel(XML_MANAGER_LOG_LEVEL, XML_MANAGER_LOG_ENABLED);
            XmlManager.setLogCategory(XML_MANAGER_LOG_CATEGORY, XML_MANAGER_LOG_ENABLED);
            System.out.println("XmlManager created!");
        }
    }

    protected void initSequenceDataBase() throws DatabaseException, FileNotFoundException 
    {
        if(SEQUENCE_DATABASE == null)
        {
            //---------Sequence Database------------------
            System.out.println("Creating/opening the sequence database....");
            SEQUENCE_DATABASE = ENVIRONMENT.openDatabase(null, SEQUENCE_DB_NAME, SEQUENCE_DB_FILE_NAME, sequenceDatabaseConfig);
            System.out.println("Sequence database opened/created!!");
        }
    }

    protected static synchronized void init() {
        if (ENVIRONMENT_CONFIG == null) {
            ENVIRONMENT_CONFIG = initEnvironmentConfiguration();
        }
        if (ENVIRONMENT == null) {
            ENVIRONMENT = initEnvironment();
        }
        if (xmlContainerConfig == null) {
            xmlContainerConfig = initXmlContainerConfiguration();
        }
        if (xmlManagerConfig == null) {
            xmlManagerConfig = initXmlManagerConfiguration();
        }
        if (sequenceConfig == null) {
            sequenceConfig = initSequenceConfiguration();
        }
        if (sequenceDatabaseConfig == null) {
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
        //environmentConfig.setTxnNoSync(false);
        //environmentConfig.setTxnNotDurable(false);
        //environmentConfig.setTxnSnapshot(true);
        //environmentConfig.setTxnWriteNoSync(false);
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
            Logger.getLogger(BdbxmlManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BdbxmlManager.class.getName()).log(Level.SEVERE, null, ex);
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
        
        if(XML_CONTAINER_INDEX_NODES_AND_STATISTICS_ENABLED)
        {
            config.setIndexNodes(XmlContainerConfig.On);
            config.setStatisticsEnabled(XmlContainerConfig.On);
        }else
        {
            config.setIndexNodes(XmlContainerConfig.Off);
            config.setStatisticsEnabled(XmlContainerConfig.Off);
        }
        
        config.setAllowValidation(XML_CONTAINER_ALLOW_VALIDATION);
        

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
    protected static SequenceConfig initSequenceConfiguration() {

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
    protected static DatabaseConfig initSequenceDatabaseConfig() {

        DatabaseConfig config = new DatabaseConfig();

        config.setAllowCreate(ALLOW_CREATE_SEQUENCE_DATABASE_CONFIG);
        config.setTransactional(TRANSACTIONAL_SEQUENCE_DATABASE_CONFIG);
        config.setType(DatabaseType.BTREE);

        return config;
    }

    /**
     * Creates a new Xml transaction
     * @return
     * @throws XmlException
     */
    public XmlTransaction createTransaction() throws XmlException {

        XmlTransaction xmlTransaction = XML_MANAGER.createTransaction();
        return xmlTransaction;

    }

    /**
     * Executes the query defined by queryString
     * @param queryString
     * @param transaction
     * @param commitTransaction If true, commits the transaction after executing the query
     * @return
     * @throws XmlException
     */
    public XmlResults executeQuery(XmlTransaction transaction, DbxmlQuery query,
            boolean commitTransaction) throws XmlException, DbxmlException {


        XmlQueryExpression queryExpression = query.getXmlQueryExpression();

        if (queryExpression.isUpdateExpression()) {
            throw new DbxmlException(DbxmlException.UPDATE_ATTEMPTED_IN_READ_QUERY);
        } else {
            XmlResults results = null;

            try {
                if (transaction != null) {
                    results = queryExpression.execute(transaction, query.getXmlQueryContext());
                    if (commitTransaction) {
                        transaction.commit();
                    }
                } else {
                    results = queryExpression.execute(query.getXmlQueryContext());
                }
            } catch (Exception e) {
                e.printStackTrace();

                //Aborting the transaction since there was an exception and it
                //could not be committed
                if (transaction != null) {
                    System.out.println("Aborting the transaction due to underlying exception... ");
                    transaction.abort();
                    System.out.println("Transaction aborted :(");
                }
                if (results != null) {
                    System.out.println("Deleting the XmlResults due to aborting the transaction...");
                    results.delete();
                    System.out.println("XmlResults deleted :(");
                }
                if (queryExpression != null) {
                    System.out.println("Deleting the QueryExpression due to aborting the transaction...");
                    queryExpression.delete();
                    System.out.println("Query expression deleted :(");
                }
            }

            return results;
        }
    }

    /**
     * Executes the update query defined by queryString
     * @param queryString
     * @param transaction
     * @param commitTransaction If true, commits the transaction after executing the query
     * @return
     * @throws XmlException
     */
    public XmlResults executeUpdateQuery(XmlTransaction transaction, DbxmlQuery query,
            boolean commitTransaction) throws XmlException, DbxmlException {


        XmlQueryExpression queryExpression = query.getXmlQueryExpression();

        if (!queryExpression.isUpdateExpression()) {
            throw new DbxmlException(DbxmlException.UPDATE_QUERY_EXPECTED);
        } else {
            XmlResults results = null;

            try {
                BasicMonitor.getMonitor().getLock();
                if (transaction != null) {
                    results = queryExpression.execute(transaction, query.getXmlQueryContext());
                    if (commitTransaction) {
                        transaction.commit();
                    }
                } else {
                    results = queryExpression.execute(query.getXmlQueryContext());
                }
            } catch (Exception e) {
                e.printStackTrace();

                //Aborting the transaction since there was an exception and it
                //could not be committed
                if (transaction != null) {
                    System.out.println("Aborting the transaction due to underlying exception... ");
                    transaction.abort();
                    System.out.println("Transaction aborted :(");
                }
                if (results != null) {
                    System.out.println("Deleting the XmlResults due to aborting the transaction...");
                    results.delete();
                    System.out.println("XmlResults deleted :(");
                }
                if (queryExpression != null) {
                    System.out.println("Deleting the QueryExpression due to aborting the transaction...");
                    queryExpression.delete();
                    System.out.println("Query expression deleted :(");
                }
            } finally {
                BasicMonitor.getMonitor().releaseLock();
            }

            return results;
        }

    }

    /**
     * Closes all resources associated to this manager
     */
    public static synchronized void close() {
        //xmlContainer.close();
        if(XML_MANAGER != null)
        {
            try {
                closeSequenceDataBase();
                closeXmlContainer();
                System.out.println("Closing XML_MANAGER...");
                XML_MANAGER.close();
                System.out.println("XML_MANAGER closed! :)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        

    }

    public XmlManager getXmlManager() {
        return XML_MANAGER;
    }

    public Database getDatabase() {
        return SEQUENCE_DATABASE;
    }

    public SequenceConfig getSequenceConfig() {
        return sequenceConfig;
    }

    private static synchronized void closeSequenceDataBase() {
        if (SEQUENCE_DATABASE != null) {
            try {
                System.out.println("Closing the sequence database...");
                SEQUENCE_DATABASE.close(false);
                System.out.println("Sequence database closed! :)");
            } catch (DatabaseException ex) {
                System.out.println("There was a problem closing the sequence database... :(");
                Logger.getLogger(BdbxmlManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static synchronized void closeXmlContainer() {
        if (XML_CONTAINER != null) {
            try {
                System.out.println("Closing the XmlContainer....");
                XML_CONTAINER.close();
                System.out.println("XmlContainer closed! :)");
            } catch (Exception ex) {
                System.out.println("There was a problem closing the XmlContainer... :(");
                Logger.getLogger(BdbxmlManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public synchronized static void closeEnvironment() {
        if (ENVIRONMENT != null) {
            try {
                System.out.println("Closing the environment....");
                ENVIRONMENT.close();
                System.out.println("Environment closed! :)");

                singleton = null;
                
            } catch (DatabaseException ex) {
                System.out.println("There was a problem closing the environment... :(");
                Logger.getLogger(BdbxmlManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static synchronized void createContainer() throws XmlException, IOException {

        init();

        //---------XmlManager----------------
        System.out.println("Creating temporal XmlManager.....");
        XmlManager manager = new XmlManager(ENVIRONMENT, xmlManagerConfig);
        XmlManager.setLogLevel(XML_MANAGER_LOG_LEVEL, XML_MANAGER_LOG_ENABLED);
        XmlManager.setLogCategory(XML_MANAGER_LOG_CATEGORY, XML_MANAGER_LOG_ENABLED);
        System.out.println("Temporal XmlManager created!");

        File containerFile = new File(ENVIRONMENT_PATH_NAME, CONTAINER_NAME);

        if (manager.existsContainer(containerFile.getCanonicalPath()) == 0){
             //Container does not exist
            System.out.println("Creando container...");
            XmlContainer container = manager.createContainer(containerFile.getCanonicalPath(), xmlContainerConfig);
            System.out.println("Container created!!  :D");

            //--->Closing everthing after creating the container cause otherwise
            //--->Database handles remain open :P
            container.close();

        }else{
            System.out.println("The container was already created.... :)");
        }       

        manager.close();
        BdbxmlManager.closeEnvironment();

        singleton = null;
        
    }


    public static synchronized BdbxmlManager getManager() throws DatabaseException, FileNotFoundException, IOException, DbxmlException
    {
        if(singleton == null){
            singleton = new BdbxmlManager();
        }
        return singleton;
    }
}
