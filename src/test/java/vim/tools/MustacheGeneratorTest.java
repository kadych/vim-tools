package vim.tools;

import java.util.*;
import java.time.*;
import java.time.format.*;
import org.junit.*;
import static org.junit.Assert.*;

public class MustacheGeneratorTest {
    private static MustacheGenerator app;

    @BeforeClass
    public static void beforeClass() {
        app = new MustacheGenerator();
    }

    @Test
    public void test01() {
        Map<String, String> params = new HashMap<>();
        params.put("packageName", "vim.mustache");
        params.put("className", "AppTest");

        String s = app.toJson(params).replace("\"", "'");
        @SuppressWarnings("rawtypes")
        Map result = (Map) app.toMap(s);

        assertNotNull(result);
        assertTrue(result.containsKey("packageName"));
        assertEquals("vim.mustache", (String) result.get("packageName"));
        assertTrue(result.containsKey("className"));
        assertEquals("AppTest", (String) result.get("className"));
    }

    @Test
    public void test02() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "kirill");
        assertEquals("hello kirill", app.render("hello {{name}}", params));
    }

    @Test
    public void test03() {
        final String DATE_FORMAT = "EEE, d MMM yyyy";
        final String TIME_FORMAT = "HH:mm:ss";
        // System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT + " " + TIME_FORMAT, Locale.ENGLISH)));
        // System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern(TIME_FORMAT, Locale.ENGLISH)));
        // System.out.println(LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.ENGLISH)));
    }

    @Test
    public void test04() {
        Map<String, Object> params = new HashMap<>();
        params.put("user", "kirill");
        app.putAll(params, app.toMap("{'user': 'kad'}"));

        assertTrue(params.containsKey("user"));
        assertEquals("kad", (String) params.get("user"));
    }
}
