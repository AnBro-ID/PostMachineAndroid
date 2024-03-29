package ru.anbroid.postmachine;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.File;
import java.io.FilenameFilter;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author AnBro-ID, 2018
 * Главная активность приложения
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RibbonAdapter.ItemClickListener
{
    protected PostAdapter pAdapter;         // адаптер для списка

    protected RecyclerView recyclerView;    // лента МП
    protected RibbonAdapter rAdapter;
    private AsyncTask currentTask;

    private boolean isPlay;                 // флаг состояния МП - работает
    private boolean isPaused;               // флаг состояния МП - приостановлена
    private boolean isPlayBySteps;
    protected static boolean isTriple;
    private volatile boolean isDialogShow;
    protected static int speed;             // скорость выполнения

    protected String Task;                  // условие задачи
    private String ChosenFile;              // открытый файл

    protected Handler handler;              // обработчик сообщений
    private Thread thread;                  // поток выполнения МП

    private ImageButton playBtn;            // кнопка для начала выполнения программы МП

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initUI();
    }

    protected void setTripleUI()
    {
        pAdapter = new TriplePostAdapter(this);
        rAdapter = new RibbonAdapterTriple(this);
        handler = new TripleHandler(this);

        ListView lvMain = findViewById(R.id.lvMain);
        lvMain.setAdapter(pAdapter);

        recyclerView.setAdapter(rAdapter);
        RibbonLayoutManager rlm = (RibbonLayoutManager) recyclerView.getLayoutManager();
        rlm.setOnItemSelectedListener(rAdapter);
        rAdapter.setClickListener(this);
    }

    protected void setBinaryUI()
    {
        pAdapter = new PostAdapter(this);
        rAdapter = new RibbonAdapter(this);
        handler = new BinaryHandler(this);

        ListView lvMain = findViewById(R.id.lvMain);
        lvMain.setAdapter(pAdapter);

        recyclerView.setAdapter(rAdapter);
        RibbonLayoutManager rlm = (RibbonLayoutManager) recyclerView.getLayoutManager();
        rlm.setOnItemSelectedListener(rAdapter);
        rAdapter.setClickListener(this);
    }

    protected void initUI()
    {
        isPlay = false;
        isPaused = false;
        isPlayBySteps = false;
        isDialogShow = false;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        speed = Integer.parseInt(sp.getString("speed_list", "500"));
        isTriple = Integer.parseInt(sp.getString("alphabet_list", "0")) > 0;

        if (isTriple)
        {
            pAdapter = new TriplePostAdapter(this);
            rAdapter = new RibbonAdapterTriple(this);
            handler = new TripleHandler(this);
        }
        else
        {
            pAdapter = new PostAdapter(this);
            rAdapter = new RibbonAdapter(this);
            handler = new BinaryHandler(this);
        }

        ListView lvMain = findViewById(R.id.lvMain);
        lvMain.setAdapter(pAdapter);

        RibbonLayoutManager ribbonLayoutManager = new RibbonLayoutManager(this, this.getResources().getInteger(R.integer.ribbon_items));

        recyclerView = findViewById(R.id.Ribbon);
        recyclerView.setAdapter(rAdapter);
        recyclerView.setLayoutManager(ribbonLayoutManager);

        ribbonLayoutManager.setOnItemSelectedListener(rAdapter);
        rAdapter.setClickListener(this);

        RibbonItemDecoration itemDecorator = new RibbonItemDecoration(this, ribbonLayoutManager.getOrientation());
        itemDecorator.setDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.divider, null));
        recyclerView.addItemDecoration(itemDecorator);

        int screenWidth = ScreenUtils.getScreenWidth(this);
        int padding = (screenWidth - screenWidth / ribbonLayoutManager.getItemsPerPage()) / 2;
        recyclerView.setPadding(padding, 0, padding, 0);
        recyclerView.setItemAnimator(null);

        findViewById(R.id.add_line_up).setOnClickListener(this);
        findViewById(R.id.add_line_down).setOnClickListener(this);
        findViewById(R.id.delete_line).setOnClickListener(this);
        findViewById(R.id.right_until_btn).setOnClickListener(this);
        findViewById(R.id.stop_btn).setOnClickListener(this);
        findViewById(R.id.left_until_btn).setOnClickListener(this);
        findViewById(R.id.restore_ribbon).setOnClickListener(this);
        findViewById(R.id.backup_ribbon).setOnClickListener(this);
        findViewById(R.id.create_btn).setOnClickListener(this);
        findViewById(R.id.open_btn).setOnClickListener(this);
        findViewById(R.id.save_btn).setOnClickListener(this);
        findViewById(R.id.save_btn).setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                showSaveDialog(true);
                return true;
            }
        });
        findViewById(R.id.step_btn).setOnClickListener(this);

        playBtn = findViewById(R.id.play_btn);
        playBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_settings:
            {
                Intent intent = new Intent(this, PrefActivity.class);
                startActivity(intent);
            }

            break;

            case R.id.menu_help:
            {
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
            }

            break;

            case R.id.menu_steps:
            {
                if (isPlayBySteps || isPlay || isPaused) stop();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = getLayoutInflater().inflate(R.layout.steps_exec, null);
                final EditText lineNumber = view.findViewById(R.id.editText_steps);

                builder.setTitle(R.string.steps_menu);
                builder.setView(view);

                builder.setPositiveButton(R.string.ok, null);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });

                AlertDialog alert = builder.create();

                alert.setOnShowListener(new DialogInterface.OnShowListener()
                {
                    @Override
                    public void onShow(final DialogInterface dialog)
                    {
                        Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                int numLine;

                                try
                                {
                                    numLine = Integer.parseInt(lineNumber.getText().toString());
                                }
                                catch (NumberFormatException e)
                                {
                                    numLine = 0;
                                }

                                if (numLine > 0 && numLine <= pAdapter.getCount())
                                {
                                    dialog.dismiss();
                                    startBySteps(--numLine);
                                }
                                else lineNumber.setText(null);
                            }
                        });
                    }
                });

                alert.show();
            }

            break;

            case R.id.menu_task:
            {
                Intent intent = new Intent(this, TaskActivity.class);
                intent.putExtra("task", Task);
                startActivityForResult(intent, 1);
            }

            break;

            case R.id.menu_about:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = getLayoutInflater().inflate(R.layout.about_dialog, null);
                TextView about_text = view.findViewById(R.id.about);
                String about_string = getString(R.string.about_desc);
                about_string += "\n© AnBro-ID\nE-mail: andrey-mail-mail@inbox.ru\n";

                try
                {
                    PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);

                    about_string += getString(R.string.about_version) + ' ' + pInfo.versionName;
                }
                catch (PackageManager.NameNotFoundException e)
                {
                    e.printStackTrace();
                }

                builder.setTitle(R.string.menu_about);
                about_text.setText(about_string);
                builder.setView(view);

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.add_line_up:

                if (pAdapter.isSelected)
                {
                    pAdapter.addObj(pAdapter.current_line);
                    pAdapter.pc.trimToSize();
                    pAdapter.notifyDataSetChanged();
                }

                break;

            case R.id.add_line_down:

                if (pAdapter.isSelected)
                {
                    pAdapter.addObj(pAdapter.current_line + 1);
                    pAdapter.pc.trimToSize();
                    pAdapter.notifyDataSetChanged();
                }

                break;

            case R.id.delete_line:

                if (pAdapter.pc.size() > 1 && pAdapter.isSelected)
                {
                    if (pAdapter.pc.get(pAdapter.current_line).hasText())
                    {
                        if (isDialogShow) return;
                        isDialogShow = true;

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);

                        builder.setTitle(R.string.line_delete_head);
                        builder.setMessage(R.string.line_delete_confirm);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                pAdapter.pc.remove(pAdapter.current_line);
                                pAdapter.pc.trimToSize();

                                if (pAdapter.current_line == pAdapter.pc.size())
                                    pAdapter.current_line--;

                                pAdapter.notifyDataSetChanged();
                            }
                        });

                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { }
                        });

                        builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface)
                            {
                                isDialogShow = false;
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else
                    {
                        pAdapter.pc.remove(pAdapter.current_line);
                        pAdapter.pc.trimToSize();

                        if (pAdapter.current_line == pAdapter.pc.size()) pAdapter.current_line--;

                        pAdapter.notifyDataSetChanged();
                    }
                }

                break;

            case R.id.play_btn:

                if (isPlay) pause();
                else if (!isPlayBySteps) start();

                break;

            case R.id.stop_btn: stop(); break;
            case R.id.right_until_btn:
            {
                int pos = rAdapter.searchSign(1);

                if (pos != -1) recyclerView.scrollToPosition(pos);

                break;
            }
            case R.id.left_until_btn:
            {
                int pos = rAdapter.searchSign(0);

                if (pos != -1) recyclerView.scrollToPosition(pos);

                break;
            }
            case R.id.restore_ribbon:

                rAdapter.restoreRibbon();
                recyclerView.scrollToPosition(rAdapter.getSelectedPosition());

                break;

            case R.id.backup_ribbon: rAdapter.backupRibbon(); break;
            case R.id.create_btn: newFile(); break;
            case R.id.open_btn: showOpenDialog(); break;
            case R.id.save_btn: showSaveDialog(false); break;
            case R.id.step_btn: if (!(isPlay || isPaused)) startBySteps(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) Task = data.getStringExtra("task");
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        boolean newValue = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("alphabet_list", "0")) > 0;

        if (newValue && !MainActivity.isTriple)
            currentTask = new ConvertData(this, true).execute();
        else if (MainActivity.isTriple && !newValue)
            currentTask = new ConvertData(this, false).execute();

        MainActivity.isTriple = newValue;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (isPlay) pause();
    }

    /**
     * Метод, инициирующий выполнение программы МП
     */

    private void start()
    {
        lock();
        playBtn.setImageResource(R.drawable.pause);

        isPlay = true;

        thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Thread.sleep(speed);
                    }
                    catch (InterruptedException e)
                    {
                        break;
                    }

                    handler.sendEmptyMessage(0);
                }
            }
        });

        if (!isPaused) pAdapter.exec_line = 0;

        isPaused = false;

        pAdapter.notifyDataSetChanged();
        thread.start();
    }

    private void startBySteps(int lineNum)
    {
        isPlayBySteps = true;

        if (thread == null)
        {
            pAdapter.exec_line = lineNum;

            thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    handler.sendEmptyMessage(0);
                }
            });
        }
        else thread.run();

        pAdapter.notifyDataSetChanged();
    }

    /**
     * Метод, останавливающий выполнение программы МП
     */

    protected void stop()
    {
        if (thread != null)
        {
            isPlay = false;
            isPaused = false;
            isPlayBySteps = false;

            playBtn.setImageResource(R.drawable.play);

            thread.interrupt();
            thread = null;

            handler.removeCallbacksAndMessages(null);

            pAdapter.exec_line = -1;
            pAdapter.notifyDataSetChanged();
            unlock();
        }
    }

    /**
     * Метод, приостанавливающий выполнение программы МП
     */

    protected void pause()
    {
        playBtn.setImageResource(R.drawable.play);

        isPlay = false;
        isPaused = true;

        handler.removeCallbacksAndMessages(null);
        thread.interrupt();
    }

    /**
     * Метод показа ошибки выполнения
     */

    protected void showError()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.exec_error_head);
        builder.setMessage(R.string.exec_error_desc);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Метод показа сообщения об остановке выполнения
     */

    protected void showStopMessage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.stop_head);
        builder.setMessage(R.string.stop_desc);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed()
    {
        saveFileConfirm();
    }

    /**
     * Метод предупреждения об несохраненных изменениях
     */

    private void saveFileConfirm()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.close_head);
        builder.setMessage(R.string.close_confirm);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) { finish(); }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Метод, показывающий диалог открытия файла
     */

    private void showOpenDialog()
    {
        if (!StorageUtils.checkStoragePermission(this)) return;

        final String[] mFileList;
        File mPath = new File(getExternalFilesDir(null).toString());

        FilenameFilter filter = new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String filename)
            {
                File file = new File(dir, filename);

                return file.isFile() && file.getName().endsWith(".pme");
            }
        };

        mFileList = mPath.list(filter);

        if (mFileList.length == 0)
                Toast.makeText(this, getString(R.string.no_file) + ' ' +
                        getExternalFilesDir(null).toString(), Toast.LENGTH_LONG).show();
        else
        {
            if (isPlay || isPaused || isPlayBySteps) stop();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(getString(R.string.open_file_head));

            builder.setItems(mFileList, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    ChosenFile = mFileList[which];
                    currentTask = new OpenFile(MainActivity.this, ChosenFile).execute();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * Метод, показывающий диалог сохранения файла
     * @param saveAs - тип диалога
     */

    private void showSaveDialog(boolean saveAs)
    {
        if (!StorageUtils.checkStoragePermission(this)) return;

        if (isPlay || isPaused || isPlayBySteps) stop();

        if (ChosenFile == null || saveAs)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.filesave_dialog, null);
            final EditText editText = view.findViewById(R.id.file_name);

            builder.setTitle(R.string.save_as);
            builder.setView(view);
            builder.setPositiveButton(R.string.ok, null);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });

            AlertDialog alert = builder.create();

            alert.setOnShowListener(new DialogInterface.OnShowListener()
            {
                @Override
                public void onShow(final DialogInterface dialog)
                {
                    Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            String filename = editText.getText().toString();

                            if (!filename.isEmpty())
                            {
                                ChosenFile = filename + ".pme";
                                currentTask = new SaveFile(MainActivity.this, ChosenFile).execute();
                                dialog.dismiss();
                            }
                        }
                    });
                }
            });

            alert.show();
        }
        else currentTask = new SaveFile(MainActivity.this, ChosenFile).execute();
    }

    /**
     * Метод создания новой программы МП
     */

    private void newFile()
    {
        if (isPlay || isPaused || isPlayBySteps) stop();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.close_head);
        builder.setMessage(R.string.close_confirm);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                resetState();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void resetState()
    {
        rAdapter.resetAdapter();
        recyclerView.scrollToPosition(rAdapter.getSelectedPosition());

        Task = null;
        ChosenFile = null;

        pAdapter.resetAdapter();
        pAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position)
    {
        if (rAdapter.getSelectedPosition() == position)
        {
            char symbol = rAdapter.getItem(position);

            switch (symbol)
            {
                case '0': rAdapter.setItem(position, '1'); break;
                case '1':
                    if (isTriple) rAdapter.setItem(position, ' ');
                    else rAdapter.setItem(position, '0');
                    break;
                case ' ': default: rAdapter.setItem(position, '0');
            }
        }
        else recyclerView.smoothScrollToPosition(position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED)
            Toast.makeText(this, R.string.access_error, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState)
    {
        super.onRestoreInstanceState(inState);

        isPlay = inState.getBoolean("isPlay");
        isPlayBySteps = inState.getBoolean("isPlayBySteps");
        isPaused = inState.getBoolean("isPaused");
        ChosenFile = inState.getString("file");
        Task = inState.getString("task");

        rAdapter.setSaved(inState.getInt("saved"));
        rAdapter.setSelectedPosition(inState.getInt("selected"));
        rAdapter.setRibbon(inState.getCharArray("ribbon"));
        rAdapter.setBackupRibbon(inState.getCharArray("ribbon_backup"));
        pAdapter.current_line = inState.getInt("current_line");
        pAdapter.exec_line = inState.getInt("exec_line");
        pAdapter.isSelected = inState.getBoolean("isSelected");

        pAdapter.pc.clear();

        int size = inState.getInt("size");

        for (int i = 0; i < size; ++i)
            pAdapter.addObj(inState.getSerializable(Integer.toString(i)));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        stop();

        outState.putBoolean("isPlay", isPlay);
        outState.putBoolean("isPlayBySteps", isPlayBySteps);
        outState.putBoolean("isPaused", isPaused);
        outState.putString("file", ChosenFile);
        outState.putString("task", Task);

        outState.putInt("saved", rAdapter.getSaved());
        outState.putInt("selected", rAdapter.getSelectedPosition());
        outState.putCharArray("ribbon", rAdapter.getRibbon());
        outState.putCharArray("ribbon_backup", rAdapter.getBackupRibbon());
        outState.putInt("current_line", pAdapter.current_line);
        outState.putInt("exec_line", pAdapter.exec_line);
        outState.putBoolean("isSelected", pAdapter.isSelected);
        outState.putInt("size", pAdapter.getCount());

        int size = pAdapter.getCount();

        for (int i = 0; i < size; ++i)
            outState.putSerializable(Integer.toString(i), pAdapter.pc.get(i));

        super.onSaveInstanceState(outState);
    }

    protected void lockScreenOrientation()
    {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    protected void unlockScreenOrientation()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }

    public void lock()
    {
        RibbonLayoutManager rm = (RibbonLayoutManager) recyclerView.getLayoutManager();
        rm.setScrollEnabled(false);
        rAdapter.setClickListener(null);
        pAdapter.lock();
        findViewById(R.id.add_line_up).setOnClickListener(null);
        findViewById(R.id.add_line_down).setOnClickListener(null);
        findViewById(R.id.delete_line).setOnClickListener(null);
        findViewById(R.id.right_until_btn).setOnClickListener(null);
        findViewById(R.id.left_until_btn).setOnClickListener(null);
        findViewById(R.id.restore_ribbon).setOnClickListener(null);
        findViewById(R.id.backup_ribbon).setOnClickListener(null);
    }

    public void unlock()
    {
        RibbonLayoutManager rm = (RibbonLayoutManager) recyclerView.getLayoutManager();
        rm.setScrollEnabled(true);
        rAdapter.setClickListener(this);
        pAdapter.unlock();
        findViewById(R.id.add_line_up).setOnClickListener(this);
        findViewById(R.id.add_line_down).setOnClickListener(this);
        findViewById(R.id.delete_line).setOnClickListener(this);
        findViewById(R.id.right_until_btn).setOnClickListener(this);
        findViewById(R.id.left_until_btn).setOnClickListener(this);
        findViewById(R.id.restore_ribbon).setOnClickListener(this);
        findViewById(R.id.backup_ribbon).setOnClickListener(this);
    }
}
