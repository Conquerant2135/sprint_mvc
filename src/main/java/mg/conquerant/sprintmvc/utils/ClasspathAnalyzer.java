package mg.conquerant.sprintmvc.utils;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is made for scanning the fullclasspath of the application
 */
public class ClasspathAnalyzer {

    private List<Class<?>> classList;

    /**
     * The method scan the classpath of the actual working application
     * Get all the classes present
     */
    public List<Class<?>> classList(String targetPackage) {
        classList = new ArrayList<>();
        String targetPackagePath = targetPackage.replace(".", File.separator);
        ClassLoader classLoader = ClasspathAnalyzer.class.getClassLoader();
        URL resourceURL = classLoader.getResource(targetPackagePath);
        try {
            URI resource = resourceURL.toURI();
            File rootDirectory = new File(resource);
            String rootDirectoryPath;
            if (targetPackage.trim().isEmpty()) {
                rootDirectoryPath = rootDirectory.getPath() + File.separator;
            } else {
                rootDirectoryPath = getRootPath();
            }
            findClass(rootDirectory.listFiles(), rootDirectoryPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classList;
    }

    private static String getRootPath() {
        String baseName = "";
        ClassLoader cl = ClasspathAnalyzer.class.getClassLoader();
        URL baseUrl = cl.getResource("");
        try {
            URI basePath = baseUrl.toURI();
            baseName = basePath.getPath();
            baseName = baseName.replace("/", File.separator);
            baseName = baseName.substring(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseName;
    }

    /**
     * Do a recursive search of all .class file for getting all class of the
     * package to scan
     * 
     * @param toAnalyze         The list of element of the parent element to scan
     * @param rootDirectoryPath The root directory of the project for removing it
     *                          from the class url
     */
    public void findClass(File[] toAnalyze, String rootDirectoryPath) {
        if (toAnalyze == null)
            return;
        for (File file : toAnalyze) {
            if (file.isDirectory()) {

                findClass(file.listFiles(), rootDirectoryPath);

            } else if (file.getName().endsWith(".class")) {
                String temp = file.getPath();
                String className = temp.replace(rootDirectoryPath, "")
                        .replace(File.separator, ".")
                        .replace(".class", "");
                try {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    classList.add(Class.forName(className, true, cl));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
