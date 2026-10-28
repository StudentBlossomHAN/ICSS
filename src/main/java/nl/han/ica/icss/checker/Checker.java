package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.Node;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.types.ExpressionType;

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
        variableTypes = new HANLinkedList<>();
        HashMap<String, ExpressionType> temp1 = new HashMap<>();
        temp1.put("color", ExpressionType.COLOR);
        variableTypes.addFirst(temp1);
        HashMap<String, ExpressionType> temp2 = new HashMap<>();
        temp2.put("if", ExpressionType.BOOL);
        variableTypes.addFirst(temp2);


        for (ASTNode node : ast.root.getChildren()) {



































            if (node instanceof VariableAssignment) {

            } else if (node instanceof Stylerule) {
                styleRuleCheck(node);}
        }

    }
    private void styleRuleCheck(ASTNode node) {
        //check node
    }}