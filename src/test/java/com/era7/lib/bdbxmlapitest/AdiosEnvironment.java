/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.era7.lib.bdbxmlapitest;

import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.dbxml.XmlManager;
import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author pablo
 */
public class AdiosEnvironment {

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
    
    protected static EnvironmentConfig ENVIRONMENT_CONFIG = null;
    protected static Environment ENVIRONMENT = null;

    public static void main(String[] args) throws DatabaseException, FileNotFoundException{

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

        ENVIRONMENT = new Environment(new File(ENVIRONMENT_PATH_NAME), environmentConfig);
        System.out.println("ENVIRONMENT created");
        ENVIRONMENT.close();
        System.out.println("ENVIRONMENT closed!! :)");
    }
}
