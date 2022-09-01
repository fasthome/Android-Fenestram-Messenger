package io.fasthome.fenestram_messenger.util

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("ClickableViewAccessibility")
fun RecyclerView.supportBottomSheetScroll() {
    setOnTouchListener { v, event ->
        when(event.action){
            MotionEvent.ACTION_DOWN->{
                v.parent.requestDisallowInterceptTouchEvent(true);
            }
            MotionEvent.ACTION_UP-> {
                v.parent.requestDisallowInterceptTouchEvent(false);
            }
        }
        v.onTouchEvent(event);
        return@setOnTouchListener true
    }
}