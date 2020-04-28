package pl.jalokim.propertiestojson.path;

import java.util.ArrayList;
import java.util.List;

class ParserContext {

    private static final PathParser OUTER_PARSER = new OuterParser();
    private static final PathParser INSIDE_BRACKETS_PARSER = new InsideBracketsParser();

    private final List<String> propertiesParts = new ArrayList<>();
    private PathParser currentParser = new OuterParser();
    private StringBuilder currentWordBuilder = new StringBuilder();

    void switchToOuterParser() {
        currentParser = OUTER_PARSER;
    }

    void switchToInsideBracketsParser() {
        currentParser = INSIDE_BRACKETS_PARSER;
    }

    void addNextPropertyPart() {
        propertiesParts.add(currentWordBuilder.toString());
        currentWordBuilder = new StringBuilder();
    }

    void appendNextChar(char nextChar) {
        currentWordBuilder.append(nextChar);
    }

    void parseNextChar(char nextChar) {
        this.currentParser.parseNextChar(this, nextChar);
    }

    List<String> getPropertiesParts() {
        addNextPropertyPart();
        return propertiesParts;
    }
}
