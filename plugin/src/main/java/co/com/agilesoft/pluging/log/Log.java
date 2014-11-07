/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.com.agilesoft.pluging.log;

/**
 * 
 * @author John Osorio <johnjairoosorio2008@gmail.com>
 */
public interface Log {
 
    void trace(String message);
 
    void debug(String message);
 
    void info(String message);
 
    void warn(String message);
 
    void severe(String message);
 
    void error(String message);
 
    void fatal(String message);
}