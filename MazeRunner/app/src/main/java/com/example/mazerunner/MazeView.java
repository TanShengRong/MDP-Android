package com.example.mazerunner;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.graphics.Paint;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.List;

public class MazeView extends View{

    private Cell[][] cells;
    public int [] exploredGrid;
    public int [] obstacleGrid;
    private static final int COLS = 15, ROWS = 20; // x-axis, y-axis
    private static final float WALL_THICKNESS = 3;
    private float cellSize, hMargin, vMargin;
    private final String DEFAULTAL = "AR,AN,"; // Sending to Arudino
    private final String DEFAULTAR = "AR,AN,"; // Sending to Arudino


    private Paint wallPaint;//black
    private Paint bgdPaint;//light grey
    private Paint obstaclePaint;//black
    private Paint exploredPaint;//white
    private Paint startPaint;//green
    private Paint goalPaint;//red
    private Paint robotBodyPaint;//yellow
    private Paint robotEyePaint;//black
    private Paint waypointPaint;//blue
    private Paint imagePaint;//purple

    private int[] waypoint = {1,1};//waypoint
    private int[] startPoint = {1,1}; //startpoint
    private int [] robotFront = {1,2};//robot starting coordinates
    private int [] robotCenter = {1, 1};//robot starting coordinates
    private int angle = 0;

    List<Integer> numberIDX = new ArrayList<>() ; // x-coordinate of identified image
    List<Integer> numberIDY= new ArrayList<>();// y-coordinate of identified image
    List<String> numberID = new ArrayList<>();//numberid of identified image
    List<Integer> robotX = new ArrayList<>();
    List<Integer> robotY =new ArrayList<>();
    ArrayList<String> obstacle_array = new ArrayList<String>();

    MainActivity activityMain = (MainActivity) getContext();

    public MazeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //Set colors
        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(WALL_THICKNESS);

        bgdPaint = new Paint();
        bgdPaint.setColor((Color.rgb(144,144,144))); // light grey

        obstaclePaint = new Paint();
        obstaclePaint.setColor(Color.BLACK); // black

        exploredPaint = new Paint();
        exploredPaint.setColor(Color.WHITE); // white

        startPaint = new Paint();
        startPaint.setColor(Color.rgb(19,198,182)); // green

        goalPaint = new Paint();
        goalPaint.setColor(Color.rgb(252,183,158)); // red

        robotBodyPaint = new Paint();
        robotBodyPaint.setColor(Color.rgb(255,244,132)); // yellow

        robotEyePaint = new Paint();
        robotEyePaint.setColor(Color.rgb(178,178,178)); //light grey

        waypointPaint = new Paint();
        waypointPaint.setColor(Color.rgb(109,188,240));

        imagePaint = new Paint();
        imagePaint.setColor(Color.rgb(210,149,246));

        waypoint = activityMain.waypoint;
        startPoint = activityMain.startPoint;

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

        //Normal grids rectangle
        for(int i=0;i<=14;i++)
            for(int j=0;j<=19;j++)
                canvas.drawRect(i * cellSize, (ROWS - 1 - j) * cellSize,
                        (i + 1) * cellSize, (ROWS - j) * cellSize, bgdPaint);



        //startZone
        for(int i=0;i<=2;i++)
            for(int j=0;j<=2;j++)
                canvas.drawRect(i * cellSize, (ROWS - 1 - j) * cellSize,
                        (i + 1) * cellSize, (ROWS - j) * cellSize, startPaint);
        //goalZone
        for(int i=12;i<=14;i++)
            for(int j=17;j<=19;j++)
                canvas.drawRect(i * cellSize, (ROWS - 1 - j) * cellSize,
                        (i + 1) * cellSize, (ROWS - j) * cellSize, goalPaint);

        //display wayPoint when user taps
        if (waypoint[0] >= 0) {
            canvas.drawRect(waypoint[0] * cellSize, (ROWS - 1 - waypoint[1]) * cellSize,
                    (waypoint[0] + 1) * cellSize, (ROWS - waypoint[1]) * cellSize, waypointPaint);
        }

        //normal grid line
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
        //obstacles
        int inc = 0;
        int inc2 = 0;

        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                //when explored then draw obstacle if any

                if (exploredGrid != null && exploredGrid[inc] == 1) {
                    canvas.drawRect(x * cellSize, (ROWS - 1 - y) * cellSize,
                            (x + 1) * cellSize, (ROWS - y) * cellSize,  exploredPaint);
                    if (obstacleGrid != null && obstacleGrid[inc2] == 1) {
                        canvas.drawRect(x * cellSize, (ROWS - 1 - y) * cellSize,
                                (x + 1) * cellSize, (ROWS - y) * cellSize, obstaclePaint);
                    }
                    inc2++;
                }
                inc++;
            }
        }

        //Numberid drawings on obstacle
        if(numberID!=null&&numberIDY!=null&&numberIDX!=null){
            for(int i =0;i<numberIDX.size();i++){

                if(Integer.parseInt(numberID.get(i))<10&&Integer.parseInt(numberID.get(i))>0)
                    canvas.drawText(numberID.get(i),(numberIDX.get(i)-1)*cellSize+9,(ROWS-numberIDY.get(i)+1)*cellSize-7,imagePaint);
                else if(Integer.parseInt(numberID.get(i))>9&&Integer.parseInt(numberID.get(i))<16)
                    canvas.drawText(numberID.get(i),(numberIDX.get(i)-1)*cellSize+6,(ROWS-numberIDY.get(i)+1)*cellSize-7,imagePaint);
            }
        }

        //display the waypoint when user taps
        if (waypoint[0] >= 0) {
            canvas.drawRect(waypoint[0] * cellSize, (ROWS - 1 - waypoint[1]) * cellSize,
                    (waypoint[0] + 1) * cellSize, (ROWS - waypoint[1]) * cellSize, waypointPaint);

        }

        //displaying robot position
        if (robotCenter[0] >= 0) {
            canvas.drawCircle(robotCenter[0] * cellSize + cellSize / 2,
                    (ROWS - robotCenter[1]) * cellSize - cellSize / 2, 1.3f * cellSize, robotBodyPaint);
        }
        if (robotFront[0] >= 0) {
            canvas.drawCircle(robotFront[0] * cellSize + cellSize / 2,
                    (ROWS - robotFront[1]) * cellSize - cellSize / 2, 0.3f * cellSize, robotEyePaint);
        }

    }


    // Robot move towards the left wall
    public void moveLeft(){

        String message;
        switch (angle) {
            case 270:
                if(robotCenter[0] == 1) break;
                updateRobotCoords(robotCenter[0] - 1, robotCenter[1], 270);
                message = "F";  //forward = 0
                //activityMain.sendCtrlToBtAct(DEFAULTAL + message);
                break;
            case 180:
                updateRobotCoords(robotCenter[0], robotCenter[1], 270);
                message = "R";  //right = 2
                //activityMain.sendCtrlToBtAct(DEFAULTAL + message);
                break;
            case 90:
                updateRobotCoords(robotCenter[0], robotCenter[1], 180);
                message = "R";
                //activityMain.sendCtrlToBtAct(DEFAULTAL + message);
                break;
            default:
                updateRobotCoords(robotCenter[0], robotCenter[1], 270);
                message = "L";   //left =1
                //activityMain.sendCtrlToBtAct(DEFAULTAL + message);
                break;
        }


    }

    // Robot move towards the right wall
    public void moveRight() {

        String message;
        switch (angle) {
            case 90:
                if(robotCenter[0] == 13) break;
                updateRobotCoords(robotCenter[0]+1, robotCenter[1], 90);
                message = "F";
                //activityMain.sendCtrlToBtAct(DEFAULTAL+message);
                break;
            case 180:
                updateRobotCoords(robotCenter[0], robotCenter[1], 90);
                message = "L";
                //activityMain.sendCtrlToBtAct(DEFAULTAL+message);
                break;
            case 270:
                updateRobotCoords(robotCenter[0], robotCenter[1], 0);
                message = "R";
                //activityMain.sendCtrlToBtAct(DEFAULTAL+message);
                break;
            default:
                updateRobotCoords(robotCenter[0], robotCenter[1], 90);
                message = "R";
                //activityMain.sendCtrlToBtAct(DEFAULTAL+message);
        }


    }

    // Robot move towards the top wall
    public void moveUp() {

        String message;
        switch (angle) {
            case 0:
                if(robotCenter[1] == 18) break;
                updateRobotCoords(robotCenter[0], robotCenter[1] + 1, 0);
                message = "F";
                //activityMain.sendCtrlToBtAct(DEFAULTAL+message);
                break;
            case 90:
                updateRobotCoords(robotCenter[0], robotCenter[1], 0);
                message = "L";
                //activityMain.sendCtrlToBtAct(DEFAULTAL+message);
                break;
            case 180:
                updateRobotCoords(robotCenter[0], robotCenter[1], 270);
                message = "R";
                //activityMain.sendCtrlToBtAct(DEFAULTAL+message);
                break;
            default:
                updateRobotCoords(robotCenter[0], robotCenter[1], 0);
                message = "R";
                //activityMain.sendCtrlToBtAct(DEFAULTAL+message);
        }


    }

    // Robot move towards the bottom wall
    public void moveDown() {

        String message;
        switch (angle) {
            case 180:
                if(robotCenter[1] == 1) break;
                updateRobotCoords(robotCenter[0], robotCenter[1]-1, 180);
                message = "F";
                //activityMain.sendCtrlToBtAct(DEFAULTAL+message);
                break;
            case 270:
                updateRobotCoords(robotCenter[0], robotCenter[1], 180);
                message = "L";
                //activityMain.sendCtrlToBtAct(DEFAULTAL+message);
                break;
            case 90:
                updateRobotCoords(robotCenter[0], robotCenter[1], 180);
                message = "R";
                //activityMain.sendCtrlToBtAct(DEFAULTAL+message);
                break;
            default:
                updateRobotCoords(robotCenter[0], robotCenter[1], 90);
                message = "R";
                //activityMain.sendCtrlToBtAct(DEFAULTAL+message);
        }


    }

    //robot moves forward
    public void moveForward() {

        switch (angle) {
            case 180:
                updateRobotCoords(robotCenter[0], robotCenter[1]-1, 180);

                break;
            case 270:
                updateRobotCoords(robotCenter[0] - 1, robotCenter[1], 270);

                break;
            case 90:
                updateRobotCoords(robotCenter[0]+1, robotCenter[1], 90);

                break;
            default:
                updateRobotCoords(robotCenter[0], robotCenter[1] + 1, 0);


        }

    }

    //robot turns right
    public void turnRight() {


        switch (angle) {
            case 90:
                updateRobotCoords(robotCenter[0], robotCenter[1], 180);

                break;
            case 180:
                updateRobotCoords(robotCenter[0], robotCenter[1], 270);

                break;
            case 270:
                updateRobotCoords(robotCenter[0], robotCenter[1], 0);

                break;
            default:
                updateRobotCoords(robotCenter[0], robotCenter[1], 90);

        }

    }

    //robot turns left
    public void turnLeft() {


        switch (angle) {
            case 90:
                updateRobotCoords(robotCenter[0], robotCenter[1], 0);

                break;
            case 180:
                updateRobotCoords(robotCenter[0], robotCenter[1], 90);

                break;
            case 270:
                updateRobotCoords(robotCenter[0], robotCenter[1], 180);

                break;
            default:
                updateRobotCoords(robotCenter[0], robotCenter[1], 270);

        }

    }

    //method to change the coordinates and direction of the robot
    public void updateRobotCoords(int col, int row, int direction) {

        robotCenter[0] = col;   //X coord
        robotCenter[1] = row;   //Y coord
        angle = direction;
        //limiting the plot grid for robot
        if(robotCenter[0] == 0)
            robotCenter[0] =1;
        else if(robotCenter[0] == COLS - 1)
            robotCenter[0] = 13;
        else if(robotCenter[1] == 0)
            robotCenter[1] = 1;
        else if(robotCenter[1] == ROWS - 1)
            robotCenter[1] = 18;


        switch (direction) {
            case 0://^
                robotFront[0] = robotCenter[0];
                robotFront[1] = robotCenter[1] + 1;
                break;
            case 90://>
                robotFront[0] = robotCenter[0] + 1;
                robotFront[1] = robotCenter[1];
                break;
            case 180://v
                robotFront[0] = robotCenter[0];
                robotFront[1] = robotCenter[1] - 1;
                break;
            case 270://<
                robotFront[0] = robotCenter[0] - 1;
                robotFront[1] = robotCenter[1];
                break;
        }

        if (activityMain.receiveAutoUpdate()) {
            invalidate();
        }
    }



    //method to update explored and obstacle grids on the maze
    public void updateMaze(int [] exploredGrid, int[] obstacleGrid){
        this.exploredGrid= exploredGrid;
        this.obstacleGrid=obstacleGrid;
        if (activityMain.receiveAutoUpdate()) {
            invalidate();
        }
    }

    //method to update explored images on the maze
    public void updateNumberID(int x, int y, String ID)
    {

        if(numberIDY == null)
        {
            numberIDX = new ArrayList<>() ;
            numberIDY = new ArrayList<>() ;
            numberID = new ArrayList<>() ;
        }
        for(int i=0; i <this.numberIDY.size();i++)
        {
            if(x == this.numberIDX.get(i)&& y == this.numberIDY.get(i)) {
                this.numberIDX.remove(i);
                this.numberIDY.remove(i);
                this.numberID.remove(i);
            }
        }

        this.numberIDX.add(x);
        this.numberIDY.add(y);
        this.numberID.add(ID);
        if(activityMain.receiveAutoUpdate()){
            invalidate();
        }


    }


    //Enable users to select specific grids by touching for waypoint and robot coordinates
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN)
            return true;

        // either plot waypoint or robot center
        String test = Boolean.toString(activityMain.receiveEnableStartPoint());
        Log.i("MazeView", test);
        if (!activityMain.receiveEnableStartPoint()) {
            int x = (int) (event.getX() / cellSize);
            int y = ROWS - 1 - (int) (event.getY() / cellSize);

            if (x == waypoint[0] && y == waypoint[1]) {
                waypoint[0] = -1;
                waypoint[1] = -1;
            } else {
                waypoint[0] = x; // when user touch and assign new waypoint value
                waypoint[1] = y;
            }
            invalidate(); // call onDraw method again
            //handlers user when user touch the maze when mazeFragment not initialised

            activityMain.sendWaypointTextView(waypoint);

        }
        else if (activityMain.receiveEnableStartPoint())
        {
            int x = (int) (event.getX() / cellSize);
            int y = ROWS - 1 - (int) (event.getY() / cellSize);

            if(x == COLS - 1)
                x = COLS - 2;
            else if(y == ROWS-1)
                y = ROWS - 2;

            if (x == robotCenter[0] && y == robotCenter[1]) {
                updateRobotCoords(-1, -1, angle);
                startPoint[0] = robotCenter[0];
                startPoint[1] = robotCenter[1];

            } else {
                updateRobotCoords(x, y, angle);
                startPoint[0] = robotCenter[0];
                startPoint[1] = robotCenter[1];
            }

            activityMain.setRobotTextView(robotCenter);
            activityMain.sendStartpointTextView(startPoint);
            invalidate();
        }
        return true;
    }


    public int[] getWaypoint() {
        return waypoint;
    }

    public int[] getRobotCenter() {
        return robotCenter;
    }
    public int[] getRobotFront() {

        return robotFront;
    }


    private class Cell{
        boolean
            topWall = true,
            leftWall = true,
            bottomWall = true,
            rightWall = true,
            visited = false;

        int col, row;

        public Cell(int col, int row){
            this.col = col;
            this.row = row;
        }
    }

    //The methods below are all for clearing array lists that stores additional maze elements and revert the maze back to its original state
    public void clearExploredGrid(){
        exploredGrid = null;
        if (activityMain.receiveAutoUpdate()) {
            invalidate();
        }
    }

    public void clearObstacleGrid(){
        obstacleGrid = null;
        if (activityMain.receiveAutoUpdate()) {
            invalidate();
        }
    }

    public void clearNumID(){
        numberID = null;
        numberIDX = null;
        numberIDY = null;
        robotX.clear();
        robotY.clear();

        if (activityMain.receiveAutoUpdate()) {
            invalidate();
        }
    }

    public ArrayList<String> getObsArray() {
        return obstacle_array;
    }

    public void setObsArray(int x, int y){

        this.obstacle_array.add(""+x+","+y);
    }

    public void clearObsArray(){
        obstacle_array.clear();
    }



}