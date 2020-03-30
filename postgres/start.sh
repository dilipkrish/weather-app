docker run --rm   --name pg-docker -e POSTGRES_PASSWORD=docker -d -p 5432:5432 -v ~/Dev/github/dilipkrish/weather-data/postgres/data:/var/lib/postgresql/data  mdillon/postgis
