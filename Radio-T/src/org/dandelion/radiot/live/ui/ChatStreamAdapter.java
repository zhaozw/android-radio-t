package org.dandelion.radiot.live.ui;

import org.dandelion.radiot.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import org.dandelion.radiot.live.chat.ChatMessage;
import org.dandelion.radiot.live.chat.ChatTranslation;

import java.util.List;

class ChatStreamAdapter extends ArrayAdapter<ChatMessage>
        implements ChatTranslation.MessageConsumer {

    private final LayoutInflater inflater;

    public ChatStreamAdapter(Context context) {
        super(context, 0);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessageView row = (ChatMessageView) convertView;
        if (row == null) {
            row = (ChatMessageView) inflater.inflate(R.layout.chat_message, parent, false);
        }
        row.setMessage(getItem(position));
        return row;
    }

    @Override
    public void initWithMessages(List<ChatMessage> messages) {
        clear();
        appendMessages(messages);
    }

    @Override
    public void appendMessages(List<ChatMessage> messages) {
        for (ChatMessage msg : messages) {
            add(msg);
        }
    }
}
