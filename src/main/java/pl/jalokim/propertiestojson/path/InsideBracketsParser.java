package pl.jalokim.propertiestojson.path;

class InsideBracketsParser implements PathParser {

    @Override
    public void parseNextChar(ParserContext parserContext, char currentChar) {
        parserContext.appendNextChar(currentChar);
        if (currentChar == ']') {
            parserContext.switchToOuterParser();
        }
    }
}
