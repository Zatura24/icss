package nl.han.ica.icss.helper;

import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public final class StyleAttributeChecker {
    private static HashMap<String, ExpressionType> map;

    private StyleAttributeChecker() {
        map = new HashMap<>();
        map.put("color", ExpressionType.COLOR);
        map.put("background-color", ExpressionType.COLOR);
        map.put("width", ExpressionType.PIXEL);
        map.put("height", ExpressionType.PIXEL);
    }

    public static HashMap<String, ExpressionType> getMap() {
        if (map == null) new StyleAttributeChecker();

        return map;
    }
}
