package com.example.example6;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.content.Context;
import android.os.Bundle;
import android.hardware.Sensor;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.stat.descriptive.moment.*;
import org.apache.commons.math3.stat.descriptive.rank.*;

/**
 * Smart Phone Sensing Example 6. Object movement and interaction on canvas.
 */
public class MainActivity extends Activity implements SensorEventListener, OnClickListener {

    private SensorManager sensorManager;

    private Sensor rotationSensor;
    private Sensor stepDetection;
    private Sensor stepCounter;
    private Sensor accSensor;
    private Sensor linearSensor;

    // Marco, 1.75m, Stef: 1.82m.
    // http://livehealthy.chron.com/determine-stride-pedometer-height-weight-4518.html
    private boolean marco = false;
    private boolean floor3 = false;
    private double[] strides = new double[]{175f * 0.415, 182f *  0.415};
    private double stride = strides[0];
    private double latestAngle = 0.0;

    // offsets

    private double offsetRotation = 0.0;
    private double varDistance = 0.1;
    private double varDirection = 0.5;

    /**
     * The buttons.
     */
    private Button switchUser, left, right, down;
    /**
     * The text view.
     */
    private TextView textView;
    /**
     * The shape.
     */
    private ShapeDrawable drawable;
    /**
     * The canvas.
     */
    private Canvas canvas;
    private EditText editText;
    /**
     * The walls.
     */

    private List<int[]> RoomParticles;

    private Integer totalParticles;

    private List<int[]> Particles;

    private List<Double> MagnitudesPast = new ArrayList<Double>();
    private List<Double> MagnitudesNow = new ArrayList<Double>();
    private List<Float> LinearList = new ArrayList<Float>();
    private int TIME_WINDOW = 650;

    private boolean standing = true;

    private int detectedSteps = 0;
    private int countedSteps = 0;
    private int ourSteps = 0;
    private int room = 0;
    private String probability = "";

    private int mLastAccuracy;

    private boolean processing = false;
    private boolean activated = false;

    private Set<Integer> collidedParticles;

    private HashMap<Integer, Integer> propabilityRoom;

    private  int radiusParticles = 5;
    private int SENSOR_DELAY_MICROS = 1000 * 1000; // 16ms

    private long lastTime;

    private List<ShapeDrawable> walls;
    private List<int[]> wallsBounds;

    private ImageView canvasView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        stepDetection = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        linearSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, rotationSensor, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, stepDetection, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, stepCounter, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accSensor, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, linearSensor, sensorManager.SENSOR_DELAY_NORMAL);

        lastTime =  System.currentTimeMillis();

        // set the buttons
        switchUser = (Button) findViewById(R.id.button1);
        down = (Button) findViewById(R.id.button4);
        left = (Button) findViewById(R.id.button2);
        // set the text view
        textView = (TextView) findViewById(R.id.textView1);

        // set listeners
        switchUser.setOnClickListener(this);
        down.setOnClickListener(this);
        left.setOnClickListener(this);

        // get the screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        this.setRoomParticlesFloor4();

        walls = new ArrayList<>();
        wallsBounds = new ArrayList<int[]>();
        this.setWall4();

        this.propabilityRoom = new HashMap<Integer, Integer>();

        this.collidedParticles = new HashSet<Integer>();

        for(int[] wallBound : wallsBounds) {
            ShapeDrawable shape = new ShapeDrawable(new RectShape());
            shape.setBounds(wallBound[0],wallBound[1],wallBound[2],wallBound[3]);
            walls.add(shape);
        }

        float scale = (float) width/2700;

        // create a canvas
        this.canvasView = (ImageView) findViewById(R.id.canvas);
        Bitmap blankBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);

        canvas = new Canvas(blankBitmap);

        this.canvas.scale(scale,scale);
        canvasView.setImageBitmap(blankBitmap);

        for(ShapeDrawable wall : walls) {
            wall.draw(canvas);
        }
    }

    private void setWall4() {
        wallsBounds.clear();
        wallsBounds.add(new int[]{0,0,2600,5});
        wallsBounds.add(new int[]{1950,500,2600,505});
        wallsBounds.add(new int[]{1830,1230,2170,1235});
        wallsBounds.add(new int[]{1600,920,1830,925});
        wallsBounds.add(new int[]{0,920,920,925});
        wallsBounds.add(new int[]{0,1440,2600,1445});
        wallsBounds.add(new int[]{440,690,1010,695});
        wallsBounds.add(new int[]{1240,690,1820,695});
        wallsBounds.add(new int[]{2170,850,2600,855});
        wallsBounds.add(new int[]{2170,1080,2600,1085});
        wallsBounds.add(new int[]{0,130,440,135});
        wallsBounds.add(new int[]{1380,130,1820,135});
        wallsBounds.add(new int[]{580,270,1010,275});
        wallsBounds.add(new int[]{1010,270,1240,275});
        wallsBounds.add(new int[]{1240,270,1380,275});
        wallsBounds.add(new int[]{440,320,580,325});
        wallsBounds.add(new int[]{0,520,440,525});
        wallsBounds.add(new int[]{0,0,5,1440});
        wallsBounds.add(new int[]{2600,0,2605,1440});
        wallsBounds.add(new int[]{2170,500,2175,850});
        wallsBounds.add(new int[]{2170,1080,2175,1440});
        wallsBounds.add(new int[]{1820,130,1825,695});
        wallsBounds.add(new int[]{1830,920,1835,1440});
        wallsBounds.add(new int[]{1600,920,1605,1440});
        wallsBounds.add(new int[]{1260,920,1265,1440});
        wallsBounds.add(new int[]{920,920,925,1440});
        wallsBounds.add(new int[]{440,130,445,690});
        wallsBounds.add(new int[]{580,270,585,690});
        wallsBounds.add(new int[]{1010,270,1015,690});
        wallsBounds.add(new int[]{1240,270,1245,690});
        wallsBounds.add(new int[]{1380,130,1385,690});
        wallsBounds.add(new int[]{1950,0,1955,500});
    }

    private void setWalls3() {
        wallsBounds.clear();
        wallsBounds.add(new int[]{0,0,2600,5});
        wallsBounds.add(new int[]{2170,500,2600,505});
        wallsBounds.add(new int[]{1830,1230,2170,1235});
        wallsBounds.add(new int[]{1600,920,1830,925});
        wallsBounds.add(new int[]{1260,920,1600,925});
        wallsBounds.add(new int[]{920,920,1260,925});
        wallsBounds.add(new int[]{0,920,920,925});
        wallsBounds.add(new int[]{0,1440,2600,1445});
        wallsBounds.add(new int[]{180,690,440,695});
        wallsBounds.add(new int[]{440,690,800,695});

        wallsBounds.add(new int[]{1240,690,1820,695});
        wallsBounds.add(new int[]{1380,130,1820,135});
        wallsBounds.add(new int[]{580,270,800,275});
        wallsBounds.add(new int[]{1010,270,1240,275});
        wallsBounds.add(new int[]{1240,550,1380,555});
        wallsBounds.add(new int[]{440,520,580,525});
        wallsBounds.add(new int[]{0,360,440,365});
        wallsBounds.add(new int[]{0,0,5,1440});
        wallsBounds.add(new int[]{2600,0,2605,1440});
        wallsBounds.add(new int[]{2170,500,2175,850});
        wallsBounds.add(new int[]{2170,850,2175,1080});
        wallsBounds.add(new int[]{2170,1080,2175,1440});
        wallsBounds.add(new int[]{1820,130,1825,695});
        wallsBounds.add(new int[]{1830,920,1835,1440});
        wallsBounds.add(new int[]{440,0,445,690});
        wallsBounds.add(new int[]{580,270,585,690});
        wallsBounds.add(new int[]{1010,270,1015,690});
        wallsBounds.add(new int[]{1240,270,1245,690});
        wallsBounds.add(new int[]{1380,130,1385,690});
        wallsBounds.add(new int[]{1950,0,1955,500});
        wallsBounds.add(new int[]{440,270,445,275});
        wallsBounds.add(new int[]{1010,270,1015,275});
        wallsBounds.add(new int[]{1240,270,1245,275});
        wallsBounds.add(new int[]{440,320,445,325});
        wallsBounds.add(new int[]{0,520,5,525});
        wallsBounds.add(new int[]{180,360,185,690});
        wallsBounds.add(new int[]{800,270,805,690});
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (mLastAccuracy != accuracy) {
            mLastAccuracy = accuracy;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (activated == false) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (event.sensor == rotationSensor) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            // formula for yaw from here: http://danceswithcode.net/engineeringnotes/rotations_in_3d/rotations_in_3d_part2.html
            latestAngle =  Math.atan2(rotationMatrix[3], rotationMatrix[0]);
        }

        if (event.sensor == stepDetection) {
            detectedSteps = detectedSteps + 1;
            textView.setText("Detected Steps: "+ detectedSteps+ " \nstep counter: "+ countedSteps + " \nours "+ ourSteps + " room " +room + " with " + probability);
            //System.out.println("GOT STEP");
        }

        if (event.sensor == stepCounter) {
            countedSteps = (int) event.values[0];
            textView.setText("Detected Steps: "+ detectedSteps+ " \nstep counter: "+ countedSteps + " \nours "+ ourSteps + " room " +room + " with " + probability);
        }

        if (event.sensor == accSensor) {
            float[] acc = event.values;
            double magnitude = Math.sqrt((Math.pow(acc[0], 2) + Math.pow(acc[1], 2) + Math.pow(acc[2], 2)));
            MagnitudesNow.add(magnitude);
            if(currentTime - lastTime> TIME_WINDOW) {
                if (processMagnitudes()){
                    ourSteps = ourSteps + 1;
                    if (ourSteps % 2 == 0){
                        this.recalc();
                    }

                }
                textView.setText("Detected Steps: "+ detectedSteps+ " \nstep counter: "+ countedSteps + " \nours "+ ourSteps + " room " +room + " with " + probability);
                //MagnitudesPast.clear();
                //for(int i = 0; i < MagnitudesNow.size(); i++) {
                //    MagnitudesPast.add(MagnitudesNow.get(i));
                //}
                MagnitudesNow.clear();
                lastTime = currentTime;
                return;
            }
        }

    }

    private void recalc() {
        if (processing) {
            return;
        }
        processing = true;
        this.updateParticles();

        this.calculateProbability();
        this.resampleParticles();

        this.canvasView.invalidate();



        // redrawing of the object
        canvas.drawColor(Color.WHITE);

        for(ShapeDrawable wall : walls) {
            wall.draw(canvas);
        }
        this.drawParticles(this.canvas);
        processing = false;
    }

    private boolean walkingDetection(double linearAcc) {
        Max maximum = new Max();
        double max = maximum.evaluate(toFloatPrimitive(LinearList));

        if (max > 2 && max < 5) {
            return true;
        }
        return false;
    }

    private boolean processMagnitudes() {
        // substract mean all the time
        Max maximum = new Max();
        Mean mean = new Mean();

        double max = maximum.evaluate(toDoublePrimitive(MagnitudesNow));
        double mu = mean.evaluate(toDoublePrimitive(MagnitudesNow));
        double updatedMag = max -  mu;
        //System.out.println(updatedMag);

        if (updatedMag > 2 && updatedMag < 5) {
            return true;
        }
        return false;

    }

    private double[] toDoublePrimitive(List<Double> array) {
        double[] result = new double[array.size()];
        for (int i = 0; i < array.size(); i++) {
           result[i] = array.get(i).doubleValue();
        }
        return result;
    }

    private double[] toFloatPrimitive(List<Float> array) {
        double[] result = new double[array.size()];
        for (int i = 0; i < array.size(); i++) {
            result[i] = array.get(i).doubleValue();
        }
        return result;
    }

    private void drawParticles(Canvas canvas) {


        for(int i = 0; i < this.Particles.size(); i++) {
            int x = this.Particles.get(i)[0];
            int y = this.Particles.get(i)[1];
            // create a drawable object
            drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(Color.BLUE);
            drawable.setBounds(x - this.radiusParticles, y - this.radiusParticles, x + this.radiusParticles, y + this.radiusParticles);
            drawable.draw(canvas);
        }

    }


    public void resetParticles(){

        this.Particles = new ArrayList<int[]>();

        int boundary = 10;

        for (int roomIdx = 0; roomIdx < this.RoomParticles.size(); roomIdx++){
            int[] room = this.RoomParticles.get(roomIdx);

            for (int particles = 0; particles < room[1]; particles++){

                int x1, x2, y1, y2, Px, Py;
                y1 = room[3] + boundary;
                x1 = room[2] + boundary;
                x2 = room[4] - boundary;
                y2 = room[5] - boundary;

                Px = ThreadLocalRandom.current().nextInt(x1, x2+1);
                Py = ThreadLocalRandom.current().nextInt(y1, y2+1);

                this.Particles.add(new int[] {Px, Py, 0});
            }

            if (room.length == 11) {
                for (int particles = 0; particles < room[6]; particles++) {
                    int x1, x2, y1, y2, Px, Py;
                    x1 = room[7] + boundary;
                    y1 = room[8] + boundary;
                    x2 = room[9] - boundary;
                    y2 = room[10] - boundary;

                    Px = ThreadLocalRandom.current().nextInt(x1, x2 + 1);
                    Py = ThreadLocalRandom.current().nextInt(y1, y2 + 1);

                    this.Particles.add(new int[] {Px, Py, 0});
                }
            }
        }
    }

    public void setRoomParticlesFloor4(){

        this.totalParticles = 108;

        this.RoomParticles = new ArrayList<int[]>();

        this.RoomParticles.add(new int[] {1,229,0,520,440,690});
        this.RoomParticles.add(new int[] {2,309,0,690,440,920});
        this.RoomParticles.add(new int[] {3,337,440,690,920,920});
        this.RoomParticles.add(new int[] {4,239,920,690,1260,920});
        this.RoomParticles.add(new int[] {5,540,920,920,1260,1440});
        this.RoomParticles.add(new int[] {6,394,1260,690,1820,920});
        this.RoomParticles.add(new int[] {7,540,1260,920,1600,1440});
        this.RoomParticles.add(new int[] {8,75,1820,850,2170,920,322,1830,920,2170,1230});

        this.RoomParticles.add(new int[] {9,302,2170,850,2600,1080});
        this.RoomParticles.add(new int[] {10,374,1820,500,2170,850});
        this.RoomParticles.add(new int[] {11,147,1820,130,1950,500});
        this.RoomParticles.add(new int[] {12,227,1380,0,1950,130});
        this.RoomParticles.add(new int[] {13,305,1010,0,1380,270});
        this.RoomParticles.add(new int[] {14,355,580,0,1010,270});
        this.RoomParticles.add(new int[] {15,231,0,0,580,130,82,440,130,580,320});

        this.RoomParticles.add(new int[] {16,279,1010,270,1240,690});

    }

    public void setRoomParticlesFloor3(){

        this.RoomParticles = new ArrayList<int[]>();

        this.RoomParticles.add(new int[] {18,176,0,360,180,690});
        this.RoomParticles.add(new int[] {102,123,0,690,180,920});
        this.RoomParticles.add(new int[] {103,504,180,690,920,920});
        this.RoomParticles.add(new int[] {104,232,920,690,1260,920});

        this.RoomParticles.add(new int[] {106,381,1260,690,1820,920});
        this.RoomParticles.add(new int[] {152,116,1240,270,1380,550});
        this.RoomParticles.add(new int[] {108,73,1820,850,2170,920,312,1830,920,2170,1230});

        this.RoomParticles.add(new int[] {19,962,1950,0,2600,500});
        this.RoomParticles.add(new int[] {110,363,1820,500,2170,850});
        this.RoomParticles.add(new int[] {111,143,1820,130,1950,500});
        this.RoomParticles.add(new int[] {112,220,1380,0,1950,130});
        this.RoomParticles.add(new int[] {113,296,1010,0,1380,270});
        this.RoomParticles.add(new int[] {114,344,580,0,1010,270});
        this.RoomParticles.add(new int[] {151,261,800,270,1010,690});
        this.RoomParticles.add(new int[] {15,216,440,0,580,520});
        this.RoomParticles.add(new int[] {17,286,1010,270,1240,690});


    }

    private void updateParticles() {
        double direction = latestAngle - Math.toRadians(offsetRotation);
        double distance = stride * 2;
        double variance = 0.25;

        for(int particleIdx = 0; particleIdx < this.Particles.size(); particleIdx++) {
            int[] particle = this.Particles.get(particleIdx);

            // variance in direction and distance
            double newdistance = ThreadLocalRandom.current().nextDouble(distance * (1.0 - varDistance), distance * (1.0 + varDistance));
            double newdirection = ThreadLocalRandom.current().nextDouble(direction - varDirection, direction + varDirection);

            int prevX = particle[0];
            int prevY = particle[1];
            int newX = particle[0] + (int) (newdistance * Math.sin(newdirection));
            int newY = particle[1] + (int) (newdistance * Math.cos(newdirection));

            //variance in x and y
            /*double factor = ThreadLocalRandom.current().nextDouble(0, variance);
            int diffX = (int) ((newX - prevX) * factor);
            int diffY = (int) ((newY - prevY) * factor);
            newX = newX + diffX;
            newY = newY + diffY;*/

            this.Particles.set(particleIdx, new int[] {newX, newY, particle[2]});
            int left = 0;
            int right = 0;
            int top = 0;
            int bottom = 0;

            drawable = new ShapeDrawable(new RectShape());
            if (prevX > newX){
                left = newX;
                right = prevX;
            }
            else {
                left = prevX;
                right = newX;
            }
            if (prevY > newY){
                top = newY;
                bottom = prevY;
            }
            else {
                top = prevY;
                bottom = newY;
            }

            drawable.setBounds(left, top, right, bottom);

            if (isCollision()) {
                this.collidedParticles.add(particleIdx);
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // This happens when you click any of the four buttons.
        // For each of the buttons, when it is clicked we change:
        // - The text in the center of the buttons
        // - The margins
        // - The text that shows the margin

        double distance = 120;
        double direction = 0.5;
        switch (v.getId()) {
            // init beleive
            case R.id.button2: {
                // draw the objects
                activated = true;
                canvas.drawColor(Color.WHITE);

                EditText tmp = findViewById(R.id.offsetRotation);
                String t = tmp.getText().toString();
                if (t.length() > 0) {
                    offsetRotation = Double.parseDouble(tmp.getText().toString());
                } else {
                    offsetRotation = Math.toDegrees(latestAngle);
                }

                tmp = findViewById(R.id.varDistance);
                t = tmp.getText().toString();
                if (t.length() > 0) {
                    varDistance = Double.parseDouble(tmp.getText().toString());
                }

                tmp = findViewById(R.id.varRotation);
                t = tmp.getText().toString();
                if (t.length() > 0) {
                    varDirection = Math.toRadians(Double.parseDouble(tmp.getText().toString()));
                }

                for(ShapeDrawable wall : walls) {
                    wall.draw(canvas);
                }
                this.collidedParticles.clear();
                this.resetParticles();

                this.drawParticles(canvas);
                break;
            }
            case R.id.button1: {
                //private boolean marco = false;
                //private double[] strides = new double[]{175f * 0.415, 182f *  0.415};
                if (marco) {
                    marco = false;
                    Toast.makeText(getApplication(), "User is Stef", Toast.LENGTH_SHORT).show();
                    stride = strides[1];
                } else {
                    marco = true;
                    Toast.makeText(getApplication(), "User is Marco", Toast.LENGTH_SHORT).show();
                    stride = strides[0];
                }
                break;
            }

            case R.id.button4: {
                //private boolean marco = false;
                //private double[] strides = new double[]{175f * 0.415, 182f *  0.415};
                canvasView.invalidate();

                canvas.drawColor(Color.WHITE);
                if (floor3) {
                    floor3 = false;
                    this.setRoomParticlesFloor4();
                    this.setWall4();
                    Toast.makeText(getApplication(), "Floor 4 activated", Toast.LENGTH_SHORT).show();
                } else {
                    floor3 = true;
                    this.setRoomParticlesFloor3();
                    this.setWalls3();
                    Toast.makeText(getApplication(), "Floor 3 activated", Toast.LENGTH_SHORT).show();
                }

                walls.clear();

                for(int[] wallBound : wallsBounds) {
                    ShapeDrawable shape = new ShapeDrawable(new RectShape());
                    shape.setBounds(wallBound[0],wallBound[1],wallBound[2],wallBound[3]);
                    walls.add(shape);
                }


                for(ShapeDrawable wall : walls) {
                    wall.draw(canvas);
                }
                break;
            }
        }

    }

    private void resampleParticles() {

        int maxValue = 0;

        for (Map.Entry<Integer, Integer> entry : this.propabilityRoom.entrySet()){
            if (entry.getValue() > maxValue){
                room = entry.getKey();
                maxValue = entry.getValue();
            }
        }

        DecimalFormat df = new DecimalFormat("#.00");
        probability = df.format((double) maxValue / this.Particles.size() * 100);
        textView.setText("Detected Steps: "+ detectedSteps+ " \nstep counter: "+ countedSteps + " \nours "+ ourSteps + " room " +room + " with " + probability);

        List<Integer> validLocations = new ArrayList<Integer>();

        for (int i = 0; i < this.Particles.size(); i++) {
            if (this.collidedParticles.contains(i)) {
                continue;
            }
            validLocations.add(i);
        }

        for (int idx : this.collidedParticles) {
            int validIdx = ThreadLocalRandom.current().nextInt(0, validLocations.size());
            int particleIdx = validLocations.get(validIdx);
            int[] particle = this.Particles.get(particleIdx);
            this.Particles.set(idx, particle.clone());
        }
        this.collidedParticles.clear();

    }

    private void calculateProbability() {

        this.propabilityRoom.clear();


        for (int particleIdx = 0; particleIdx < this.Particles.size(); particleIdx++){
            int[] particle = this.Particles.get(particleIdx);
            int Px = particle[0];
            int Py = particle[1];

            if (this.collidedParticles.contains(particleIdx)){
                continue;
            }

            for (int[] room : this.RoomParticles) {
                int roomNr = room[0];
                int x1 = room[2];
                int y1 = room[3];
                int x2 = room[4];
                int y2 = room[5];

                if (x1 < Px && x2 > Px && y1 < Py && y2 > Py) {
                    if (this.propabilityRoom.containsKey(roomNr)) {
                        this.propabilityRoom.put(roomNr, this.propabilityRoom.get(roomNr) + 1);
                    }
                    else {
                        this.propabilityRoom.put(roomNr, 1);
                    }

                    // set room
                    particle[2] = roomNr;
                }

                if (room.length == 11){

                    x1 = room[7];
                    y1 = room[8];
                    x2 = room[9];
                    y2 = room[10];

                    if (x1 < Px && x2 > Px && y1 < Py && y2 > Py) {
                        if (this.propabilityRoom.containsKey(roomNr)) {
                            this.propabilityRoom.put(roomNr, this.propabilityRoom.get(roomNr) + 1);
                        }
                        else {
                            this.propabilityRoom.put(roomNr, 1);
                        }
                        // set room
                        particle[2] = roomNr;
                    }

                }
                //System.out.println(this.propabilityRoom.get(roomNr));


            }

        }

    }


    /**
     * Determines if the drawable dot intersects with any of the walls.
     * @return True if that's true, false otherwise.
     */
    private boolean isCollision() {
        for(ShapeDrawable wall : walls) {
            if(isCollision(wall,drawable))
                return true;
        }
        return false;
    }

    /**
     * Determines if two shapes intersect.
     * @param first The first shape.
     * @param second The second shape.
     * @return True if they intersect, false otherwise.
     */
    private boolean isCollision(ShapeDrawable first, ShapeDrawable second) {
        Rect firstRect = new Rect(first.getBounds());
        return firstRect.intersect(second.getBounds());
    }
}