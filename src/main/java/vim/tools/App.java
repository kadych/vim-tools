// Created by Kirill Dunaev on Mon, 1 Sep 2014 at 10:55
package vim.tools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        if (args.length == 0)
            System.exit(-1);

        List<String> list = new ArrayList<>(Arrays.asList(args));
        String className = list.remove(0);

        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Method mainMethod = null;
        try {
            mainMethod = clazz.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        try {
            mainMethod.invoke(null, new Object[] {list.toArray(new String[] {})});
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
