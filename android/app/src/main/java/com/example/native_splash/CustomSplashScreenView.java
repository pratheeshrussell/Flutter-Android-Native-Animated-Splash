package com.example.native_splash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.animation.ValueAnimator;
import android.widget.ImageView;

public class CustomSplashScreenView extends FrameLayout {
  private static final int ANIMATION_TIME_IN_MILLIS = 1000;

  private ImageView flutterLogo;
  private float currentScale = 0;
  private ViewPropertyAnimator scaleAnimator;
  //fade animation
  private float transitionPercentWhenAnimationStarted = 0.0f;
  private float totalTransitionPercent = 0.0f;
  private Runnable onTransitionComplete;
  private ViewPropertyAnimator fadeAnimator;

  private final Animator.AnimatorListener animatorListener = new AnimatorListenerAdapter() {
    @SuppressLint("NewApi")
    @Override
    public void onAnimationEnd(Animator animation) {
      animation.removeAllListeners();
      if (currentScale <= 1 ) {
        animateFlutterLogo((float)1.5);
      } else {
        animateFlutterLogo((float)1);
      }
    }

    @SuppressLint("NewApi")
    @Override
    public void onAnimationCancel(Animator animation) {
      animation.removeAllListeners();
    }
  };

  @SuppressLint("NewApi")
  public CustomSplashScreenView(Context context) {
    super(context);
    setBackgroundColor(Color.parseColor("#FFFFFF"));
    flutterLogo = new ImageView(getContext());
    flutterLogo.setImageDrawable(getResources().getDrawable(R.drawable.launch_background, getContext().getTheme()));
    addView(flutterLogo, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
    animateFlutterLogo((float)1.5);
  }

  @SuppressLint("NewApi")
  private void animateFlutterLogo(float scaleTo) {
    this.currentScale = scaleTo;
    scaleAnimator = flutterLogo
        .animate()
        .scaleX(scaleTo).scaleY(scaleTo)
        .setDuration(ANIMATION_TIME_IN_MILLIS/2)
        .setInterpolator(new AccelerateDecelerateInterpolator())
        .setListener(animatorListener);
    scaleAnimator.start();
  }
  public void animateAway(@NonNull Runnable onTransitionComplete) {
    this.onTransitionComplete = onTransitionComplete;
    fadeAnimator = animate()
        .alpha(0.0f)
        .setDuration(Math.round(ANIMATION_TIME_IN_MILLIS * (1.0 - totalTransitionPercent)))
        .setUpdateListener(animatorUpdateListener)
        .setListener(animatorListener);
    fadeAnimator.start();
  }
  private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
   @Override
    public void onAnimationUpdate(ValueAnimator animation) {
      totalTransitionPercent = transitionPercentWhenAnimationStarted + 
      (animation.getAnimatedFraction() * (1.0f - transitionPercentWhenAnimationStarted));
    }
  };
  @SuppressLint("NewApi")
  @Override
  protected void onDetachedFromWindow() {
    if (scaleAnimator != null) {
      scaleAnimator.cancel();
    }
    super.onDetachedFromWindow();
  }

  @Nullable
  public Bundle saveSplashState() {
    Bundle state = new Bundle();
    state.putFloat("totalTransitionPercent", totalTransitionPercent);
    return state;
  }

  public void restoreSplashState(@Nullable Bundle bundle) {
    if (bundle != null) {
      transitionPercentWhenAnimationStarted = bundle.getFloat("totalTransitionPercent");
      setAlpha(1.0f - transitionPercentWhenAnimationStarted);
    } 
  }
}