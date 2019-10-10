package nl.han.ica.icss.parser;

import java.util.Stack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

import javax.swing.text.Style;
import javax.swing.text.html.StyleSheet;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private Stack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new Stack<>();
	}
    public AST getAST() {
        return ast;
    }

    // Stylesheet
    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        currentContainer.push(ast.root);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        currentContainer.pop();
    }

    // Variable
    @Override
    public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        ASTNode variableAssignment = new VariableAssignment();
        currentContainer.peek().addChild(variableAssignment);
        currentContainer.push(variableAssignment);
    }

    @Override
    public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
        ASTNode variableReference = new VariableReference(ctx.getChild(0).getText());
        currentContainer.peek().addChild(variableReference);
    }

    // Stylerule
    @Override
    public void enterStyleRule(ICSSParser.StyleRuleContext ctx) {
        ASTNode stylerule = new Stylerule();
        currentContainer.peek().addChild(stylerule);
        currentContainer.push(stylerule);
    }

    @Override
    public void exitStyleRule(ICSSParser.StyleRuleContext ctx) {
	    currentContainer.pop();
    }

    // Selector
    @Override
    public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
        Selector selector = new IdSelector(ctx.getChild(0).getText());
        currentContainer.peek().addChild(selector);
    }

    @Override
    public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
        Selector selector = new ClassSelector(ctx.getChild(0).getText());
        currentContainer.peek().addChild(selector);
    }

    @Override
    public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
        Selector selector = new TagSelector(ctx.getChild(0).getText());
        currentContainer.peek().addChild(selector);
    }

    // Declaration
    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        ASTNode declaration = new Declaration(ctx.getChild(0).getText());
        currentContainer.peek().addChild(declaration);
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        currentContainer.pop();
    }

    // Operation
    @Override
    public void enterMultiplyOperation(ICSSParser.MultiplyOperationContext ctx) {
        Operation operation = new MultiplyOperation();
        currentContainer.peek().addChild(operation);
        currentContainer.push(operation);
    }

    @Override
    public void exitMultiplyOperation(ICSSParser.MultiplyOperationContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterAddOperation(ICSSParser.AddOperationContext ctx) {
        Operation operation = new AddOperation();
        currentContainer.peek().addChild(operation);
        currentContainer.push(operation);
    }

    @Override
    public void exitAddOperation(ICSSParser.AddOperationContext ctx) {
        currentContainer.pop();
    }

    @Override
    public void enterSubtractOperation(ICSSParser.SubtractOperationContext ctx) {
        Operation operation = new SubtractOperation();
        currentContainer.peek().addChild(operation);
        currentContainer.push(operation);
    }

    @Override
    public void exitSubtractOperation(ICSSParser.SubtractOperationContext ctx) {
        currentContainer.pop();
    }

    // Literal
    @Override
    public void exitBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
        Literal literal = null;

	    if (ctx.getChild(0) instanceof ICSSParser.TrueBoolContext) {
            literal = new BoolLiteral(true);
        } else if (ctx.getChild(0) instanceof ICSSParser.FalseBoolContext) {
            literal = new BoolLiteral(false);
        }

	    currentContainer.peek().addChild(literal);
    }

    @Override
    public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        Literal literal = new ColorLiteral(ctx.getChild(0).getText());
        currentContainer.peek().addChild(literal);
    }

    @Override
    public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
        Literal literal = new PercentageLiteral(ctx.getChild(0).getText());
        currentContainer.peek().addChild(literal);
    }

    @Override
    public void exitPixelsizeLiteral(ICSSParser.PixelsizeLiteralContext ctx) {
        Literal literal = new PixelLiteral(ctx.getChild(0).getText());
        currentContainer.peek().addChild(literal);
    }

    @Override
    public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
        Literal literal = new ScalarLiteral(ctx.getChild(0).getText());
        currentContainer.peek().addChild(literal);
    }


}
