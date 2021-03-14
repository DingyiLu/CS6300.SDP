package edu.gatech.seclass.wordfind6300;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class LetterBoundary {
    public Letter letter;
    public int left, right, top, bottom;
    private Boolean hilight = false;
    public int row, col;

    private static int pixels;
    private static int boardDimension;
    private static int fontSize=0;
    private static int fontOffset = 0;

    static public void setup (int displayPixels, int boardDimension) {
        LetterBoundary.boardDimension = boardDimension;
        LetterBoundary.pixels = displayPixels;
    }

    public LetterBoundary(int row, int col) {
        this.letter = new Letter("");
        this.row = row;
        this.col = col;
    }

    public void setLetter(Letter letter) {
        int margin = LetterBoundary.pixels/7;
        this.letter = letter;
        int width = LetterBoundary.pixels;// + margin; //No need for a margin in rightmost column
        this.left = letter.getCol()*width + margin;
        this.right = (letter.getCol()+1)*width - margin;
        this.top = letter.getRow()*width + margin;
        this.bottom = (letter.getRow()+1)*width - margin;
    }

    public void setHilite(boolean way) {
        this.hilight = way;
    }

    public String toString() {
        String str = "row:"+this.row+" col:"+this.col;
        //str += " boundary Left:"+this.left+" Right:"+ this.right+ " Top:"+ this.top +" Bot:"+this.bottom+" let:"+this.letter.getChars();
        str += " let:"+this.letter.getChars();
        //str += " lv:"+searchLevel;
        return str;
    }

    public boolean isSameBorder(LetterBoundary b) {
        return this.left == b.left && this.right == b.right && this.top == b.top && this.bottom == b.bottom;
    }

    public void draw(Paint paint, Canvas canvas) {
        // cell interior
        if (LetterBoundary.pixels==0) {
            return;
        }
        // figure out best font size - but can only be done with a paint context
        if (LetterBoundary.fontSize == 0) {
            for (int fs = 3; fs < 100; fs++) {
                paint.setTextSize(fs);
                Paint.FontMetrics fm = paint.getFontMetrics();
                float height = fm.bottom - fm.top + fm.leading;
                if (height > (LetterBoundary.pixels*2)/4) {
                    LetterBoundary.fontSize = fs;
                    LetterBoundary.fontOffset = LetterBoundary.pixels/6;
                    break;
                }
            }
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(left, top, right, bottom, paint);

        // border
        paint.setStyle(Paint.Style.STROKE);
        float border = 4.5f;
        if (hilight) {
            paint.setStrokeWidth(2*border);
            paint.setColor(Color.BLUE);
        }
        else {
            paint.setStrokeWidth(border);
            paint.setColor(Color.BLACK);
        }
        canvas.drawRect(left, top, right, bottom, paint);

        // letter text
        paint.setTextSize(fontSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("" + this.letter.getChars(), (left + right) / 2, (top + bottom) / 2 + LetterBoundary.fontOffset, paint);
    }
}
