package com.jqyzyh.learn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.UUID;

/**
 * @author yuhang
 */

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMe();
            }
        });
        view.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMe();
            }
        });
        return view;
    }

    private void addMe() {
        getFragmentManager().beginTransaction().addToBackStack(UUID.randomUUID().toString()).hide(this).add(android.R.id.content, new LoginFragment(), "login").commitAllowingStateLoss();
    }

    void closeMe(){
        getFragmentManager().popBackStack("login", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_in);
        }else{
            return AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_out);
        }
    }
}
