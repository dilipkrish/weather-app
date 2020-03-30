create table weather_station
(
	station_id char(11),
	latitude numeric(8,4),
	longitude numeric(9,4),
	elavation numeric(5,1),
	state char(2),
	name char(30),
	gsn_flag char(3),
	hcn_crn_flag char(3),
	wmo_id char(5)
);
-- alter table weather_station drop constraint weather_recording_pk;
-- drop index weather_recording_pk;
--
-- create unique index weather_recording_pk
-- 	on weather_recording (id);

alter table weather_recording drop constraint weather_recording_pk;

alter table weather_recording drop column id;

alter table weather_recording
	add constraint measurement_pk
		 UNIQUE (station_id, element, measurement_date, measurement_time);


create  index weather_station_ix
	on weather_station (latitude, longitude);

create  index weather_station_state_ix
	on weather_station (state);

CREATE INDEX weather_station_location_ix ON weather_station (ST_Distance_Sphere(ST_SetSRID( ST_Point(longitude, latitude), 4326), ST_MakePoint(-97.0970, 33.0146)));

create table weather_recording
(
	id uuid
		constraint weather_recording_pk
			primary key,
	station_id char(11)
        constraint weather_recording_weather_station_station_id_fk
			references weather_station,
	measurement_date date,
	element char(4),
	measured_value numeric(5),
	measurement_flag char,
	quality_flag char,
	source_flag char,
	measurement_time time
);

comment on table weather_recording is 'Table that captures the daily measurements by weather stations';

create unique index weather_recording_measurement_ix
	on weather_recording (station_id, element, measurement_date, measurement_time);

-- CREATE EXTENSION postgis;
-- CREATE EXTENSION postgis_topology;

SELECT * FROM weather_recording;


-- SELECT *
-- FROM weather_recording
-- WHERE ST_Distance_Sphere(the_geom, ST_MakePoint(your_lon,your_lat)) <= radius_mi * 1609.34
INSERT INTO public.weather_station (station_id, latitude, longitude, elevation, state, name, gsn_flag, hcn_crn_flag, wmo_id)VALUES ('US1TXDN0008', 33.0562, -97.0703, null, 'TX', null, null, null, null);
INSERT INTO public.weather_station (station_id, latitude, longitude, elevation, state, name, gsn_flag, hcn_crn_flag, wmo_id)VALUES ('ACW00011604', 17.1167, -61.7833, null, null, null, null, null, null);
INSERT INTO public.weather_station (station_id, latitude, longitude, elevation, state, name, gsn_flag, hcn_crn_flag, wmo_id)VALUES ('ACW00011647', 17.1333, -61.7833, null, null, null, null, null, null);

INSERT INTO public.weather_recording (station_id, measurement_date, element, measured_value, measurement_flag, quality_flag, source_flag, measurement_time) VALUES ('ACW00011604', '2017-01-01', 'TMIN', -56.000, null, null, '7', '07:00:00');

SELECT *
FROM public.weather_station ws
WHERE
      ST_Distance_Sphere(ST_SetSRID( ST_Point(ws.longitude, ws.latitude), 4326),    ST_MakePoint(-97.0970, 33.0146)) <= 25 * 1609.34
AND ws.state = 'TX';

-- 33.0146° N, 97.0970q
SELECT count(1) FROM weather_station ;
SELECT count(1) FROM weather_recording WHERE station_id = 'USC00415192';
SELECT * FROM weather_recording;
SELECT * FROM weather_station WHERE state = 'TX';
SELECT max(measurement_date) FROM weather_recording;
-- DELETE  FROM weather_station;
-- TRUNCATE weather_recording;

SELECT DISTINCT element
FROM weather_recording r  JOIN weather_station s
    ON r.station_id = s.station_id
WHERE s.state = 'TX';

SELECT * FROM weather_recording WHERE station_id = 'USR0000TCLD' AND element = 'TMAX';

;

UPDATE weather_recording r
SET measurement_time = '00:00:00'
WHERE measurement_time IS NULL;

UPDATE weather_recording r
SET measurement_time = '12:01:00'
FROM weather_station s
WHERE s.station_id = r.station_id
AND s.state = 'TX';

SELECT * FROM


SELECT * FROM weather_recording WHERE station_id = 'US1TXTG0038' AND element = 'PRCP' AND measurement_date = '2017-01-04'
;




