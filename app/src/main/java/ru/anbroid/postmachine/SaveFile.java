package ru.anbroid.postmachine;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class SaveFile extends AsyncTask<Void, Void, Boolean>
{
    protected AlertDialog dialog;
    protected WeakReference<MainActivity> activity;
    protected String fileName;

    public SaveFile(MainActivity myApp, String filename)
    {
        activity = new WeakReference<>(myApp);
        fileName = filename;
    }

    protected void onPreExecute()
    {
        activity.get().lockScreenOrientation();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity.get());

        View view = activity.get().getLayoutInflater().inflate(R.layout.progress_indicator, null);
        TextView textView = view.findViewById(R.id.progressTitle);

        textView.setText(activity.get().getString(R.string.saving_file));
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onPostExecute(Boolean success)
    {
        if (dialog.isShowing())
        {
            dialog.dismiss();
        }

        if (success)
        {
            Toast.makeText(activity.get(), activity.get().getString(R.string.save_file_succ) + ' ' +
                    activity.get().getExternalFilesDir(null).toString() + '/' + fileName, Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(activity.get(), R.string.access_error, Toast.LENGTH_LONG).show();

        activity.get().unlockScreenOrientation();
    }

    protected Boolean doInBackground(Void... args)
    {
        DataOutputStream dos = null;

        try
        {
            File file = new File(activity.get().getExternalFilesDir(null).toString(), fileName);
            dos = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(file)));

            dos.writeBoolean(MainActivity.isTriple);

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
                    logOrIgnore.printStackTrace();
                }
        }
        return true;
    }
}

