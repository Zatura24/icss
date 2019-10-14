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
        variableTypes.add(new HashMap<>());

        for (ASTNode node :
                ast.root.getChildren()) {
            checkCH01FindAssignment(node);
//            checkCH01FindReference(node);
            checkCH03(node);
            checkCH04(node);
            checkCH05(node);
        }
    }

    private void checkCH01FindAssignment(ASTNode node) {
        if (node.getChildren().size() != 1) {
            if (node instanceof VariableAssignment) {
                variableTypes.get(0).put(((VariableAssignment) node).name.name, expressionTypeResolver(((VariableAssignment) node).expression));
            } else if (node instanceof VariableReference) {
                if (!variableTypes.get(0).containsKey(((VariableReference) node).name)) {
                    node.setError(String.format("Variable \'%s\' is not yet defined.", ((VariableReference) node).name));
                }
            }
            node.getChildren().forEach(this::checkCH01FindAssignment);
        }
    }

//    private void checkCH01FindReference(ASTNode node) {
//        if (node.getChildren().size() != 1) {
//            if (node instanceof VariableReference) {
//                if (!variableTypes.get(0).containsKey(((VariableReference) node).name)) {
//                    node.setError(String.format("Variable \'%s\' is not yet defined.", ((VariableReference) node).name));
//                }
//            }
//            node.getChildren().forEach(this::checkCH01FindReference);
//        }
//    }

    private void checkCH03(ASTNode node) {
        if (node.getChildren().size() != 1) {
            if (node instanceof Operation) {
                if (((Operation) node).lhs instanceof ColorLiteral || ((Operation) node).rhs instanceof ColorLiteral) {
                    node.setError("An operand cannot be of type: Color");
                }
            }
            node.getChildren().forEach(this::checkCH03);
        }
    }

    private void checkCH04(ASTNode node) {
        if (node.getChildren().size() != 1) {

            node.getChildren().forEach(this::checkCH04);
        }
    }

    private void checkCH05(ASTNode node) {
        if (node.getChildren().size() != 1) {
            if (node instanceof IfClause) {
                if (((IfClause) node).conditionalExpression instanceof VariableReference) {
                    if (variableTypes.get(0).get(((VariableReference) ((IfClause) node).conditionalExpression).name) != ExpressionType.BOOL) {
                        node.setError("If condition must be of type boolean");
                    }
                } else {
                    if (expressionTypeResolver(((IfClause) node).conditionalExpression) != ExpressionType.BOOL) {
                        node.setError("If condition must be of type boolean");
                    }
                }
            }

            node.getChildren().forEach(this::checkCH05);
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
