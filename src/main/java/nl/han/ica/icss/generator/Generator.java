package nl.han.ica.icss.generator;


import nl.han.ica.datastructures.Node;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Generator {

    public String generate(AST ast) {
        String result = "";
        for (var node : ast.root.getChildren()) {
            if (!(node instanceof Stylerule)) {
                continue;
            }
            result += styleruleGenerator((Stylerule) node);
            result += declarationGenerator(node);
            result += "}\n\n";
        }
        return result;
    }


    private String styleruleGenerator(Stylerule stylerule) {
        var selectors = new ArrayList<String>();

        for (var selector : stylerule.selectors) {
            selectors.add(selector.toString());
        }
        String str = String.join(", ", selectors) + " {\n";
        return str;
    }

    private String declarationGenerator(ASTNode astNode) {
        String str = "";
        for (var node : astNode.getChildren()) {
            if(!(node instanceof Declaration)) {
                continue;
            }
            var declartion = (Declaration) node;
            str += "  " + declartion.property.name + ": " + literalToString(declartion.expression) + "\n";
        }
        return str;
    }

    private String literalToString(Expression expression) {
        if  (expression instanceof PixelLiteral) {
            return ((PixelLiteral) expression).value + "px";
        } else if (expression instanceof PercentageLiteral) {
            return ((PercentageLiteral) expression).value + "%";
        } else if (expression instanceof ColorLiteral) {
            return ((ColorLiteral) expression).value;
        }
        return null;
    }
}
