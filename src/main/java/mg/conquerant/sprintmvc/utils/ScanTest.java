package mg.conquerant.sprintmvc.utils;

import java.util.ArrayList;
import java.util.List;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class ScanTest {
    public static void classList(){
        List<String> stuff = new ArrayList<>();
        try(ScanResult scanRes = new ClassGraph().enableAllInfo().enableSystemJarsAndModules().scan()){
            for(ClassInfo ci : scanRes.getAllClasses()){
                System.out.println(ci.getName());
            }
        }
    }
}
