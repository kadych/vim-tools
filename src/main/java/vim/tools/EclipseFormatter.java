package vim.tools;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

public class EclipseFormatter {
    public static final File USER_PROPERTIES = new File(System.getProperty("user.home"),
        "ecf.properties");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private final CodeFormatter cf;

    public EclipseFormatter() throws IOException {
        cf = new DefaultCodeFormatter(getOptions());
    }

    private String readLines(File file) throws IOException {
        try (BufferedReader reader =
            new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            return reader.lines().collect(Collectors.joining(LINE_SEPARATOR));
        }
    }

    public static Properties getDefaultOptions(Properties options) throws IOException {
        try (InputStream is =
            new BufferedInputStream(EclipseFormatter.class.getResourceAsStream("/ecf.properties"))) {
            options.load(is);
        }
        return options;
    }

    public static Properties getEclipseOptions(Properties options) throws IOException {
        EclipseConfig config = new EclipseConfig();
        config.load(options);
        return options;
    }

    public static Properties getUserOptions(Properties options) throws IOException {
        final File ecfrc = USER_PROPERTIES;
        if (ecfrc.isFile()) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(ecfrc))) {
                options.load(is);
            }
        }
        return options;
    }

    public static Properties getOptions() throws IOException {
        Properties options = getUserOptions(getEclipseOptions(new Properties()));
        options.put(JavaCore.COMPILER_SOURCE, "1.8");
        return options;
    }

    public void format(File file) {
        try {
            final String code = readLines(file);
            TextEdit te =
                cf.format(CodeFormatter.K_UNKNOWN, code, 0, code.length(), 0,
                    LINE_SEPARATOR);
            IDocument dc = new Document(code);
            te.apply(dc, TextEdit.NONE);
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file))) {
                writer.write(dc.get());
            }
        } catch (IOException | BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("EclipseFormatter <javafile1> <javafile2> ...");
            return;
        }
        try {
            EclipseFormatter app = new EclipseFormatter();
            Arrays.stream(args).map(File::new).filter(File::isFile).forEach(app::format);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
