package org.dandelion.radiot;

import android.app.Application;
import org.dandelion.radiot.podcasts.PodcastsApp;
import org.dandelion.radiot.podcasts.core.*;
import org.dandelion.radiot.podcasts.core.PodcastListLoader;

import java.io.File;
import java.util.HashMap;

public class RadiotApplication extends Application {
    private static final int CACHE_FORMAT_VERSION = 1;
    private HashMap<String, PodcastListLoader> engines;

    @Override
    public void onCreate() {
        super.onCreate();
        createEngines();
        PodcastsApp.initialize(this);
    }

    protected void createEngines() {
        engines = new HashMap<String, PodcastListLoader>();
        engines.put("main-show",
                podcastLoader("main-show", "http://feeds.rucast.net/radio-t", new HttpThumbnailProvider()));
        engines.put(
                "after-show",
                podcastLoader("after-show", "http://feeds.feedburner.com/pirate-radio-t", new NullThumbnailProvider()));
        engines.put(
                "test-show",
                TestPodcastListLoader.create(this));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        PodcastsApp.release();
    }

    protected PodcastListLoader podcastLoader(String showName, String url, ThumbnailProvider thumbnails) {
        return new AsyncPodcastListLoader(new RssFeedProvider(url),
                thumbnails, createPodcastsCache(showName));
    }

    private PodcastsCache createPodcastsCache(String showName) {
        File cacheFile = new File(getCacheDir(), showName);
        return new FilePodcastsCache(cacheFile, CACHE_FORMAT_VERSION);
    }

    public PodcastListLoader getPodcastEngine(String name) {
        return engines.get(name);
    }

    public void setPodcastEngine(String name, PodcastListLoader loader) {
        engines.put(name, loader);
    }

}
