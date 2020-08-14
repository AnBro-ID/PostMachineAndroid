package ru.anbroid.postmachine;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

class BinaryHandler extends Handler
{
    protected WeakReference<MainActivity> MainActivityRef;

    public BinaryHandler(MainActivity myApp) {
        MainActivityRef = new WeakReference<>(myApp);
    }

    @Override
    public void handleMessage(Message msg) {
        MainActivity myApp = MainActivityRef.get();

        if (myApp.pAdapter.exec_line < myApp.pAdapter.pc.size() && MainActivityRef != null)
        {
            switch (myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).command)
            {
                case '>':

                    if (myApp.rAdapter.getSelectedPosition() == myApp.rAdapter.getRibbon().length - 1)
                    {
                        myApp.stop();
                        myApp.showError();
                    }
                    else
                    {
                        myApp.recyclerView.scrollToPosition(myApp.rAdapter.getSelectedPosition() + 1);
                        myApp.rAdapter.setSelectedPosition(myApp.rAdapter.getSelectedPosition() + 1);

                        if (myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).isGotoEmpty()) myApp.pAdapter.exec_line++;
                        else myApp.pAdapter.exec_line = myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).getGotoByInt() - 1;

                        myApp.pAdapter.notifyDataSetChanged();
                    }

                    break;

                case '<':

                    if (myApp.rAdapter.getSelectedPosition() == 0)
                    {
                        myApp.stop();
                        myApp.showError();
                    }
                    else
                    {
                        myApp.recyclerView.scrollToPosition(myApp.rAdapter.getSelectedPosition() - 1);
                        myApp.rAdapter.setSelectedPosition(myApp.rAdapter.getSelectedPosition() - 1);

                        if (myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).isGotoEmpty()) myApp.pAdapter.exec_line++;
                        else myApp.pAdapter.exec_line = myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).getGotoByInt() - 1;

                        myApp.pAdapter.notifyDataSetChanged();
                    }

                    break;

                case '0':

                    if (myApp.rAdapter.getItem(myApp.rAdapter.getSelectedPosition()) == '0')
                    {
                        myApp.stop();
                        myApp.showError();
                    }
                    else
                    {
                        myApp.rAdapter.setItem(myApp.rAdapter.getSelectedPosition(), '0');

                        if (myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).isGotoEmpty()) myApp.pAdapter.exec_line++;
                        else myApp.pAdapter.exec_line = myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).getGotoByInt() - 1;

                        myApp.pAdapter.notifyDataSetChanged();
                    }

                    break;

                case '1':

                    if (myApp.rAdapter.getItem(myApp.rAdapter.getSelectedPosition()) == '1')
                    {
                        myApp.stop();
                        myApp.showError();
                    }
                    else
                    {
                        myApp.rAdapter.setItem(myApp.rAdapter.getSelectedPosition(), '1');

                        if (myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).isGotoEmpty()) myApp.pAdapter.exec_line++;
                        else myApp.pAdapter.exec_line = myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).getGotoByInt() - 1;

                        myApp.pAdapter.notifyDataSetChanged();
                    }

                    break;

                case '?':

                    if (myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).isGotoEmpty())
                    {
                        myApp.stop();
                        myApp.showError();
                    }
                    else
                    {
                        if (myApp.rAdapter.getItem(myApp.rAdapter.getSelectedPosition()) == '0')
                            myApp.pAdapter.exec_line = myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).getConcatGotoByInt()[0] - 1;
                        else
                            myApp.pAdapter.exec_line = myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).getConcatGotoByInt()[1] - 1;

                        myApp.pAdapter.notifyDataSetChanged();
                    }

                    break;

                case '.':

                    myApp.stop();
                    myApp.showStopMessage();

                    break;

                default:

                    myApp.stop();
                    myApp.showError();
            }
        }
        else
        {
            myApp.stop();
            myApp.showError();
        }
    }
}
