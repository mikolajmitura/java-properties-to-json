package pl.jalokim.propertiestojson.path;

class OuterParser implements PathParser {

    @Override
    public void parseNextChar(ParserContext parserContext, char currentChar) {
        if (currentChar == '.') {
            parserContext.addNextPropertyPart();
        } else {
            parserContext.appendNextChar(currentChar);
            if (currentChar == '[') {
                parserContext.switchToInsideBracketsParser();
            }
        }
    }
}
