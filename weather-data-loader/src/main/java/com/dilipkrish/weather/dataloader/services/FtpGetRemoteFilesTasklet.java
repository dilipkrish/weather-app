package com.dilipkrish.weather.dataloader.services;

import com.dilipkrish.weather.dataloader.BatchConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

@Slf4j
public class FtpGetRemoteFilesTasklet implements Tasklet {
    private final String ftpUrl;
    private final String localFileName;

    public FtpGetRemoteFilesTasklet(String ftpUrl, String localFileName) {
        this.ftpUrl = ftpUrl;
        this.localFileName = localFileName;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        File localFile = new File(BatchConfiguration.SYSTEM_TEMPORARY_FOLDER + localFileName);
        if (!localFile.exists()) {
            File tempFolder = new File(BatchConfiguration.SYSTEM_TEMPORARY_FOLDER);
            if (!tempFolder.canWrite()) {
                URLConnection urlConnection = new URL(ftpUrl).openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Files.copy(inputStream, localFile.toPath());
                inputStream.close();
            } else {
                log.warn("No permissions to write to folder {}, skipping this step", tempFolder.toPath());
            }
        } else {
            log.warn("File {} already exists, skipping this step", localFile.toPath());
        }
        return RepeatStatus.FINISHED;
    }
}