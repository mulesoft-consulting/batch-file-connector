package org.mule.modules.batchfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Source;
import org.mule.api.callback.SourceCallback;
import org.mule.modules.batchfile.config.ConnectorConfig;
import org.mule.util.FileUtils;

@Connector(name="batch-file", friendlyName="BatchFile")
public class BatchFileConnector {
	
    protected transient Log logger = LogFactory.getLog(getClass());

	ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    @Config
    ConnectorConfig config;

 
    @Source
    public void poll(final String directory, int interval, TimeUnit timeunit, final boolean deleteFile,
    		final SourceCallback callback) {
    	
    	scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				logger.debug("Polling: " + directory);
				List<DeletingFileInputStream> files = new ArrayList<>(); 
				for (File file : FileUtils.listFiles(new File(directory), null, false)) {
					logger.debug("Processing file: " + file.getAbsolutePath()) ;
					try {
						files.add(new DeletingFileInputStream(file, deleteFile));
					} catch (FileNotFoundException e) {
						throw new RuntimeException(e);
					}
				}
				try {
					if (files.size() > 0) {
						callback.process(files);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
    		
    	}, 0, interval, timeunit);
    	
    }

    public ConnectorConfig getConfig() {
        return config;
    }

    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }

}