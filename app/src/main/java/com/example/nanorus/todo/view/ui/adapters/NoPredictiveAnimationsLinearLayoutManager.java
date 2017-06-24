package com.example.nanorus.todo.view.ui.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class NoPredictiveAnimationsLinearLayoutManager extends LinearLayoutManager {
    public NoPredictiveAnimationsLinearLayoutManager(Context context) {
        super(context);
    }

    public NoPredictiveAnimationsLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public NoPredictiveAnimationsLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }


}
