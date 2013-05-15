package message;

import java.io.Serializable;

/**
 */
public class CalculationRequest implements Serializable {
    private final String calculationString;

    public CalculationRequest(String calculationString) {
        this.calculationString = calculationString;
    }

    public String getCalculationString() {
        return calculationString;
    }

    @Override
    public String toString() {
        return "CalculationRequest{" +
                "calculationString='" + calculationString + '\'' +
                "} ";
    }
}
