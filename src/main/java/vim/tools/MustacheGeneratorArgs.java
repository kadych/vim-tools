package vim.tools;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class MustacheGeneratorArgs {
    private final static Option inputOption = new Option("i", "input", true, "template file");
    private final static Option outputOption = new Option("o", "output", true, "rendered file");
    private final static Option paramsOption = new Option("p", "params", true, "json string");
    private final static Options options = new Options().addOption(inputOption)
        .addOption(outputOption).addOption(paramsOption);
    private final static CommandLineParser cmdLineParser = new PosixParser();

    public final String inputFile;
    public final String outputFile;
    public final String params;

    public MustacheGeneratorArgs(String[] args) throws ParseException {
        CommandLine cmdLine = cmdLineParser.parse(options, args);
        this.inputFile = cmdLine.getOptionValue(inputOption.getOpt());
        this.outputFile = cmdLine.getOptionValue(outputOption.getOpt());
        if (cmdLine.hasOption(paramsOption.getOpt())) {
            this.params = cmdLine.getOptionValue(paramsOption.getOpt());
        } else {
            @SuppressWarnings("unchecked")
            List<String> argList = (List<String>) cmdLine.getArgList();
            this.params = argList.stream().collect(Collectors.joining(" "));
        }
    }

    public String getInputFile() {
        return inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public String getParams() {
        return params;
    }
}

