package com.jdownload;


import com.jdownload.pool.DownloadThreadPool;
import com.jdownload.pool.type.DownloadRequest;
import com.jdownload.pool.type.builder.DownloadRequestBuilder;

public class JDownload {

    private DownloadThreadPool pool;

    public JDownload(int simultaneousDownloads) {
        this.pool = new DownloadThreadPool(simultaneousDownloads);
    }

    public JDownload() {
        this(10);
    }

    public void download(DownloadRequestBuilder builder) {
        pool.submitDownload(builder.build());
    }

    public void download(DownloadRequest request) {
        pool.submitDownload(request);
    }

    public void dispose() {
        pool.dispose();
        pool = null;
    }

}
