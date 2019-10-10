package nl.han.ica.icss.checker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.*;

import javax.swing.*;
import javax.swing.text.Style;

public class Checker {

    private LinkedList<HashMap<String,ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();

//        checkCH01(ast);
        variableTypes.add(new HashMap<>());
        for (ASTNode node :
                ast.root.getChildren()) {
            checkCH01(node);
            checkCH03(node);
        }
    }

    private void checkCH01(ASTNode node) {
        if (node.getChildren().size() != 1) {
            if (node instanceof VariableAssignment) {
                variableTypes.getFirst().put(((VariableAssignment) node).name.name, expressionTypeResolver(((VariableAssignment) node).expression));
            } else if (node instanceof Declaration) {
                if (((Declaration) node).expression instanceof VariableReference
                        && !variableTypes.getFirst().containsKey(((VariableReference) ((Declaration) node).expression).name)) {
                    node.setError(String.format("Variable \'%s\' is not yet defined.", ((VariableReference) ((Declaration) node).expression).name));
                }
            }

            node.getChildren().forEach(this::checkCH01);
        }
    }

    private void checkCH02(ASTNode node) {
        if (node.getChildren().size() == 1) return;

        if (node instanceof Operation) {
            if (expressionTypeResolver(((Operation) node).lhs) != expressionTypeResolver(((Operation) node).rhs)) {
                node.setError("Operands must be of same type");
            }
        }
    }

    private void checkCH03(ASTNode node) {
        if (node.getChildren().size() != 1) {
            if (node instanceof Operation) {
                if (((Operation) node).lhs instanceof ColorLiteral || ((Operation) node).rhs instanceof ColorLiteral) {
                    node.setError(String.format("An operand cannot be of type: Color"));
                }
            }

            node.getChildren().forEach(this::checkCH03);
        }
    }

    private static ExpressionType expressionTypeResolver(Expression expression) {
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
