package message;

import java.io.Serializable;

/**
 */
public class CalculationResult implements Serializable{
    private final CalculationRequest calculationRequest;
    private final Number result;

    public CalculationResult(CalculationRequest calculationRequest, Number result) {
        this.calculationRequest = calculationRequest;
        this.result = result;
    }

    @Override
    public String toString() {
        return "CalculationResult{" +
                "calculationRequest=" + calculationRequest +
                ", result='" + result + '\'' +
                "} ";
    }
}
