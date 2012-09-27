package org.dandelion.radiot.podcasts;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import org.dandelion.radiot.R;
import org.dandelion.radiot.podcasts.core.*;
import org.dandelion.radiot.podcasts.download.*;
import org.dandelion.radiot.podcasts.download.DownloadManager;
import org.dandelion.radiot.podcasts.download.NotificationManager;
import org.dandelion.radiot.podcasts.ui.PodcastListActivity;

import java.io.File;

public class PodcastsApp {
    private static final int CACHE_FORMAT_VERSION = 5;
    private static final String THUMBNAIL_HOST = "http://www.radio-t.com";

    private static PodcastsApp instance;
    protected Context application;

    public static void openScreen(Context context, String title, String showName) {
        context.startActivity(PodcastListActivity.createIntent(context, title, showName));
    }

    public static void initialize(Context context) {
        if (null == instance) {
            instance = new PodcastsApp(context);
        }
    }
    
    public static void release() {
        instance.releaseInstance();
        instance = null;
    }

    public static PodcastsApp getInstance() {
        return instance;
    }

    public static void setTestingInstance(PodcastsApp newInstance) {
        instance = newInstance;
    }

    protected PodcastsApp(Context application) {
        this.application = application;
    }

    private void releaseInstance() {
    }

    public PodcastAction createPlayer() {
        return new ExternalPlayer();
    }

    public PodcastAction createDownloader() {
        if (supportsDownload()) {
            return createDownloaderClient();
        } else {
            return fakeDownloader();
        }
    }

    protected boolean supportsDownload() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    private PodcastAction fakeDownloader() {
        return new FakeDownloader();
    }

    private PodcastAction createDownloaderClient() {
        return new DownloadServiceClient();
    }

    protected File getSystemDownloadFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    public DownloadManager createDownloadManager() {
        return new SystemDownloadManager(application);
    }

    public DownloadFolder getPodcastDownloadFolder() {
        return new DownloadFolder(getSystemDownloadFolder());
    }

    public MediaScanner createMediaScanner() {
        return new SystemMediaScanner(application);
    }

    public NotificationManager createNotificationManager() {
        return new DownloadNotifier(application, createDownloadErrorMessages());
    }

    private DownloadErrorMessages createDownloadErrorMessages() {
        Resources resources = application.getResources();
        return new DownloadErrorMessages(
                resources.getStringArray(R.array.download_error_messages),
                android.app.DownloadManager.ERROR_UNKNOWN,
                resources.getString(R.string.download_default_message)
        );
    }

    public PodcastsCache createPodcastsCache(String name) {
        File cacheFile = new File(application.getCacheDir(), name);
        return new FilePodcastsCache(cacheFile, CACHE_FORMAT_VERSION);
    }

    public PodcastListLoader createLoaderForShow(String name) {
        PodcastProperties props = PodcastProperties.propertiesForShow(name);
        return createPodcastLoader(props);
    }

    public PodcastListLoader createPodcastLoader(PodcastProperties props) {
        HttpThumbnailProvider thumbnails = new HttpThumbnailProvider(THUMBNAIL_HOST);
        return new AsyncPodcastListLoader(new RssFeedProvider(props.url, thumbnails),
                createPodcastsCache(props.name));
    }
}
