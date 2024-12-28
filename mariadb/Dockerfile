FROM mariadb:latest

# Set the timezone
RUN echo "USE mysql;" > /docker-entrypoint-initdb.d/timezones.sql &&  mariadb-tzinfo-to-sql /usr/share/zoneinfo >> /docker-entrypoint-initdb.d/timezones.sql