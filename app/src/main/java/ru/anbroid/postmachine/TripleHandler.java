package ru.anbroid.postmachine;

import android.os.Message;

class TripleHandler extends BinaryHandler
{
    public TripleHandler(MainActivity myApp)
    {
        super(myApp);
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

                case 'X':

                    if (myApp.rAdapter.getItem(myApp.rAdapter.getSelectedPosition()) == ' ')
                    {
                        myApp.stop();
                        myApp.showError();
                    }
                    else
                    {
                        myApp.rAdapter.setItem(myApp.rAdapter.getSelectedPosition(), ' ');

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
                        if (myApp.rAdapter.getItem(myApp.rAdapter.getSelectedPosition()) == ' ')
                            myApp.pAdapter.exec_line = myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).getConcatGotoByInt()[0] - 1;
                        else if (myApp.rAdapter.getItem(myApp.rAdapter.getSelectedPosition()) == '0')
                            myApp.pAdapter.exec_line = myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).getConcatGotoByInt()[1] - 1;
                        else
                            myApp.pAdapter.exec_line = myApp.pAdapter.pc.get(myApp.pAdapter.exec_line).getConcatGotoByInt()[2] - 1;

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
