# Weather Application

## What is does the app do?
The application is a simple weather application that shows the historical temperature data hosted by [National Centers for Environmental Information](https://www.ncdc.noaa.gov).
![weather app](images/weather-app.png)

This is an application that showcases the following
- Setting up of a spring boot application backed by a postgres database
- [Dockerized postgres](https://github.com/dilipkrish/weather-app/tree/master/postgres) database with the following:
     - Comes with pre-configured schema and index to load weather data
     - Enables postgis 
- React based front end
- Batch application that can be configured to pull in yearly(defaulted to 2017) weather data dump.
- Dockerized spring applications
    - [Weather Data Loader](https://github.com/dilipkrish/weather-app/tree/master/weather-data-loader)
    - [Weather API](https://github.com/dilipkrish/weather-app/tree/master/weather-api)
- Docker composed database/boot application

## Getting Started
Lets build the images first.

NOTE: This build infrastructure depends on docker being installed.
```bash
docker-compose build
```                
This will build the `weather-app` and the `weather-data-loader-docker` boot applications. Once it has been built, 
we can now run it using the following command.

```bash
docker-compose up
```
As the data loader is loading the data, you can now navigate to `http://localhost:8080` to watch play with the app.

#### TODO
- [ ] Add some test. This mostly uses frameworks and libraries with very little custom code. Add some integration/e2e test
- [ ] Currently it naively supports all of the data given weather station. 
Its only been tested with a years worth of data (365 days). Given that the current transport is json
, adding more years will increase importance of caching/intelligent data fetching (i.e. tsv,csv formats).
- [ ] Currently only supports temperatures. There is a whole host of data for e.g. snowfall, precipitation etc.
- [ ] Use of [Spring Cloud Data Flow](https://spring.io/projects/spring-cloud-dataflow) to manage data pipelines
- [ ] Separate the API and the UI layer. Currently the react based front end is bundled with the [API app](https://github.com/dilipkrish/weather-app/tree/master/weather-api)
- [ ] Optimize the docker build for speed/caching of layers
- [ ] Versioning of artifacts
- [ ] Service discovery/secrets/kubernetes
  