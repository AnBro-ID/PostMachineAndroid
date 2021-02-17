package ru.anbroid.postmachine;

import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

class OpenFile extends SaveFile
{
    boolean mode;
    String taskDesc;
    ArrayList<PostCode> arrayList;
    char[] ribbon;
    int position;

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
            dialog.dismiss();

        if (!success)
        {
            Toast.makeText(activity.get(), R.string.access_error, Toast.LENGTH_LONG).show();
            activity.get().resetState();
        }
        else
        {
            MainActivity.isTriple = mode;
            activity.get().Task = taskDesc;
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(activity.get()).edit();
            int mode_int = mode ? 1 : 0;

            editor.putString("alphabet_list", Integer.toString(mode_int));
            editor.commit();

            if (mode) activity.get().setTripleUI();
            else activity.get().setBinaryUI();

            activity.get().pAdapter.pc = arrayList;
            activity.get().rAdapter.setRibbon(ribbon);
            activity.get().rAdapter.setSelectedPosition(position);

            activity.get().rAdapter.backupRibbon();
            activity.get().pAdapter.notifyDataSetChanged();
            activity.get().recyclerView.scrollToPosition(activity.get().rAdapter.getSelectedPosition());
        }

        activity.get().unlockScreenOrientation();
    }

    protected Boolean doInBackground(Void... args)
    {
        DataInputStream in = null;

        int Length;

        try
        {
            File file = new File(activity.get().getExternalFilesDir(null).toString(), fileName);
            in = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(file)));

            mode = in.readBoolean();

            Length = in.readBoolean() ? 1 : 0;

            if (Length == 1)
                taskDesc = in.readUTF();

            Length = in.readInt();
            arrayList = new ArrayList<>(Length);

            for (int i = 0; i < Length; ++i)
            {
                char command = in.readChar();
                String goTo = in.readUTF();
                String comment = in.readUTF();

                if (mode)
                    arrayList.add(new PostCodeTriple(command, goTo, comment));
                else
                    arrayList.add(new PostCode(command, goTo, comment));
            }

            position = in.readInt();
            ribbon = new char[100];

            for (int i = 0; i < ribbon.length; ++i)
                ribbon[i] = in.readChar();
        }
        catch (IOException e)
        {
            return false;
        }
        finally
        {
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
