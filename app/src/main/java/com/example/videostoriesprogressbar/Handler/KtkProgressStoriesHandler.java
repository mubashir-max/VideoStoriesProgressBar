package com.example.videostoriesprogressbar.Handler;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.videostoriesprogressbar.R;

import java.util.ArrayList;
import java.util.List;


public class KtkProgressStoriesHandler extends LinearLayout {

    private final LinearLayout.LayoutParams PROGRESS_BAR_LAYOUT_PARAM = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
    private final LinearLayout.LayoutParams SPACE_LAYOUT_PARAM = new LinearLayout.LayoutParams(5, LinearLayout.LayoutParams.WRAP_CONTENT);

    private final List<ProgressBar> progressBars = new ArrayList<>();
    int storiesCount = -1;
    private ObjectAnimator progressAnimator;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public KtkProgressStoriesHandler(Context context) {
        this(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public KtkProgressStoriesHandler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StoriesProgressView);
        storiesCount = typedArray.getInt(R.styleable.StoriesProgressView_progressCount, 0);
        typedArray.recycle();
        bindViews();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void bindViews() {
        progressBars.clear();
        removeAllViews();

        for (int i = 0; i < storiesCount; i++) {
            final ProgressBar p = createProgressBar();
//            p.setMax();
            progressBars.add(p);
            addView(p);
            if ((i + 1) < storiesCount) {
                addView(createSpace());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private ProgressBar createProgressBar() {
        ProgressBar p = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        p.setLayoutParams(PROGRESS_BAR_LAYOUT_PARAM);
//        p.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        return p;
    }

    private View createSpace() {
        View v = new View(getContext());
        v.setLayoutParams(SPACE_LAYOUT_PARAM);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setStoriesCount(int storiesCount) {
        this.storiesCount = storiesCount;
        bindViews();
    }

    public void setStoryDuration(long duration) {
        try {
            progressAnimator = ObjectAnimator.ofInt(progressBars.get(0), "progress", 0, 100);
            progressAnimator.setDuration(duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startProgress() {
        if (progressAnimator != null)
            progressAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startfrom(long duration, int from) {
//        current = from;
//        if (current != -1) {
        progressAnimator = ObjectAnimator.ofInt(progressBars.get(from), "progress", 0, 100);
        progressAnimator.setAutoCancel(true);
        progressAnimator.setDuration(duration);
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pauseProgressBar() {
        progressAnimator.pause();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void resumeProgressBar() {
        progressAnimator.resume();
    }

    public void fillProgressBar(int ofcurrent) {
        progressAnimator = ObjectAnimator.ofInt(progressBars.get(ofcurrent), "progress", 100);
        progressAnimator.start();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//            };
//        },0);
    }

    public void ClearProgressBar(int ofcurrent, int next) {
        progressAnimator = ObjectAnimator.ofInt(progressBars.get(next), "progress", 0);
        progressAnimator.start();
        progressAnimator = ObjectAnimator.ofInt(progressBars.get(ofcurrent), "progress", 0);
        progressAnimator.start();
//        if (ofcurrent != -1) {
//            new Handler().postDelayed(() -> ChildClearProgressBar(ofcurrent), 500);
//        }
    }

    public void ChildClearProgressBar(int ofcurrent) {
        progressAnimator = ObjectAnimator.ofInt(progressBars.get(ofcurrent), "progress", 0);
        progressAnimator.start();
    }
}
