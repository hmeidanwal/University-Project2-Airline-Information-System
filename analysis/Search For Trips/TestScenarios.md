# TEST SCENARIOS REGARDING: SEARCH FOR TRIPS

| **Name**      | Search for trips with valid search input |
|:--------------|:--------------------------------------|
| **Scenario** | 1. The actor enters “Amsterdam” as the departure, “London” as the destination, 01/07/2025 as the date and 08:00 as time <br>  |
|              | 2. The system displays the search results that match the criteria <br>  |
| **Result**   | The search is completed with the correct results: one or more trips are displayed with potentionally multiple flights. |

<br><br>

| **Name**      | Searching for trips with no results |
|:--------------|:--------------------------------------|
| **Scenario** | 1. The actor enters “Miami” as the departure, “Berlin” as the destination, 01/07/2025 as the date and 08:00 as time <br> |
|              | 2. The system displays a message saying “No trips found for the selected route, please try again!” |
| **Result**   | The flight search is completed with the correct results: No trips available for the selected criteria. |

<br><br>

| **Name**      | Searching for trips with same departure and arrival |
|:--------------|:--------------------------------------|
| **Scenario** | 1. The actor enters “Berlin” as the departure, “Berlin” as the destination, 01/07/2025 as the date and 08:00 as time <br> |
|              | 2. The system displays a message saying “Departure and arrival cannot be the same.” |
| **Result**   | The flight search is completed with the correct results: Error message was shown |


<br><br>

| **Name**      | Searching for trips with empty departure |
|:--------------|:--------------------------------------|
| **Scenario** | 1. The actor does not enters the departure, but enters “Berlin” as the destination, 01/07/2025 as the date and 08:00 as time <br> |
|              | 2. The system displays a message saying “Missing required parameters” |
| **Result**   | The flight search is completed with the correct results: Error message was shown |
