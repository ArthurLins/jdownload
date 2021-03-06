package com.jdownload.pool;

import com.jdonwload.file.FileFacade;
import com.jdownload.pool.tasks.DownloadTask;
import com.jdownload.pool.type.DownloadRequest;
import com.jdownload.pool.type.DownloadedFile;
import com.jdownload.pool.util.QueueWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadThreadPool {

    private static final Logger logger = LogManager.getLogger(DownloadThreadPool.class.getCanonicalName());


    private ThreadPoolExecutor executor;
    private FileFacade fileFacade;
    private ConcurrentHashMap<String, List<DownloadRequest>> observers;

    public DownloadThreadPool(int simultaneous_downloads) {

        this.observers = new ConcurrentHashMap<>();
        this.executor = new ThreadPoolExecutor(1, simultaneous_downloads, Long.MAX_VALUE, TimeUnit.DAYS, new QueueWrapper());
        this.executor.setRejectedExecutionHandler((runnable, poolExecutor) -> {
            try {
                logger.debug("No threads available, queuing...");
                poolExecutor.getQueue().put(runnable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        this.fileFacade = new FileFacade();
    }

    public void submitDownload(DownloadRequest request) {
        if (observers.containsKey(request.id())) {
            logger.debug("[ID:" + request.id() + "] Download already submitted. Added on waiting list.");
            observers.get(request.id()).add(request);
            return;
        }
        List<DownloadRequest> newList = new ArrayList<>();
        newList.add(request);
        observers.put(request.id(), newList);
        executor.submit(new DownloadTask(this, request));
        logger.debug("[ID:" + request.id() + "] Download submitted.");
    }

    public void notifyAll(DownloadRequest request, DownloadedFile resultFile) {
        if (observers.containsKey(request.id())) {
            if (observers.get(request.id()).size() > 1) {
                resultFile.setFromMultiRequests(true);
            }
            observers.get(request.id()).forEach((req) -> req.getFileConsumer().accept(resultFile));
            observers.remove(request.id());
        }
    }

    public FileFacade getFileFacade() {
        return fileFacade;
    }

    public void dispose() {
        this.executor.shutdown();
        this.fileFacade = null;
        this.observers.clear();
        this.observers = null;
        logger.debug("Poll disposed.");
    }


}
