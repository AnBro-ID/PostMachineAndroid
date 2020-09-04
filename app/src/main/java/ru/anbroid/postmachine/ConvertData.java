package ru.anbroid.postmachine;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ConvertData extends AsyncTask<Void, Void, Void>
{
    protected AlertDialog dialog;
    protected WeakReference<MainActivity> activity;
    protected boolean direction;

    ArrayList<PostCode> arrayList;
    char[] ribbon;
    char[] ribbonBackup;
    int current_line;
    int exec_line;
    boolean isSelected;
    int mSelected;
    int mSaved;

    public ConvertData(MainActivity myApp, boolean direction)
    {
        activity = new WeakReference<>(myApp);
        this.direction = direction;
    }

    protected void onPreExecute()
    {
        activity.get().lockScreenOrientation();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity.get());

        View view = activity.get().getLayoutInflater().inflate(R.layout.progress_indicator, null);
        TextView textView = view.findViewById(R.id.progressTitle);

        textView.setText(activity.get().getString(R.string.converting));
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();

        mSaved = activity.get().rAdapter.mSaved;
        mSelected = activity.get().rAdapter.mSelected;
        current_line = activity.get().pAdapter.current_line;
        exec_line = activity.get().pAdapter.exec_line;
        isSelected = activity.get().pAdapter.isSelected;
        ribbon = activity.get().rAdapter.mRibbon;
        ribbonBackup = activity.get().rAdapter.mRibbonBackup;
    }

    @Override
    protected void onPostExecute(Void nothing)
    {
        if (dialog.isShowing())
            dialog.dismiss();

        if (MainActivity.isTriple) activity.get().setTripleUI();
        else activity.get().setBinaryUI();

        activity.get().pAdapter.pc = arrayList;
        activity.get().pAdapter.current_line = current_line;
        activity.get().pAdapter.isSelected = isSelected;
        activity.get().pAdapter.exec_line = exec_line;
        activity.get().rAdapter.setRibbon(ribbon);
        activity.get().rAdapter.setBackupRibbon(ribbonBackup);
        activity.get().rAdapter.setSelectedPosition(mSelected);
        activity.get().rAdapter.mSaved = mSaved;

        activity.get().pAdapter.notifyDataSetChanged();
        activity.get().recyclerView.scrollToPosition(activity.get().rAdapter.getSelectedPosition());

        activity.get().unlockScreenOrientation();
    }

    protected Void doInBackground(Void... args)
    {
        arrayList = new ArrayList<PostCode>();

        if (direction)
        {
            for (int i = 0; i < activity.get().pAdapter.getCount(); ++i)
                arrayList.add(new PostCodeTriple(activity.get().pAdapter.pc.get(i)));
        }
        else
        {
            for (int i = 0; i < activity.get().pAdapter.getCount(); ++i)
                arrayList.add(new PostCode((PostCodeTriple)activity.get().pAdapter.pc.get(i)));

            for (int i = 0; i < ribbon.length; ++i)
            {
                if (ribbon[i] == ' ')
                    ribbon[i] = '0';

                if (ribbonBackup != null && ribbonBackup[i] == ' ')
                    ribbonBackup[i] = '0';
            }
        }
        return null;
    }
}
