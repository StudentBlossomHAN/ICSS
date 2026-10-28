package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import nl.han.ica.icss.ast.selectors.*;
import static nl.han.ica.icss.parser.ICSSParser.*;
import javax.swing.text.html.StyleSheet;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer = new HANStack<>();

	public ASTListener() {
		ast = new AST();
		//currentContainer = new HANStack<>();
	}
    public AST getAST() {
        return ast;
    }

    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        var styleSheet = new Stylesheet();
        currentContainer.push(styleSheet);
    }
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
    ast.setRoot((Stylesheet) currentContainer.pop());
    }

    public void enterStyleRule(StyleRuleContext ctx) {
        var rule = new Stylerule();
        currentContainer.push(rule);
    }

    public void exitStyleRule(StyleRuleContext ctx) {
        var rule = currentContainer.pop();
        currentContainer.peek().addChild(rule);
    }

    public void enterSelectorTag(SelectorTagContext ctx) {
        var tagSelector = new TagSelector(ctx.getText());
        currentContainer.push(tagSelector);
    }

    public void exitSelectorTag(SelectorTagContext ctx) {
        var tagSelector = currentContainer.pop();
        currentContainer.peek().addChild(tagSelector);
    }

    public void enterSelectorClass(SelectorClassContext ctx) {
        var classSelector = new ClassSelector(ctx.getText());
        currentContainer.push(classSelector);
    }

    public void exitSelectorClass(SelectorClassContext ctx) {
        var classSelector = currentContainer.pop();
        currentContainer.peek().addChild(classSelector);
    }

    public void enterSelectorId(SelectorIdContext ctx) {
        var idSelector = new IdSelector(ctx.getText());
        currentContainer.push(idSelector);
    }

    public void exitSelectorId(SelectorIdContext ctx) {
        var idSelector = currentContainer.pop();
        currentContainer.peek().addChild(idSelector);
    }

    public void enterVariableReference(VariableReferenceContext ctx) {
        var variableReference = new VariableReference(ctx.getText());
        currentContainer.peek().addChild(variableReference);
    }

    public void enterVariableAssignment(VariableAssignmentContext ctx) {
        var variableAssignment = new VariableAssignment();
        currentContainer.push(variableAssignment);
    }

    public void exitVariableAssignment(VariableAssignmentContext ctx) {
        var variableAssignment = currentContainer.pop();
        currentContainer.peek().addChild(variableAssignment);
    }

    public void enterDeclaration(DeclarationContext ctx) {
        var declaration = new Declaration();
        currentContainer.push(declaration);
    }

    public void exitDeclaration(DeclarationContext ctx) {
        var declaration = currentContainer.pop();
        currentContainer.peek().addChild(declaration);
    }

    public void enterPropertyName(PropertyNameContext ctx) {
        var propertyName = new PropertyName(ctx.getText());
        currentContainer.peek().addChild(propertyName);
    }

    public void enterBoolLiteral(BoolLiteralContext ctx) {
        var boolLiteral = new BoolLiteral(ctx.getText());
        currentContainer.peek().addChild(boolLiteral);
    }

    public void enterColorLiteral(ColorLiteralContext ctx) {
        var colorLiteral = new ColorLiteral(ctx.getText());
        currentContainer.peek().addChild(colorLiteral);
    }

    public void enterPercentLiteral(PercentLiteralContext ctx) {
        var percentageLiteral = new PercentageLiteral(ctx.getText());
        currentContainer.peek().addChild(percentageLiteral);
    }

    public void enterPixelLiteral(PixelLiteralContext ctx) {
        var pixelLiteral = new PixelLiteral(ctx.getText());
        currentContainer.peek().addChild(pixelLiteral);
    }

    public void enterScalarLiteral(ScalarLiteralContext ctx) {
        var scalarLiteral = new ScalarLiteral(ctx.getText());
        currentContainer.peek().addChild(scalarLiteral);
    }


    @Override
    public void enterExpression(ExpressionContext ctx) {
        Operation operation;

        if (ctx.getChildCount() == 3) {
            switch (ctx.getChild(1).getText()) {
                case "+":
                    operation = new AddOperation();
                    break;
                case "-":
                    operation = new SubtractOperation();
                    break;
                default:
                    operation = new MultiplyOperation();
            }
            currentContainer.push(operation);
        }
    }

    @Override
    public void exitExpression(ExpressionContext ctx) {
        if (expressionHasOperation(ctx)) {
            var operation = currentContainer.pop();
            currentContainer.peek().addChild(operation);
        }
    }

    @Override
    public void enterIfStatement(IfStatementContext ctx) {
        var ifStatement = new IfClause();
        currentContainer.push(ifStatement);
    }

    @Override
    public void exitIfStatement(IfStatementContext ctx) {
        var ifStatement = currentContainer.pop();
        currentContainer.peek().addChild(ifStatement);
    }

    @Override
    public void enterElseStatement(ElseStatementContext ctx) {
        var elseStatement = new ElseClause();
        currentContainer.push(elseStatement);
    }

    @Override
    public void exitElseStatement(ElseStatementContext ctx) {
        var elseStatement = currentContainer.pop();
        currentContainer.peek().addChild(elseStatement);
    }

    private boolean expressionHasOperation(ExpressionContext ctx) {
        return ctx.PLUS() != null || ctx.MIN() != null || ctx.MUL() != null;
    }
}