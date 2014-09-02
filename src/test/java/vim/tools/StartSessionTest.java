package vim.tools;

import java.nio.file.*;
import org.junit.*;
import static org.junit.Assert.*;

public class StartSessionTest {
    private StartSession startSession;

    @Before
    public void before() {
        startSession = new StartSession();
    }

    @Test
    public void test01() {
        assertTrue(startSession.isWindows() != startSession.isUnix());
    }

    @Test
    public void test02() {
        assertTrue(Files.exists(startSession.getVimrc()));
    }

    @Test
    public void test03() {
        assertTrue(startSession.isSessionExist("default"));
    }

    @Test
    public void test04() {
        assertEquals("true", startSession.getParamValue("let g:session_loaded=\"true\""));
        assertEquals("1", startSession.getParamValue("let g:session_loaded = 1"));
        assertEquals(".sessions", startSession.getParamValue("get g:session_directory='.sessions'"));
        assertEquals(".sessions", startSession.getParamValue(".sessions"));
    }
}
