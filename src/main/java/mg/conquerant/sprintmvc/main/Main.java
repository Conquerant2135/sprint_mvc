package mg.conquerant.sprintmvc.main;

import java.util.List;

import mg.conquerant.sprintmvc.utils.ClasspathAnalyzer;

public class Main {
    public static void main(String[] args) {
        ClasspathAnalyzer cpa = new ClasspathAnalyzer();
        List<Class<?>> classList = cpa.classList("mg");
        for (Class<?> cls : classList) {
            System.out.println(cls.getName());
        }
    }
}
