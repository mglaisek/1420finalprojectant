package jfxtest;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import javafx.application.Application;

import javafx.scene.shape.*;
import javafx.scene.paint.Color;

import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;

import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.stage.Stage;

/**
 *
 * @author mylog
 */
public class AnimationPlayer extends Application    {
    private boolean debug = false; // true for debugging mode, false for regular operation
    public int numFrames, fps, numAnimations;
    public static Group root = new Group(); // must be static so that it can be accessed from start
    public static Timeline timeline = new Timeline();
    
    /**
     * Default constructor
     */
    public AnimationPlayer()    { //default constructor 
        
    }
    
    
    /**
     * Starts the animation
     * @param primaryStage passed stage
     */
    @Override
    public void start(Stage primaryStage)   {
        timeline.play(); // starts the animation
        Scene scene = new Scene(root, 300, 300); // creates a scene and adds root to it
        if(debug)
            System.out.println("Successfully initialized scene");
        primaryStage.setTitle("Animation Player"); // titles the display
        primaryStage.setScene(scene); // adds the scene to the stage
        primaryStage.show(); // shows the stage
    }
    
    /**
     * loads an animation from a file
     * @param fileName the file to load from
     */
    public void loadAnimationFromFile(String fileName) {
        if(debug)
            System.out.println("Loading animation from "+fileName);
        
        File file = new File(fileName); // creates a file object from the passed string
        
        try {
            Scanner sc = new Scanner(file); // initialize file reader
            
            // variables used to store information during parsing
            String line; //creates variables
            Duration time;
            String pline[]; //creates array for string
            KeyFrame animation; //creates animation object
            boolean readNext = true; //creates variable set to true
            
            // parses the total frames of the animation
            line = sc.nextLine(); //read next line
            pline = line.split(": ",2); //split text at colon 
            numFrames = Integer.parseInt(pline[1]); //set variable to element 1 of array
            if(debug)
                System.out.println(numFrames+" total frames");
            
            // parses the framerate
            line = sc.nextLine();
            pline = line.split(": ",2);
            pline = pline[1].split("f",2);
            fps = Integer.parseInt(pline[0]);
            timeline = new Timeline(fps);
            if(debug)
                System.out.println(fps+" frames per second");
            
            // parses the number of shapes
            line = sc.nextLine(); 
            numAnimations = Integer.parseInt(line);
            if(debug)
                System.out.println(numAnimations+" shapes to be animated");
            
            sc.nextLine(); // skips the next line as it should be blank
            
            // parses each shape and each of their animations, then adds them to the root
            for(int i=0; i<numAnimations; i++)  {
                line = sc.nextLine(); //reads next line
                if(line.equals("Line")) { //checks type of shape
                    if(debug)
                        System.out.println("Line shape");
                    Line ln = new Line();
                    //<editor-fold defaultstate="collapsed" desc="Loads a line's properties">
                    if(debug)
                        ln.setVisible(true);
                    else 
                        ln.setVisible(false);
                    
                    // parse the startX
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("startX"))    {
                        ln.setStartX(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("startX "+pline[1]);
                        readNext = true;
                    } else { // default length
                        ln.setStartX(0);
                        readNext = false;
                    }
                    
                    // parse the startY
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("startY"))    {
                        ln.setStartY(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("startY "+pline[1]);
                        readNext = true;
                    } else { // default width
                        ln.setStartX(0);
                        readNext = false;
                    }

                    // parse the endX
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("endX"))    {
                        ln.setEndX(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("endX "+pline[1]);
                        readNext = true;
                    } else { // default y
                        ln.setEndX(0);
                        readNext = false;
                    }
                    
                    // parse the endY
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("endY"))    {
                        ln.setEndY(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("endY "+pline[1]);
                        readNext = true;
                    } else { // default y
                        ln.setEndY(0);
                        readNext = false;
                    }
                    
                    // parse the color
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("color")) {
                        pline = pline[1].split(", ",3);
                        Color co = Color.rgb(Integer.parseInt(pline[0]),Integer.parseInt(pline[1]),Integer.parseInt(pline[2]));
                        ln.setFill(co);
                        if(debug)
                            System.out.println("color "+pline[0]+","+pline[1]+","+pline[2]);
                        readNext = true;
                    }
                    else    { // default color
                        ln.setFill(Color.BLACK);
                        readNext = false;
                    }
                    
                    // parse the border thickness
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("border"))    {
                        ln.setStrokeWidth(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("border "+pline[1]);
                        readNext = true;
                    } else { // default border thickness
                        ln.setStrokeWidth(0);
                        readNext = false;
                    }
                    // no border color as per the instructions
                    //</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Loads a line's animations">
                    while(!line.isEmpty())  {
                        if(line.equals("effect"))   { // parse an effect if there is an effect declared, otherwise go to next line
                            if(debug)
                                System.out.println("Parsing new effect");
                            
                            line = sc.nextLine();
                            
                            if(line.equals("Jump")) {
                                int nX,nY;
                                KeyValue setX;
                                KeyValue setY;
                                
                                if(debug)
                                    System.out.println("Jump effect");
                                
                                line = sc.nextLine();
                                pline = line.split(": ",2);
                                if(pline[0].equals("start"))    {
                                    time = new Duration(Integer.parseInt(pline[1])*1000/fps); // converts the frame number into milliseconds
                                    readNext = true;
                                }
                                else    {
                                    time = new Duration(0);
                                    readNext = false;
                                }

                                if(readNext)    {
                                    line = sc.nextLine();
                                    pline = line.split(": ",2);
                                }
                                if(pline[0].equals("x"))    {
                                    nX = Integer.parseInt(pline[1]);
                                    readNext = true;
                                }
                                else    { // default, keep original X
                                    nX = (int)ln.getStartX();
                                    readNext = false;
                                }

                                if(readNext)    {
                                    line = sc.nextLine();
                                    pline = line.split(": ",2);
                                }
                                if(pline[0].equals("y"))    {
                                    nY = Integer.parseInt(pline[1]);
                                    readNext = true;
                                }
                                else    { // default, keep original Y
                                    nY = (int)ln.getStartY();
                                    readNext = false;
                                }
                                // for instantaneous movement, the DISCRETE Interpolator is used
                                setX = new KeyValue(ln.startXProperty(), nX, Interpolator.DISCRETE);
                                setY = new KeyValue(ln.startYProperty(), nY, Interpolator.DISCRETE);
                                animation = new KeyFrame(time,setX,setY);
                                timeline.getKeyFrames().add(animation);
                            }
                            else if(line.equals("Show")) {
                                if(debug)
                                    System.out.println("Show effect");
                                line = sc.nextLine();
                                pline = line.split(": ",2);
                                if(pline[0].equals("start"))    {
                                    time = new Duration(Integer.parseInt(pline[1])*1000/fps); // converts the frame number into milliseconds
                                    readNext = true;
                                }
                                else    { // default case
                                    time = new Duration(0);
                                    readNext = false;
                                }

                                KeyValue show = new KeyValue(ln.visibleProperty(), true);
                                animation = new KeyFrame(time,show);
                                timeline.getKeyFrames().add(animation);
                            } 
                            else if(line.equals("Hide"))    {
                                if(debug)
                                    System.out.println("Hide effect");
                                line = sc.nextLine();
                                pline = line.split(": ",2);
                                if(pline[0].equals("start"))    {
                                    time = new Duration(Integer.parseInt(pline[1])*1000/fps); // converts the frame number into milliseconds
                                    readNext = true;
                                }
                                else    {
                                    time = new Duration(0);
                                    readNext = false;
                                }

                                KeyValue hide = new KeyValue(ln.visibleProperty(), false);
                                animation = new KeyFrame(time,hide);
                                timeline.getKeyFrames().add(animation);
                            }
                            else if(line.equals("ChangeColor")) {
                                line = sc.nextLine();
                                Color co;

                                pline = line.split(": ",2);
                                if(pline[0].equals("start"))    {
                                    time = new Duration(Integer.parseInt(pline[1])*1000/fps); // converts the frame number into milliseconds
                                    readNext = true;
                                }
                                else    {
                                    time = new Duration(0);
                                    readNext = false;
                                }

                                if(readNext)
                                    pline = sc.nextLine().split(": ",2);
                                if(pline[0].equals("color")) {
                                    pline = pline[1].split(", ",3);
                                    co = new Color(Integer.parseInt(pline[0]),Integer.parseInt(pline[1]),Integer.parseInt(pline[2]),255);
                                    readNext = true;
                                }
                                else    { // default color
                                    co = Color.BLACK;
                                    readNext = false;
                                }   
                                KeyValue change = new KeyValue(ln.fillProperty(), co, Interpolator.DISCRETE);
                                animation = new KeyFrame(time,change);
                                timeline.getKeyFrames().add(animation);
                            }
                        }
                        if(sc.hasNext())
                            line = sc.nextLine();
                        else
                            break;
                    }
                    //</editor-fold>
                    
                    root.getChildren().add(ln);
                    if(debug)
                        System.out.println("Added line to root");
                
                } else if(line.equals("Rect")) {
                    if(debug)
                        System.out.println("Rectangle shape");
                    Rectangle r = new Rectangle(); // creates an empty rectangle object
                    
                    //<editor-fold defaultstate="collapsed" desc="Loads a rectangle's properties">
                    if(debug)
                        r.setVisible(true);
                    else 
                        r.setVisible(false);
                    
                    // parse the length
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("length"))    {
                        r.setHeight(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("length "+pline[1]);
                        readNext = true;
                    } else { // default length
                        r.setHeight(0);
                        readNext = false;
                    }
                    
                    // parse the width
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("width"))    {
                        r.setWidth(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("width "+pline[1]);
                        readNext = true;
                    } else { // default width
                        r.setWidth(0);
                        readNext = false;
                    }

                    // parse the x coordinate
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("x"))    {
                        r.setX(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("x "+pline[1]);
                        readNext = true;
                    } else { // default y
                        r.setX(0);
                        readNext = false;
                    }
                    
                    // parse the y coordinate
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("y"))    {
                        r.setY(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("y "+pline[1]);
                        readNext = true;
                    } else { // default y
                        r.setY(0);
                        readNext = false;
                    }
                    
                    // parse the color
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("color")) {
                        pline = pline[1].split(", ",3);
                        Color co = Color.rgb(Integer.parseInt(pline[0]),Integer.parseInt(pline[1]),Integer.parseInt(pline[2]));
                        r.setFill(co);
                        if(debug)
                            System.out.println("color "+pline[0]+","+pline[1]+","+pline[2]);
                        readNext = true;
                    }
                    else    { // default color
                        r.setFill(Color.BLACK);
                        readNext = false;
                    }
                    
                    // parse the border thickness
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("border"))    {
                        r.setStrokeWidth(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("border "+pline[1]);
                        readNext = true;
                    } else { // default border thickness
                        r.setStrokeWidth(0);
                        readNext = false;
                    }
                    
                    // parse the border color
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("borderColor")) {
                        pline = pline[1].split(", ",3);
                        Color co = Color.rgb(Integer.parseInt(pline[0]),Integer.parseInt(pline[1]),Integer.parseInt(pline[2]));
                        r.setStroke(co);
                        if(debug)
                            System.out.println("border color "+pline[0]+","+pline[1]+","+pline[2]);
                        readNext = true;
                    }
                    else    { // default border color
                        r.setStroke(Color.BLACK);
                        readNext = false;
                    }
                    //</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Loads a rectangle's animations">
                    while(!line.isEmpty())  {
                        if(line.equals("effect"))   { // parse an effect if there is an effect declared, otherwise go to next line
                            if(debug)
                                System.out.println("Parsing new effect");
                            
                            line = sc.nextLine();
                            
                            if(line.equals("Jump")) {
                                int nX,nY;
                                KeyValue setX;
                                KeyValue setY;
                                
                                if(debug)
                                    System.out.println("Jump effect");
                                
                                line = sc.nextLine();
                                pline = line.split(": ",2);
                                if(pline[0].equals("start"))    {
                                    time = new Duration(Integer.parseInt(pline[1])*1000/fps); // converts the frame number into milliseconds
                                    readNext = true;
                                }
                                else    {
                                    time = new Duration(0);
                                    readNext = false;
                                }

                                if(readNext)    {
                                    line = sc.nextLine();
                                    pline = line.split(": ",2);
                                }
                                if(pline[0].equals("x"))    {
                                    nX = Integer.parseInt(pline[1]);
                                    readNext = true;
                                }
                                else    { // default, keep original X
                                    nX = (int)r.getX();
                                    readNext = false;
                                }

                                if(readNext)    {
                                    line = sc.nextLine();
                                    pline = line.split(": ",2);
                                }
                                if(pline[0].equals("y"))    {
                                    nY = Integer.parseInt(pline[1]);
                                    readNext = true;
                                }
                                else    { // default, keep original Y
                                    nY = (int)r.getY();
                                    readNext = false;
                                }
                                // for instantaneous movement, the DISCRETE Interpolator is used
                                setX = new KeyValue(r.xProperty(), nX, Interpolator.DISCRETE);
                                setY = new KeyValue(r.yProperty(), nY, Interpolator.DISCRETE);
                                animation = new KeyFrame(time,setX,setY);
                                timeline.getKeyFrames().add(animation);
                            }
                            else if(line.equals("Show")) {
                                if(debug)
                                    System.out.println("Show effect");
                                line = sc.nextLine();
                                pline = line.split(": ",2);
                                if(pline[0].equals("start"))    {
                                    time = new Duration(Integer.parseInt(pline[1])*1000/fps); // converts the frame number into milliseconds
                                    readNext = true;
                                }
                                else    { // default case
                                    time = new Duration(0);
                                    readNext = false;
                                }

                                KeyValue show = new KeyValue(r.visibleProperty(), true);
                                animation = new KeyFrame(time,show);
                                timeline.getKeyFrames().add(animation);
                            } 
                            else if(line.equals("Hide"))    {
                                if(debug)
                                    System.out.println("Hide effect");
                                line = sc.nextLine();
                                pline = line.split(": ",2);
                                if(pline[0].equals("start"))    {
                                    time = new Duration(Integer.parseInt(pline[1])*1000/fps); // converts the frame number into milliseconds
                                    readNext = true;
                                }
                                else    {
                                    time = new Duration(0);
                                    readNext = false;
                                }

                                KeyValue hide = new KeyValue(r.visibleProperty(), false);
                                animation = new KeyFrame(time,hide);
                                timeline.getKeyFrames().add(animation);
                            }
                            else if(line.equals("ChangeColor")) {
                                line = sc.nextLine();
                                Color co;

                                pline = line.split(": ",2);
                                if(pline[0].equals("start"))    {
                                    time = new Duration(Integer.parseInt(pline[1])*1000/fps); // converts the frame number into milliseconds
                                    readNext = true;
                                }
                                else    {
                                    time = new Duration(0);
                                    readNext = false;
                                }

                                if(readNext)
                                    pline = sc.nextLine().split(": ",2);
                                if(pline[0].equals("color")) {
                                    pline = pline[1].split(", ",3);
                                    co = new Color(Integer.parseInt(pline[0]),Integer.parseInt(pline[1]),Integer.parseInt(pline[2]),255);
                                    readNext = true;
                                }
                                else    { // default color
                                    co = Color.BLACK;
                                    readNext = false;
                                }   
                                KeyValue change = new KeyValue(r.fillProperty(), co, Interpolator.DISCRETE);
                                animation = new KeyFrame(time,change);
                                timeline.getKeyFrames().add(animation);
                            }
                        }
                        if(sc.hasNext())
                            line = sc.nextLine();
                        else
                            break;
                    }
                    //</editor-fold>
                    
                    root.getChildren().add(r);
                    if(debug)
                        System.out.println("Added rectangle to root");
                } else if(line.equals("Circle"))    {
                    if(debug)
                        System.out.println("Circle shape");
                    Circle c = new Circle(); // creates an empty circle object
                    
                    //<editor-fold defaultstate="collapsed" desc="Loads a circle's properties">
                    if(debug)
                        c.setVisible(true);
                    else 
                        c.setVisible(false);
                    
                    // parse the radius
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("r"))    { //if first element equals r, set variable equal to r
                        c.setRadius(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("radius "+pline[1]);
                        readNext = true;
                    } else { //if element 0 does not equal r, set radius to 0 and continue using same line
                        c.setRadius(0);
                        readNext = false;
                    }
                    
                    // parse the x coordinate
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("x"))    {
                        c.setCenterX(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("x "+pline[1]);
                        readNext = true;
                    } else { // default x
                        c.setCenterX(0);
                        readNext = false;
                    }
                    
                    // parse the y coordinate
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("y"))    {
                        c.setCenterY(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("y "+pline[1]);
                        readNext = true;
                    } else { // default y
                        c.setCenterY(0);
                        readNext = false;
                    }
                    
                    // parse the color
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("color")) {
                        pline = pline[1].split(", ",3);
                        Color co = Color.rgb(Integer.parseInt(pline[0]),Integer.parseInt(pline[1]),Integer.parseInt(pline[2]));
                        c.setFill(co);
                        if(debug)
                            System.out.println("color "+pline[0]+","+pline[1]+","+pline[2]);
                        readNext = true;
                    }
                    else    { // default color
                        c.setFill(Color.BLACK);
                        readNext = false;
                    }
                    
                    // parse the border thickness
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("border"))    {
                        c.setStrokeWidth(Integer.parseInt(pline[1]));
                        if(debug)
                            System.out.println("border "+pline[1]);
                        readNext = true;
                    } else { // default border thickness
                        c.setStrokeWidth(0);
                        readNext = false;
                    }
                    
                    // parse the border color
                    if(readNext)    {
                        line = sc.nextLine();
                        pline = line.split(": ",2);
                    }
                    if(pline[0].equals("borderColor")) {
                        pline = pline[1].split(", ",3);
                        Color co = Color.rgb(Integer.parseInt(pline[0]),Integer.parseInt(pline[1]),Integer.parseInt(pline[2]));
                        c.setStroke(co);
                        if(debug)
                            System.out.println("border color "+pline[0]+","+pline[1]+","+pline[2]);
                        readNext = true;
                    }
                    else    { // default border color
                        c.setStroke(Color.BLACK);
                        readNext = false;
                    }
                    //</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Loads a circle's animations">
                    while(!line.isEmpty())  {
                        if(line.equals("effect"))   { // parse an effect if there is an effect declared, otherwise go to next line
                            if(debug)
                                System.out.println("Parsing new effect");
                            
                            line = sc.nextLine();
                            
                            if(line.equals("Jump")) {
                                int nX,nY;
                                KeyValue setX;
                                KeyValue setY;
                                
                                if(debug)
                                    System.out.println("Jump effect");
                                
                                line = sc.nextLine();
                                pline = line.split(": ",2);
                                if(pline[0].equals("start"))    {
                                    time = new Duration(Integer.parseInt(pline[1])*1000/fps); // converts the frame number into milliseconds
                                    readNext = true;
                                }
                                else    {
                                    time = new Duration(0);
                                    readNext = false;
                                }

                                if(readNext)    {
                                    line = sc.nextLine();
                                    pline = line.split(": ",2);
                                }
                                if(pline[0].equals("x"))    {
                                    nX = Integer.parseInt(pline[1]);
                                    readNext = true;
                                }
                                else    { // default, keep original X
                                    nX = (int)c.getCenterX();
                                    readNext = false;
                                }

                                if(readNext)    {
                                    line = sc.nextLine();
                                    pline = line.split(": ",2);
                                }
                                if(pline[0].equals("y"))    {
                                    nY = Integer.parseInt(pline[1]);
                                    readNext = true;
                                }
                                else    { // default, keep original Y
                                    nY = (int)c.getCenterY();
                                    readNext = false;
                                }
                                // for instantaneous movement, the DISCRETE Interpolator is used
                                setX = new KeyValue(c.centerXProperty(), nX, Interpolator.DISCRETE);
                                setY = new KeyValue(c.centerYProperty(), nY, Interpolator.DISCRETE);
                                animation = new KeyFrame(time,setX,setY);
                                timeline.getKeyFrames().add(animation);
                            }
                            else if(line.equals("Show")) {
                                if(debug)
                                    System.out.println("Show effect");
                                line = sc.nextLine();
                                pline = line.split(": ",2);
                                if(pline[0].equals("start"))    {
                                    time = new Duration(Integer.parseInt(pline[1])*1000/fps); // converts the frame number into milliseconds
                                    readNext = true;
                                }
                                else    { // default case
                                    time = new Duration(0);
                                    readNext = false;
                                }

                                KeyValue show = new KeyValue(c.visibleProperty(), true);
                                animation = new KeyFrame(time,show);
                                timeline.getKeyFrames().add(animation);
                            } 
                            else if(line.equals("Hide"))    {
                                if(debug)
                                    System.out.println("Hide effect");
                                line = sc.nextLine();
                                pline = line.split(": ",2);
                                if(pline[0].equals("start"))    {
                                    time = new Duration(Integer.parseInt(pline[1])*1000/fps); // converts the frame number into milliseconds
                                    readNext = true;
                                }
                                else    {
                                    time = new Duration(0);
                                    readNext = false;
                                }

                                KeyValue hide = new KeyValue(c.visibleProperty(), false);
                                animation = new KeyFrame(time,hide);
                                timeline.getKeyFrames().add(animation);
                            }
                            else if(line.equals("ChangeColor")) {
                                line = sc.nextLine();
                                Color co;

                                pline = line.split(": ",2);
                                if(pline[0].equals("start"))    {
                                    time = new Duration(Integer.parseInt(pline[1])*1000/fps); // converts the frame number into milliseconds
                                    readNext = true;
                                }
                                else    {
                                    time = new Duration(0);
                                    readNext = false;
                                }

                                if(readNext)
                                    pline = sc.nextLine().split(": ",2);
                                if(pline[0].equals("color")) {
                                    pline = pline[1].split(", ",3);
                                    co = new Color(Integer.parseInt(pline[0]),Integer.parseInt(pline[1]),Integer.parseInt(pline[2]),255);
                                    readNext = true;
                                }
                                else    { // default color
                                    co = Color.BLACK;
                                    readNext = false;
                                }   
                                KeyValue change = new KeyValue(c.fillProperty(), co, Interpolator.DISCRETE);
                                animation = new KeyFrame(time,change);
                                timeline.getKeyFrames().add(animation);
                            }
                        }
                        if(sc.hasNext())
                            line = sc.nextLine();
                        else
                            break;
                    }
                    //</editor-fold>
                    
                    root.getChildren().add(c);
                    if(debug)
                        System.out.println("Added circle to root");
                    
                } else  {
                    if(debug)
                        System.out.println("No shape recognized");
                }
                
            }
            sc.close();
            if(debug)
                System.out.println("Finished assembling root");
        } catch (FileNotFoundException e)   {
            System.out.println("Invalid File");
        }
    }
    
    public static void main(String[] args) {
        AnimationPlayer player = new AnimationPlayer();
        player.loadAnimationFromFile("C:\\Users\\zaa20\\OneDrive\\Documents\\NetBeansProjects\\1420finalprojectant\\src\\jfxtest\\newfile.txt");
        player.launch(args);
    }
}