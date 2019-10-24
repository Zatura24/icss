package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.IfClause;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.literals.BoolLiteral;

import java.util.ArrayList;

public class RemoveIf implements Transform {

    @Override
    public void apply(AST ast) {
        ifClauseTraverse(ast.root);
    }

    private void ifClauseTraverse(ASTNode root) {
        if (root instanceof Stylerule)
            replaceIfClause(root);

        root.getChildren().forEach(this::ifClauseTraverse);
    }

    private void replaceIfClause(ASTNode root) {
        ArrayList<ASTNode> currentBody = new ArrayList<>();
        ArrayList<ASTNode> newBody = new ArrayList<>();
        ASTNode nodeToRemove = null;

        if (root instanceof Stylerule) currentBody = ((Stylerule) root).body;
        if (root instanceof IfClause) currentBody = ((IfClause) root).body;

        for (ASTNode node :
                currentBody) {
            if (node instanceof IfClause) {
                if (isIfTrue((IfClause) node)) newBody.addAll(((IfClause) node).body);
                nodeToRemove = node;
            }
        }

        if (root instanceof Stylerule) ((Stylerule) root).body.remove(nodeToRemove);
        if (root instanceof IfClause) ((IfClause) root).body.remove(nodeToRemove);
        newBody.forEach(root::addChild);

        if (!newBody.isEmpty()) replaceIfClause(root);
    }

    private boolean isIfTrue(IfClause node) {
        return node.conditionalExpression.equals(new BoolLiteral(true));
    }
}
