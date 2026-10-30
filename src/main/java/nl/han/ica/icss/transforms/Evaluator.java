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
    private ASTNode newAST = new ASTNode();
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

                    toRemove.add(node);
                    //potential bug met dat hij niet tmp zal checken?
                //vind dat moeilijk na te kijken
                } else if (node instanceof Operation) {
                ASTNode tmp = getOperationValue((Operation) node,parent);
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
        ArrayList<ASTNode> nodes = parent.getChildren();
        ASTNode boolNode = nodes.get(0);
        Boolean executeIfStatement = true;
        if(boolNode instanceof BoolLiteral) {
            executeIfStatement = ((BoolLiteral) boolNode).value;
        } else if(boolNode instanceof VariableReference){
            executeIfStatement = ((BoolLiteral) variableValues.get((VariableReference) boolNode)).value;
        }
//        parent.getChildren().remove(0);//this node should not be added to the replacement as it is the boolean
        toRemove.add(boolNode);
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

        Literal left = getOperationValue(lhs,parent);
        Literal right = getOperationValue(rhs,parent);

        int value=0;

        if(parent instanceof AddOperation) {
            value = getLiteralIntValue(left)+getLiteralIntValue(right);
        } else if(parent instanceof SubtractOperation) {
            value = getLiteralIntValue(left)-getLiteralIntValue(right);
        } else if(parent instanceof  MultiplyOperation) {
            value = getLiteralIntValue(left)*getLiteralIntValue(right);
        }

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

    private Literal getOperationValue(ASTNode parent,ASTNode granparent) {
        if(parent instanceof Operation) {
            Literal value = executeOperation((Operation)  parent);
            granparent.addChild(value);
            toRemove.add(parent);
            return value;

        } else if(parent instanceof Literal) {
            return (Literal)  parent;
        } else if (parent instanceof VariableReference) {
            Literal value = variableValues.get((VariableReference) parent);
            granparent.addChild(value);
            toRemove.add(parent);
            return value;
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

