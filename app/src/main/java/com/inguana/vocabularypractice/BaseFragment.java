package com.inguana.vocabularypractice;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class BaseFragment extends Fragment {
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        switch (transit) {
            case FragmentTransaction.TRANSIT_FRAGMENT_FADE:
                if (enter) {
                    return AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
                } else {
                    return AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
                }
            case FragmentTransaction.TRANSIT_FRAGMENT_CLOSE:
                if (enter) {
                    return AnimationUtils.loadAnimation(getActivity(), R.anim.anim_right_enter);
                } else {
                    return AnimationUtils.loadAnimation(getActivity(), R.anim.anim_right_exit);
                }
            case FragmentTransaction.TRANSIT_FRAGMENT_OPEN:
            default:
                if (enter) {
                    return AnimationUtils.loadAnimation(getActivity(), R.anim.anim_left_enter);
                } else {
                    return AnimationUtils.loadAnimation(getActivity(), R.anim.anim_left_exit);
                }
        }
    }
}
