package com.sebastian_daschner.coffee.mcp.entity;

import java.util.Objects;

public class FlavorProfile {

    public String flavor;

    public double percentage;

    @Override
    public String toString() {
        return "FlavorProfile{" +
               "flavor=" + flavor +
               ", percentage=" + percentage +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlavorProfile that = (FlavorProfile) o;
        return Double.compare(that.percentage, percentage) == 0 &&
               Objects.equals(flavor, that.flavor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flavor, percentage);
    }

}
