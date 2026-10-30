package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private HashMap<VariableReference, Literal> variableValues;

    public Evaluator() {
        variableValues = new HashMap<>();
    }

    @Override
    public void apply(AST ast) {
    branchCheck(ast.root);
    }

    private void branchCheck(ASTNode parent) {
        ArrayList<ASTNode> nodes = parent.getChildren();

        for(ASTNode node : nodes) {
            if(node instanceof VariableAssignment) {
                setVariableValues((VariableAssignment) node);
            } else if (node instanceof IfClause) {
                    ASTNode tmp = ifChecker(node);
                    parent.addChild(tmp);
                    parent.removeChild(node);
                    //potential bug met dat hij niet tmp zal checken?
                //vind dat moeilijk na te kijken
                } else {
                activateBranchCheck(node);
                }
            }
        }
    private void activateBranchCheck(ASTNode parent) {
        if(!parent.getChildren().isEmpty()) {
            branchCheck(parent);
        }
    }

    private ASTNode ifChecker (ASTNode parent) {
        System.out.println(parent);
        ArrayList<ASTNode> nodes = parent.getChildren();
        ASTNode boolNode = nodes.get(0);
        Boolean executeIfStatement = true;
        if(boolNode instanceof BoolLiteral) {
            executeIfStatement = ((BoolLiteral) boolNode).value;
        } else if(boolNode instanceof VariableReference){
            executeIfStatement = ((BoolLiteral) variableValues.get((VariableReference) boolNode)).value;
        }
        //okay so I had fun with this one ;)
        //I was cooking!
        /*
        so the nodes in an if statement should stay if they are
        1. an else clause and the if statement did not execute
        2. not an else clause and the if statement executed
        they should be removed if:
        3. an else clause and the if statement got executed
        4. not an else clause if the statement did not get executed

        bij 3. moeten we wel de kinderen overzetten

         */
        nodes.remove(0);//this node should not be added to the replacement as it is the boolean

        ASTNode replacement = new ASTNode();
        for(ASTNode node : nodes) {
            if((node instanceof ElseClause &&!executeIfStatement)) {
                for(ASTNode child : node.getChildren()){
                    replacement.addChild(child);
                }
            } else if(!(node instanceof ElseClause) && executeIfStatement) {
                replacement.addChild(node);
            }
        }
        return replacement;
    }

    private Literal executeOperation(Operation parent) {
        Expression lhs = parent.lhs;
        Expression rhs = parent.rhs;

        Literal left = getOperationValue(lhs);
        Literal right = getOperationValue(rhs);

        int value=0;

        if(parent instanceof AddOperation) {
            value = getLiteralIntValue(left)+getLiteralIntValue(right);
        } else if(parent instanceof SubtractOperation) {

        }

        //public enum ExpressionType {
        //    PIXEL,
        //    PERCENTAGE,
        //    COLOR,
        //    SCALAR,
        //    UNDEFINED,
        //    BOOL
        //}
        Literal returnValue = null;

        if(lhs instanceof PixelLiteral ||  rhs instanceof PixelLiteral) {
            returnValue = new PixelLiteral(value);
        } else if(lhs instanceof PercentageLiteral || rhs instanceof PercentageLiteral) {
            returnValue = new PercentageLiteral(value);
        } else if(lhs instanceof ScalarLiteral || rhs instanceof ScalarLiteral) {
            returnValue = new ScalarLiteral(value);
        }
        return returnValue;
    }

    private int getLiteralIntValue(Literal literal) {
        if(literal instanceof PixelLiteral) {
            return ((PixelLiteral) literal).value;
        } else if(literal instanceof PercentageLiteral) {
            return ((PercentageLiteral) literal).value;
        } else if(literal instanceof ScalarLiteral) {
            return ((ScalarLiteral) literal).value;
        } else {
            System.out.println("je probeert een int waarde te krijgen van een niet int waarde bestand in getLiteralIntValue");
        }
        return 0;
    }

    private Literal getOperationValue(ASTNode parent) {
        if(parent instanceof Operation) {
            return executeOperation((Operation)  parent);
        } else if(parent instanceof Literal) {
            return (Literal)  parent;
        } else if (parent instanceof VariableReference) {
            return variableValues.get((VariableReference) parent);
        }else {
            System.out.println("Mistake in de aanroep van getOperationValue?");
        }
        return null;
    }











































    private void setVariableValues (VariableAssignment parent) {
        VariableReference ref = parent.name;
        Literal value = (Literal) parent.expression;
        variableValues.put(ref, value);
    }


}

