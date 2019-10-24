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
        if (root instanceof VariableAssignment)
            findVariableAssignment((VariableAssignment) root);
        else if (root instanceof VariableReference)
            checkVariableReference((VariableReference) root);
        else if (root instanceof Operation) {
            checkOperationOperands((Operation) root);
            colorNotAllowedInOperation((Operation) root);
        }
        else if (root instanceof Declaration)
            checkStyleDeclaration((Declaration) root);
        else if (root instanceof IfClause)
            checkIfCondition((IfClause) root);

        root.getChildren().forEach(this::traverse);
    }

    /**
     * Stores found variable declaration in hash map
     * @param node to check
     */
    private void findVariableAssignment(VariableAssignment node) {
        if (node != null)
            variableTypes.getFirst().put(node.name.name, getExpressionTypeFromExpression(node.expression));
    }

    /**
     * Checks found variable reference against hash map
     * @param node to check
     */
    private void checkVariableReference(VariableReference node) {
        if (node != null && !variableTypes.getFirst().containsKey(node.name))
            node.setError(String.format("Variable \'%s\' is not yet defined.", node.name));
    }

    /**
     * Checks if left- and right hand side are of equal expression type
     * Multiplication allows one operand to be of expression type scalar
     * @param node to check
     * @return expression type of the operation
     */
    private ExpressionType checkOperationOperands(Operation node) {
        if (node != null) {
            ExpressionType lhsNode = getExpressionTypeFromExpression(node.lhs),
                    rhsNode = getExpressionTypeFromExpression(node.rhs);

            if (node instanceof MultiplyOperation) {
                if (lhsNode != ExpressionType.SCALAR && rhsNode != ExpressionType.SCALAR)
                    node.setError("A multiplication must be made with a scalar value");
                return lhsNode != ExpressionType.SCALAR ? lhsNode : rhsNode;
            } else if (node instanceof AddOperation || node instanceof SubtractOperation) {
                if (!lhsNode.equals(rhsNode)) node.setError("Operands must be of same expression type");
                return lhsNode;
            }
        }

        return ExpressionType.UNDEFINED;
    }

    /**
     * Prevents expression type color in operations
     * @param node to check
     */
    private void colorNotAllowedInOperation(Operation node) {
        if (node != null && (node.lhs instanceof ColorLiteral || node.rhs instanceof ColorLiteral)) {
            node.setError(String.format("An operand cannot be of type: %s", ExpressionType.COLOR));
        }
    }

    /**
     * Checks if available properties are of their corresponding expression type
     * @param node to check
     */
    private void checkStyleDeclaration(Declaration node) {
        if (node != null) {
            HashMap<String, List<ExpressionType>> map = StyleAttributeChecker.getMap();
            ExpressionType expressionType = getExpressionTypeFromExpression(node.expression);

            if (map.containsKey(node.property.name)) {
                boolean valid = false;

                for (ExpressionType validExpressionType :
                        map.get(node.property.name)) {
                    valid = validExpressionType.equals(expressionType);
                    if (valid) break;
                }

                if (!valid)
                    node.setError(String.format("Style attribute \'%s\' cannot be of expression type \'%s\'", node.property.name, expressionType));
            } else {
                node.setError(String.format("Property \'%s\' does not exist", node.property.name));
            }
        }
    }

    /**
     * Checks if if-condition is of expression type boolean
     * @param node to check
     */
    private void checkIfCondition(IfClause node) {
        if (node != null) {
            if (node.conditionalExpression instanceof VariableReference) {
                if (variableTypes.get(0).get(((VariableReference) node.conditionalExpression).name) != ExpressionType.BOOL)
                    node.setError("If condition must be of type boolean");
            } else if (getExpressionType(node.conditionalExpression) != ExpressionType.BOOL)
                node.setError("If condition must be of type boolean");
        }
    }

    /**
     * Returns the type of expression be it a Literal, VariableReference or Operation
     * @param node to check
     * @return type of expression
     */
    private ExpressionType getExpressionTypeFromExpression(Expression node) {
        if (node instanceof Literal) return ExpressionResolver.getExpressionType(node);
        if (node instanceof VariableReference) return variableTypes.getFirst().get(((VariableReference) node).name);
        if (node instanceof Operation) return checkOperationOperands((Operation) node);
        else return ExpressionType.UNDEFINED;
    }
}
