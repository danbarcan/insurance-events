package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Contract;
import entities.Event;
import entities.EventType;
import entities.MonthReport;
import utils.DateUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting");

        readFile();
    }

    private static void readFile() {
        URL url = Main.class.getClassLoader().getResource("small-test-data.txt");
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        List<Contract> contracts = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(url.toURI()))) {

            stream.forEach(line -> {
                Event event = null;
                try {
                    event = mapper.readValue(line, Event.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (event.validateEvent()) {
                    Event finalEvent = event;
                    Optional<Contract> optionalContract = contracts.stream().filter(contract -> contract.getContractId().equals(finalEvent.getContractId())).findAny();

                    if (event.getName().equals(EventType.ContractCreatedEvent)) {
                        if (optionalContract.isPresent()) {
                            System.out.println(event);//TODO: throw error trying to create already existing contract
                        }
                        contracts.add(Contract.builder().contractId(event.getContractId()).premium(event.getPremium()).startDate(event.getStartDate()).events(new ArrayList<>()).build());
                    } else if (event.getName().equals(EventType.ContractTerminatedEvent)) {
                        if (optionalContract.isEmpty()) {
                            System.out.println("Err"); //TODO: throw error trying to terminate a contract that do not exist
                        }

                        Contract contract = optionalContract.get();
                        if (contract.getTerminationDate() != null) {
                            System.out.println("Err"); //TODO: throw error trying to terminate a terminated contract
                        }

                        contract.setTerminationDate(event.getTerminationDate());
                    } else {
                        if (optionalContract.isEmpty()) {
                            System.out.println("Err"); //TODO: throw error trying to modify a contract that do not exist
                        }

                        Contract contract = optionalContract.get();
                        if (contract.getTerminationDate() != null) {
                            System.out.println("Err"); //TODO: throw error trying to modify a terminated contract
                        }

                        contract.addEvent(event);
                    }
                } else {
                    System.out.println("INVALID EVENT" + event); //TODO: throw invalid event error
                }
            });

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        List<MonthReport> reports = Arrays.stream(Month.values()).map(month -> MonthReport.builder().month(month).agwp(BigDecimal.ZERO).egwp(BigDecimal.ZERO).noOfContracts(0l).build()).collect(Collectors.toList());

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
            System.out.println(report);
        });
    }
}
