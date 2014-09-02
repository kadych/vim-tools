package vim.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StartSession {
    private final static String userHome = System.getProperty("user.home");
    private final static String userDir = System.getProperty("user.dir");

    public boolean isWindows() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.indexOf("win") >= 0;
    }

    public boolean isUnix() {
        final String osName = System.getProperty("os.name").toLowerCase();
        return osName.indexOf("nix") >= 0 || osName.indexOf("mac") >= 0;
    }

    public String getParamValue(final String param) {
        final Pattern pattern = Pattern.compile("=\\s*['\"]?([^'\"]+)['\"]?\\s*");
        Matcher matcher = pattern.matcher(param);
        if (!matcher.find())
            return param;
        return matcher.group(1);
    }

    public String getVimHome() {
        return !isWindows() ? "~/.vim" : "~/vimfiles";
    }

    public Path getVimrc() {
        return Paths.get(getVimHome().replace("~", userHome), "vimrc");
    }

    public boolean isSessionExist(final String sessionName) {
        final Path vimrc = getVimrc();
        try (BufferedReader reader = Files.newBufferedReader(vimrc)) {
            final String sessionLine =
                reader.lines().filter(line -> line.indexOf("g:session_directory") >= 0)
                    .findFirst().orElse(getVimHome() + "/.sessions");
            final String sessionDirectory = getParamValue(sessionLine).replace("~", userHome);
            return Files.list(Paths.get(sessionDirectory))
                .map(file -> file.getFileName().toString())
                .filter(file -> file.equals(sessionName + ".vim"))
                .findFirst().isPresent();
        } catch (IOException e) {
            return false;
        }
    }

    public String getActualSessionName(final String sessionName) {
        return isSessionExist(sessionName) ? sessionName : "";
    }

    public void startProcess(final String sessionName) throws IOException {
        final String openSession = String.format("OpenSession! %s", getActualSessionName(sessionName));
        final List<String> processArgs = Arrays.asList("gvim", "-c", openSession);
        ProcessBuilder builder = new ProcessBuilder(processArgs);
        builder.directory(new File(userDir));
        builder.start();
    }

    public static void main(String[] args) {
        try {
            StartSession session = new StartSession();
            session.startProcess(new File(userDir).getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
