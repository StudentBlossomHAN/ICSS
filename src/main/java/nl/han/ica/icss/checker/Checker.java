package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.Node;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private HashMap<VariableReference, Class> globalVariables =  new HashMap<>();
     Map<String, List<Class>> allowedDataTypes = new HashMap<>();

     public Checker() {
         makeAllowedDataTypes();
     }

     private void makeAllowedDataTypes() {
         allowedDataTypes.put("color", new ArrayList<>());
         allowedDataTypes.get("color").add(ColorLiteral.class);

         allowedDataTypes.put("background-color", new ArrayList<>());
         allowedDataTypes.get("background-color").add(ColorLiteral.class);

         allowedDataTypes.put("width", new ArrayList<>());
         allowedDataTypes.get("width").add(PixelLiteral.class);
         allowedDataTypes.get("width").add(PercentageLiteral.class);

         allowedDataTypes.put("height", new ArrayList<>());
         allowedDataTypes.get("height").add(PixelLiteral.class);
         allowedDataTypes.get("height").add(PercentageLiteral.class);

     }

    public void check(AST ast) {
        for (ASTNode node : ast.root.getChildren()) {
            if(node instanceof Stylerule) {
                checkStyleRule(node);
            } else if(node instanceof VariableAssignment) {
                checkGlobalVariableAssignment((VariableAssignment) node);
            }
        }
    }
    private void checkGlobalVariableAssignment(VariableAssignment parent) {
        if(parent.getChildren().get(0) instanceof VariableReference) {
            if(parent.getChildren().get(1) instanceof Literal) {
                globalVariables.put(parent.name,parent.getChildren().get(1).getClass());
            } else {
                parent.setError("Variable assignment mishap");
            }
        } else {
            parent.setError("Not a proper way to write a variable");
        }
    }
    private void checkLocalVariableAssignment (VariableAssignment parent) {

    }
    private void  checkStyleRule(VariableAssignment parent) {

    }
    private void checkStyleRule(ASTNode parent) {
        ArrayList<ASTNode> nodes = parent.getChildren();
        if(nodes.get(0) instanceof IdSelector
        ||nodes.get(0) instanceof TagSelector
        ||nodes.get(0) instanceof ClassSelector) {} else {
        //error?
        }
        for(ASTNode node : nodes) {
            if(node instanceof Declaration) {
                checkDeclaration((Declaration) node);
            } else if (node instanceof VariableAssignment) {
                checkLocalVariableAssignment((VariableAssignment)  node);
            }
        }
    }
    private void checkDeclaration(Declaration parent) {
         HashMap<VariableReference, Class> localVariables = new HashMap<>();
                 localVariables.putAll(globalVariables);
//        System.out.println(localVariables);
        if(parent.getChildren().size() <2) {
            //error te weinig
        } else if (parent.getChildren().size() ==2){
            //check variable
            List<Class> acceptedTypes = allowedDataTypes.get(parent.property.name);
            if(!acceptedTypes.contains(parent.getChildren().get(1).getClass())) {
                if(parent.getChildren().get(1) instanceof VariableReference) {
                    System.out.println(11);
                    if(localVariables.containsKey(parent.expression)) {
                        //dan is alles goed
                    } else {
                        parent.setError("non existing variable");
                    }
                } else {
                    parent.setError("mismatched data types 2/0");
                }
            }
        } else {


        }


    }
    private ASTNode operationCheck(Operation operation) {
        Expression lhs = operation.lhs;
        Expression rhs = operation.rhs;

        if (lhs instanceof Operation) {
            lhs = (Expression) operationCheck((Operation) lhs);

        }
        else if (lhs instanceof VariableReference) {
            lhs = (Expression) getVariableReference((VariableReference) lhs);
        }

        if (rhs instanceof Operation) {
            rhs = (Expression) operationCheck((Operation) lhs);

        }
        else if (rhs instanceof VariableReference) {
            rhs = (Expression) getVariableReference((VariableReference) lhs);
        }

        if (lhs instanceof ColorLiteral ||  rhs instanceof ColorLiteral ) {
            operation.setError("operation cannot be done on invalid data type");
            return null;
        }
        if (operation instanceof MultiplyOperation) {
            if (!(lhs instanceof ScalarLiteral) && !(rhs instanceof ScalarLiteral)) {
                operation.setError("multiplication cannot be done with non scalar literal");
                return null;
            }
            return lhs instanceof ScalarLiteral ? rhs : lhs;
        }
        if (lhs.getClass() != rhs.getClass()) {
            operation.setError("operation must be performed with identical data types");
        } else {
            return rhs;
        }
        return null;
    }



}