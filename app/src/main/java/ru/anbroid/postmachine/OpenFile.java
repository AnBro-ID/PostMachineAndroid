package ru.anbroid.postmachine;

import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

class OpenFile extends SaveFile
{
    public OpenFile(MainActivity myApp, String filename)
    {
        super(myApp, filename);
    }

    protected void onPreExecute()
    {
        activity.get().lockScreenOrientation();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity.get());

        View view = activity.get().getLayoutInflater().inflate(R.layout.progress_indicator, null);
        TextView textView = view.findViewById(R.id.progressTitle);

        textView.setText(activity.get().getString(R.string.opening_file));
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

        if (!success)
        {
            Toast.makeText(activity.get(), R.string.access_error, Toast.LENGTH_LONG).show();
        }
        else
        {
            activity.get().resetState();
        }

        activity.get().unlockScreenOrientation();
    }

    protected Boolean doInBackground(Void... args)
    {
        DataInputStream in = null;

        int Length;

        try
        {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            in = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(file)));

            MainActivity.isTriple = in.readBoolean();

            Length = in.readBoolean() ? 1 : 0;

            if (Length == 1)
                activity.get().Task = in.readUTF();

            Length = in.readInt();
            activity.get().pAdapter.pc.clear();

            for (int i = 0; i < Length; ++i)
            {
                char command = in.readChar();
                String goTo = in.readUTF();
                String comment = in.readUTF();

                activity.get().pAdapter.pc.add(new PostCode(command, goTo, comment));
            }

            Length = in.readInt();
            activity.get().rAdapter.setSelectedPosition(Length);

            char[] ribbon = new char[100];

            for (int i = 0; i < ribbon.length; ++i)
                ribbon[i] = in.readChar();

            activity.get().rAdapter.setRibbon(ribbon);
        }
        catch (IOException e)
        {
            return false;
        }
        finally
        {
            activity.get().rAdapter.backupRibbon();
            activity.get().pAdapter.notifyDataSetChanged();
            activity.get().recyclerView.scrollToPosition(activity.get().rAdapter.getSelectedPosition());

            if (in != null)
                try
                {
                    in.close();
                }
                catch (IOException logOrIgnore)
                {
                    logOrIgnore.printStackTrace();
                }
        }
        return true;
    }
}
