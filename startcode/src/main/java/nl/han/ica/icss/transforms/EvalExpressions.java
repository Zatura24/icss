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
        findVariableAssignment(root);
        removeVariableReference(root);

        root.getChildren().forEach(this::variableTraverse);
    }

    private void expressionTraverse(ASTNode root) {
        removeExpression(root);

        root.getChildren().forEach(this::expressionTraverse);
    }

    private void findVariableAssignment(ASTNode node) {
        if (node instanceof VariableAssignment) {
            if (((VariableAssignment) node).expression instanceof Literal)
                variableValues.getFirst().put(((VariableAssignment) node).name.name, ((Literal) ((VariableAssignment) node).expression));

            if (((VariableAssignment) node).expression instanceof VariableReference) {
                Literal variableReferenceLiteral = variableValues.getFirst().get(((VariableReference) ((VariableAssignment) node).expression).name);
                variableValues.getFirst().put(((VariableAssignment) node).name.name, variableReferenceLiteral);
            }

            if (((VariableAssignment) node).expression instanceof Operation) {
                variableValues.getFirst().put(((VariableAssignment) node).name.name, calculateExpression((Operation) ((VariableAssignment) node).expression));
            }
        }
    }

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

    private void removeExpression(ASTNode node) {
        if (node instanceof Declaration && ((Declaration) node).expression instanceof Operation)
            ((Declaration) node).expression = calculateExpression((Operation) ((Declaration) node).expression);
    }

    /**
     * Calculate operation
     * @param operation
     * @return Literal with calculated value
     */
    private Literal calculateExpression(Operation operation) {
        Literal lhs = getLiteral(operation.rhs), rhs = getLiteral(operation.lhs);

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

    private Literal getLiteral(Expression node) {
        Literal hs;
        if (node instanceof Operation)
            hs = calculateExpression((Operation) node);
        else if (node instanceof VariableReference)
            hs = variableValues.getFirst().get(((VariableReference) node).name);
        else
            hs = (Literal) node;
        return hs;
    }

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
