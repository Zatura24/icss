package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.helper.LiteralToValue;

import java.util.HashMap;
import java.util.LinkedList;

public class EvalExpressions implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public EvalExpressions() {
        variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new LinkedList<>();
        variableValues.add(new HashMap<>());

        variableTraverse(ast.root);
        expressionTraverse(ast.root);
    }

    private void variableTraverse(ASTNode root) {
        if (root instanceof VariableAssignment)
            findVariableAssignment((VariableAssignment) root);

        removeVariableReference(root);

        root.getChildren().forEach(this::variableTraverse);
    }

    private void expressionTraverse(ASTNode root) {
        if (root instanceof Declaration)
            removeOperation((Declaration) root);

        root.getChildren().forEach(this::expressionTraverse);
    }

    /**
     * Stores found variable declaration in hash map
     * @param node to check
     */
    private void findVariableAssignment(VariableAssignment node) {
        if (node != null && node.expression != null) {
            variableValues.getFirst().put(node.name.name, getLiteral(node.expression));
        }
    }

    /**
     * Replaces variable references with hash map's literal
     * @param node to check
     */
    private void removeVariableReference(ASTNode node) {
        if (node instanceof Declaration && ((Declaration) node).expression instanceof VariableReference)
            ((Declaration) node).expression = variableValues.getFirst().get(((VariableReference) ((Declaration) node).expression).name);

        if (node instanceof Operation) {
            if (((Operation) node).lhs instanceof VariableReference)
                ((Operation) node).lhs = variableValues.getFirst().get(((VariableReference) ((Operation) node).lhs).name);

            if (((Operation) node).rhs instanceof VariableReference)
                ((Operation) node).rhs = variableValues.getFirst().get(((VariableReference) ((Operation) node).rhs).name);
        }

        if (node instanceof IfClause && ((IfClause) node).conditionalExpression instanceof VariableReference)
            ((IfClause) node).conditionalExpression = variableValues.getFirst().get(((VariableReference) ((IfClause) node).conditionalExpression).name);
    }

    /**
     * Removes found operation with calculated literal
     * @param node to check
     */
    private void removeOperation(Declaration node) {
        if (node != null && node.expression instanceof Operation)
            node.expression = calculateOperation((Operation) node.expression);
    }

    /**
     * Calculates operation
     * @param operation to check
     * @return Literal with calculated value
     */
    private Literal calculateOperation(Operation operation) {
        Literal lhs = getLiteral(operation.lhs),
                rhs = getLiteral(operation.rhs);

        int value = 0;

        if (operation instanceof MultiplyOperation) {
            value = LiteralToValue.getIntValue(lhs) * LiteralToValue.getIntValue(rhs);
        } else if (operation instanceof SubtractOperation) {
            value = LiteralToValue.getIntValue(lhs) - LiteralToValue.getIntValue(rhs);
        } else if (operation instanceof AddOperation) {
            value = LiteralToValue.getIntValue(lhs) + LiteralToValue.getIntValue(rhs);
        }

        return createNewLiteral(!(lhs instanceof ScalarLiteral) ? lhs : rhs, value);
    }

    /**
     * Get literal value of given expression
     * @param node to check
     * @return Literal with value
     */
    private Literal getLiteral(Expression node) {
        Literal literal;
        if (node instanceof Operation)
            literal = calculateOperation((Operation) node);
        else if (node instanceof VariableReference)
            literal = variableValues.getFirst().get(((VariableReference) node).name);
        else
            literal = (Literal) node;
        return literal;
    }

    /**
     * Creates a new literal with the same value
     * @param literal to create
     * @param value of new literal
     * @return literal with new value
     */
    private Literal createNewLiteral(Literal literal, int value) {
        if (literal instanceof PercentageLiteral) {
            return new PercentageLiteral(value);
        } else if (literal instanceof PixelLiteral) {
            return new PixelLiteral(value);
        } else if (literal instanceof ScalarLiteral) {
            return new ScalarLiteral(value);
        }

        return null;
    }
}
