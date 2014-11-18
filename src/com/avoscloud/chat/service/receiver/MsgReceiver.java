package com.avoscloud.chat.service.receiver;

import android.content.Context;
import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.AVMessageReceiver;
import com.avos.avoscloud.Session;
import com.avoscloud.chat.service.ChatService;
import com.avoscloud.chat.service.listener.MsgListener;
import com.avoscloud.chat.service.listener.StatusListener;
import com.avoscloud.chat.util.Logger;

import java.util.*;

/**
 * Created by lzw on 14-8-7.
 */
public class MsgReceiver extends AVMessageReceiver {
  public static StatusListener statusListener;
  public static Set<String> onlineIds = new HashSet<String>();
  public static Set<MsgListener> msgListeners = new HashSet<MsgListener>();

  @Override
  public void onSessionOpen(Context context, Session session) {
    Logger.d("onSessionOpen");
  }

  @Override
  public void onSessionPaused(Context context, Session session) {
    Logger.d("onSessionPaused");
  }


  @Override
  public void onSessionResumed(Context context, Session session) {
    Logger.d("onSessionResumed");
  }

  @Override
  public void onPeersWatched(Context context, Session session, List<String> peerIds) {
    Logger.d("watched " + peerIds);
  }

  @Override
  public void onPeersUnwatched(Context context, Session session, List<String> peerIds) {
    Logger.d("unwatch " + peerIds);
  }

  @Override
  public void onMessage(final Context context, Session session, AVMessage avMsg) {
    Logger.d("onMessage " + avMsg.getMessage());
    ChatService.onMessage(context, avMsg, msgListeners, null);
  }

  @Override
  public void onMessageSent(Context context, Session session, AVMessage avMsg) {
    Logger.d("onMessageSent " + avMsg.getMessage());
    ChatService.onMessageSent(avMsg, msgListeners, null);
  }

  @Override
  public void onMessageDelivered(Context context, Session session, AVMessage msg) {
    Logger.d("onMessageDelivered " + msg.getMessage() + " fromPeerId=" + msg.getFromPeerId());
  }

  @Override
  public void onMessageFailure(Context context, Session session, AVMessage avMsg) {
    ChatService.updateStatusToFailed(avMsg, msgListeners);
  }

  @Override
  public void onStatusOnline(Context context, Session session, List<String> strings) {
    Logger.d("onStatusOnline " + strings);
    onlineIds.addAll(strings);
    if (statusListener != null) {
      statusListener.onStatusOnline(new ArrayList<String>(onlineIds));
    }
  }

  @Override
  public void onStatusOffline(Context context, Session session, List<String> strings) {
    Logger.d("onStatusOff " + strings);
    onlineIds.removeAll(strings);
    if (statusListener != null) {
      statusListener.onStatusOnline(new ArrayList<String>(onlineIds));
    }
  }

  @Override
  public void onError(Context context, Session session, Throwable throwable) {
    throwable.printStackTrace();
    ChatService.onMessageError(throwable, msgListeners);
  }

  public static void registerStatusListener(StatusListener listener) {
    statusListener = listener;
  }

  public static void unregisterSatutsListener() {
    statusListener = null;
  }

  public static void addMsgListener(MsgListener listener) {
    msgListeners.add(listener);
  }

  public static void removeMsgListener(MsgListener listener) {
    msgListeners.remove(listener);
  }

  public static List<String> getOnlineIds() {
    return new ArrayList<String>(onlineIds);
  }
}
