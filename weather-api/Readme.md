# To get started

Lets build the docker image. 
```bash
docker build -t weather-app .
```                          

This will produce an image with the tag weather-app:latest


```bash
POSTGRES_PASSWORD=<your-password> $MAPQUEST_API_KEY=<your-mapquest-key> ./docker-run.sh weather-app:latest 
```
NOTE: This requires a pre-running postgres database to connect to @ localhost. To override the 
docker host set the `POSTGRES_HOST` environment variable to the overiden location. This is typically 
useful in the context of a docker compose
