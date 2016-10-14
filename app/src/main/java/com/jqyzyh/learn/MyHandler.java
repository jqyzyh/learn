package com.jqyzyh.learn;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Created by jqyzyh on 2016/9/14.
 */
public class MyHandler extends Handler {

    private Context mContext;

    public MyHandler(Context context){
        super();
        mContext = context;

        if(context instanceof FragmentActivity){
            FragmentActivity fa = (FragmentActivity) context;
            fa.getSupportFragmentManager().beginTransaction().add(new StateFragment(), StateFragment.class.getName()).commit();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        if(mContext == null){
            return;
        }
        super.handleMessage(msg);
    }

    class StateFragment extends Fragment{
        @Override
        public void onStart() {
            super.onStart();
            Log.d("StateFragment", "onStart");
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.d("StateFragment", "onStop");
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            Log.d("StateFragment", "onAttach");
        }

        @Override
        public void onDetach() {
            mContext = null;
            super.onDetach();
            Log.d("StateFragment", "onDetach");
        }
    }
}
