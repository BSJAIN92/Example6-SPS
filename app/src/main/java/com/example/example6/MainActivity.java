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
import java.util.List;
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

    private List<ArrayList<Integer>> RoomParticles;

    private Integer totalParticles;

    private List<ArrayList<Integer>> Particles;

    private List<ShapeDrawable> walls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the buttons
        up = (Button) findViewById(R.id.button1);
        left = (Button) findViewById(R.id.button2);
        right = (Button) findViewById(R.id.button3);
        down = (Button) findViewById(R.id.button4);

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
        /*
        ShapeDrawable d = new ShapeDrawable(new RectShape());
        d.setBounds(width/2-200, height/2-90, width/2+200, height/2-80);
        ShapeDrawable d2 = new ShapeDrawable(new RectShape());
        d2.setBounds(width/2-200, height/2+60, width/2+200, height/2+70);
        ShapeDrawable d3 = new ShapeDrawable(new RectShape());
        d3.setBounds(width/2+200, height/2-90, width/2+210, height/2+70);
        walls.add(d);
        walls.add(d2);
        walls.add(d3);
        */

        ShapeDrawable h1 = new ShapeDrawable(new RectShape());
        ShapeDrawable h2 = new ShapeDrawable(new RectShape());
        ShapeDrawable h3 = new ShapeDrawable(new RectShape());
        ShapeDrawable h4 = new ShapeDrawable(new RectShape());
        ShapeDrawable h5 = new ShapeDrawable(new RectShape());
        ShapeDrawable h6 = new ShapeDrawable(new RectShape());
        ShapeDrawable h7 = new ShapeDrawable(new RectShape());
        ShapeDrawable h8 = new ShapeDrawable(new RectShape());
        ShapeDrawable h9 = new ShapeDrawable(new RectShape());
        ShapeDrawable h10 = new ShapeDrawable(new RectShape());
        ShapeDrawable h11 = new ShapeDrawable(new RectShape());
        ShapeDrawable h12 = new ShapeDrawable(new RectShape());
        ShapeDrawable h13 = new ShapeDrawable(new RectShape());
        ShapeDrawable h14 = new ShapeDrawable(new RectShape());
        ShapeDrawable h15 = new ShapeDrawable(new RectShape());
        ShapeDrawable h16 = new ShapeDrawable(new RectShape());
        ShapeDrawable h17 = new ShapeDrawable(new RectShape());
        ShapeDrawable h18 = new ShapeDrawable(new RectShape());
        ShapeDrawable h19 = new ShapeDrawable(new RectShape());
        ShapeDrawable h20 = new ShapeDrawable(new RectShape());
        ShapeDrawable h21 = new ShapeDrawable(new RectShape());

        ShapeDrawable v1 = new ShapeDrawable(new RectShape());
        ShapeDrawable v2 = new ShapeDrawable(new RectShape());
        ShapeDrawable v3 = new ShapeDrawable(new RectShape());
        ShapeDrawable v4 = new ShapeDrawable(new RectShape());
        ShapeDrawable v5 = new ShapeDrawable(new RectShape());
        ShapeDrawable v6 = new ShapeDrawable(new RectShape());
        ShapeDrawable v7 = new ShapeDrawable(new RectShape());
        ShapeDrawable v8 = new ShapeDrawable(new RectShape());
        ShapeDrawable v9 = new ShapeDrawable(new RectShape());
        ShapeDrawable v10 = new ShapeDrawable(new RectShape());
        ShapeDrawable v11 = new ShapeDrawable(new RectShape());
        ShapeDrawable v12 = new ShapeDrawable(new RectShape());
        ShapeDrawable v13 = new ShapeDrawable(new RectShape());
        ShapeDrawable v14 = new ShapeDrawable(new RectShape());
        ShapeDrawable v15 = new ShapeDrawable(new RectShape());
        ShapeDrawable v16 = new ShapeDrawable(new RectShape());
        ShapeDrawable v17 = new ShapeDrawable(new RectShape());
        ShapeDrawable v18 = new ShapeDrawable(new RectShape());
        ShapeDrawable v19 = new ShapeDrawable(new RectShape());
        ShapeDrawable v20 = new ShapeDrawable(new RectShape());




        h1.setBounds(0,0,2600,5);
        h2.setBounds(1950,500,2600,505);
        h3.setBounds(1830,1230,2170,1235);
        h4.setBounds(1600,920,1830,925);
        h5.setBounds(1260,920,1600,925);
        h6.setBounds(920,920,1260,925);
        h7.setBounds(0,920,920,925);
        h8.setBounds(0,1440,2600,1445);
        h9.setBounds(0,690,440,695);
        h10.setBounds(440,690,1010,695);
        h11.setBounds(1010,690,1240,695);
        h12.setBounds(1240,690,1820,695);
        h13.setBounds(2170,850,2600,855);
        h14.setBounds(2170,1080,2600,1085);
        h15.setBounds(0,130,440,135);
        h16.setBounds(1380,130,1820,135);
        h17.setBounds(580,270,1010,275);
        h18.setBounds(1010,270,1240,275);
        h19.setBounds(1240,270,1380,275);
        h20.setBounds(440,320,580,325);
        h21.setBounds(0,520,440,525);


        v1.setBounds(0,0,5,1440);
        v2.setBounds(2600,0,2605,1440);
        v3.setBounds(2170,500,2175,850);
        v4.setBounds(2170,850,2175,1080);
        v5.setBounds(2170,1080,2175,1440);
        v6.setBounds(1820,130,1825,695);
        v7.setBounds(1830,920,1835,1440);
        v8.setBounds(1600,920,1605,1440);
        v9.setBounds(1260,920,1265,1440);
        v10.setBounds(920,920,925,1440);
        v11.setBounds(440,130,445,690);
        v12.setBounds(580,270,585,690);
        v13.setBounds(1010,270,1015,690);
        v14.setBounds(1240,270,1245,690);
        v15.setBounds(1380,130,1385,690);
        v16.setBounds(1950,0,1955,500);


        walls.add(h1);
        walls.add(h2);
        walls.add(h3);
        walls.add(h4);
        //walls.add(h5);
        //walls.add(h6);
        walls.add(h7);
        walls.add(h8);
        //walls.add(h9);
        walls.add(h10);
        //walls.add(h11);
        walls.add(h12);
        walls.add(h13);
        walls.add(h14);
        walls.add(h15);
        walls.add(h16);
        walls.add(h17);
        walls.add(h18);
        walls.add(h19);
        walls.add(h20);
        walls.add(h21);



        walls.add(v1);
        walls.add(v2);
        walls.add(v3);
        //walls.add(v4);
        walls.add(v5);
        walls.add(v6);
        walls.add(v7);
        walls.add(v8);
        walls.add(v9);
        walls.add(v10);
        walls.add(v11);
        walls.add(v12);
        walls.add(v13);
        walls.add(v14);
        walls.add(v15);
        walls.add(v16);


        float scale = (float) width/2700;

        // create a canvas
        ImageView canvasView = (ImageView) findViewById(R.id.canvas);
        Bitmap blankBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        canvas.scale(scale,scale);
        canvasView.setImageBitmap(blankBitmap);

        // draw the objects

        for(ShapeDrawable wall : walls) {
            wall.draw(canvas);
        }

        this.setRoomParticles();

        this.setParticles();

        this.drawParticles(canvas);



    }

    private void drawParticles(Canvas canvas) {
        int radiusParticles = 5;

        for(int i = 0; i < this.Particles.size(); i++) {
            int x = this.Particles.get(i).get(0);
            int y = this.Particles.get(i).get(1);
            // create a drawable object
            drawable = new ShapeDrawable(new OvalShape());
            drawable.getPaint().setColor(Color.BLUE);
            drawable.setBounds(x - radiusParticles, y - radiusParticles, x + radiusParticles, y + radiusParticles);
            drawable.draw(canvas);
        }

    }


    public void setParticles(){

        this.Particles = new ArrayList<ArrayList<Integer>>();

        int boundary = 10;

        for (int room = 0; room < this.RoomParticles.size(); room++){
            for (int particles = 1; particles<= this.RoomParticles.get(room).get(1); particles++){

                int x1, x2, y1, y2, Px, Py;
                x1 = this.RoomParticles.get(room).get(2) + boundary;
                y1 = this.RoomParticles.get(room).get(3) + boundary;
                x2 = this.RoomParticles.get(room).get(4) - boundary;
                y2 = this.RoomParticles.get(room).get(5) - boundary;

                Px = ThreadLocalRandom.current().nextInt(x1, x2+1);
                Py = ThreadLocalRandom.current().nextInt(y1, y2+1);

                this.Particles.add(new ArrayList<Integer>(Arrays.asList(Px, Py)));
            }

            if (this.RoomParticles.get(room).size() == 11) {
                for (int particles = 1; particles <= this.RoomParticles.get(room).get(6); particles++) {
                    int x1, x2, y1, y2, Px, Py;
                    x1 = this.RoomParticles.get(room).get(7) + boundary;
                    y1 = this.RoomParticles.get(room).get(8) + boundary;
                    x2 = this.RoomParticles.get(room).get(9) - boundary;
                    y2 = this.RoomParticles.get(room).get(10) - boundary;

                    Px = ThreadLocalRandom.current().nextInt(x1, x2 + 1);
                    Py = ThreadLocalRandom.current().nextInt(y1, y2 + 1);

                    this.Particles.add(new ArrayList<Integer>(Arrays.asList(Px, Py)));
                }
            }
        }
    }

    public void setRoomParticles(){

        this.totalParticles = 108;

        this.RoomParticles = new ArrayList<ArrayList<Integer>>();

        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(1,5,0,520,440,690)));
        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(2,7,0,690,440,920)));
        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(3,7,440,690,920,920)));
        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(4,5,920,690,1260,920)));
        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(5,11,920,920,1260,1440)));
        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(6,8,1260,690,1820,920)));
        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(7,11,1260,920,1600,1440)));
        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(8,2,1820,850,2170,920,7,1830,920,2170,1230)));

        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(9,6,2170,850,2600,1080)));
        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(10,8,1820,500,2170,850)));
        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(11,3,1820,130,1950,500)));
        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(12,5,1380,0,1950,130)));
        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(13,7,1010,0,1380,270)));
        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(14,8,580,0,1010,270)));
        this.RoomParticles.add(new ArrayList<Integer>(Arrays.asList(15,5,0,0,580,130,2,440,130,580,320)));




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
        switch (v.getId()) {
            // UP BUTTON
            case R.id.button1: {
                Toast.makeText(getApplication(), "UP", Toast.LENGTH_SHORT).show();
                Rect r = drawable.getBounds();
                drawable.setBounds(r.left,r.top-20,r.right,r.bottom-20);
                textView.setText("\n\tMove Up" + "\n\tTop Margin = "
                        + drawable.getBounds().top);
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
        // if there is a collision between the dot and any of the walls
        if(isCollision()) {
            // reset dot to center of canvas
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            drawable.setBounds(width/2-20, height/2-20, width/2+20, height/2+20);
        }

        // redrawing of the object
        canvas.drawColor(Color.WHITE);
        drawable.draw(canvas);
        for(ShapeDrawable wall : walls)
            wall.draw(canvas);
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