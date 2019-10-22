package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.helper.ExpressionTypeResolver;

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

        traverse(ast.root);
    }

    private void traverse(ASTNode root) {
        findVariableAssignment(root);
        removeVariableReference(root);

        for (ASTNode node :
                root.getChildren()) {
            traverse(node);
        }
    }

    private void findVariableAssignment(ASTNode node) {
        if (node instanceof VariableAssignment) {
            if (variableValues.isEmpty()) variableValues.add(new HashMap<>());

            variableValues.getFirst().put(((VariableAssignment) node).name.name, ((Literal) ((VariableAssignment) node).expression));
        }
    }

    private void removeVariableReference(ASTNode node) {
        if (node instanceof Declaration && ((Declaration) node).expression instanceof VariableReference)
            ((Declaration) node).expression = variableValues.getFirst().get(((VariableReference) ((Declaration) node).expression).name);
    }
}
