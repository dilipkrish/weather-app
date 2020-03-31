package com.dilipkrish.weather.dataloader.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

@Slf4j
public class GZipFileTasklet implements Tasklet {
    private final String gzipSourceFilePath;
    private final String uncompressedFilePath;

    public GZipFileTasklet(
            String gzipSourceFilePath,
            String uncompressedFilePath) {
        this.gzipSourceFilePath = gzipSourceFilePath;
        this.uncompressedFilePath = uncompressedFilePath;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        byte[] buffer = new byte[1024];

        try {

            log.info("Uncompressing file: {} to {}...", gzipSourceFilePath, uncompressedFilePath);
            GZIPInputStream gzis =
                    new GZIPInputStream(new FileInputStream(gzipSourceFilePath));

            FileOutputStream out =
                    new FileOutputStream(uncompressedFilePath);

            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            gzis.close();
            out.close();
        } catch (IOException ex) {
            log.error("Error unzipping file", ex);
        }
        return RepeatStatus.FINISHED;
    }
}