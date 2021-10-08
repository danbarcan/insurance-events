package entities;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Event {
    private EventType name;
    private Long contractId;
    private BigDecimal premium;
    private BigDecimal premiumIncrease;
    private BigDecimal premiumReduction;
    private Date startDate;
    private Date atDate;
    private Date terminationDate;

    private boolean applied = false;

    public boolean validateEvent() {
        switch (name) {
            case ContractCreatedEvent:
                return contractId != null && contractId > 0 && premium != null && premium.compareTo(BigDecimal.ZERO) > 0 && startDate != null;
            case PriceIncreasedEvent:
                return contractId != null && contractId > 0 && premiumIncrease != null && premiumIncrease.compareTo(BigDecimal.ZERO) > 0 && atDate != null;
            case PriceDecreasedEvent:
                return contractId != null && contractId > 0 && premiumReduction != null && premiumReduction.compareTo(BigDecimal.ZERO) > 0 && atDate != null;
            case ContractTerminatedEvent:
                return contractId != null && contractId > 0 && terminationDate != null;
            default:
                return false;
        }
    }
}
