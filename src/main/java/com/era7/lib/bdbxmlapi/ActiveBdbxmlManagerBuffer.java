/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.era7.lib.bdbxmlapi;

import java.util.HashSet;

/**
 *
 * @author pablo
 */
public final class ActiveBdbxmlManagerBuffer {


    private static HashSet<BdbxmlManager> BUFFER = new HashSet<BdbxmlManager>();

    /**
     *  Closes every DbxmlService
     */
    public static synchronized void closeAllDbxmlServices(){

        System.out.println("Iniciando el close de los berkeleyDbxmlManagers... (hay " + BUFFER.size() + " abiertos)");

        for(BdbxmlManager manager : BUFFER){
            System.out.println("Closing BdbxmlManager " + manager.hashCode());
            manager.close();
            System.out.println("BdbxmlManager closed! :)");
        }

        BUFFER.clear();
        
        System.out.println("Close de los dbxmlServices finalizado... (hay " + BUFFER.size() + " abiertos)");
    }
    /**
     * Adds a service to the buffer
     * @param service
     */
    public static void addBerkeleyDbxmlManager(BdbxmlManager manager){
        BUFFER.add(manager);
    }

    public static synchronized void closeBdbxmlManager(BdbxmlManager manager){
        System.out.println("Closing BdbxmlManager " + manager.hashCode());
        manager.close();
        System.out.println("BdbxmlManager closed! :)");
        BUFFER.remove(manager);
    }

    public static int getActiveBdbxmlManagerBufferSize(){
        return BUFFER.size();
    }
}
