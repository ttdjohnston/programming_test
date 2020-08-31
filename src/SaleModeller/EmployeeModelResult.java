package SaleModeller;

import java.math.BigDecimal;

public class EmployeeModelResult {
    private String employeeNum;
    private BigDecimal totalGainAvailable;
    private BigDecimal totalGainFromSale;

    private EmployeeModelResult() {}

    public EmployeeModelResult(String employeeNumber, BigDecimal gainAvailable, BigDecimal gainFromSale) {
        employeeNum = employeeNumber;
        totalGainAvailable = gainAvailable;
        totalGainFromSale = gainFromSale;//.setScale(2)
    }

    public BigDecimal getTotalGainAvailable() {
        return totalGainAvailable;
    }

    public String getEmployeeNumber() {
        return employeeNum;
    }

    public BigDecimal getTotalGainFromSale() {
        return totalGainFromSale;
    }



}
