import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;


import java.awt.Color;

public class World
{
  private boolean paused = false;
  private boolean started = false;
  private String elementName = "NULL";
  public String imageName="";
  public String smiles="";
  boolean wait=false;
  boolean updated=false;
  boolean graphUpdate=false;
  private int numSteps=0;
  public BufferedImage visual;

  public static void main(String[] args)
  {
    run();
  }

  public static void run()
  {
    //Creates a new display of specified size
    Display display = new Display(600, 600);
    display.run();
    //run the display
  }



  private ArrayList<Sprite> sprites;
  private int width;
  private int height;

  public World(int w, int h)
  {
    width = w;
    height = h;
    sprites = new ArrayList<Sprite>();
  }

  public void stepAll()
  {
    //tick's the entire world to render a frame
    numSteps++;
    if(graphUpdate) {
      sprites.clear();
      try {
        //Adds a new sprite (image) to the world of specified size visual
        sprites.add(new Sprite(10, 10, 500, 500, visual));
        graphUpdate = false;
        wait = false;
        return;
      }
      catch (RuntimeException e)
      {
        return;
      }
    }
    else if(result!=null && !result.equals("NULL") && !wait && updated)
    {
      try {
        //If the result is not null, then the image is updated
        smiles = solveString(getData());
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        //Uses CDK to parse the SMILES string
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles(smiles);
        mol.setProperty(CDKConstants.TITLE, result);
        DepictionGenerator dptgen = new DepictionGenerator();
        dptgen.withSize(200, 250).withMolTitle().withTitleColor(Color.DARK_GRAY);
        visual = dptgen.depict(mol).toImg();
        //Converts image to buffered image
        wait=true;
        updated=false;
        return;
      }
      catch (IOException | CDKException e){
        throw new RuntimeException("Given compound does not exist, you entered: " + result);
        //If the compound does not exist, then an error is thrown
      }
    }
  }

  //Scrapes wikipedia for the SMILES string of the compound
  public String getData() throws IOException {
    URL url = new URL("https://en.wikipedia.org/wiki/" + result);
    URLConnection urlConn = url.openConnection();
    InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
    BufferedReader buff = new BufferedReader(inStream);
    String line = buff.readLine();
    String b = "didn't work";
    while (line != null) {
      if (line.contains("SMILES")) {
        b = buff.readLine();
        break;
        //If the line contains SMILES, then the next line is the SMILES string
      }
      line = buff.readLine();
    }
    inStream.close();
    buff.close();
    //Returns the SMILES string
    return b;
  }

  //solves the string to get the SMILES string
  public static String solveString(String result) {
    //Takes in HTML string to convert to SMILES string
    String value = "didn't work";
    String last = "no work";
    if (result.contains("<ul class=\"mw-collapsible-content\" style=\"font-size: 105%; margin-top: 0; margin-bottom: 0; line-height: inherit; text-align: left; list-style: none none; margin-left: 0; word-break:break-all;\"><li style=\"line-height: inherit; margin: 0\"><div style=\"border-top:1px solid #ccc; padding:0.2em 0 0.2em 1.6em; word-wrap:break-word; text-indent:-1.5em; text-align:left; font-size:97%; line-height:120%;\">")) {
      value = result.substring(399);
      //Splits up the string into the SMILES string
      last = value;
      for (int i = 0; i<value.length(); i++) {
        if ((Character.toString(value.charAt(i))).equals("<")) {
          last = value.substring(0, i);
          break;
        }
      }
    }
    //Returns the SMILES string
    return last;
  }


  //Sets and gets properties of the World
  public int getWidth()
  {
    return width;
  }

  public String getResult(){
    return result;
  }

  public int getHeight()
  {
    return height;
  }

  public int getNumSprites()
  {
    return sprites.size();
  }

  public Sprite getSprite(int index)
  {
    return sprites.get(index);
  }

  public void mouseClicked(int x, int y)
  {
    if(225<=x && x<=437 && y<=555 && y>=530)
    {
      createWindow();
    }

  }

  public void keyPressed(int key)
  {

  }

  public void keyReleased(int key)
  {

  }

  public String getTitle()
  {
    return "Chemistry Visualization";
  }

  public void paintComponent(Graphics g)
  {
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);
    for (int i = 0; i < sprites.size(); i++)
    {
      Sprite sprite = sprites.get(i);
      g.drawImage(Display.getImage(sprite.getImage()),
              (int)sprite.getLeft(), (int)sprite.getTop(),
              sprite.getWidth(), sprite.getHeight(), null);
    }

  }

  public String result;
  
  //Creates a new window to input the compound
  public void createWindow() {
    JFrame frame = new JFrame("Enter an element");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    createUI(frame);
    frame.setSize(560, 200);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  public void createUI(final JFrame frame){
    //Creates a new panel to add the text field and button
    JPanel panel = new JPanel();
    LayoutManager layout = new FlowLayout();
    panel.setLayout(layout);
    JButton button = new JButton("Click to enter an element");
    final JLabel label = new JLabel();
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String theVal= (String)JOptionPane.showInputDialog(
                frame,
                "Type your element",
                "Enter an element",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null
        );
        if(result==null || !result.equals(theVal))
        {
          updated=true;
          if(theVal.contains(" "))
          {
            theVal=theVal.replaceAll(" ", "_");
            theVal=theVal.toLowerCase();
          }
          result=theVal;
        }
        else
        {
          updated=false;
        }
        if(result != null && result.length() > 0){
          label.setText("You entered:" + result);
          //Sees the text that you entered
        }else {
          label.setText("None enetered:");
          theVal ="NULL";
          //Nothing is entered
        }

      }
    });
    //Creates a new button to click to graph the element
    JButton graph = new JButton("Click to graph element");
    graph.addActionListener(new ActionListener() {
      @Override

      public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Click to graph element")){
          graphUpdate=true;
        }

      }
    });
    //Adds the button and text field to the panel
    panel.add(button);
    panel.add(graph);
    panel.add(label);
    frame.getContentPane().add(panel, BorderLayout.CENTER);
  }
}