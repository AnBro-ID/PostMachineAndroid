package ru.anbroid.postmachine;

import android.content.Context;

class RibbonAdapterTriple extends RibbonAdapter
{
    public RibbonAdapterTriple(Context context)
    {
        super(context);
    }

    public RibbonAdapterTriple(Context context, RibbonAdapter rb)
    {
        super(context);
        mRibbon = rb.mRibbon;
        mRibbonBackup = rb.mRibbonBackup;
        mSelected = rb.mSelected;
        mSaved = rb.mSaved;
    }

    public int searchSign(int direction)
    {
        int searchPos = -1;

        if (direction == 0)
        {
            for (int i = mSelected - 5; i >= 0; --i)
            {
                if (mRibbon[i] == '1' || mRibbon[i] == '0')
                {
                    searchPos = mSelected = i;
                    break;
                }
            }
        }
        else
        {
            for (int i = mSelected + 5; i < mRibbon.length; ++i)
            {
                if (mRibbon[i] == '1' || mRibbon[i] == '0')
                {
                    searchPos = mSelected = i;
                    break;
                }
            }
        }

        return searchPos;
    }
}
