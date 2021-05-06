package com.czdxwx.lbs.broadcast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.czdxwx.lbs.MainActivity;
import com.czdxwx.lbs.MyApplication;
import com.czdxwx.lbs.PushNotification.RecommenderActivity;
import com.czdxwx.lbs.R;
import com.czdxwx.lbs.entity.Data;
import com.czdxwx.lbs.entity.News;
import com.czdxwx.lbs.PushNews.NewsActivity;
import com.czdxwx.lbs.logActivity.InformationActivity;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import net.tsz.afinal.FinalDb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.czdxwx.lbs.MyApplication.TAG;

/*
两个作用：
1.获取服务器推送的消息
2.获取调用MIPUSHClient方法的返回结果
 */
    public class MIPUSH_MessageReceiver extends PushMessageReceiver {

    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;
    private FinalDb db= MainActivity.db;

        @Override
        //接受服务器推送的透传消息，消息封装在MiPUSHMessage类中
        public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
            Log.v(TAG,
                    "onReceivePassThroughMessage is called. " + message.toString());
            String log = context.getString(R.string.recv_passthrough_message, message.getContent());
            InformationActivity.logList.add(0, getSimpleDate() + " " + log);

            //依次判断别名，订阅主题：有别名就是消息推送，有订阅主题则是新闻推送
            if (!TextUtils.isEmpty(message.getTopic())) {
                mTopic = message.getTopic();
                if(mTopic.equals("新闻")){
                    News news=new News();
                    news.setNewsName(message.getExtra().get("标题"));
                    news.setNewsText(message.getExtra().get("副标题"));
                    news.setContent(message.getContent());
                    news.setDate(getSimpleDate());
                    db.save(news);
                }
            } else if (!TextUtils.isEmpty(message.getAlias())) {
                mAlias = message.getAlias();
                if(mAlias.equals("韦欣")){
                    Data data=new Data();
                    data.setTweetName(message.getExtra().get("标题"));
                    data.setTweetText(message.getExtra().get("副标题"));
                    data.setContent(message.getContent());
                    data.setDate(getSimpleDate());
                    db.save(data);
                }
            }

//            //记录到日志中
//            Message msg = Message.obtain();
//            msg.obj = log;
//            MyApplication.getHandler().sendMessage(msg);
        }


        @Override
        //接收服务器推送的通知消息，用户点击后触发，消息封装在 MiPushMessage类中。
        public void onNotificationMessageClicked(Context context, MiPushMessage message) {

            Log.v(TAG,
                    "onNotificationMessageClicked is called. " + message.toString());
            String log = context.getString(R.string.click_notification_message, message.getContent());
            InformationActivity.logList.add(0, getSimpleDate() + " " + log);

            //依次判断别名，订阅主题
            if (!TextUtils.isEmpty(message.getTopic())) {
                mTopic = message.getTopic();
                if(mTopic.equals("新闻")){
                    Intent intent=new Intent();
                    intent.setAction("android.intent.action.news");
                    //广播里跳转需加上这句,否则报异常 android.util.AndroidRuntimeException
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            } else if (!TextUtils.isEmpty(message.getAlias())) {
                mAlias = message.getAlias();
                if(mAlias.equals("韦欣")){
                    Intent intent=new Intent();
                    intent.setAction("android.intent.action.notification");
                    //广播里跳转需加上这句,否则报异常 android.util.AndroidRuntimeException
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }

//            Message msg = Message.obtain();
//            //isNotified表示消息是否通过通知栏传给app的。
//            // 如果为true，表示消息在通知栏出过通知；
//            // 如果为false，表示消息是直接传给app的，没有弹出过通知。
//            if (message.isNotified()) {
//                msg.obj = log;
//            }
//            MyApplication.getHandler().sendMessage(msg);
        }


        @Override
        /*接收服务器推送的通知消息，消息到达客户端时触发，还可以接受应用在前台时不弹出通知的通知消息，消息封装在 MiPushMessage类中。
         在MIUI上，只有应用处于启动状态，或者自启动白名单中，才可以通过此方法接受到该消息。
        */
        public void onNotificationMessageArrived(Context context, MiPushMessage message) {
            Log.v(TAG,
                    "onNotificationMessageArrived is called. " + message.toString());
            String log = context.getString(R.string.arrive_notification_message, message.getContent());
            InformationActivity.logList.add(0, getSimpleDate() + " " + log);

            //依次判断别名，订阅主题：有别名就是消息推送，有订阅主题则是新闻推送
            if (!TextUtils.isEmpty(message.getTopic())) {
                mTopic = message.getTopic();
                if(mTopic.equals("新闻")){
                    News news=new News();
                    news.setNewsName(message.getExtra().get("标题"));
                    news.setNewsText(message.getExtra().get("副标题"));
                    news.setContent(message.getContent());
                    news.setDate(getSimpleDate());
                    db.save(news);
                }
            } else if (!TextUtils.isEmpty(message.getAlias())) {
                mAlias = message.getAlias();
                if(mAlias.equals("韦欣")){
                    Data data=new Data();
                    data.setTweetName(message.getExtra().get("标题"));
                    data.setTweetText(message.getExtra().get("副标题"));
                    data.setContent(message.getContent());
                    data.setDate(getSimpleDate());
                    db.save(data);
                }
            }

//            Message msg = Message.obtain();
//            msg.obj = log;
//            MyApplication.getHandler().sendMessage(msg);
        }


        @Override
        //获取给服务器发送命令的结果，结果封装在MiPushCommandMessage类中。
        public void onCommandResult(Context context, MiPushCommandMessage message) {
            Log.v(TAG,
                    "onCommandResult is called. " + message.toString());
            String command = message.getCommand();
            List<String> arguments = message.getCommandArguments();
            String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
            String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
            String log;

            //注册回调
            if (MiPushClient.COMMAND_REGISTER.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mRegId = cmdArg1;
                    log = context.getString(R.string.register_success);
                } else {
                    log = context.getString(R.string.register_fail);
                }
                //设置别名回调
            } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mAlias = cmdArg1;
                    log = context.getString(R.string.set_alias_success, mAlias);
                } else {
                    log = context.getString(R.string.set_alias_fail, message.getReason());
                }
//设置取消别名
            } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mAlias = cmdArg1;
                    log = context.getString(R.string.unset_alias_success, mAlias);
                } else {
                    log = context.getString(R.string.unset_alias_fail, message.getReason());
                }
                //设置账户
            } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mAccount = cmdArg1;
                    log = context.getString(R.string.set_account_success, mAccount);
                } else {
                    log = context.getString(R.string.set_account_fail, message.getReason());
                }
                //撤销账户
            } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mAccount = cmdArg1;
                    log = context.getString(R.string.unset_account_success, mAccount);
                } else {
                    log = context.getString(R.string.unset_account_fail, message.getReason());
                }
                //设置订阅主题
            } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mTopic = cmdArg1;
                    log = context.getString(R.string.subscribe_topic_success, mTopic);
                } else {
                    log = context.getString(R.string.subscribe_topic_fail, message.getReason());
                }
                //取消订阅
            } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mTopic = cmdArg1;
                    log = context.getString(R.string.unsubscribe_topic_success, mTopic);
                } else {
                    log = context.getString(R.string.unsubscribe_topic_fail, message.getReason());
                }
                //设置接收时间
            } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mStartTime = cmdArg1;
                    mEndTime = cmdArg2;
                    log = context.getString(R.string.set_accept_time_success, mStartTime, mEndTime);
                } else {
                    log = context.getString(R.string.set_accept_time_fail, message.getReason());
                }
            } else {
                log = message.getReason();
            }
            InformationActivity.logList.add(0, getSimpleDate() + "    " + log);

//            Message msg = Message.obtain();
//            msg.obj = log;
//            MyApplication.getHandler().sendMessage(msg);
        }

    @Override
    //获取给服务器发送注册命令的结果，结果封装在MiPushCommandMessage类中。
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.v(TAG,
                "onReceiveRegisterResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String log;

        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                Log.d(TAG,"注册成功");
                log = context.getString(R.string.register_success);
            } else {
                log = context.getString(R.string.register_fail);
            }
        } else {
            log = message.getReason();
        }

//        Message msg = Message.obtain();
//        msg.obj = log;
//        MyApplication.getHandler().sendMessage(msg);
    }

    @SuppressLint("SimpleDateFormat")
    //获取当前时间
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }

    }

