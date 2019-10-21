package nl.han.ica.icss.helper;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

public final class ExpressionTypeResolver {
    public static ExpressionType expressionTypeResolver(Expression expression) {
        if (expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if (expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (expression instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (expression instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        } else {
            return null;
        }
    }
}
