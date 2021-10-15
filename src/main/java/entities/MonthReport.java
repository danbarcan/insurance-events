package entities;

import lombok.Builder;
import lombok.Data;
import utils.DateUtils;

import java.math.BigDecimal;
import java.time.Month;

@Data
@Builder
public class MonthReport {
    private Month month;
    private Long noOfContracts;
    private BigDecimal agwp;
    private BigDecimal egwp;

    public void addContract(Contract contract) {
        noOfContracts++;

        if (!contract.getEvents().isEmpty()) {
            contract.getEvents().forEach(event -> {
                if (!event.isApplied() && DateUtils.endOfMonth(month, DateUtils.YEAR_2020).compareTo(event.getAtDate()) >= 0) {
                    if (event.getName().equals(EventType.PriceIncreasedEvent)) {
                        BigDecimal premiumModification = computePremiumModificationProportionalWithDayOfMonth(event, event.getPremiumIncrease());
                        contract.setPremium(contract.getPremium().add(premiumModification));
                    } else if (event.getName().equals(EventType.PriceDecreasedEvent)) {
                        BigDecimal premiumModification = computePremiumModificationProportionalWithDayOfMonth(event, event.getPremiumReduction());
                        contract.setPremium(contract.getPremium().subtract(premiumModification));
                    }
                    event.setApplied(true);
                }
            });
        }

        this.agwp = this.agwp.add(contract.getPremium());
        if (!DateUtils.dateIsInMonth(contract.getTerminationDate(), month, DateUtils.YEAR_2020)) {
            this.egwp = this.egwp.add(contract.getPremium().multiply(BigDecimal.valueOf(13 - month.getValue())));
        } else {
            this.egwp = this.egwp.add(this.getAgwp());
        }
    }

    private BigDecimal computePremiumModificationProportionalWithDayOfMonth(Event event, BigDecimal premiumWholeMonth) {
        return premiumWholeMonth
            .multiply(BigDecimal.valueOf((DateUtils.YEAR_2020.isLeap() ? month.maxLength() : month.minLength()) + 1)
                .subtract(BigDecimal.valueOf(event.getAtDate().getDate()))
                .divide(BigDecimal.valueOf(DateUtils.YEAR_2020.isLeap() ? month.maxLength() : month.minLength())));
    }
}
