/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.proycomp.pluging.loader.services;

import co.com.agilesoft.pluging.log.Log;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author John Osorio <johnjairoosorio2008@gmail.com>
 */
public class LogService {
 
    private static LogService service;
    private ServiceLoader serviceLoader;
 
    private LogService() {
        serviceLoader = ServiceLoader.load(Log.class);
    }
 
    public static synchronized LogService getInstance() {
        if (service == null) {
            service = new LogService();
        }
        return service;
    }
 
    public List<Log> getLoggers() {
        List<Log> loggers = new ArrayList<>();
        Iterator<Log> it = serviceLoader.iterator();
        while (it.hasNext()) {
            loggers.add(it.next());
        }
        return loggers;
    }
 
    public Log getFirstAvailableLogger() {
        Log log = null;
        Iterator<Log> it = serviceLoader.iterator();
        while (it.hasNext()) {
            log = it.next();
            break;
        }
        return log;
    }
 
    public void reload() throws MalformedURLException, IOException, ClassNotFoundException {
 
        // 1. Read the WEB-INF/lib directory and get a list of jar files.
        // 2. Examine the jar files and look for META-INF/services directories.
        // 3. If the directory has services, read the file names from each service entry.
        // 4. Store the class names in a list.
        // 5. Create a list of URLs for files.
        // 6. Create a URLClassLoader, add the parent ClassLoader and URLs.
        // 7. Call URLClassloader.loadClass(String name) using saved names.
        // 8. Create a new ServiceLoader instance with the URLClassLoader, and call load on the interface.
        ServletContext context = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession().getServletContext();
        //Path webInfLibDirectory = Paths.get(context.getRealPath("WEB-INF/lib"));
        Path webInfLibDirectory = Paths.get(((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession().getServletContext().getRealPath("/WEB-INF/lib"));
        URLClassLoader urlcl;
        List<URL> jarURLs = new ArrayList<>();
        FileSystemProvider provider = getZipFileSystemProvider();
        List<String> implementationsToLoad = new ArrayList<>();
 
        if (Files.exists(webInfLibDirectory, LinkOption.NOFOLLOW_LINKS)) {
            List<Path> files = listJars(webInfLibDirectory);
 
            for (Path p : files) {
                System.out.println("LOCATED JAR " + p.toFile().getName());
                jarURLs.add(p.toUri().toURL());
                FileSystem fs = provider.newFileSystem(p, new HashMap<String, Object>());
                Path serviceDirectory = fs.getPath("/META-INF", "services");
                System.out.println("SCANNING SERVICES");
 
                if (Files.exists(serviceDirectory)) {
                    DirectoryStream<Path> serviceListings = Files.newDirectoryStream(serviceDirectory);
 
                    for (Path px : serviceListings) {
                        List<String> services = Files.readAllLines(px, Charset.forName("UTF-8"));
                        System.out.println(MessageFormat.format("SERVICES FOUND: {0}", Arrays.toString(services.toArray())));
                        implementationsToLoad.addAll(services);
                    }
                }
            }
 
            urlcl = new URLClassLoader(jarURLs.toArray(new URL[jarURLs.size()]), context.getClassLoader());
 
            load(implementationsToLoad, urlcl);
 
            serviceLoader = ServiceLoader.load(Log.class, urlcl);
            Iterator<Log> it = serviceLoader.iterator();
            while (it.hasNext()) {
                System.out.println(it.next().getClass().getName());
            }
        }
    }
 
    private List<Path> listJars(Path path) throws IOException {
        List<Path> jars = new ArrayList<>();
        DirectoryStream<Path> ds = Files.newDirectoryStream(path, "*.jar");
 
        for (Path child : ds) {
            if (!Files.isDirectory(child)) {
                jars.add(child);
            }
        }
 
        return jars;
    }
 
    private void load(final List<String> FQCN, final ClassLoader classLoader)
            throws ClassNotFoundException {
        for (String s : FQCN) {
            try {
                System.out.println(MessageFormat.format("LOAD CLASS {0}", s));
                Class<?> clazz = classLoader.loadClass(s);
                System.out.println(MessageFormat.format("CLASS {0} LOADED", clazz.getName()));
            } catch (Exception ex) {
                System.out.println("No se cargara la implementacion: " + ex.getMessage());
            }
        }
    }
 
    private static FileSystemProvider getZipFileSystemProvider() {
        for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
            if ("jar".equals(provider.getScheme())) {
                return provider;
            }
        }
        return null;
    }
 
    private void info(final String message) {
        getFirstAvailableLogger().info(message);
    }
}