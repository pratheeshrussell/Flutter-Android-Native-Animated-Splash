//android\app\src\main\java\com\example\native_splash\CustomSplashScreenView.java
package com.example.native_splash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.animation.ValueAnimator;
import android.widget.ImageView;

public class CustomSplashScreenView extends FrameLayout {
  //rotate animation variables
  private static final int ANIMATION_TIME_IN_MILLIS = 800;
  private ImageView flutterLogo;
  private float rotateAngle = 360;
  private ViewPropertyAnimator rotateAnimator;
  //fade animation variables
  private static final int TRANSITION_TIME_IN_MILLIS = 1000;
  private float transitionPercentWhenAnimationStarted = 0.0f;
  private float totalTransitionPercent = 0.0f;
  private Runnable onTransitionComplete;
  private ViewPropertyAnimator fadeAnimator;


// Listener for rotateAnimator for event onAnimationEnd and onAnimationCancel
  private final Animator.AnimatorListener rotateAnimatorListener = new AnimatorListenerAdapter() {
    @SuppressLint("NewApi")
    @Override
    public void onAnimationEnd(Animator animation) {
      animation.removeAllListeners();
      rotateAngle = rotateAngle + 360;
      animateFlutterLogo();
    }

    @SuppressLint("NewApi")
    @Override
    public void onAnimationCancel(Animator animation) {
      animation.removeAllListeners();
    }
  };

//update Listener for transittion animator
   private final ValueAnimator.AnimatorUpdateListener transitionAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
   @Override
    public void onAnimationUpdate(ValueAnimator animation) {
      totalTransitionPercent = transitionPercentWhenAnimationStarted + 
      (animation.getAnimatedFraction() * (1.0f - transitionPercentWhenAnimationStarted));
    }
  };
  private final  Animator.AnimatorListener transitionAnimatorListener = new AnimatorListenerAdapter() {
   @Override
    public void onAnimationEnd(Animator animation) {
      animation.removeAllListeners();
      if (onTransitionComplete != null) {
        onTransitionComplete.run();
      }
    }
  };

//main function
  @SuppressLint("NewApi")
  public CustomSplashScreenView(Context context) {
    super(context);
    setBackgroundColor(Color.parseColor("#FFFFFF"));
    flutterLogo = new ImageView(getContext());
    flutterLogo.setImageDrawable(getResources().getDrawable(R.drawable.launch_background, getContext().getTheme()));
    addView(flutterLogo, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
    animateFlutterLogo();
  }

//rotation animation
  @SuppressLint("NewApi")
  private void animateFlutterLogo() {
    rotateAnimator = flutterLogo
        .animate().rotation(rotateAngle).setDuration(ANIMATION_TIME_IN_MILLIS)
        .setInterpolator(new LinearInterpolator())
        .setListener(rotateAnimatorListener);
    rotateAnimator.start();
  }

//transition animation
  public void animateAway(@NonNull Runnable onTransitionComplete) {
    this.onTransitionComplete = onTransitionComplete;
    fadeAnimator = animate()
        .alpha(0.0f)
        .setDuration(Math.round(TRANSITION_TIME_IN_MILLIS * (1.0 - totalTransitionPercent)))
        .setUpdateListener(transitionAnimatorUpdateListener)
        .setListener(transitionAnimatorListener);
    fadeAnimator.start();
  }
 
  @SuppressLint("NewApi")
  @Override
  protected void onDetachedFromWindow() {
    if (rotateAnimator != null) {
      rotateAnimator.cancel();
    }
    super.onDetachedFromWindow();
  }

//state saving
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
