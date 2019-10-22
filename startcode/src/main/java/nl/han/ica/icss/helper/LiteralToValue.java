package nl.han.ica.icss.helper;

import nl.han.ica.icss.ast.Literal;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

public class LiteralToValue {
    public static int getIntValue(Literal literal) {
        if (literal instanceof PercentageLiteral) {
            return ((PercentageLiteral) literal).value;
        } else if (literal instanceof PixelLiteral) {
            return ((PixelLiteral) literal).value;
        } else if (literal instanceof ScalarLiteral) {
            return ((ScalarLiteral) literal).value;
        }

        return Integer.parseInt(null);
    }

    public static String getStringValue(Literal literal) {
        if (literal instanceof PercentageLiteral) {
            return String.valueOf(((PercentageLiteral) literal).value);
        } else if (literal instanceof PixelLiteral) {
            return String.valueOf(((PixelLiteral) literal).value);
        } else if (literal instanceof ScalarLiteral) {
            return String.valueOf(((ScalarLiteral) literal).value);
        } else if (literal instanceof ColorLiteral) {
            return ((ColorLiteral) literal).value;
        }

        return null;
    }
}
