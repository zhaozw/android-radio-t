package org.dandelion.radiot.podcasts.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import org.dandelion.radiot.R;
import org.dandelion.radiot.podcasts.core.PodcastDownloader;
import org.dandelion.radiot.podcasts.core.PodcastItem;
import org.dandelion.radiot.podcasts.core.PodcastPlayer;

public class PodcastSelectionHandler {
    private Context context;
    private PodcastPlayer player;
    private PodcastDownloader downloader;

    public PodcastSelectionHandler(Context context, PodcastPlayer player, PodcastDownloader downloader) {
        this.context = context;
        this.player = player;
        this.downloader = downloader;
    }

    public void podcastSelected(PodcastItem item) {
        selectAction(item);
    }

    private void selectAction(final PodcastItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.podcast_actions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                switch(index) {
                    case 0: 
                        downloader.downloadPodcast(item);
                    case 1:
                        player.startPlaying(context, item.getAudioUri());
                }
            }
        });
        builder.show();
    }
}
