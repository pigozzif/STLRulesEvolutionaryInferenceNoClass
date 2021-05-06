package it.units.malelab.learningstl.BuildingBlocks;


public enum MonitorExpressions {

    PROP(".prop"),
    NOT(".not"),
    OR(".or"),
    AND(".and"),
    SINCE(".since"),
    HISTORICALLY(".historically"),
    ONCE(".once"),
    UNTIL(".until"),
    GLOBALLY(".globally"),
    EVENTUALLY(".eventually");

    private final String string;

    MonitorExpressions(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

}
