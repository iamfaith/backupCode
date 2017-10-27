package org.gradle.cli;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class CommandLineParser {
    static final /* synthetic */ boolean $assertionsDisabled = (!CommandLineParser.class.desiredAssertionStatus());
    private boolean allowMixedOptions;
    private boolean allowUnknownOptions;
    private final PrintWriter deprecationPrinter;
    private Map<String, CommandLineOption> optionsByString;

    private static final class CaseInsensitiveStringComparator implements Comparator<String> {
        private CaseInsensitiveStringComparator() {
        }

        public int compare(String option1, String option2) {
            int diff = option1.compareToIgnoreCase(option2);
            return diff != 0 ? diff : option1.compareTo(option2);
        }
    }

    private static final class OptionComparator implements Comparator<CommandLineOption> {
        private OptionComparator() {
        }

        public int compare(CommandLineOption option1, CommandLineOption option2) {
            return new CaseInsensitiveStringComparator().compare((String) Collections.min(option1.getOptions(), new OptionStringComparator()), (String) Collections.min(option2.getOptions(), new OptionStringComparator()));
        }
    }

    private static abstract class OptionParserState {
        public abstract boolean getHasArgument();

        public abstract ParserState onArgument(String str);

        public abstract ParserState onComplete();

        public abstract ParserState onStartNextArg();

        private OptionParserState() {
        }
    }

    private static class OptionString {
        private final String arg;
        private final String option;

        private OptionString(String arg, String option) {
            this.arg = arg;
            this.option = option;
        }

        public String getDisplayName() {
            return this.arg.startsWith("--") ? "--" + this.option : "-" + this.option;
        }

        public String toString() {
            return getDisplayName();
        }
    }

    private static final class OptionStringComparator implements Comparator<String> {
        private OptionStringComparator() {
        }

        public int compare(String option1, String option2) {
            boolean short1;
            if (option1.length() == 1) {
                short1 = true;
            } else {
                short1 = false;
            }
            boolean short2;
            if (option2.length() == 1) {
                short2 = true;
            } else {
                short2 = false;
            }
            if (short1 && !short2) {
                return -1;
            }
            if (short1 || !short2) {
                return new CaseInsensitiveStringComparator().compare(option1, option2);
            }
            return 1;
        }
    }

    private static abstract class ParserState {
        public abstract boolean maybeStartOption(String str);

        public abstract ParserState onNonOption(String str);

        public abstract OptionParserState onStartOption(String str, String str2);

        private ParserState() {
        }

        boolean isOption(String arg) {
            return arg.matches("-.+");
        }

        public void onCommandLineEnd() {
        }
    }

    private static class AfterOptions extends ParserState {
        private final ParsedCommandLine commandLine;

        private AfterOptions(ParsedCommandLine commandLine) {
            super();
            this.commandLine = commandLine;
        }

        public boolean maybeStartOption(String arg) {
            return false;
        }

        public OptionParserState onStartOption(String arg, String option) {
            return new UnknownOptionParserState(arg, this.commandLine, this);
        }

        public ParserState onNonOption(String arg) {
            this.commandLine.addExtraValue(arg);
            return this;
        }
    }

    private class KnownOptionParserState extends OptionParserState {
        private final ParsedCommandLine commandLine;
        private final CommandLineOption option;
        private final OptionString optionString;
        private final ParserState state;
        private final List<String> values;

        private KnownOptionParserState(OptionString optionString, CommandLineOption option, ParsedCommandLine commandLine, ParserState state) {
            super();
            this.values = new ArrayList();
            this.optionString = optionString;
            this.option = option;
            this.commandLine = commandLine;
            this.state = state;
        }

        public ParserState onArgument(String argument) {
            if (!getHasArgument()) {
                throw new CommandLineArgumentException(String.format("Command-line option '%s' does not take an argument.", new Object[]{this.optionString}));
            } else if (argument.length() == 0) {
                throw new CommandLineArgumentException(String.format("An empty argument was provided for command-line option '%s'.", new Object[]{this.optionString}));
            } else {
                this.values.add(argument);
                return onComplete();
            }
        }

        public ParserState onStartNextArg() {
            if (this.option.getAllowsArguments() && this.values.isEmpty()) {
                return new MissingOptionArgState(this);
            }
            return onComplete();
        }

        public boolean getHasArgument() {
            return this.option.getAllowsArguments();
        }

        public ParserState onComplete() {
            if (getHasArgument() && this.values.isEmpty()) {
                throw new CommandLineArgumentException(String.format("No argument was provided for command-line option '%s'.", new Object[]{this.optionString}));
            }
            ParsedCommandLineOption parsedOption = this.commandLine.addOption(this.optionString.option, this.option);
            if (this.values.size() + parsedOption.getValues().size() <= 1 || this.option.getAllowsMultipleArguments()) {
                for (String value : this.values) {
                    parsedOption.addArgument(value);
                }
                if (this.option.getDeprecationWarning() != null) {
                    CommandLineParser.this.deprecationPrinter.println("The " + this.optionString + " option is deprecated - " + this.option.getDeprecationWarning());
                }
                if (this.option.getSubcommand() != null) {
                    return this.state.onNonOption(this.option.getSubcommand());
                }
                return this.state;
            }
            throw new CommandLineArgumentException(String.format("Multiple arguments were provided for command-line option '%s'.", new Object[]{this.optionString}));
        }
    }

    private static class MissingOptionArgState extends ParserState {
        private final OptionParserState option;

        private MissingOptionArgState(OptionParserState option) {
            super();
            this.option = option;
        }

        public boolean maybeStartOption(String arg) {
            return isOption(arg);
        }

        public OptionParserState onStartOption(String arg, String option) {
            return this.option.onComplete().onStartOption(arg, option);
        }

        public ParserState onNonOption(String arg) {
            return this.option.onArgument(arg);
        }

        public void onCommandLineEnd() {
            this.option.onComplete();
        }
    }

    private abstract class OptionAwareParserState extends ParserState {
        protected final ParsedCommandLine commandLine;

        protected OptionAwareParserState(ParsedCommandLine commandLine) {
            super();
            this.commandLine = commandLine;
        }

        public boolean maybeStartOption(String arg) {
            return isOption(arg);
        }

        public ParserState onNonOption(String arg) {
            this.commandLine.addExtraValue(arg);
            return CommandLineParser.this.allowMixedOptions ? new AfterFirstSubCommand(this.commandLine) : new AfterOptions(this.commandLine);
        }
    }

    private static class UnknownOptionParserState extends OptionParserState {
        private final String arg;
        private final ParsedCommandLine commandLine;
        private final ParserState state;

        private UnknownOptionParserState(String arg, ParsedCommandLine commandLine, ParserState state) {
            super();
            this.arg = arg;
            this.commandLine = commandLine;
            this.state = state;
        }

        public boolean getHasArgument() {
            return true;
        }

        public ParserState onStartNextArg() {
            return onComplete();
        }

        public ParserState onArgument(String argument) {
            return onComplete();
        }

        public ParserState onComplete() {
            this.commandLine.addExtraValue(this.arg);
            return this.state;
        }
    }

    private class AfterFirstSubCommand extends OptionAwareParserState {
        private AfterFirstSubCommand(ParsedCommandLine commandLine) {
            super(commandLine);
        }

        public OptionParserState onStartOption(String arg, String option) {
            CommandLineOption commandLineOption = (CommandLineOption) CommandLineParser.this.optionsByString.get(option);
            if (commandLineOption == null) {
                return new UnknownOptionParserState(arg, this.commandLine, this);
            }
            return new KnownOptionParserState(new OptionString(arg, option), commandLineOption, this.commandLine, this);
        }
    }

    private class BeforeFirstSubCommand extends OptionAwareParserState {
        private BeforeFirstSubCommand(ParsedCommandLine commandLine) {
            super(commandLine);
        }

        public OptionParserState onStartOption(String arg, String option) {
            OptionString optionString = new OptionString(arg, option);
            CommandLineOption commandLineOption = (CommandLineOption) CommandLineParser.this.optionsByString.get(option);
            if (commandLineOption != null) {
                return new KnownOptionParserState(optionString, commandLineOption, this.commandLine, this);
            }
            if (CommandLineParser.this.allowUnknownOptions) {
                return new UnknownOptionParserState(arg, this.commandLine, this);
            }
            throw new CommandLineArgumentException(String.format("Unknown command-line option '%s'.", new Object[]{optionString}));
        }
    }

    public CommandLineParser() {
        this(new OutputStreamWriter(System.out));
    }

    public CommandLineParser(Writer deprecationPrinter) {
        this.optionsByString = new HashMap();
        this.deprecationPrinter = new PrintWriter(deprecationPrinter);
    }

    public ParsedCommandLine parse(String... commandLine) throws CommandLineArgumentException {
        return parse(Arrays.asList(commandLine));
    }

    public ParsedCommandLine parse(Iterable<String> commandLine) throws CommandLineArgumentException {
        ParsedCommandLine parsedCommandLine = new ParsedCommandLine(new HashSet(this.optionsByString.values()));
        ParserState parseState = new BeforeFirstSubCommand(parsedCommandLine);
        for (String arg : commandLine) {
            if (!parseState.maybeStartOption(arg)) {
                parseState = parseState.onNonOption(arg);
            } else if (arg.equals("--")) {
                parseState = new AfterOptions(parsedCommandLine);
            } else if (arg.matches("--[^=]+")) {
                parseState = parseState.onStartOption(arg, arg.substring(2)).onStartNextArg();
            } else if (arg.matches("--[^=]+=.*")) {
                int endArg = arg.indexOf(61);
                parseState = parseState.onStartOption(arg, arg.substring(2, endArg)).onArgument(arg.substring(endArg + 1));
            } else if (arg.matches("-[^=]=.*")) {
                parseState = parseState.onStartOption(arg, arg.substring(1, 2)).onArgument(arg.substring(3));
            } else if ($assertionsDisabled || arg.matches("-[^-].*")) {
                String option = arg.substring(1);
                if (this.optionsByString.containsKey(option)) {
                    parseState = parseState.onStartOption(arg, option).onStartNextArg();
                } else {
                    String option1 = arg.substring(1, 2);
                    if (this.optionsByString.containsKey(option1)) {
                        OptionParserState parsedOption = parseState.onStartOption("-" + option1, option1);
                        if (parsedOption.getHasArgument()) {
                            parseState = parsedOption.onArgument(arg.substring(2));
                        } else {
                            parseState = parsedOption.onComplete();
                            for (int i = 2; i < arg.length(); i++) {
                                String optionStr = arg.substring(i, i + 1);
                                parseState = parseState.onStartOption("-" + optionStr, optionStr).onComplete();
                            }
                        }
                    } else if (this.allowUnknownOptions) {
                        parseState = parseState.onStartOption(arg, option).onComplete();
                    } else {
                        parseState = parseState.onStartOption("-" + option1, option1).onComplete();
                    }
                }
            } else {
                throw new AssertionError();
            }
        }
        parseState.onCommandLineEnd();
        return parsedCommandLine;
    }

    public CommandLineParser allowMixedSubcommandsAndOptions() {
        this.allowMixedOptions = true;
        return this;
    }

    public CommandLineParser allowUnknownOptions() {
        this.allowUnknownOptions = true;
        return this;
    }

    public void printUsage(Appendable out) {
        Formatter formatter = new Formatter(out);
        Set<CommandLineOption> orderedOptions = new TreeSet(new OptionComparator());
        orderedOptions.addAll(this.optionsByString.values());
        Map<String, String> lines = new LinkedHashMap();
        for (CommandLineOption option : orderedOptions) {
            Set<String> orderedOptionStrings = new TreeSet(new OptionStringComparator());
            orderedOptionStrings.addAll(option.getOptions());
            List<String> prefixedStrings = new ArrayList();
            for (String optionString : orderedOptionStrings) {
                if (optionString.length() == 1) {
                    prefixedStrings.add("-" + optionString);
                } else {
                    prefixedStrings.add("--" + optionString);
                }
            }
            String key = join(prefixedStrings, ", ");
            String value = option.getDescription();
            if (value == null || value.length() == 0) {
                value = "";
            }
            lines.put(key, value);
        }
        int max = 0;
        for (String optionStr : lines.keySet()) {
            max = Math.max(max, optionStr.length());
        }
        for (Entry<String, String> entry : lines.entrySet()) {
            if (((String) entry.getValue()).length() == 0) {
                formatter.format("%s%n", new Object[]{((Entry) r3.next()).getKey()});
            } else {
                formatter.format("%-" + max + "s  %s%n", new Object[]{entry.getKey(), entry.getValue()});
            }
        }
        formatter.flush();
    }

    private static String join(Collection<?> things, String separator) {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        if (separator == null) {
            separator = "";
        }
        for (Object thing : things) {
            if (!first) {
                buffer.append(separator);
            }
            buffer.append(thing.toString());
            first = false;
        }
        return buffer.toString();
    }

    public CommandLineOption option(String... options) {
        String[] arr$ = options;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            String option = arr$[i$];
            if (this.optionsByString.containsKey(option)) {
                throw new IllegalArgumentException(String.format("Option '%s' is already defined.", new Object[]{option}));
            } else if (option.startsWith("-")) {
                throw new IllegalArgumentException(String.format("Cannot add option '%s' as an option cannot start with '-'.", new Object[]{option}));
            } else {
                i$++;
            }
        }
        CommandLineOption option2 = new CommandLineOption(Arrays.asList(options));
        for (String optionStr : option2.getOptions()) {
            this.optionsByString.put(optionStr, option2);
        }
        return option2;
    }
}
