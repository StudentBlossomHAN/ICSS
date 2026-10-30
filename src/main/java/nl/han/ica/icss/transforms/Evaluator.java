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

import java.lang.reflect.Array;
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
        var stylesheet = ast.root;
        branchCheck(stylesheet);
    }

    private void branchCheck(ASTNode parent) {
        ArrayList<ASTNode> nodes = parent.getChildren();

        ArrayList<ASTNode> toRemove = new ArrayList<>();
        ArrayList<ASTNode> toAdd = new ArrayList<>();
        for (ASTNode node : nodes) {
            if (node instanceof VariableAssignment) {
                setVariableValues((VariableAssignment) node);
                toRemove.add(node);
            } else if (node instanceof IfClause) {
                    ArrayList<ASTNode> tmp = ifChecker(node);
                toRemove.add(node);
                    tmp.forEach(toAdd::add);
            } else if (node instanceof Operation) {
                ASTNode tmp = getOperationValue((Operation) node);
                toRemove.add(node);//sometimes works?
                toAdd.add(tmp);
            } else if (node instanceof VariableReference) {
                Literal tmp = getOperationValue((VariableReference) node);
                toRemove.add(node);
                toAdd.add(tmp);
            } else {
                activateBranchCheck(node);
            }
        }
        toRemove.forEach(parent::removeChild);
        toAdd.forEach(parent::addChild);
        parent.getChildren().removeAll(toRemove);
        parent.getChildren().forEach(node -> {
            if(node instanceof IfClause){
                System.out.println(parent);
                System.out.println();
                System.out.println(parent.removeChild(node));
                System.out.println();
                System.out.println(parent);}});

    }
    private void activateBranchCheck(ASTNode parent) {
        if(!parent.getChildren().isEmpty()) {
            branchCheck(parent);
        }
    }

    private ArrayList<ASTNode> ifChecker (ASTNode parent) {
        ArrayList<ASTNode> nodes = parent.getChildren();
        ASTNode boolNode = nodes.get(0);
        Boolean executeIfStatement = true;
        if(boolNode instanceof BoolLiteral) {
            executeIfStatement = ((BoolLiteral) boolNode).value;
        } else if(boolNode instanceof VariableReference){
            executeIfStatement = ((BoolLiteral) variableValues.get((VariableReference) boolNode)).value;
        }
        ArrayList<ASTNode> returnArray = new ArrayList<>();
        for(ASTNode node : nodes) {
            if((node instanceof ElseClause &&!executeIfStatement)) {
                for(ASTNode child : node.getChildren()){
                    returnArray.add(child);
                }
            } else if(!(node instanceof ElseClause) && executeIfStatement) {
                returnArray.add(node);
            }
        }
        returnArray.remove(boolNode);
        return returnArray;
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

    private Literal getOperationValue(ASTNode parent) {
        if(parent instanceof Operation) {
            Literal value = executeOperation((Operation)  parent);
            return value;
        } else if(parent instanceof Literal) {
            return (Literal)  parent;
        } else if (parent instanceof VariableReference) {
            Literal value = variableValues.get((VariableReference) parent);
            return value;
        }else {
            System.out.println("Mistake in de aanroep van getOperationValue?");
        }
        return null;
    }


    private void setVariableValues (VariableAssignment parent) {
        VariableReference ref = parent.name;
        Literal value;
        if(parent.getChildren().get(1) instanceof Literal) {
            value = (Literal) parent.expression;
        } else  {
            value = getOperationValue(parent);
        }
        variableValues.put(ref, value);
    }


}

