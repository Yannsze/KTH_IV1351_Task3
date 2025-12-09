package org.example.model;

/**
 * Represents the cost calculation for a course, including both planned and actual costs.
 * This is a domain model that encapsulates cost data.
 */
public class Cost {
    private final double plannedCost;
    private final double actualCost;

    /**
     * Creates a new cost object.
     *
     * @param plannedCost The planned cost for the course
     * @param actualCost The actual cost for the course
     */
    public Cost(double plannedCost, double actualCost) {
        this.plannedCost = plannedCost;
        this.actualCost = actualCost;
    }

    public double getPlannedCost() {
        return plannedCost;
    }

    public double getActualCost() {
        return actualCost;
    }

    /**
     * Calculates the cost variance (difference between actual and planned).
     *
     * @return The cost variance (positive means over budget, negative means under budget)
     */
    public double getVariance() {
        return actualCost - plannedCost;
    }

    /**
     * Checks if the course is over budget.
     *
     * @return true if actual cost exceeds planned cost
     */
    public boolean isOverBudget() {
        return actualCost > plannedCost;
    }

    @Override
    public String toString() {
        return "Cost{" +
                "plannedCost=" + plannedCost +
                ", actualCost=" + actualCost +
                ", variance=" + getVariance() +
                '}';
    }
}
