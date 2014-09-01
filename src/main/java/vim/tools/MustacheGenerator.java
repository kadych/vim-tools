package vim.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.cli.ParseException;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;

public class MustacheGenerator {
    private final static Gson gson = new Gson();
    private final static MustacheFactory factory = new DefaultMustacheFactory();

    public String toJson(Object o) {
        return gson.toJson(o);
    }

    public Object toMap(String s) {
        return gson.fromJson(s, Map.class);
    }

    public void render(Reader reader, Object params, Writer writer) {
        Mustache mustache = factory.compile(reader, "render");
        mustache.execute(writer, params);
    }

    public void render(Path template, Object params, Path result) throws IOException {
        Path resultDir = result.getParent();
        if (resultDir != null && !Files.isDirectory(resultDir))
            resultDir.toFile().mkdirs();
        try (BufferedReader reader = Files.newBufferedReader(template);
            BufferedWriter writer = Files.newBufferedWriter(result)) {
            render(reader, params, writer);
        }
    }

    public String render(String template, Object params) {
        StringWriter writer = new StringWriter();
        render(new StringReader(template), params, writer);
        return writer.toString();
    }

    public Object getDefaultParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("user", System.getProperty("user.name"));
        LocalDateTime dateTime = LocalDateTime.now();

        final String DATE_FORMAT = "EEE, d MMM yyyy";
        final String TIME_FORMAT = "HH:mm";
        params.put("dateTime", dateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT
            + " " + TIME_FORMAT, Locale.ENGLISH)));
        params.put("time", dateTime.toLocalTime().format(
            DateTimeFormatter.ofPattern(TIME_FORMAT, Locale.ENGLISH)));
        params.put("date", dateTime.toLocalDate().format(
            DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.ENGLISH)));
        params.put("year", dateTime.getYear());
        return params;
    }

    @SuppressWarnings("unchecked")
    public Object putAll(Object params, Object params2) {
        if (params instanceof Map && params2 instanceof Map) {
            ((Map) params).putAll((Map) params2);
        }
        return params;
    }

    public static void main(String[] args) {
        try {
            MustacheGeneratorArgs arguments = new MustacheGeneratorArgs(args);
            MustacheGenerator app = new MustacheGenerator();
            app.render(Paths.get(arguments.inputFile), app.putAll(app.getDefaultParams(),
                app.toMap(arguments.params)), Paths.get(arguments.outputFile));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}
