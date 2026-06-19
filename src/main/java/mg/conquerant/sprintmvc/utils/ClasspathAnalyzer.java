package mg.conquerant.sprintmvc.utils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

/**
 * This class is made for scanning the fullclasspath of the application 
 */
public class ClasspathAnalyzer {
    private List<Class<?>> classList; 

    /**
     * The method scan the classpath of the actual working application
     */ 
    public List<Class<?>> classList(){
        classList = new ArrayList<>();
        ClassLoader classLoader = ClasspathAnalyzer.class.getClassLoader();
        URL resourceURL = classLoader.getResource("");
        try {
            URI resource = resourceURL.toURI();
            File rootDirectory = new File(resource);
            String rootDirectoryPath = rootDirectory.getPath() + File.separator;
            findClass(rootDirectory.listFiles() , rootDirectoryPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classList;
    }

    /**
     * Do a recursive search of all .class file for getting all class of the application
     * @toAnalyze  The list of element of the parent element to scan
     * @rootDirectoryPath  the root directory of the project for removing it from 
     *  the class url
     */ 
    public void findClass(File[] toAnalyze , String rootDirectoryPath){
        if ( toAnalyze == null ) return;
        for(File file : toAnalyze){
            if ( !file.isDirectory() ){
                String temp = file.getPath();
                String className = temp.replace(rootDirectoryPath , "")
                                    .replace(File.separator , ".")
                                    .replace(".class" , "");
                try {
                    classList.add(Class.forName(className));
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                findClass(file.listFiles() , rootDirectoryPath);
            }
        }
    }
}
