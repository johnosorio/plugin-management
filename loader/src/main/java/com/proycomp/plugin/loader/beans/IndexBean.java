/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proycomp.plugin.loader.beans;

import co.com.agilesoft.pluging.log.Log;
import com.proycomp.pluging.loader.services.LogService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author John Osorio <johnjairoosorio2008@gmail.com>
 */
@ManagedBean
@RequestScoped
public class IndexBean {

    private Log log;
    private LogService service;

    private UploadedFile file;

    public IndexBean(){
        service = LogService.getInstance();
    }
    
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void upload() {
        if (file != null) {
            Path path = Paths.get(((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession().getServletContext().getRealPath("/WEB-INF/lib"), this.file.getFileName());

            try {
                if( Files.exists(path) ){
                    Files.delete(path);
                }
                Files.copy(this.file.getInputstream(), path);
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
            FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void callLogger() {
        log = service.getFirstAvailableLogger();
        log.info("Constructor called.");
    }

    public String getLoggerName() {
        log.info("getLoggerName() called.");
        return log.getClass().getName();
    }

    public List<Log> getLoggers() {
        return service.getLoggers();
    }

    public String reload() {
        try {
            service.reload();
            System.out.println("LogService reloaded.");
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Error cargando clases para el plugin log." + ex.getMessage());
        }
        return null;
    }
}
