package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;
import nl.han.ica.icss.helper.ExpressionResolver;
import nl.han.ica.icss.helper.StyleAttributeChecker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static nl.han.ica.icss.helper.ExpressionResolver.getExpressionType;

public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        variableTypes.add(new HashMap<>());

        traverse(ast.root);
    }

    private void traverse(ASTNode root) {
        findVariableAssignment(root);
        checkVariableReference(root);
        checkOperationOperands(root);
        colorNotAllowedInOperation(root);
        checkStyleDeclaration(root);
        checkIfCondition(root);

        root.getChildren().forEach(this::traverse);
    }

    /**
     * Stores found variable declaration in hash map
     * @param node to check
     */
    private void findVariableAssignment(ASTNode node) {
        if (node instanceof VariableAssignment) {
            variableTypes.getFirst().put(((VariableAssignment) node).name.name, ExpressionResolver.getExpressionType(((VariableAssignment) node).expression));
        }
    }

    /**
     * Checks found variable reference against hash map
     * @param node to check
     */
    private void checkVariableReference(ASTNode node) {
        if (node instanceof VariableReference) {
            if (!variableTypes.getFirst().containsKey(((VariableReference) node).name)) {
                node.setError(String.format("Variable \'%s\' is not yet defined.", ((VariableReference) node).name));
            }
        }
    }

    /**
     * Checks if left- and right hand side are of equal expression type
     * Multiplication allows one operand to be of expression type scalar
     * @param node to check
     * @return expression type of the operation
     */
    private ExpressionType checkOperationOperands(ASTNode node) {
        if (node instanceof Literal) return ExpressionResolver.getExpressionType((Literal) node);
        if (node instanceof VariableReference) return variableTypes.getFirst().get(((VariableReference) node).name);

        if (node instanceof Operation) {
            ExpressionType lhsNode = checkOperationOperands(((Operation) node).lhs),
                            rhsNode = checkOperationOperands(((Operation) node).rhs);

            if (node instanceof MultiplyOperation) {
                if (lhsNode != ExpressionType.SCALAR && rhsNode != ExpressionType.SCALAR)
                    node.setError("A multiplication must be made with a scalar value");

                return lhsNode != ExpressionType.SCALAR ? lhsNode : rhsNode;
            }

            if (node instanceof AddOperation || node instanceof SubtractOperation) {
                if (lhsNode.equals(rhsNode)) return lhsNode;
                node.setError("Operands must be of same expression type");
            }
        }
        return null;
    }

    /**
     * Prevents expression type color in operations
     * @param node to check
     */
    private void colorNotAllowedInOperation(ASTNode node) {
        if (node instanceof Operation) {
            if (((Operation) node).lhs instanceof ColorLiteral || ((Operation) node).rhs instanceof ColorLiteral) {
                node.setError(String.format("An operand cannot be of type: %s", ExpressionType.COLOR));
            }
        }
    }

    /**
     * Checks if available properties are of their corresponding expression type
     * @param node to check
     */
    private void checkStyleDeclaration(ASTNode node) {
        if (node instanceof Declaration) {
            HashMap<String, List<ExpressionType>> map = StyleAttributeChecker.getMap();
            ExpressionType expressionType = checkOperationOperands(((Declaration) node).expression);

            if (map.containsKey(((Declaration) node).property.name)) {
                boolean valid = false;

                for (ExpressionType validExpressionType :
                        map.get(((Declaration) node).property.name)) {
                    valid = validExpressionType == expressionType;
                    if (valid) break;
                }

                if (!valid)
                    node.setError(String.format("Style attribute \'%s\' cannot be of expression type \'%s\'", ((Declaration) node).property.name, expressionType));
            } else {
                node.setError(String.format("Property \'%s\' does not exist", ((Declaration) node).property.name));
            }
        }
    }

    /**
     * Checks if if-condition is of expression type boolean
     * @param node to check
     */
    private void checkIfCondition(ASTNode node) {
        if (node instanceof IfClause) {
            if (((IfClause) node).conditionalExpression instanceof VariableReference) {
                if (variableTypes.get(0).get(((VariableReference) ((IfClause) node).conditionalExpression).name) != ExpressionType.BOOL)
                    node.setError("If condition must be of type boolean");
            } else if (getExpressionType(((IfClause) node).conditionalExpression) != ExpressionType.BOOL)
                node.setError("If condition must be of type boolean");
        }
    }
}
