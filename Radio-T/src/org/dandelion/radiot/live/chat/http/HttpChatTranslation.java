package org.dandelion.radiot.live.chat.http;

import org.dandelion.radiot.common.ui.Announcer;
import org.dandelion.radiot.live.chat.ChatTranslation;
import org.dandelion.radiot.live.chat.Message;
import org.dandelion.radiot.live.chat.MessageConsumer;
import org.dandelion.radiot.live.chat.ProgressListener;
import org.dandelion.radiot.live.schedule.Scheduler;

import java.util.List;

public class HttpChatTranslation implements ChatTranslation, MessageConsumer {

    final Announcer<ProgressListener> progressAnnouncer = new Announcer<ProgressListener>(ProgressListener.class);
    private final Announcer<MessageConsumer> messageAnnouncer = new Announcer<MessageConsumer>(MessageConsumer.class);
    final HttpChatClient chatClient;
    private final Scheduler refreshScheduler;
    private HttpTranslationState currentState;

    public HttpChatTranslation(String baseUrl, Scheduler refreshScheduler) {
        this(new HttpChatClient(baseUrl), refreshScheduler);
    }

    public HttpChatTranslation(HttpChatClient chatClient, Scheduler refreshScheduler) {
        this.chatClient = chatClient;
        this.refreshScheduler = refreshScheduler;
        refreshScheduler.setPerformer(new Scheduler.Performer() {
            @Override
            public void performAction() {
                requestNextMessages();
            }
        });
        currentState = new HttpTranslationState.Disconnected(this);
    }

    @Override
    public void setProgressListener(ProgressListener listener) {
        progressAnnouncer.setTarget(listener);
    }

    @Override
    public void setMessageConsumer(MessageConsumer consumer) {
        messageAnnouncer.setTarget(consumer);
    }

    @Override
    public void start() {
        currentState.onStart();
    }

    @Override
    public void stop() {
        currentState.onStop();
    }

    void cancelRefresh() {
        refreshScheduler.cancel();
    }

    @Override
    public void shutdown() {
        setMessageConsumer(null);
        setProgressListener(null);
        chatClient.shutdown();
    }


    void setCurrentState(HttpTranslationState state) {
        this.currentState = state;
    }

    private void requestNextMessages() {
        new HttpChatRequest.Next(chatClient, progressAnnouncer.announce(), this).execute();
    }

    void consumeMessages(List<Message> messages) {
        messageAnnouncer.announce().processMessages(messages);
        scheduleRefresh();
    }

    void scheduleRefresh() {
        setCurrentState(new HttpTranslationState.Connected(this));
        refreshScheduler.scheduleNext();
    }

    @Override
    public void processMessages(List<Message> messages) {
        consumeMessages(messages);
    }
}
