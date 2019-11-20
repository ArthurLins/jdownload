package com.jdownload.pool;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.function.Consumer;

public class DownloadRequestBuilder {

    private Path path;
    private URI uri;
    private Consumer<File> fileConsumer;

    public DownloadRequestBuilder url(URI uri) {
        this.uri = uri;
        return this;
    }

    public DownloadRequestBuilder url(String uri) {
        try {
            this.uri = new URI(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return this;
    }

    public DownloadRequestBuilder path(String path) {
        this.path = Path.of(path);
        return this;

    }

    public DownloadRequestBuilder path(Path path) {
        this.path = path;
        return this;

    }

    public DownloadRequestBuilder onFinished(Consumer<File> resultFileConsumer) {
        this.fileConsumer = resultFileConsumer;
        return this;
    }

    public DownloadRequest build() {
        DownloadRequest request = new DownloadRequest();
        request.setUri(this.uri);
        request.setFileConsumer(this.fileConsumer);
        request.setPath(this.path);
        return request;

    }


}
