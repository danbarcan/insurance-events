# Insurance events

## The problem
For this test you will be given a number of events for a figurative insurance system. Your task is to go over all events and generate a report. The choice of programming language and the output format is up to you. During the interview, you will present your solution to us and we can hopefully have some good discussions.
The events can be used to create, terminate and change the premium of the insurance. The report should include the metrics defined below for every month ​between ​jan-dec 2020.
Metrics:
Number of contracts​: The number of contracts that started but not yet been terminated. Expected gross written premium (EGWP)​: The expected sum of all premiums for the year. Actual gross written premium (AGWP)​: The accumulated premium that should have been paid in every month.

Event structure:
ContractCreatedEvent
{
"contractId":  "contractId",
"premium": 100,
"startDate": "2020-02-17"
}
PriceIncreasedEvent
{
"contractId":  "contractId",
"premiumIncrease": 10,
"atDate": "2020-02-17"
}
PriceDecreasedEvent
{
"contractId":  "contractId",
"premiumReduction": 10,
"atDate": "2020-02-17"
}
ContractTerminatedEvent
{
"contractId":  "contractId",
"terminationDate": "2020-02-17"
}

## The solution

### Assumptions

First of all, I want to say that after reading the problem and the report for the two given tasks, I arrived to the following definitions:
AGWP is the sum of all premium paid until this month
EGWP is the sum of all premium paid until last month and the sum of all premiums that could be paid until the end of the year (for the contracts that are not terminated, with the updated premiums)

This solution is based on the assumption that the premium is paid in advance (for June is paid in the last day of May) and, although I write the function that calculates the premium if it is modified in the middle of the month, I assume that the modified premium is activated next month.

Also, based on my assumptions, I think the reports for both tasks are wrong:
- in task 1, in February we have EGWP 2300 (because the second contract was signed in february, so we cannot get the money for January), for March EGWP is 1400 (300 for first contract ended in March and 1100 for second one) 
- in task 2, in April we have EGWP 500 (contract is terminated and we have only the money paid until then = AGWP)

### Implementation details

This is kind of a raw implementation, I did not have the time I wanted to do this test (I am writing this while on holiday in Cairo) and you might find things that could have been done better (and I would be delighted to hear your improvement ides).

#### Entities
I used for this implementation three entities:
- Event - that represents a line from the file, with an event type and all the other fields that any event can have, as described above
- Contract - represents a contract, with start date, termination date, premium and a list of events that were applied to this contract that year
- MonthReport - this is the result of the computation with the month it represents and the three values computed by this app (number of contracts, agwp and egwp)

#### Flow
First app reads the file into a list of Contracts. While doing this, the following steps are done:
- a line from file is transformed with Jackson into an Event object
- the object is validated (checked that all fields are correctly completed)
- then if the event is ContractCreated a Contract object is created with start date and premium (if the contract does not already exist)
- else if the event is ContractTerminated, the contract in question is retrieved and the termination date is set (if the contract exist and is not terminated)
- else, the event is PriceIncreased or PriceDecreased and the event is added to the list of events of the contract
  
Then app initializes a list of MonthReports for all months of the year.
After this initialization we go through this list, and for each month we go through the list of contracts and if the contract is active in the month in question (created and not terminated) we add this contract to our report by doing the following steps:
- if the contract have PriceIncrease/Decrease events to be applied, we check if the event occured in this month and the change the contract premium according to this event
- then add the premium to contract agwp
- then for egwp we check that the termination date is not at the end of this month, and if it is not we add the potentially gains until the end of the year to egwp, if the termination date is this month we add only agwp to egwp

After iterating through the contracts list (we computed this month agwp and egwp) we add the last month agwp to both current agwp and current egwp and update the value of the last agwp.

### Tests
I added only two tests to check all the two given tasks and to be sure I don't do some silly mistakes when refactoring.

### Improvements to be done

- Change the way dates are handled, maybe go for Calendar or research for a better way. I had to do some tricks to make the app work with Date, but, at the moment, was the easiest/fasest way for me (not proud of this).
- Add more tests, way more

### Libraries used

- Jackson
- jUnit
- Lombok