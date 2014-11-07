/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.com.agilesoft.logplugin;

import co.com.agilesoft.pluging.log.Log;

/**
 * 
 * @author John Osorio <johnjairoosorio2008@gmail.com>
 */
public class LogImpl implements Log {
 
    @Override
    public void trace(String message) {
        log("TRACE --> " + message);
    }
 
    @Override
    public void debug(String message) {
        log("DEBUG --> " + message);
    }
 
    @Override
    public void info(String message) {
        log("INFO --> " + message);
    }
 
    @Override
    public void warn(String message) {
        log("WARN --> " + message);
    }
 
    @Override
    public void severe(String message) {
        log("SEVERE --> " + message);
    }
 
    @Override
    public void error(String message) {
        log("ERROR --> " + message);
    }
 
    @Override
    public void fatal(String message) {
        log("FATAL --> " + message);
    }
 
    private void log(String message) {
        System.out.println(message);
    }
}
