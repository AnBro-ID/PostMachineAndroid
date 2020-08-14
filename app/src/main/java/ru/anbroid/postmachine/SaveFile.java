package ru.anbroid.postmachine;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class SaveFile extends AsyncTask<Void, Void, Boolean>
{
    private ProgressDialog dialog;
    private WeakReference<MainActivity> activity;
    private String fileName;

    public SaveFile(MainActivity myApp, String filename)
    {
        activity = new WeakReference<>(myApp);
        fileName = filename;
        dialog = new ProgressDialog(activity.get());
    }

    protected void onPreExecute()
    {
        activity.get().lockScreenOrientation();
        this.dialog.setMessage(activity.get().getResources().getString(R.string.saving_file));
        this.dialog.show();
    }

    @Override
    protected void onPostExecute(Boolean success)
    {
        if (dialog.isShowing())
        {
            dialog.dismiss();
        }

        if (success) {
            Toast.makeText(activity.get(), activity.get().getString(R.string.save_file_succ) + ' ' +
                    Environment.getExternalStorageDirectory().toString() + '/' + fileName, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity.get(), R.string.access_error, Toast.LENGTH_LONG).show();
        }

        activity.get().unlockScreenOrientation();
    }

    protected Boolean doInBackground(Void... args)
    {
        DataOutputStream dos = null;

        try
        {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            dos = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(file)));

            dos.writeBoolean(false);

            if (activity.get().Task != null)
            {
                dos.writeBoolean(true);
                dos.writeUTF(activity.get().Task);
            } else dos.writeBoolean(false);

            dos.writeInt(activity.get().pAdapter.pc.size());

            for (int i = 0; i < activity.get().pAdapter.pc.size(); ++i)
            {
                PostCode pst = activity.get().pAdapter.pc.get(i);

                dos.writeChar(pst.command);
                dos.writeUTF(pst.getGoto());
                dos.writeUTF(pst.comment);
            }

            dos.writeInt(activity.get().rAdapter.getSelectedPosition());

            char[] ribbon = activity.get().rAdapter.getRibbon();

            for (char c : ribbon) dos.writeChar(c);
        } catch (IOException e)
        {
            return false;
        } finally
        {
            if (dos != null)
                try
                {
                    dos.close();
                } catch (IOException logOrIgnore)
                {
                    return false;
                }
        }
        return true;
    }
}

