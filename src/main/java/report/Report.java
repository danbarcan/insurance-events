package report;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Contract;
import entities.Event;
import entities.EventType;
import entities.MonthReport;
import lombok.extern.java.Log;
import utils.DateUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
public class Report {

    public List<MonthReport> createAnnuallyReportForEvents(String eventsFileName) {
        log.info(String.format("Start - create annually report based on events found in file %s", eventsFileName));
        List<Contract> contracts = readFileToListOfContracts(eventsFileName);
        List<MonthReport> reports = initializeMonthReportsForYear();

        log.info("Start - compute the report values for each month.");
        AtomicReference<BigDecimal> lastAgwp = new AtomicReference<>(BigDecimal.ZERO);
        reports.forEach(report -> {
            contracts.stream().forEach(contract -> {
                if (contract.wasActiveMonth(report.getMonth(), DateUtils.YEAR_2020)) {
                    report.addContract(contract);
                }
            });
            report.setAgwp(lastAgwp.get().add(report.getAgwp()));
            report.setEgwp(lastAgwp.get().add(report.getEgwp()));

            lastAgwp.set(report.getAgwp());
        });

        log.info(String.format("End - create annually report based on events found in file %s", eventsFileName));
        return reports;
    }

    private List<MonthReport> initializeMonthReportsForYear() {
        return Arrays.stream(Month.values()).map(month ->
            MonthReport.builder()
                .month(month)
                .agwp(BigDecimal.ZERO)
                .egwp(BigDecimal.ZERO)
                .noOfContracts(0l)
                .build())
            .collect(Collectors.toList());
    }

    private List<Contract> readFileToListOfContracts(String eventsFileName) {
        log.info(String.format("Start - read file %s", eventsFileName));
        URL url = getClass().getClassLoader().getResource(eventsFileName);
        ObjectMapper mapper = new ObjectMapper();
        List<Contract> contracts = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(url.toURI()))) {
            stream.forEach(line -> {
                Event event = getEventFromString(mapper, line);
                if (event != null && event.isValid()) {
                    Event finalEvent = event;
                    Optional<Contract> optionalContract = contracts.stream()
                        .filter(contract -> contract.getContractId().equals(finalEvent.getContractId()))
                        .findAny();

                    if (event.getName().equals(EventType.ContractCreatedEvent)) {
                        if (optionalContract.isPresent()) {
                            log.warning(String.format(
                                "Trying to create an already existing contract with event: %s",
                                event
                            ));
                        }
                        contracts.add(Contract.builder()
                            .contractId(event.getContractId())
                            .premium(event.getPremium())
                            .startDate(event.getStartDate())
                            .events(new ArrayList<>())
                            .build());
                    } else if (event.getName().equals(EventType.ContractTerminatedEvent)) {
                        if (optionalContract.isEmpty()) {
                            log.warning(String.format(
                                "Trying to terminate a nonexistent contract with event: %s",
                                event
                            ));
                        } else {
                            Contract contract = optionalContract.get();
                            if (contract.getTerminationDate() != null) {
                                log.warning(String.format(
                                    "Trying to terminate a terminated contract with event: %s",
                                    event
                                ));
                            }
                            contract.setTerminationDate(event.getTerminationDate());
                        }
                    } else {
                        if (optionalContract.isEmpty()) {
                            log.warning(String.format(
                                "Trying to modify a nonexistent contract with event: %s",
                                event
                            ));
                        } else {
                            Contract contract = optionalContract.get();
                            if (contract.getTerminationDate() != null) {
                                log.warning(String.format(
                                    "Trying to modify a terminated contract with event: %s",
                                    event
                                ));
                            }
                            contract.addEvent(event);
                        }
                    }
                } else {
                    log.warning(String.format("INVALID EVENT: ", event));
                }
            });

        } catch (IOException | URISyntaxException e) {
            log.severe(String.format("Error while parsing the file. %s", e.getMessage()));
        }

        log.info("End - read file");
        return contracts;
    }

    private Event getEventFromString(ObjectMapper mapper, String line) {
        Event event = null;
        try {
            event = mapper.readValue(line, Event.class);
        } catch (IOException e) {
            log.severe(String.format(
                "Error while transforming line from file: %s to Event object. %s",
                line,
                e.getMessage()
            ));
        }
        return event;
    }
}
