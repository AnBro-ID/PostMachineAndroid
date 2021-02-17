package ru.anbroid.postmachine;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearSnapHelper;

import android.view.View;
import android.view.ViewGroup;

public class RibbonLayoutManager extends LinearLayoutManager
{
    private int itemsPerPage;
    private boolean isScrollEnabled;
    private LinearSnapHelper linSnapHelp;
    private onItemSelectedListener itemSelectedListener;

    RibbonLayoutManager(Context context, int itemsNum)
    {
        super(context, RecyclerView.HORIZONTAL, false);
        itemsPerPage = itemsNum;
        isScrollEnabled = true;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view)
    {
        super.onAttachedToWindow(view);
        RibbonAdapter rA = (RibbonAdapter) view.getAdapter();

        linSnapHelp = new LinearSnapHelper();
        linSnapHelp.attachToRecyclerView(view);

        scrollToPosition(rA.getSelectedPosition());
    }

    @Override
    public void onScrollStateChanged(int state)
    {
        super.onScrollStateChanged(state);

        if (state == RecyclerView.SCROLL_STATE_IDLE)
        {
            View centerView = linSnapHelp.findSnapView(this);
            itemSelectedListener.onItemSelected(getPosition(centerView));
        }
    }

    public void setOnItemSelectedListener(onItemSelectedListener ItemSelectedListener)
    {
        this.itemSelectedListener = ItemSelectedListener;
    }

    public interface onItemSelectedListener
    {
        void onItemSelected(int position);
    }

    @Override
    public boolean checkLayoutParams(RecyclerView.LayoutParams lp)
    {
        return super.checkLayoutParams(lp) && lp.width == calcItemSize();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams()
    {
        return setProperItemSize(super.generateDefaultLayoutParams());
    }

    @Override
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp)
    {
        return setProperItemSize(super.generateLayoutParams(lp));
    }

    private RecyclerView.LayoutParams setProperItemSize(RecyclerView.LayoutParams lp)
    {
        int itemSize = calcItemSize();
        if (getOrientation() == HORIZONTAL)
        {
            lp.width = itemSize;
        } else
        {
            lp.height = itemSize;
        }

        return lp;
    }

    private int calcItemSize()
    {
        int pageSize = getOrientation() == HORIZONTAL ? getWidth() : getHeight();
        return Math.round((float) pageSize / itemsPerPage);
    }

    public int getItemsPerPage()
    {
        return itemsPerPage;
    }

    public void setScrollEnabled(boolean flag)
    {
        isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollHorizontally()
    {
        return isScrollEnabled && super.canScrollHorizontally();
    }
}
