package com.dilipkrish.weather.dataloader;

import com.dilipkrish.weather.dataloader.ghcn.Measurement;
import com.dilipkrish.weather.dataloader.ghcn.WeatherRecording;
import com.dilipkrish.weather.dataloader.ghcn.WeatherStation;
import com.dilipkrish.weather.dataloader.services.FtpGetRemoteFilesTasklet;
import com.dilipkrish.weather.dataloader.services.GZipFileTasklet;
import com.dilipkrish.weather.dataloader.services.WeatherRecordingRepository;
import com.dilipkrish.weather.dataloader.services.WeatherStationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableTask
@EnableBatchProcessing
@EnableTransactionManagement
@Slf4j
public class BatchConfiguration extends DefaultBatchConfigurer {
    public static final String GHCN_FTP_ROOT = "ftp://ftp.ncdc.noaa.gov/pub/data/ghcn/daily";
    public static final String DOWNLOAD_FOLDER = System.getProperty("user.dir") + "/";

    @Value("${stations.file.name:ghcnd-stations.txt}")
    private String stationsFile;

    @Value("${recordings.file.year:2017}")
    private String recordingsYear;

    public static final Set<String> STATIONS_TO_BRING_IN =
            new HashSet<>(Arrays.asList(
                    "US1TXCLL053"
                    , "US1TXCLL040"
                    , "US1TXDN0012"
                    , "US1TXDN0028"
                    , "US1TXDN0039"
                    , "US1TXDN0047"
                    , "US1TXDN0048"
                    , "USC00413370"
                    , "USC00415191"
                    , "US1TXDN0008"
                    , "US1TXDN0019"
                    , "US1TXDN0044"
                    , "US1TXDN0053"
                    , "US1TXDN0055"
                    , "USC00410415"
                    , "USC00413476"
                    , "USC00415192"
                    , "USR0000TCLD")); // <-- for tmax/tmin

    @Bean
    @Profile("!skip")
    public Job weatherStationInitJob(
            StepBuilderFactory stepBuilderFactory,
            JobBuilderFactory jobBuilderFactory,
            WeatherStationRepository stationRepository,
            WeatherRecordingRepository recordingRepository,
            DataSource dataSource,
            EntityManagerFactory entityManagerFactory,
            ItemProcessor<WeatherRecording, WeatherRecording> maybeSkip) {

        TaskletStep ftpStationFile = stepBuilderFactory.get("getStationFile")
                .tasklet(new FtpGetRemoteFilesTasklet(
                        GHCN_FTP_ROOT + "/" + stationsFile,
                        stationsFile))
                .build();

        TaskletStep ftpWeatherRecordingFile = stepBuilderFactory.get("Get Weather Recording File For " + recordingsYear)
                .tasklet(new FtpGetRemoteFilesTasklet(
                        GHCN_FTP_ROOT + "/by_year/" + recordingsYear + ".csv.gz",
                        recordingsYear + ".csv.gz"))
                .build();

        TaskletStep uncompressWeatherRecording = stepBuilderFactory.get("Uncompressing Weather Data")
                .tasklet(new GZipFileTasklet(
                        String.format("%s%s",
                                DOWNLOAD_FOLDER,
                                recordingsYear + ".csv.gz"),
                        String.format("%s%s",
                                DOWNLOAD_FOLDER,
                                recordingsYear + ".csv")))
                .build();

        Step stationStep = stepBuilderFactory.get("Weather Station Setup")
                .<WeatherStation, WeatherStation>chunk(1000)
                .reader(stationItemReader())
                .writer(repositoryStationWriter(stationRepository))
                .listener(chunkListener())
                .transactionManager(platformTransactionManager(dataSource, entityManagerFactory))
                .taskExecutor(taskExecutor())
                .throttleLimit(10)
                .build();

        Step recordingsStep = stepBuilderFactory.get("2017 Weather Recording Import")
                .<WeatherRecording, WeatherRecording>chunk(1000)
                .reader(recordingItemReader())
                .processor(maybeSkip)
                .writer(repositoryRecordingWriter(recordingRepository))
                .transactionManager(platformTransactionManager(dataSource, entityManagerFactory))
                .listener(chunkListener())
                .taskExecutor(taskExecutor())
                .throttleLimit(10)
                .build();

        return jobBuilderFactory.get("Weather Data Setup")
                .incrementer(new RunIdIncrementer())
                .start(ftpStationFile)
                .next(stationStep)
                .next(ftpWeatherRecordingFile)
                .next(uncompressWeatherRecording)
                .next(recordingsStep)
                .build();
    }

    @Bean
    @Profile("production")
    @StepScope
    ItemProcessor<WeatherRecording, WeatherRecording> passThroughProcessor() {
        return item -> item;
    }

    @Bean
    @Profile("!production")
    @StepScope
    ItemProcessor<WeatherRecording, WeatherRecording> skipProcessor() {
        return item -> {
            if (STATIONS_TO_BRING_IN.contains(item.getId().getStationId())) {
                return item;
            }
            return null;
        };
    }

    private ChunkListener chunkListener() {
        return new ChunkListener() {
            @Override
            public void beforeChunk(ChunkContext context) {
                log.debug("Starting {} - {}, chunk {}",
                        context.getStepContext().getJobName(),
                        context.getStepContext().getStepName(),
                        context.getStepContext().getStepExecution().getReadCount());
            }

            @Override
            public void afterChunk(ChunkContext context) {
                log.info("Finished {} - {}, chunk {}",
                        context.getStepContext().getJobName(),
                        context.getStepContext().getStepName(),
                        context.getStepContext().getStepExecution().getReadCount());
            }

            @Override
            public void afterChunkError(ChunkContext context) {
                log.error("Error {} - {}, chunk {}\n{}",
                        context.getStepContext().getJobName(),
                        context.getStepContext().getStepName(),
                        context.getStepContext().getStepExecution().getReadCount(),
                        context.getStepContext().getStepExecution().getFailureExceptions());
            }
        };
    }

    @Bean
    @StepScope
    FlatFileItemReader<WeatherStation> stationItemReader() {
        // * ID            1-11   Character
        // * LATITUDE     13-20   Real
        // * LONGITUDE    22-30   Real
        // * ELEVATION    32-37   Real
        // * STATE        39-40   Character
        // * NAME         42-71   Character
        // * GSN FLAG     73-75   Character
        // * HCN/CRN FLAG 77-79   Character
        // * WMO ID       81-85   Character
        return new FlatFileItemReaderBuilder<WeatherStation>()
                .encoding(StandardCharsets.ISO_8859_1.displayName())
                .resource(new FileSystemResource(
                        DOWNLOAD_FOLDER + File.separator + stationsFile))
                .name("WeatherStationFileReader")
                .fixedLength()
                .columns(new Range[]{
                        new Range(1, 11),
                        new Range(13, 20),
                        new Range(22, 30),
                        new Range(32, 37),
                        new Range(39, 40),
                        new Range(42, 72),
                        new Range(73, 76),
                        new Range(77, 79),
                        new Range(81, 85)
                })
                .names(new String[]{
                        "stationId",
                        "latitude",
                        "longitude",
                        "elevation",
                        "state",
                        "name",
                        "gsnFlag",
                        "hcnCrnFlag",
                        "wmoId"})
                .targetType(WeatherStation.class)
                .build();
    }

    @Bean
    @StepScope
    RepositoryItemWriter<WeatherStation> repositoryStationWriter(
            WeatherStationRepository repository) {
        return new RepositoryItemWriterBuilder<WeatherStation>()
                .repository(repository)
                .methodName("save")
                .build();
    }

    @Bean
    @StepScope
    FlatFileItemReader<WeatherRecording> recordingItemReader() {
//        ID = 11 character station identification code
//        YEAR/MONTH/DAY = 8 character date in YYYYMMDD format (e.g. 19860529 = May 29, 1986)
//        ELEMENT = 4 character indicator of element type
//        DATA VALUE = 5 character data value for ELEMENT
//        M-FLAG = 1 character Measurement Flag
//        Q-FLAG = 1 character Quality Flag
//        S-FLAG = 1 character Source Flag
//        OBS-TIME = 4-character time of observation in hour-minute format (i.e. 0700 =7:00 am)
        return new FlatFileItemReaderBuilder<WeatherRecording>()
                .encoding(StandardCharsets.ISO_8859_1.displayName())
                .resource(new FileSystemResource(DOWNLOAD_FOLDER + recordingsYear + ".csv"))
                .name("WeatherRecording")
                .delimited()
                .delimiter(",")
                .names(new String[]{
                        "stationId",
                        "measurementDate",
                        "element",
                        "measuredValue",
                        "measurementFLag",
                        "qualityFlag",
                        "sourceFlag",
                        "measurementTime"})
                .fieldSetMapper(recordingFieldSetMapper())
                .build();
    }

    private FieldSetMapper<WeatherRecording> recordingFieldSetMapper() {
        return fieldSet -> {
            WeatherRecording recording = new WeatherRecording();
            Measurement measurement = new Measurement();
            measurement.setStationId(fieldSet.readString("stationId"));
            measurement.setMeasurementDate(fieldSet.readDate("measurementDate", "yyyyMMdd")
                    .toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDate());
            measurement.setElement(fieldSet.readString("element"));
            recording.setMeasuredValue(fieldSet.readBigDecimal("measuredValue"));
            recording.setMeasurementFLag(fieldSet.readString("measurementFLag"));
            recording.setQualityFlag(fieldSet.readString("qualityFlag"));
            recording.setSourceFlag(fieldSet.readString("sourceFlag"));

            String timeString = fieldSet.readString("measurementTime");
            if (!Strings.isEmpty(timeString)) {
                if (!"2400".equals(timeString)) {
                    measurement.setMeasurementTime(fieldSet.readDate("measurementTime", "HHmm")
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalTime());
                } else {
                    measurement.setMeasurementTime(LocalTime.MIDNIGHT);
                }
            } else {
                measurement.setMeasurementTime(LocalTime.NOON);
            }
            recording.setId(measurement);
            return recording;
        };
    }

    @Bean
    @StepScope
    RepositoryItemWriter<WeatherRecording> repositoryRecordingWriter(
            WeatherRecordingRepository repository) {
        return new RepositoryItemWriterBuilder<WeatherRecording>()
                .repository(repository)
                .methodName("save")
                .build();
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        //We dont care about saving batch jobs for now
    }

    PlatformTransactionManager platformTransactionManager(
            DataSource targetDataSource,
            EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(targetDataSource);
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    @StepScope
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("weather-data-import");
    }
}