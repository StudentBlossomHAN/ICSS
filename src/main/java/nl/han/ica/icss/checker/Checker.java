package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.Node;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
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
     List<Class> disallowedOperationTypes = new ArrayList<>();

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

        disallowedOperationTypes.add(ColorLiteral.class);
        disallowedOperationTypes.add(BoolLiteral.class);
     }

    public void check(AST ast) {
        for (ASTNode node : ast.root.getChildren()) {
            if(node instanceof Stylerule) {
                checkStyleRule(node);
            } else if(node instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) node,globalVariables);
            }
        }
    }
    private void checkVariableAssignment(VariableAssignment parent,HashMap<VariableReference, Class> variableTypes) {
         ASTNode lhs = parent.getChildren().get(0);
         ASTNode rhs = parent.getChildren().get(1);

         Class type = null;

        if(lhs instanceof VariableReference) {
            if(rhs instanceof Literal) {
                type = rhs.getClass();
            } else if(rhs instanceof VariableReference){
                type=getVariableReference((VariableReference) rhs,variableTypes);
            } else if(rhs instanceof Operation) {
                type=operationCheck((Operation) rhs,variableTypes);
            } else {
                parent.setError("Variable assignment mishap");
            }
            variableTypes.put(parent.name, type);
        } else {
            parent.setError("Not a proper way to write a variable");
        }
    }
    private void checkStyleRule(ASTNode parent) {
        ArrayList<ASTNode> nodes = parent.getChildren();
        if(! (nodes.get(0) instanceof Selector)) {
            parent.setError("no selector declared");
        }
        //we make new local variables
        HashMap<VariableReference, Class> localVariables = new HashMap<>();
        localVariables.putAll(globalVariables);

        for(ASTNode node : nodes) {
            if(node instanceof Declaration) {
                checkDeclaration((Declaration) node,localVariables);
            } else if (node instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment)  node,localVariables);
            } else if (node instanceof IfClause) {
                checkIfClause((IfClause) node, localVariables);
            }
        }
    }
    private void checkIfClause(IfClause parent, HashMap<VariableReference, Class> localVariables) {
        ArrayList<ASTNode> nodes = parent.getChildren();
        if(! (nodes.get(0) instanceof BoolLiteral)) {
            if(nodes.get(0) instanceof VariableReference) {
                if(getVariableReference((VariableReference) nodes.get(0),localVariables) == BoolLiteral.class) {

                } else {
                    parent.setError("non boolean variable in the if statement");
                }
            } else {
                parent.setError("not a boolean after the If");
            }
        }
        checkInsideStatements(nodes,localVariables);
    }
private void checkInsideStatements (ArrayList<ASTNode> nodes,HashMap<VariableReference, Class> localVariables) {
    for (ASTNode node : nodes) {
        if (node instanceof Declaration) {
            checkDeclaration((Declaration) node, localVariables);
        } else if (node instanceof VariableAssignment) {
            checkVariableAssignment((VariableAssignment) node, localVariables);
        } else if (node instanceof IfClause) {
            checkIfClause((IfClause) node, localVariables);
        } else if (node instanceof ElseClause) {
            checkInsideStatements(node.getChildren(), localVariables);
        }
    }
}
    private void checkDeclaration(Declaration parent,HashMap<VariableReference, Class> localVariables) {
        if(parent.getChildren().size() <2) {
            parent.setError("not a complete declaration");
        } else if (parent.getChildren().size() ==2){
            ASTNode leftChild = parent.getChildren().get(0);
            ASTNode rightChild = parent.getChildren().get(1);
            //get all allowed data
            List<Class> acceptedTypes = allowedDataTypes.get(parent.property.name);
            //check if other side is in there
            if(!acceptedTypes.contains(rightChild.getClass())) {//if right side is numba
                if(rightChild instanceof VariableReference) {//if it is variable (crazy)
                    if(!acceptedTypes.contains(getVariableReference((VariableReference) rightChild,localVariables))){
                        parent.setError("variable is the wrong type");
                    }
                } else if (rightChild instanceof Operation) {
                   if(acceptedTypes.contains( operationCheck((Operation) rightChild ,localVariables))){

                   } else {
                    parent.setError("Operation with wrong data types");
                   }
                } else {
                    parent.setError("mismatched data types");
                }
            }
        } else {
            parent.setError("too many childern on the declaration");
        }


    }
    private Class operationCheck(Operation operation,HashMap<VariableReference, Class> localVariables) {
        Expression lhs = operation.lhs;
        Expression rhs = operation.rhs;

        Class lClass = checkOperationSide(lhs,localVariables);
        Class rClass = checkOperationSide(rhs,localVariables);

        if(disallowedOperationTypes.contains(lClass) || disallowedOperationTypes.contains(rClass)) {
            operation.setError("operation on unoperable data types");
        } else if(operation instanceof MultiplyOperation) {
            if(rClass == ScalarLiteral.class || lClass == ScalarLiteral.class) {
                return (lClass == ScalarLiteral.class) ? rClass : lClass;
            } else {
                operation.setError("multiplication without scalars");
            }
        } else if(lClass == rClass){
            return lClass;
        } else {
            operation.setError("Operation with different data types");
        }
    return null;
    }

    private Class checkOperationSide (Expression node,HashMap<VariableReference, Class> localVariables) {
         if(node instanceof  Operation) {
             return operationCheck((Operation) node,localVariables);
         } else {
             if(node instanceof VariableReference){
                 return getVariableReference((VariableReference) node,localVariables);
             }
             return node.getClass();
         }
    }

    private Class getVariableReference(VariableReference variableReference, HashMap<VariableReference, Class> localVariables) {
         if(localVariables.containsKey(variableReference)) {
             return localVariables.get(variableReference);
         } else {
             variableReference.setError("variable not found");
             return null;
         }
    }


}