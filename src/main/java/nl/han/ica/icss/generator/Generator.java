package nl.han.ica.icss.generator;


import nl.han.ica.datastructures.Node;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.VariableAssignment;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Generator {

	public String generate(AST ast) {
        String result = diver(ast.root);

        return result;
	}

    private String diver (ASTNode parent) {
        String result = "";
        ArrayList<ASTNode> nodes = parent.getChildren();
        for(ASTNode node : nodes){
            if(node.getChildren().size()==0){
                result+= generateWord(node);
            } else {
                diver(node);

            }
        }
        return result;
    }

//	private String generateLines (ASTNode parent, int level) {
//        if(parent instanceof ) {
////            return
//        } else if(parent instanceof Stylerule) {
//
//        }
//        return "";
//    }

    private String generateWord(ASTNode node) {
        try {
            return node.getNodeLabel().split("\\(")[1].split("\\)")[0];
        } catch (Exception ex) {
            return node.getNodeLabel();
        }
    }
}
