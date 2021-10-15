package integration;

import entities.MonthReport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import report.Report;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;

public class TasksTest {

    @Test
    public void testTask1() {
        List<MonthReport> expected = List.of(
            MonthReport.builder().month(Month.JANUARY).noOfContracts(1l).agwp(new BigDecimal(100)).egwp(new BigDecimal(1200)).build(),
            MonthReport.builder().month(Month.FEBRUARY).noOfContracts(2l).agwp(new BigDecimal(300)).egwp(new BigDecimal(2300)).build(),
            MonthReport.builder().month(Month.MARCH).noOfContracts(2l).agwp(new BigDecimal(500)).egwp(new BigDecimal(1400)).build(),
            MonthReport.builder().month(Month.APRIL).noOfContracts(1l).agwp(new BigDecimal(600)).egwp(new BigDecimal(600)).build(),
            MonthReport.builder().month(Month.MAY).noOfContracts(0l).agwp(new BigDecimal(600)).egwp(new BigDecimal(600)).build(),
            MonthReport.builder().month(Month.JUNE).noOfContracts(0l).agwp(new BigDecimal(600)).egwp(new BigDecimal(600)).build(),
            MonthReport.builder().month(Month.JULY).noOfContracts(0l).agwp(new BigDecimal(600)).egwp(new BigDecimal(600)).build(),
            MonthReport.builder().month(Month.AUGUST).noOfContracts(0l).agwp(new BigDecimal(600)).egwp(new BigDecimal(600)).build(),
            MonthReport.builder().month(Month.SEPTEMBER).noOfContracts(0l).agwp(new BigDecimal(600)).egwp(new BigDecimal(600)).build(),
            MonthReport.builder().month(Month.OCTOBER).noOfContracts(0l).agwp(new BigDecimal(600)).egwp(new BigDecimal(600)).build(),
            MonthReport.builder().month(Month.NOVEMBER).noOfContracts(0l).agwp(new BigDecimal(600)).egwp(new BigDecimal(600)).build(),
            MonthReport.builder().month(Month.DECEMBER).noOfContracts(0l).agwp(new BigDecimal(600)).egwp(new BigDecimal(600)).build()
        );

        List<MonthReport> actual = new Report().createAnnuallyReportForEvents("small-test-data-task1.txt");

        Assertions.assertArrayEquals(actual.toArray(), expected.toArray());
    }

    @Test
    public void testTask2() {
        List<MonthReport> expected = List.of(
            MonthReport.builder().month(Month.JANUARY).noOfContracts(1l).agwp(new BigDecimal(100)).egwp(new BigDecimal(1200)).build(),
            MonthReport.builder().month(Month.FEBRUARY).noOfContracts(1l).agwp(new BigDecimal(300)).egwp(new BigDecimal(2300)).build(),
            MonthReport.builder().month(Month.MARCH).noOfContracts(1l).agwp(new BigDecimal(400)).egwp(new BigDecimal(1300)).build(),
            MonthReport.builder().month(Month.APRIL).noOfContracts(1l).agwp(new BigDecimal(500)).egwp(new BigDecimal(500)).build(),
            MonthReport.builder().month(Month.MAY).noOfContracts(0l).agwp(new BigDecimal(500)).egwp(new BigDecimal(500)).build(),
            MonthReport.builder().month(Month.JUNE).noOfContracts(0l).agwp(new BigDecimal(500)).egwp(new BigDecimal(500)).build(),
            MonthReport.builder().month(Month.JULY).noOfContracts(0l).agwp(new BigDecimal(500)).egwp(new BigDecimal(500)).build(),
            MonthReport.builder().month(Month.AUGUST).noOfContracts(0l).agwp(new BigDecimal(500)).egwp(new BigDecimal(500)).build(),
            MonthReport.builder().month(Month.SEPTEMBER).noOfContracts(0l).agwp(new BigDecimal(500)).egwp(new BigDecimal(500)).build(),
            MonthReport.builder().month(Month.OCTOBER).noOfContracts(0l).agwp(new BigDecimal(500)).egwp(new BigDecimal(500)).build(),
            MonthReport.builder().month(Month.NOVEMBER).noOfContracts(0l).agwp(new BigDecimal(500)).egwp(new BigDecimal(500)).build(),
            MonthReport.builder().month(Month.DECEMBER).noOfContracts(0l).agwp(new BigDecimal(500)).egwp(new BigDecimal(500)).build()
        );
        List<MonthReport> actual = new Report().createAnnuallyReportForEvents("small-test-data-task2.txt");

        Assertions.assertArrayEquals(actual.toArray(), expected.toArray());
    }
}
