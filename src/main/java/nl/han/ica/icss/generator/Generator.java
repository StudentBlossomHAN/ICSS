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
        String result = "AAAAAAAAAAAAAA";
        result += generateLines (ast.root,0);
        ArrayList<ASTNode> tmp = ast.root.getChildren();
        for(ASTNode node : tmp) {
            result+= node.toString() + "\n";
        }

        return result;
	}

	private String generateLines (ASTNode parent, int level) {
        if(parent instanceof VariableAssignment) {
//            return
        } else if(parent instanceof Stylerule) {

        }
        return "";
    }

    private String generateWord(ASTNode node) {
        String result = "";

        return  result;
    }
}
