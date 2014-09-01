package vim.tools;

import java.util.Map;
import java.util.HashMap;
import org.apache.commons.cli.*;
import org.junit.*;
import static org.junit.Assert.*;

public class MustacheGeneratorArgsTest {
    private static MustacheGenerator app;

    @BeforeClass
    public static void beforeClass() {
        app = new MustacheGenerator();
    }

    @Test
    public void test01() throws ParseException {
        Map<String, String> params = new HashMap<>();
        params.put("packageName", "vim.mustache");
        params.put("className", "AppTest");
        String sParams = app.toJson(params);

        String[] args = {"-i", "java.tmp", "-o", "MyClass.java", sParams};

        MustacheGeneratorArgs arguments = new MustacheGeneratorArgs(args);
        assertEquals("java.tmp", arguments.getInputFile());
        assertEquals("MyClass.java", arguments.getOutputFile());
        assertEquals(sParams, arguments.getParams());
    }
}
