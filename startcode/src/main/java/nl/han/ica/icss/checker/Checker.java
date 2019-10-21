package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;
import nl.han.ica.icss.helper.ExpressionTypeResolver;
import nl.han.ica.icss.helper.StyleAttributeChecker;

import java.util.HashMap;
import java.util.LinkedList;

import static nl.han.ica.icss.helper.ExpressionTypeResolver.expressionTypeResolver;

public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();

        for (ASTNode node :
                ast.root.getChildren()) {
            traverse(node);
        }
    }

    private ASTNode traverse(ASTNode root) {
        if (root.getChildren().size() != 1) {
            findVariableDecleration(root);
            checkVariableReference(root);
            colorNotAllowedInOpperation(root);
            checkStyleDecleration(root);
            checkIfCondition(root);

            for (ASTNode node : root.getChildren()) {
                traverse(node);
            }
        }

        return root;
    }

    private void findVariableDecleration(ASTNode node) {
        if (node instanceof VariableAssignment) {
            if (variableTypes.isEmpty()) variableTypes.add(new HashMap<>());

            variableTypes.getFirst().put(((VariableAssignment) node).name.name, ExpressionTypeResolver.expressionTypeResolver(((VariableAssignment) node).expression));
        }
    }

    private void checkVariableReference(ASTNode node) {
        if (node instanceof VariableReference) {
            if (!variableTypes.getFirst().containsKey(((VariableReference) node).name)) {
                node.setError(String.format("Variable \'%s\' is not yet defined.", ((VariableReference) node).name));
            }
        }
    }

    private void colorNotAllowedInOpperation(ASTNode node) {
        if (node instanceof Operation) {
            if (((Operation) node).lhs instanceof ColorLiteral || ((Operation) node).rhs instanceof ColorLiteral) {
                node.setError("An operand cannot be of type: " + ExpressionType.COLOR);
            }
        }
    }

    private void checkCH02(ASTNode node) {
        if (node instanceof Operation) {
            if (((Operation) node).lhs instanceof Operation) checkCH02(((Operation) node).lhs);
            if (((Operation) node).rhs instanceof Operation) checkCH02(((Operation) node).rhs);

            if (!((Operation) node).lhs.equals(((Operation) node).rhs))
                node.setError("Left- and right-hand operands must be of same type.");
        }

        node.getChildren().forEach(this::checkCH02);
    }

    private void checkStyleDecleration(ASTNode node) {
        if (node instanceof Declaration) {
            HashMap map = StyleAttributeChecker.getMap();
            ExpressionType expression = ((Declaration) node).expression instanceof VariableReference
                    ? variableTypes.getFirst().get(((VariableReference) ((Declaration) node).expression).name)
                    : ExpressionTypeResolver.expressionTypeResolver(((Declaration) node).expression);

            if (map.get(((Declaration) node).property.name) != expression) {
                node.setError(String.format("Style attribute \'%s\' cannot have an expression type \'%s\'", ((Declaration) node).property.name, ExpressionTypeResolver.expressionTypeResolver(((Declaration) node).expression)));
            }
        }
    }

    private void checkIfCondition(ASTNode node) {
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
    }
}
