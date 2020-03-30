create table weather_station
(
	station_id char(11)
         constraint weather_station_pk
			primary key,
	latitude numeric(8,4),
	longitude numeric(9,4),
	elevation numeric(5,1),
	state char(2),
	name char(30),
	gsn_flag char(3),
	hcn_crn_flag char(3),
	wmo_id char(5)
);

create  index weather_station_lat_long_ix
	on weather_station (latitude, longitude);

create  index weather_station_state_ix
	on weather_station (state);

CREATE INDEX weather_station_location_ix ON weather_station (ST_Distance_Sphere(ST_SetSRID( ST_Point(longitude, latitude), 4326), ST_MakePoint(-97.0970, 33.0146)));

create table weather_recording
(
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

create unique index weather_recording__ix
	on weather_recording (station_id, element, measurement_date, measurement_time);


