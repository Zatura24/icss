package nl.han.ica.icss.helper;

import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.List;

public final class StyleAttributeChecker {
    private static HashMap<String, List<ExpressionType>> map;

    private StyleAttributeChecker() {
        map = new HashMap<>();
        map.put("color", List.of(ExpressionType.COLOR));
        map.put("background-color", List.of(ExpressionType.COLOR));
        map.put("width", List.of(ExpressionType.PIXEL, ExpressionType.PERCENTAGE));
        map.put("height", List.of(ExpressionType.PIXEL, ExpressionType.PERCENTAGE));
    }

    public static HashMap<String, List<ExpressionType>> getMap() {
        if (map == null) new StyleAttributeChecker(); return map;
    }
}
