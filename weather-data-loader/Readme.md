# Weather Data Loader App

This application is a short lived batch application that does the following
1. Download the Weather Station info `ftp://ftp.ncdc.noaa.gov/pub/data/ghcn/daily/ghcnd-stations.txt`
2. Loads the weather stations into the postgres database
2. Download the Weather Data for 2017 by default `ftp://ftp.ncdc.noaa.gov/pub/data/ghcn/daily/by_year/2017.csv` 
3. Unzips the archive weather data archive
4. Loads weather recording 

## To get started

Lets build the docker image. 
```bash
docker build -t weather-data-loader .
```                          

This will produce an image with the tag weather-data-loader:latest

```bash
POSTGRES_PASSWORD=<your-password> ./docker-run.sh weather-data-loader:latest 
```
NOTE: This requires a pre-running postgres database to connect to @ localhost. To override the 
docker host set the `POSTGRES_HOST` environment variable to the overidden location. This is typically 
useful in the context of a docker compose or running in a container or non local development.

