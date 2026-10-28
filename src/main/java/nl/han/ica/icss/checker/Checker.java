package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.Node;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;

/*

public enum ExpressionType {
    PIXEL,
    PERCENTAGE,
    COLOR,
    SCALAR,
    UNDEFINED,
    BOOL
}

 */

public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;


    public void check(AST ast) {
//        variableTypes = new HANLinkedList<>();
//        HashMap<String, ExpressionType> temp1 = new HashMap<>();
//        temp1.put("color", ExpressionType.COLOR);
//        variableTypes.addFirst(temp1);
//        HashMap<String, ExpressionType> temp2 = new HashMap<>();
//        temp2.put("if", ExpressionType.BOOL);
//        variableTypes.addFirst(temp2);


        for (ASTNode node : ast.root.getChildren()) {
            if(node instanceof Stylerule) {
                checkStyleRule(node);
            } else if(node instanceof VariableAssignment) {
                checkVariableAssignment(node);
            }
        }
    }
    private void checkVariableAssignment(ASTNode node) {

    }
    private void checkStyleRule(ASTNode parent) {
        ArrayList<ASTNode> nodes = parent.getChildren();
        if(nodes.get(0) instanceof IdSelector
                ||nodes.get(0) instanceof TagSelector) {}
        for(ASTNode node : nodes) {
            if(node instanceof Declaration) {
                checkDeclaration(node);
            }
        }
    }
    private void checkDeclaration(ASTNode parent) {
        System.out.println(parent.getChildren());
        if(parent.getChildren().size() <2) {
            //error te weinig
        } else if (parent.getChildren().size() ==2){
            //check variable types

        } else {
            parent.getChildren().get(0);

        }


    }
    private void operationCheck(ASTNode node) {

    }



}