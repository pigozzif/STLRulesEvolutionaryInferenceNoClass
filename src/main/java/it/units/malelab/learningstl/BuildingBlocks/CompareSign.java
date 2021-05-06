package it.units.malelab.learningstl.BuildingBlocks;

import java.util.function.BiFunction;


public enum CompareSign {

    SMALLER("<", (Double a, Double b) -> b - a),
    GREATER(">", (Double a, Double b) -> a - b),
    SMALLER_OR_EQUAL("<=", (Double a, Double b) -> b - a + 1),
    GREATER_OR_EQUAL(">=", (Double a, Double b) -> a - b + 1),
    EQUAL("==", (Double a, Double b) -> - Math.abs(a - b));

    private final String sign;
    private final BiFunction<Double, Double, Double> function;

    CompareSign(String type, BiFunction<Double, Double, Double> func) {
        this.sign = type;
        this.function = func;
    }

    @Override
    public String toString() {
        return sign;
    }

    public BiFunction<Double, Double, Double> getValue() {
        return this.function;
    }

}
