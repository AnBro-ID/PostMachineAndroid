package ru.anbroid.postmachine;

class PostCodeTriple extends PostCode
{
    public PostCodeTriple(char cmd, String gt, String comm)
    {
        super(cmd, gt, comm);
    }

    public PostCodeTriple(char cmd, String gt)
    {
        super(cmd, gt);
    }

    public PostCodeTriple(char cmd)
    {
        super(cmd);
    }

    public PostCodeTriple()
    {
        super();
    }

    public PostCodeTriple(PostCode pst)
    {
        command = pst.command;
        go_to = pst.go_to;
        comment = pst.comment;

        if (isGotoConcatenated())
        {
            int[] nums = pst.getConcatGotoByInt();
            setConcatGotoByInt(nums[0], nums[1], 1);
        }
    }

    public void setConcatGotoByInt(int first, int second, int third) { go_to = Integer.toString(first) + ',' + second + ',' + third; }

    public int[] getConcatGotoByInt()
    {
        int[] num = new int[3];
        String[] split = go_to.split(",");

        try
        {
            num[0] = Integer.parseInt(split[0]);
            num[1] = Integer.parseInt(split[1]);
            num[2] = Integer.parseInt(split[2]);
        }
        catch (NumberFormatException e)
        {
            num[0] = num[1] = num[2] = 1;
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            num[2] = num[1] = num[0];
        }

        return num;
    }

    public void concatGoto() { go_to = go_to + ',' + go_to + ',' + go_to; }
}
