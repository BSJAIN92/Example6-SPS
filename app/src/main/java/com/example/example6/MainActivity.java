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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Smart Phone Sensing Example 6. Object movement and interaction on canvas.
 */
public class MainActivity extends Activity implements OnClickListener {

    /**
     * The buttons.
     */
    private Button up, left, right, down;
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
    /**
     * The walls.
     */

    private List<int[]> RoomParticles;

    private Integer totalParticles;

    private List<int[]> Particles;

    private Set<Integer> collidedParticles;

    private HashMap<Integer, Integer> propabilityRoom;

    private  int radiusParticles = 5;

    private List<ShapeDrawable> walls;
    private List<int[]> wallsBounds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the buttons
        up = (Button) findViewById(R.id.button1);
        right = (Button) findViewById(R.id.button3);
        down = (Button) findViewById(R.id.button4);
        left = (Button) findViewById(R.id.button2);
        // set the text view
        textView = (TextView) findViewById(R.id.textView1);

        // set listeners
        up.setOnClickListener(this);
        down.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);

        // get the screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        walls = new ArrayList<>();
        wallsBounds = new ArrayList<int[]>();
        this.propabilityRoom = new HashMap<Integer, Integer>();

        this.collidedParticles = new HashSet<Integer>();

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

        for(int[] wallBound : wallsBounds) {
            ShapeDrawable shape = new ShapeDrawable(new RectShape());
            shape.setBounds(wallBound[0],wallBound[1],wallBound[2],wallBound[3]);
            walls.add(shape);
        }

        float scale = (float) width/2700;

        // create a canvas
        ImageView canvasView = (ImageView) findViewById(R.id.canvas);
        Bitmap blankBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);

        canvas = new Canvas(blankBitmap);

        this.canvas.scale(scale,scale);
        canvasView.setImageBitmap(blankBitmap);

        // draw the objects

        for(ShapeDrawable wall : walls) {
            wall.draw(canvas);
        }

        this.setRoomParticles();

        this.setParticles();

        this.drawParticles(canvas);

        left.setEnabled(false);
        right.setEnabled(false);
        down.setEnabled(false);

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


    public void setParticles(){

        this.Particles = new ArrayList<int[]>();

        int boundary = 10;

        for (int room = 0; room < this.RoomParticles.size(); room++){
            for (int particles = 0; particles < this.RoomParticles.get(room)[1]; particles++){

                int x1, x2, y1, y2, Px, Py;
                y1 = this.RoomParticles.get(room)[3] + boundary;
                x1 = this.RoomParticles.get(room)[2] + boundary;
                x2 = this.RoomParticles.get(room)[4] - boundary;
                y2 = this.RoomParticles.get(room)[5] - boundary;

                Px = ThreadLocalRandom.current().nextInt(x1, x2+1);
                Py = ThreadLocalRandom.current().nextInt(y1, y2+1);

                this.Particles.add(new int[] {Px, Py, 0});
            }

            if (this.RoomParticles.get(room).length == 11) {
                for (int particles = 0; particles < this.RoomParticles.get(room)[6]; particles++) {
                    int x1, x2, y1, y2, Px, Py;
                    x1 = this.RoomParticles.get(room)[7] + boundary;
                    y1 = this.RoomParticles.get(room)[8] + boundary;
                    x2 = this.RoomParticles.get(room)[9] - boundary;
                    y2 = this.RoomParticles.get(room)[10] - boundary;

                    Px = ThreadLocalRandom.current().nextInt(x1, x2 + 1);
                    Py = ThreadLocalRandom.current().nextInt(y1, y2 + 1);

                    this.Particles.add(new int[] {Px, Py, 0});
                }
            }
        }
    }

    public void setRoomParticles(){

        this.totalParticles = 108;

        this.RoomParticles = new ArrayList<int[]>();

        this.RoomParticles.add(new int[] {1,5,0,520,440,690});
        this.RoomParticles.add(new int[] {2,7,0,690,440,920});
        this.RoomParticles.add(new int[] {3,7,440,690,920,920});
        this.RoomParticles.add(new int[] {4,5,920,690,1260,920});
        this.RoomParticles.add(new int[] {5,11,920,920,1260,1440});
        this.RoomParticles.add(new int[] {6,8,1260,690,1820,920});
        this.RoomParticles.add(new int[] {7,11,1260,920,1600,1440});
        this.RoomParticles.add(new int[] {8,2,1820,850,2170,920,7,1830,920,2170,1230});

        this.RoomParticles.add(new int[] {9,7,2170,850,2600,1080});
        this.RoomParticles.add(new int[] {10,8,1820,500,2170,850});
        this.RoomParticles.add(new int[] {11,3,1820,130,1950,500});
        this.RoomParticles.add(new int[] {12,5,1380,0,1950,130});
        this.RoomParticles.add(new int[] {13,7,1010,0,1380,270});
        this.RoomParticles.add(new int[] {14,8,580,0,1010,270});
        this.RoomParticles.add(new int[] {15,5,0,0,580,130,2,440,130,580,320});

    }

    private void updateParticles(double distance, double direction) {

        int movementSize = 3;

        for (int p = 0; p < this.Particles.size(); p++){

            double changeX = new Double(distance * Math.sin(direction));
            double changeY = new Double(distance * Math.cos(direction));

            while (changeX != 0 && changeY != 0) {

                if (changeX >= 3) {
                    int newX = this.Particles.get(p)[0] + movementSize;
                    this.Particles.get(p)[0] = newX;
                    changeX = changeX - movementSize;
                } else {
                    int newX = this.Particles.get(p)[0] + (int) changeX;
                    this.Particles.get(p)[0] = newX;
                    changeX = 0;
                }

                if (changeY >= 3) {
                    int newY = this.Particles.get(p)[0]+ movementSize;
                    this.Particles.get(p)[0] = newY;
                    changeY = changeY - movementSize;
                } else {
                    int newY = this.Particles.get(p)[0] + (int) changeY;
                    this.Particles.get(p)[0] = newY;
                    changeY = 0;
                }

                drawable = new ShapeDrawable(new OvalShape());

                drawable.setBounds(this.Particles.get(p)[0] - this.radiusParticles,
                        this.Particles.get(p)[1] - radiusParticles,
                        this.Particles.get(p)[0] + this.radiusParticles,
                        this.Particles.get(p)[1] + radiusParticles);

                // if there is a collision between the dot and any of the walls
                if(isCollision()) {
                    // reset dot to center of canvas
                    this.collidedParticles.add(p);
                    break;
                }

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
            // UP BUTTON
            case R.id.button1: {
                Toast.makeText(getApplication(), "UP", Toast.LENGTH_SHORT).show();
                Rect r = drawable.getBounds();
                drawable.setBounds(r.left,r.top-20,r.right,r.bottom-20);
                textView.setText("\n\tMove Up" + "\n\tTop Margin = "
                        + drawable.getBounds().top);
                this.updateParticles(distance, direction);

                this.calculateProbability();
                this.resampleParticles();

                // redrawing of the object
                canvas.drawColor(Color.WHITE);

                for(ShapeDrawable wall : walls) {
                    wall.draw(canvas);
                }
                this.drawParticles(this.canvas);

                break;
            }
            // DOWN BUTTON
            case R.id.button4: {
                Toast.makeText(getApplication(), "DOWN", Toast.LENGTH_SHORT).show();
                Rect r = drawable.getBounds();
                drawable.setBounds(r.left,r.top+20,r.right,r.bottom+20);
                textView.setText("\n\tMove Down" + "\n\tTop Margin = "
                        + drawable.getBounds().top);
                break;
            }
            // LEFT BUTTON
            case R.id.button2: {
                Toast.makeText(getApplication(), "LEFT", Toast.LENGTH_SHORT).show();
                Rect r = drawable.getBounds();
                drawable.setBounds(r.left-20,r.top,r.right-20,r.bottom);
                textView.setText("\n\tMove Left" + "\n\tLeft Margin = "
                        + drawable.getBounds().left);
                break;
            }
            // RIGHT BUTTON
            case R.id.button3: {
                Toast.makeText(getApplication(), "RIGHT", Toast.LENGTH_SHORT).show();
                Rect r = drawable.getBounds();
                drawable.setBounds(r.left+20,r.top,r.right+20,r.bottom);
                textView.setText("\n\tMove Right" + "\n\tLeft Margin = "
                        + drawable.getBounds().left);
                break;
            }
        }

    }

    private  int findParticle(int nr, int room) {
        int count = 0;
        for(int i = 0; i < this.Particles.size(); i++) {
            if (this.Particles.get(i).length == 2) {
                continue;
            }
            if (this.Particles.get(i)[2] == room) {
                if (nr == count) {
                    break;
                } else {
                    count++;
                }
            }
        }
        return count;
    }

    private void resampleParticles() {

        int maxValue = 0;
        int currentKey = 0;

        for (Map.Entry<Integer, Integer> entry : this.propabilityRoom.entrySet()){
            if (entry.getValue() > maxValue){
                currentKey = entry.getKey();
                maxValue = entry.getValue();
            }
        }

        for (int idx : this.collidedParticles) {
            int particleNr = ThreadLocalRandom.current().nextInt(0, maxValue);
            int particleIdx = this.findParticle(particleNr, this.propabilityRoom.get(currentKey));
            int[] particle = this.Particles.get(particleIdx);
            this.Particles.set(idx, particle.clone());
        }
        this.collidedParticles.clear();

    }

    private void calculateProbability() {

        this.propabilityRoom.clear();


        for (int particle = 0; particle < this.Particles.size(); particle++){
            int Px = this.Particles.get(particle)[0];
            int Py = this.Particles.get(particle)[1];

            if (this.collidedParticles.contains(particle)){
                continue;
            }

            for (int room = 0; room < this.RoomParticles.size(); room ++){
                int roomNr = this.RoomParticles.get(room)[0];
                int x1 = this.RoomParticles.get(room)[2];
                int y1 = this.RoomParticles.get(room)[3];
                int x2 = this.RoomParticles.get(room)[4];
                int y2 = this.RoomParticles.get(room)[5];

                if (x1 < Px && x2 > Px && y1 < Py && y2 > Py) {
                    if (this.propabilityRoom.containsKey(roomNr)) {
                        this.propabilityRoom.put(roomNr, this.propabilityRoom.get(roomNr) + 1);
                    }
                    else {
                        this.propabilityRoom.put(roomNr, 1);
                    }

                    // set room
                    this.Particles.get(particle)[2] = roomNr;
                }

                if (this.RoomParticles.get(room).length == 11){

                    x1 = this.RoomParticles.get(room)[7];
                    y1 = this.RoomParticles.get(room)[8];
                    x2 = this.RoomParticles.get(room)[9];
                    y2 = this.RoomParticles.get(room)[10];

                    if (x1 < Px && x2 > Px && y1 < Py && y2 > Py) {
                        if (this.propabilityRoom.containsKey(roomNr)) {
                            this.propabilityRoom.put(roomNr, this.propabilityRoom.get(roomNr) + 1);
                        }
                        else {
                            this.propabilityRoom.put(roomNr, 1);
                        }
                        // set room
                        this.Particles.get(particle)[2] = roomNr;
                    }

                }


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