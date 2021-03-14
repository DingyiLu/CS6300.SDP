package edu.gatech.seclass.wordfind6300;

public class Letter {
    private int row = -1;
    private int col = -1;
    private String chars;

    public Letter(String chars) {
        this.chars = chars;
    }

    public void setLoc(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getChars() {
        return chars;
    }
}
