
# Currency Exchanger

## Demo

## Before Running
Fill in the BASE_URL parameter in the API config of the application which is located in

```bash
  app/src/main/res/raw/api_config.properties
```
This application requires the path to the "currency-exchange-rates" repository, but it is not specifing in the path.

---
For example.
The path to your API: "https://my.server.com/tasks/api/currency-exchange-rates", then you should specify "https://my.server.com/tasks/api/" in the field BASE_URL of the config:

**api_config.properties**
```bash
BASE_URL=https://my.server.com/tasks/api/
TIMEOUT=5000
```
## Internal construction

```
|-- model
    |-- db
        |-- dao
        |-- entity
        | WalletRepository          //working with the balance database
        | TransactionRepository     //working with a transaction database
        ...
    |-- network
        |-- entity
        |-- exception
        |-- retrofit
        CurrencyModel               //handler of persistent requests to API
        ...
|-- presentor
    | CommissionStrategy            //strategy for calculating transaction fees
    | CurrencyExchangerPresenterImpl
|-- view
    | CurrencyExchangerView         //view interface
    | MainActivity                  //main view
    | WalletFragment
```
## Tech Stack

- Android.minSdk = 26
- Android.targetSdk = 34
---
- Architecture=MVP
---
- Room=2.6.1
- Retrofit2=2.9.0
- Material Design=1.11.0

## Useful

**Description of network exceptions**

```bash
error_documentation.md
```
In the main project directory