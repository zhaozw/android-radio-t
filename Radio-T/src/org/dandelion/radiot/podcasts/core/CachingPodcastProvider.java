package org.dandelion.radiot.podcasts.core;

public class CachingPodcastProvider implements PodcastsProvider {
    private final PodcastsProvider podcasts;
    private PodcastsCache cache;

    public CachingPodcastProvider(PodcastsProvider podcasts, PodcastsCache cache) {
        this.podcasts = podcasts;
        this.cache = cache;
    }

    @Override
    public PodcastList retrieve() throws Exception {
        if (cache.isValid()) {
            return cache.getData();
        } else {
            return updateCacheWith(podcasts.retrieve());
        }
    }

    private PodcastList updateCacheWith(PodcastList newData) {
        cache.updateWith(newData);
        return newData;
    }
}
