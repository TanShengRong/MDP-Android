package com.example.mazerunner;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class MazeView extends View{

    private Cell[][] cells;
    private static final int COLS = 15, ROWS = 20;
    private static final float WALL_THICKNESS = 3;
    private float cellSize, hMargin, vMargin;
    private Paint wallPaint;//black
    private Paint obstaclePaint;//black
    private Paint exploredPaint;//white
    private Paint startPaint;//green
    private Paint endPaint;//red


    public MazeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //Set colors
        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(WALL_THICKNESS);

        obstaclePaint = new Paint();
        obstaclePaint.setColor(Color.BLACK);

        exploredPaint = new Paint();
        exploredPaint.setColor(Color.WHITE);

        startPaint = new Paint();
        startPaint.setColor(Color.GREEN);

        endPaint = new Paint();
        endPaint.setColor(Color.RED);


        createMaze();
    }

    private void createMaze(){
        cells = new Cell[COLS][ROWS];

        for(int x=0; x<COLS; x++){
            for(int y=0; y<ROWS; y++){
                cells[x][y] = new Cell(x,y);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.LTGRAY);

        int width = getWidth();
        int height = getHeight();

        if(width/height < COLS/ROWS)
            cellSize = width/(COLS+1);
        else
            cellSize = height/(ROWS+1);

        hMargin = (width-COLS*cellSize)/2;
        vMargin = (height-ROWS*cellSize)/2;

        canvas.translate(hMargin,vMargin);

        for(int x=0; x<COLS; x++){
            for(int y=0; y<ROWS; y++){
                if(cells[x][y].topWall)
                    canvas.drawLine(x*cellSize,y*cellSize,(x+1)*cellSize,y*cellSize,wallPaint);
                if(cells[x][y].leftWall)
                    canvas.drawLine(x*cellSize,y*cellSize,x*cellSize,(y+1)*cellSize,wallPaint);
                if(cells[x][y].rightWall)
                    canvas.drawLine((x+1)*cellSize,y*cellSize,(x+1)*cellSize,(y+1)*cellSize,wallPaint);
                if(cells[x][y].bottomWall)
                    canvas.drawLine(x*cellSize,(y+1)*cellSize,(x+1)*cellSize,(y+1)*cellSize,wallPaint);

            }
        }
    }

    private class Cell{
        boolean
            topWall = true,
            leftWall = true,
            bottomWall = true,
            rightWall = true;

        int col, row;

        public Cell(int col, int row){
            this.col = col;
            this.row = row;
        }
    }
}