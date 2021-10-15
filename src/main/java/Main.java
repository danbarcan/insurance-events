import report.Report;

public class Main {
    public static void main(String[] args) {
        new Report().createAnnuallyReportForEvents("test-data.txt").forEach(System.out::println);
    }
}
