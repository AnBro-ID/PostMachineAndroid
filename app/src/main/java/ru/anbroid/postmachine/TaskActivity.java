package ru.anbroid.postmachine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

/**
* @author AnBro-ID, 2018
* Активность для ввода условия задачи
*/

public class TaskActivity extends AppCompatActivity
{
    private boolean isChanged;      // флаг состояния строки
    private EditText editTask;      // компонент для ввода условия задачи

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        isChanged = false;
        Toolbar toolbar = findViewById(R.id.toolbar_task);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) { saveConfirm(); }
        });

        editTask = findViewById(R.id.task);
        editTask.setText(getIntent().getStringExtra("task"));
    }

    @Override
    public void onBackPressed() { saveConfirm(); }

    /**
     * Диалог для подтверждения изменений
     * при закрытии активности
     */

    private void saveConfirm()
    {
        if (isChanged)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.save_head);
            builder.setMessage(R.string.save_confirm);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Intent intent = new Intent();

                    intent.putExtra("task", editTask.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });

            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
        else finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putString("text", editTask.getText().toString());
        outState.putBoolean("state", isChanged);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState)
    {
        super.onRestoreInstanceState(inState);

        isChanged = inState.getBoolean("state");
        editTask.setText(inState.getString("text"));
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        editTask.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s) { isChanged = true; }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }
}
