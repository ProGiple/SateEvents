package org.satellite.dev.progiple.sateevents.timeParsers;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.satellite.dev.progiple.sateevents.exceptions.ParserFormatIdException;
import org.satellite.dev.progiple.sateevents.timeParsers.impl.NamedParser;
import org.satellite.dev.progiple.sateevents.timeParsers.impl.SameParser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@UtilityClass
public class ParserStorage {
    public final Pattern IDENTIFIER_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private final Map<String, Parser> parsers;
    @Getter
    private final Parser defaultParser;
    static {
        parsers = new HashMap<>();
        defaultParser = new SameParser(new String[]{"%02d", ":"});
        register("same", defaultParser);
        register("named", new NamedParser(new String[]{"д.", "ч.", "м.", "c."}));
        register("named_en", new NamedParser(new String[]{"d", "h", "m", "s"}));
    }

    @Nullable
    public Parser getParserIfPresent(String string) {
        return parsers.get(string);
    }

    @NotNull
    public Parser getParser(String string) {
        return parsers.getOrDefault(string, defaultParser);
    }

    @Nullable
    public String getParserId(Parser parser) {
        for (Map.Entry<String, Parser> e : parsers.entrySet()) {
            if (e.getValue().equals(parser)) return e.getKey();
        }
        return null;
    }

    public void register(String id, Parser parser) throws ParserFormatIdException, NullPointerException {
        validateString(id);
        parsers.put(id, parser);
    }

    void validateString(String input) throws ParserFormatIdException, NullPointerException {
        if (input == null || input.isEmpty()) {
            throw new NullPointerException("Строка идентификатора не может быть пустой!");
        }

        if (!IDENTIFIER_PATTERN.matcher(input).matches()) {
            throw new ParserFormatIdException(input);
        }
    }

    public void unregister(String id) {
        parsers.remove(id);
    }

    public void unregister(Parser parser) {
        String id = getParserId(parser);
        if (id != null) unregister(id);
    }
}
