package com.inguana.vocabularypractice;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import static com.inguana.vocabularypractice.MainActivity.MAIN_MENU_FRAGMENT_TAG;

public class FragmentSplashScreen extends BaseFragment {

    private int fragmentContainerId;

    private VideoView vvSplashScreenMmf;
    private ImageView ivSplashScreenBackgroundMmf, ivExtraImageCoverMmf, ivSplashScreenImageMmf;
    private MainActivity activity;
    private YoYo.YoYoString animationSplashBackground;

    private void initialize(View view, ViewGroup container) {
        fragmentContainerId = container.getId();
        vvSplashScreenMmf = view.findViewById(R.id.vvSplashScreenMmf);
        ivSplashScreenBackgroundMmf = view.findViewById(R.id.ivSplashScreenBackgroundMmf);
        ivSplashScreenImageMmf = view.findViewById(R.id.ivSplashScreenImageMmf);

        activity = ((MainActivity) getActivity());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_launch, container, false);
        initialize(view, container);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                initializePlayer();
            }
        }, 1000);
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    private void initializePlayer() {
        Uri uri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.app_launch_splash_animation);
        vvSplashScreenMmf.setVisibility(View.VISIBLE);
        vvSplashScreenMmf.setVideoURI(uri);
        vvSplashScreenMmf.setZOrderOnTop(true);
        vvSplashScreenMmf.start();
        vvSplashScreenMmf.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                            //TODO: try loading the image from start (visible), then elevate it to the right height
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    ivSplashScreenImageMmf.setVisibility(View.VISIBLE);
                                }
                            }, 1000);
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
        vvSplashScreenMmf.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                releasePlayer();
                activity.initializePlayer();
                waitAnimation();
            }
        });
        vvSplashScreenMmf.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return false;
            }
        });
    }

    private void waitAnimation() {
        Handler handler = new Handler();
        final Techniques transition = Techniques.FadeOut;

        animationSplashBackground = YoYo.with(transition)
                //.delay(1000)
                .duration(1000)
                .playOn(ivSplashScreenBackgroundMmf);

        handler.postDelayed(new Runnable() {
            public void run() {
                activity.currentMainFragment = MAIN_MENU_FRAGMENT_TAG;
                activity.isSplashScreenDone = true;
                activity.getSupportFragmentManager().beginTransaction().replace(fragmentContainerId, new MainMenuFragment(), activity.currentMainFragment)/*.addToBackStack(null)*/.commit();
            }
        }, 1000);
    }

    private void releasePlayer() {
        activity.isSplashScreenDone = true;
        vvSplashScreenMmf.stopPlayback();
        vvSplashScreenMmf.setVisibility(View.GONE);
    }
}
