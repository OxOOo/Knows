package com.java.g39.data;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * 朗读者类，用于语音播报
 * 使用方法:
 speaker = new Speech(this,"今天天气真好，气温竟然只有四十度！",
 new Speech.StateChangeListener(){public void onChange(){ }});
 speaker.start();
 */
public class Speech
{
    public interface StateChangeListener
    {
        void onChange();
    }
    public enum State{ready,reading,pause,stoped};

    String text;
    State nowState;
    StateChangeListener stateChangeListener;
    private SpeechSynthesizer mySynthesizer;

    /**
     * @param context 上下文
     * @param text_ 朗读文本
     * @param listener 处理状态改变的监听器
     */
    public Speech(Context context, String text_, StateChangeListener listener)
    {
        stateChangeListener=listener;
        nowState=State.ready;
        text=text_;
        //语音初始化，在使用应用使用时需要初始化一次就好，如果没有这句会出现10111初始化失败
        SpeechUtility.createUtility(context, "appid=59b25b94");
        //处理语音合成关键类
        mySynthesizer = SpeechSynthesizer.createSynthesizer(context, new InitListener() {
            @Override
            public void onInit(int code) {

            }
        });
        mySynthesizer.setParameter(SpeechConstant.VOICE_NAME,"xiaoyan");
        mySynthesizer.setParameter(SpeechConstant.PITCH,"50");
        mySynthesizer.setParameter(SpeechConstant.VOLUME,"50");
    }

    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
        }
        @Override
        public void onSpeakPaused() {
        }
        @Override
        public void onSpeakResumed() {
        }
        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
        }
        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        @Override
        public void onCompleted(SpeechError error) {
            if(error!=null)
            {
                Log.d("mySynth complete code:", error.getErrorCode()+"");
            }
            else
            {
                Log.d("mySynth complete code:", "0");
                changeState(State.stoped);
                changeState(State.ready);
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
            //doNothin
        }
    };

    private void changeState(State newState)
    {
        nowState = newState;
        if (stateChangeListener != null)
            stateChangeListener.onChange();
    }

    public int start()
    {
        if(nowState!=State.ready)return -1;
        changeState(State.reading);
        int code = mySynthesizer.startSpeaking(text, mTtsListener);
        Log.d("mySyn start_:",code+"");
        return code;
    }

    public void stop()
    {
        changeState(State.stoped);
        mySynthesizer.stopSpeaking();
        changeState(State.ready);
    }

    public void pause()
    {
        if(nowState!=State.reading)return;
        changeState(State.pause);
        mySynthesizer.pauseSpeaking();
    }

    public void resume()
    {
        if(nowState!=State.pause)return;
        changeState(State.reading);
        mySynthesizer.resumeSpeaking();
    }

    public State getState()
    {
        return nowState;
    }

    public void setStateChangeListener(StateChangeListener listener)
    {
        stateChangeListener=listener;
    }
}
