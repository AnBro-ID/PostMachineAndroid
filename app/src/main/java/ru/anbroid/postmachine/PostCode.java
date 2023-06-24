package ru.anbroid.postmachine;

import java.io.Serializable;

public class PostCode implements Serializable
{
    public char command;        // команда МП
    protected String go_to;     // строка переходов
    public String comment;      // комментарий к строке программы МП

    /**
     * Конструктор - создание нового объекта с определенными значениями
     * @param cmd - символ команды МП
     * @param gt - номер(а) строк(и) для перехода
     * @param comm - строка комментария
     */

    public PostCode(char cmd, String gt, String comm)
    {
        command = cmd;
        go_to = gt;
        comment = comm;
    }

    public PostCode(char cmd, String gt)
    {
        command = cmd;
        go_to = gt;
        comment = "";
    }

    public PostCode(char cmd)
    {
        command = cmd;
        go_to = "";
        comment = "";
    }

    public PostCode()
    {
        command = '\0';
        go_to = "";
        comment = "";
    }

    public PostCode(PostCodeTriple pst)
    {
        command = pst.command;
        go_to = pst.go_to;
        comment = pst.comment;

        if (command == 'X')
            command = '0';

        if (isGotoConcatenated())
        {
            int[] nums = pst.getConcatGotoByInt();
            setConcatGotoByInt(nums[0], nums[1]);
        }
    }

    /**
     * Метод, определяющий наличие текста в строке программы МП
     * @return true, если строка не пуста и false в противном случае
     */

    public boolean hasText() { return command != '\0' || !go_to.isEmpty() || !comment.isEmpty(); }

    /**
     * Метод, объединяющий два номера для перехода в строку
     * @param first - первый порядковый номер
     * @param second - второй порядковый номер
     */

    public void setConcatGotoByInt(int first, int second) { go_to = Integer.toString(first) + ',' + second; }

    /**
     * Метод, очищающий строку переходов
     */

    public void setGotoEmpty() { go_to = ""; }

    /**
     * Метод, устанавливающий один номер строки для перехода
     * @param first - порядковый номер строки
     */

    public void setGotoByInt(int first) { go_to = Integer.toString(first); }

    /**
     * Метод, возвращающий массив номеров строк для перехода
     * @return массив чисел с порядковыми номерами
     */

    public int[] getConcatGotoByInt()
    {
        int[] num = new int[2];
        String[] split = go_to.split(",");

        try
        {
            num[0] = Integer.parseInt(split[0]);
            num[1] = Integer.parseInt(split[1]);
        }
        catch (NumberFormatException e)
        {
            num[0] = num[1] = 1;
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            num[1] = num[0];
        }

        return num;
    }

    /**
     * Метод, возвращающий номер строки для перехода
     * @return порядковый номер строки
     */

    public int getGotoByInt()
    {
        try
        {
            return Integer.parseInt(go_to);
        }
        catch (NumberFormatException e)
        {
            return 1;
        }
    }

    public String getGoto() { return go_to; }

    /**
     * Метод, формирующий составную строку переходов
     */

    public void concatGoto() { go_to = go_to + ',' + go_to; }

    /**
     * Метод, разделяющий составную строку переходов
     * Второй порядковый номер отбрасывается
     */

    public void splitGoto()
    {
        String[] split = go_to.split(",");

        go_to = split[0];
    }

    /**
     * Метод, определяющий является ли строка переходов составной
     * @return true, если составная и false в противном случае
     */

    public boolean isGotoConcatenated() { return go_to.contains(","); }

    /**
     * Метод, определяющий является ли строка переходов пустой
     * @return true, если пуста и false в противном случае
     */

    public boolean isGotoEmpty() { return go_to.isEmpty(); }
}
