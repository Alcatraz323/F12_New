package io.alcatraz.f12.utils;

import android.view.*;
import android.view.animation.*;
import android.widget.*;

public class AnimateUtils {
    public static void textChange(final TextView txv, final CharSequence tx) {
        AnimationSet as1 = new AnimationSet(true);
        AlphaAnimation aa1 = new AlphaAnimation(1, 0);
        aa1.setDuration(200);
        as1.addAnimation(aa1);
        txv.startAnimation(as1);
        as1.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation p1) {

            }

            @Override
            public void onAnimationEnd(Animation p1) {
                txv.setVisibility(View.GONE);
                txv.setText(tx);
                AnimationSet as = new AnimationSet(true);
                AlphaAnimation aa = new AlphaAnimation(0, 1);
                aa.setDuration(200);
                as.addAnimation(aa);
                txv.startAnimation(as);
                as.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation p1) {

                    }

                    @Override
                    public void onAnimationEnd(Animation p1) {
                        txv.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationRepeat(Animation p1) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation p1) {

            }
        });

    }

    public static void fadeIn(final View v, final SimpleAnimateInterface animateInterface) {
        AnimationSet as = new AnimationSet(true);
        AlphaAnimation aa = new AlphaAnimation(0, 1);
        aa.setDuration(1200);
        as.addAnimation(aa);
        v.startAnimation(as);
        as.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation p1) {
            }

            @Override
            public void onAnimationEnd(Animation p1) {
                v.setVisibility(View.VISIBLE);
                animateInterface.onEnd();
            }

            @Override
            public void onAnimationRepeat(Animation p1) {

            }
        });
    }

    public static void fadeOut(final View v, final SimpleAnimateInterface animateInterface) {
        AnimationSet as = new AnimationSet(true);
        AlphaAnimation aa = new AlphaAnimation(1, 0);
        aa.setDuration(300);
        as.addAnimation(aa);
        v.startAnimation(as);
        as.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation p1) {
            }

            @Override
            public void onAnimationEnd(Animation p1) {
                v.setVisibility(View.GONE);
                animateInterface.onEnd();
            }

            @Override
            public void onAnimationRepeat(Animation p1) {

            }
        });
    }

    public interface SimpleAnimateInterface {
        void onEnd();
    }
}