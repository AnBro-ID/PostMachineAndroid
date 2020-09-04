package ru.anbroid.postmachine;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author AnBro-ID, 2018
 * Модифицированный адаптер для списка строк программы МП
 */

public class PostAdapter extends ArrayAdapter<PostCode>
{
    public ArrayList<PostCode> pc;   // списочный массив строк программы МП

    public int current_line;                // номер выбранной строки
    public int exec_line;                   // номер выполняемой строки
    public boolean isSelected;              // флаг состояния строки

    /**
     * Класс, реализующий шаблон проектирования Singleton
     */

    static class ViewHolder
    {
        public TextView Count;      // компонент для вывода порядкового номера строки
        public EditText Command;    // компонент для ввода команды
        public EditText Goto;       // компонент для ввода перехода
        public EditText Comment;    // компонент для ввода комментария
    }

    /**
     * Конструктор класса
     * @param context - активность приложения
     */

    public PostAdapter(Context context)
    {
        super(context, R.layout.command_list);

        pc = new ArrayList<>(1);
        pc.add(new PostCode());

        current_line = -1;
        exec_line = -1;
        isSelected = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;

        if (rowView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            rowView = inflater.inflate(R.layout.command_list, parent, false);

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.Count = rowView.findViewById(R.id.Count);
            viewHolder.Command = rowView.findViewById(R.id.Command);
            viewHolder.Goto = rowView.findViewById(R.id.Goto);
            viewHolder.Comment = rowView.findViewById(R.id.Comment);

            View.OnClickListener OCL = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    switch (v.getId())
                    {
                        case R.id.Count:

                            if (isSelected && current_line == (int) v.getTag())
                            {
                                isSelected = false;
                                current_line = -1;
                                notifyDataSetChanged();
                            }
                            else
                            {
                                isSelected = true;
                                current_line = (int) v.getTag();
                                notifyDataSetChanged();
                            }

                            break;

                        case R.id.Command:
                        {
                            final int pos = (int) v.getTag();
                            final String[] commands = getContext().getResources().getStringArray(R.array.post_command);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                            builder.setTitle(R.string.command_text);
                            builder.setItems(R.array.post_command, new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    if (commands[id].charAt(0) == '?')
                                    {
                                        if (!pc.get(pos).isGotoEmpty() && !pc.get(pos).isGotoConcatenated()) pc.get(pos).concatGoto();

                                        pc.get(pos).command = commands[id].charAt(0);
                                        notifyDataSetChanged();
                                    }
                                    else if (id != commands.length - 1)
                                    {
                                        if (!pc.get(pos).isGotoEmpty()) pc.get(pos).splitGoto();

                                        pc.get(pos).command = commands[id].charAt(0);
                                        notifyDataSetChanged();
                                    }
                                    else
                                    {
                                        pc.get(pos).command = '\0';
                                        notifyDataSetChanged();
                                    }
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                        break;

                        case R.id.Goto:
                        {
                            final int pos = (int) v.getTag();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            View view;

                            if (pc.get(pos).command == '?')
                            {
                                view = LayoutInflater.from(getContext()).inflate(R.layout.goto_dialog_adv, null);
                                final NumberPicker numpick1 = view.findViewById(R.id.numberPicker1);
                                final NumberPicker numpick2 = view.findViewById(R.id.numberPicker2);

                                numpick1.setMaxValue(pc.size());
                                numpick1.setMinValue(1);
                                numpick2.setMaxValue(pc.size());
                                numpick2.setMinValue(1);

                                if (!pc.get(pos).isGotoEmpty())
                                {
                                    if (!pc.get(pos).isGotoConcatenated()) pc.get(pos).concatGoto();

                                    numpick1.setValue(pc.get(pos).getConcatGotoByInt()[0]);
                                    numpick2.setValue(pc.get(pos).getConcatGotoByInt()[1]);
                                }

                                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        pc.get(pos).setConcatGotoByInt(numpick1.getValue(), numpick2.getValue());
                                        notifyDataSetChanged();
                                    }
                                });
                            }
                            else
                            {
                                view = LayoutInflater.from(getContext()).inflate(R.layout.goto_dialog, null);
                                final NumberPicker numpick = view.findViewById(R.id.numberPicker);

                                numpick.setMaxValue(pc.size());
                                numpick.setMinValue(1);

                                if (!pc.get(pos).isGotoEmpty())
                                {
                                    if (pc.get(pos).isGotoConcatenated()) pc.get(pos).splitGoto();

                                    numpick.setValue(pc.get(pos).getGotoByInt());
                                }

                                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        pc.get(pos).setGotoByInt(numpick.getValue());
                                        notifyDataSetChanged();
                                    }
                                });
                            }

                            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) { }
                            });

                            builder.setNeutralButton(R.string.clean, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    if (!pc.get(pos).isGotoEmpty())
                                    {
                                        pc.get(pos).setGotoEmpty();
                                        notifyDataSetChanged();
                                    }
                                }
                            });

                            builder.setTitle(R.string.goto_text);
                            builder.setView(view);

                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                        break;

                        case R.id.Comment:
                        {
                            final int pos = (int) v.getTag();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            View view = LayoutInflater.from(getContext()).inflate(R.layout.comment_dialog, null);
                            final EditText comment = view.findViewById(R.id.editComment);

                            comment.setText(pc.get(pos).comment);

                            builder.setTitle(R.string.comment_text);
                            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    pc.get(pos).comment = comment.getText().toString();
                                    notifyDataSetChanged();
                                }
                            });
                            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            });

                            builder.setView(view);

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
                }
            };

            viewHolder.Count.setOnClickListener(OCL);
            viewHolder.Command.setOnClickListener(OCL);
            viewHolder.Goto.setOnClickListener(OCL);
            viewHolder.Comment.setOnClickListener(OCL);
            rowView.setTag(viewHolder);
        }

        if (exec_line == position) rowView.setBackgroundResource(R.color.exec);
        else if (current_line == position) rowView.setBackgroundResource(R.color.selected);
        else rowView.setBackgroundResource(R.color.normal);

        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.Count.setText(String.valueOf(position + 1));
        holder.Count.setTag(position);
        holder.Command.setText(Character.toString(pc.get(position).command));
        holder.Command.setTag(position);
        holder.Goto.setText(pc.get(position).getGoto());
        holder.Goto.setTag(position);
        holder.Comment.setText(pc.get(position).comment);
        holder.Comment.setTag(position);

        return rowView;
    }

    /**
     * Метод, сбрасывающий значения полей класса
     */

    public void resetAdapter()
    {
        current_line = -1;
        exec_line = -1;
        isSelected = false;

        pc.clear();
        pc.add(new PostCode());
        pc.trimToSize();
    }

    @Override
    public int getCount()
    {
        return pc.size();
    }

    public void addObj(int where)
    {
        pc.add(where, new PostCode());
    }

    public void addObj()
    {
        pc.add(new PostCode());
    }

    public void addObj(char command, String go_to, String comment)
    {
        pc.add(new PostCode(command, go_to, comment));
    }

    public void addObj(Serializable obj)
    {
        pc.add((PostCode) obj);
    }
}
