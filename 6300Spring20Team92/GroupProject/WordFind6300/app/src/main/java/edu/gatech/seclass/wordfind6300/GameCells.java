package edu.gatech.seclass.wordfind6300;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import java.util.*;
import android.view.MotionEvent;
import android.view.*;
import java.util.ArrayList;
import android.content.res.*;

public class GameCells extends View {

    private int boardDim = Settings.getSettings().getDimensions();
    public LetterBoundary letterBoundary[][] = new LetterBoundary[boardDim][boardDim];

    private Path mPath;
    private Context context;
    private Paint mPaint;
    private WordGameActivity activity;
    public ArrayList<LetterBoundary> usedBoxes = null;

    // Game settings and board
    private Settings settings = Settings.getSettings(); // TODO: not sure if singleton pattern is best for Settings, but ok for now..
    private GameBoard board = new GameBoard(settings);
    private int touch_ctr = 0;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    public GameCells(Context c, AttributeSet a) {
        super(c, a);
        activity = (WordGameActivity) getContext();
        context = c;
        mPath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(8);

        usedBoxes = new ArrayList<LetterBoundary>();

        for (int row = 0; row < boardDim; row++) {
            for (int col = 0; col < boardDim; col++) {
                letterBoundary[row][col] = new LetterBoundary(row, col);
            }
        }
    }

    // Must be called **after** layout is fully completed so the cells can be set to the correct size.
    // Also called for a game reset.
    public void populateBoard(boolean withDict) {
        usedBoxes = new ArrayList<LetterBoundary>();
        Letter[][] boardLetters = null;
        Random rand = new Random(System.currentTimeMillis());
        if (withDict) {
            //pick letter weighted by dictionary frequency
            int compress = 1024;
            Character distro[] = new Character[MainActivity.dictionary.getTotalLetterCount()/(compress-1)];
            int distIndex = 0;
            Iterator it = MainActivity.dictionary.getLetterCounts().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Character key = (Character) pair.getKey();
                Integer count = (Integer) pair.getValue();
                count /= compress;
                for (int i=0; i<count; i++) {
                    distro[distIndex++] = key;
                }
            }
            boardLetters = new Letter[boardDim][boardDim];
            for (int row = 0; row < boardDim; row++) {
                for (int col = 0; col < boardDim; col++) {
                    int r = rand.nextInt(distIndex);
                    Character c = distro[r];
                    Letter l = new Letter(""+c);
                    l.setLoc(row, col);
                    boardLetters[row][col] = l;
                }
            }
        }
        else {
            this.board = new GameBoard(settings);
            board.generateBoard();
            boardLetters = board.getBoard();
        }

        int cellsPixels = 0;
        if (true) {
            int width_m = this.getMeasuredWidth();
            int height_m = this.getMeasuredHeight();
            cellsPixels = Math.min(width_m, height_m);
            // cannot overlap UI to right or bottom
            //Log.e("------------------MMM", " wid:"+width_m+" h::"+cellsPixels+ " cell:"+height_m);
        }
        else {
            int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
            int screen_width = Resources.getSystem().getDisplayMetrics().widthPixels;
            int displayPixels = Math.min(screenHeight, screen_width);
            cellsPixels = displayPixels / 2; // cells can have 1/2 the screen size
            //Log.e("---------------------", "screen wid:"+displayPixels+" all:"+cellsPixels+ " cell:"+cellsPixels);
        }
        int cellPixels = cellsPixels/boardDim;
        LetterBoundary.setup(cellPixels, boardDim);
        for (int row = 0; row < boardDim; row++) {
            for (int col = 0; col < boardDim; col++) {
                this.letterBoundary[row][col].setLetter(boardLetters[row][col]);
            }
        }
        this.invalidate();
    }

    public ArrayList<LetterBoundary> getAdjoingLetters(LetterBoundary letter) {
        ArrayList<LetterBoundary> adjLetters = new ArrayList<>();
        int row = letter.row;
        int col = letter.col;
        for (int r = -1; r <= 1; r++) {
            for (int c = -1; c <= 1; c++) {
                if (r==0 && c == 0) {
                    continue;
                }
                if (r+row<0 || c+col<0 || r+row>boardDim-1 || c+col>boardDim-1) {
                    continue;
                }
                adjLetters.add(letterBoundary[r+row][c+col]);
            }
        }
        return adjLetters;
    }

    private void clear_hilights() {
        for (int row = 0; row < boardDim; row++) {
            for (int col = 0; col < boardDim; col++) {
                letterBoundary[row][col].setHilite(false);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String word = "";
        if (usedBoxes.size() > 0) {
            word += "   ";
            for (LetterBoundary b : usedBoxes) {
                if (b != null) {
                    word += b.letter.getChars();
                }
            }
            word += "   ";
        }
        activity.showWord(word);

        Paint paint = new Paint();
        for (int row = 0; row < boardDim; row++) {
            for (int col = 0; col < boardDim; col++) {
                letterBoundary[row][col].draw(paint, canvas);
            }
        }
        canvas.drawPath(mPath, mPaint);
    }

    private LetterBoundary find_box(float x, float y) {
        for (int r = 0; r < boardDim; r++) {
            for (int c = 0; c < boardDim; c++) {
                LetterBoundary b = letterBoundary[r][c];
                if (b.left < x && b.right > x) {
                    if (b.top < y && b.bottom > y) {
                        return b;
                    }
                }
            }
        }
        return null;
    }

    public void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        LetterBoundary b = find_box(x, y);
        if (b != null && !usedBoxes.contains(b)) {
            usedBoxes.add(b);
            b.setHilite(true);
        }
        //activity.message("");
    }

    public String getWordFromLetters() {
        ArrayList<Letter> letters = new ArrayList<Letter>();
        String word = "";
        for (LetterBoundary box : usedBoxes) {
            if (box != null) {
                letters.add(box.letter);
                word += box.letter.getChars();
            }
        }
        return word;
    }

    public void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
        LetterBoundary box_at_touch = find_box(x, y);
        if (box_at_touch == null) {
            return;
        }
        boolean hilite = true;
        // hilite if it is adjacent to the last letter
        if (usedBoxes.size()>0) {
            LetterBoundary last = usedBoxes.get(usedBoxes.size()-1);
                if ((Math.abs(box_at_touch.letter.getCol() - last.letter.getCol()) > 1) ||
                        (Math.abs(box_at_touch.letter.getRow() - last.letter.getRow()) > 1)) {
                    hilite = false;
                }
        }
        if (hilite) {
            box_at_touch.setHilite(true);
            boolean fnd = false;
            for (LetterBoundary b : usedBoxes) {
                if (b.isSameBorder(box_at_touch)) {
                    fnd = true;
                    break;
                }
            }
            if (!fnd) {
                usedBoxes.add(box_at_touch);
            }
        }
        if (!MainActivity.robot.playsGame) {
            activity.showHint(getWordFromLetters(), box_at_touch);
        }
    }

    public void touch_up(float x, float y) {
        String word = getWordFromLetters();
        clear_hilights();
        usedBoxes.clear();
        mPath.lineTo(mX, mY);
        mPath.reset();
        activity.scoreWord(board, word);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up(x, y);
                invalidate();
                break;
        }
        return true;
    }


}

