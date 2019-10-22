package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.helper.LiteralToValue;

public class Generator {

    private final char OPEN_CURLY_BRACKET = '{';
    private final char CLOSE_CURLY_BRACKET = '}';
    private final char COLON = ':';
    private final char SEMI_COLON = ';';
    private final char BLANK_SPACE = ' ';
    private final String NEW_LINE = System.lineSeparator();
    private final char TAB = '\t';
    private final String PIXEL = "px";
    private final char PERCENTAGE = '%';

    public String generate(AST ast) {
        StringBuilder stringBuilder = new StringBuilder();

        for (ASTNode node :
                ast.root.getChildren()) {
            stringBuilder.append(generate(node));
        }

        return stringBuilder.toString();
    }

    private String generate(ASTNode node) {
        StringBuilder stringBuilder = new StringBuilder();

        if (node instanceof Stylerule) {
            for (Selector selector :
                    ((Stylerule) node).selectors) {
                stringBuilder.append(selector.toString()).append(BLANK_SPACE);
            }

            stringBuilder.append(OPEN_CURLY_BRACKET).append(NEW_LINE);

            for (ASTNode declaration :
                    ((Stylerule) node).body) {
                if (declaration instanceof Declaration) {
                    stringBuilder.append(TAB)
                            .append(((Declaration) declaration).property.name)
                            .append(COLON)
                            .append(BLANK_SPACE);

					stringBuilder.append(LiteralToValue.getStringValue((Literal) ((Declaration) declaration).expression));

                    if (((Declaration) declaration).expression instanceof PixelLiteral) {
                        stringBuilder.append(PIXEL);
                    } else if (((Declaration) declaration).expression instanceof PercentageLiteral) {
                        stringBuilder.append(PERCENTAGE);
                    }

                    stringBuilder.append(SEMI_COLON).append(NEW_LINE);
                }
            }

            stringBuilder.append(CLOSE_CURLY_BRACKET).append(NEW_LINE).append(NEW_LINE);
        }

        return stringBuilder.toString();
    }
}
