package vim.tools;

import java.util.Properties;
import org.junit.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import static org.junit.Assert.*;

public class EclipseConfigTest {
    private Properties properties;
    private EclipseConfig config;

    @Before
    public void before() throws Exception {
        config = new EclipseConfig();
    }

    @Test
    public void test1() throws Exception {
        Document document = config.loadDocument();
        assertNotNull(document);

        Element profile = config.loadProfile(document);
        assertNotNull(profile);
        assertNotNull(profile.getChildNodes());

        assertTrue(config.loadSettings(profile, new Properties()) > 0);
    }
}
