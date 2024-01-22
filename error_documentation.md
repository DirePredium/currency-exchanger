## Error Documentation
 
This file contains documentation for common errors in the application and their resolutions.
 
### Error 1001: Cannot complete request
- **Description:** Unable to establish a network connection.
- **Resolution:** Check the device's internet connection, Wi-Fi, or mobile data settings.

### Error 1002: Cannot parse into object *object*
- **Description:** Error parsing data received from the server.
- **Resolution:** Ensure the data from the server is in the expected *object* format. Check for server-side issues.

### Error 1003: Unable to connect to API
- **Description:** Error parsing API URI in config file.
- **Resolution:** Make sure that the path to your API is correct. This application requires the path to the "currency-exchange-rates" repository, but it is not specified in the path. For example, the path to your API: "https://my.server.com/tasks/api/currency-exchange-rates", then you should specify "https://my.server.com/tasks/api/" in the field BASE_URL of the config app/src/main/res/raw/api_config.properties
