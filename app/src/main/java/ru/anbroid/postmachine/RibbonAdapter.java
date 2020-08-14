package ru.anbroid.postmachine;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RibbonAdapter extends RecyclerView.Adapter<RibbonAdapter.ViewHolder> implements RibbonLayoutManager.onItemSelectedListener
{
    protected int mSelected;
    protected int mSaved;
    protected char[] mRibbon;
    protected char[] mRibbonBackup;
    protected LayoutInflater mInflater;
    protected ItemClickListener mClickListener;

    public RibbonAdapter(Context context)
    {
        this.mInflater = LayoutInflater.from(context);
        mRibbon = new char[100];
        mSelected = 49;

        for (int i = 0; i < mRibbon.length; ++i) mRibbon[i] = '0';
    }

    public RibbonAdapter(RibbonAdapterTriple rb)
    {
        mInflater = rb.mInflater;
        mRibbon = rb.mRibbon;
        mRibbonBackup = rb.mRibbonBackup;
        mSelected = rb.mSelected;
        mSaved = rb.mSaved;
        mClickListener = rb.mClickListener;

        for (int i = 0; i < mRibbon.length; ++i)
        {
            if (mRibbon[i] == ' ')
                mRibbon[i] = '0';

            if (mRibbonBackup[i] == ' ')
                mRibbonBackup[i] = '0';
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.ribbon_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.myTextView.setText(String.valueOf(mRibbon[position]));
        holder.num.setText(String.valueOf(position - 49));
    }

    @Override
    public int getItemCount()
    {
        return mRibbon.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView myTextView;
        TextView num;

        ViewHolder(View itemView)
        {
            super(itemView);
            myTextView = itemView.findViewById(R.id.RibbonItem);
            num = itemView.findViewById(R.id.num);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public char getItem(int id)
    {
        return mRibbon[id];
    }

    public void setItem(int id, char newValue)
    {
        mRibbon[id] = newValue;
        notifyItemChanged(id);
    }

    public void setClickListener(ItemClickListener itemClickListener)
    {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public void onItemSelected(int position)
    {
        mSelected = position;
    }

    public int getSelectedPosition()
    {
        return mSelected;
    }

    public void setSelectedPosition(int newValue)
    {
        mSelected = newValue;
    }

    public void backupRibbon()
    {
        if (mRibbonBackup == null) mRibbonBackup = new char[100];

        mSaved = mSelected;
        System.arraycopy(mRibbon, 0, mRibbonBackup, 0, 100);
    }

    public void restoreRibbon()
    {
        if (mRibbonBackup != null)
        {
            mSelected = mSaved;
            System.arraycopy(mRibbonBackup, 0, mRibbon, 0, 100);
            notifyDataSetChanged();
        }
    }

    public char[] getRibbon()
    {
        return mRibbon;
    }

    public int getSaved() { return mSaved; }

    public void setSaved(int num) { mSaved = num; }

    public char[] getBackupRibbon()
    {
        return mRibbonBackup;
    }

    public void setBackupRibbon(char [] newRibbon)
    {
        mRibbonBackup = newRibbon;
    }

    public void setRibbon(char[] newRibbon)
    {
        mRibbon = newRibbon;
        notifyDataSetChanged();
    }

    public void resetAdapter()
    {
        mSelected = 49;
        mRibbonBackup = null;

        for (int i = 0; i < mRibbon.length; ++i) mRibbon[i] = '0';
        notifyDataSetChanged();
    }

    public int searchSign(int direction)
    {
        int searchPos = -1;

        if (direction == 0)
        {
            for (int i = mSelected - 5; i >= 0; --i)
            {
                if (mRibbon[i] == '1')
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
                if (mRibbon[i] == '1')
                {
                    searchPos = mSelected = i;
                    break;
                }
            }
        }

        return searchPos;
    }
}