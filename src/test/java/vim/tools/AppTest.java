// Created by Kirill Dunaev on Mon, 1 Sep 2014 at 10:58
package vim.tools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void test01() {
        String[] args = new String[] {"vim.tools.App", "vim.tools.EclipseFormatter", "My.java"};
        assertEquals(3, args.length);

        List<String> list = new ArrayList<>(Arrays.asList(args));
        assertEquals(3, list.size());

        String firstArg = list.remove(0);
        assertEquals("vim.tools.App", firstArg);
        assertEquals(2, list.size());

        Class<?> clazz = null;
        try {
            clazz = Class.forName(firstArg);
        } catch (ClassNotFoundException e) {
            fail(firstArg);
        }

        Method mainMethod = null;
        try {
            mainMethod = clazz.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            fail("main method not found");
        }

        try {
            mainMethod.invoke(null, new Object[] {list.toArray(new String[] {})});
        } catch (ReflectiveOperationException e) {
            fail("main method doesn't invokable");
        }
    }
}
