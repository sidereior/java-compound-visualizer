import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.depict.DepictionGenerator;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import javax.swing.*;

public class VisualizeElement
{
    private double left;  //the x-coordinate of the left edge of the sprite
    private double top;   //the y-coordinate of the top edge of the sprite
    private int width;
    private int height;
    private String elementToDisplay;

    public VisualizeElement(double theLeft, double theTop, int theWidth, int theHeight, String element)
    {
        left = theLeft;
        top = theTop;
        width = theWidth;
        height = theHeight;
        elementToDisplay=element;
    }

    public double getLeft()
    {
        return left;
    }

    public void setLeft(double l)
    {
        left = l;
    }

    public double getTop()
    {
        return top;
    }

    public void setTop(double t)
    {
        top = t;
    }

    public int getWidth()
    {
        return width;
    }

    public void setWidth(int w)
    {
        width = w;
    }

    public int getHeight()
    {
        return height;
    }

    public void setHeight(int h)
    {
        height = h;
    }

    public Image getImage() throws Exception
    {
        try {
            IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
            SmilesParser smipar = new SmilesParser(bldr);
            IAtomContainer mol = smipar.parseSmiles("CN1C=NC2=C1C(=O)N(C(=O)N2C)C");
            mol.setProperty(CDKConstants.TITLE, "caffeine");
            DepictionGenerator dptgen = new DepictionGenerator();
            dptgen.withSize(200, 250).withMolTitle().withTitleColor(Color.DARK_GRAY);
            BufferedImage visual = dptgen.depict(mol).toImg();
            return visual;
        }
        catch(Exception e)
        {
            throw new RuntimeException("Invalid Depiction of Element " + elementToDisplay + ", try altering strucutre because structure is not supported");
        }
    }

    public void setElementToDisplay(String i)
    {
        elementToDisplay = i;
    }

    public boolean overlap(Sprite other)
    {
        if(this.getLeft() < other.getLeft() + other.getWidth())
        {
            if(this.getLeft() + this.getWidth() > other.getLeft())
            {
                if(this.getTop() < other.getTop() + other.getHeight())
                {
                    if(this.getTop() + this.getHeight() > other.getTop())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isClicked(int x, int y)
    {
        if(x > getLeft() && x < getLeft() + getWidth() && y < getTop() + getHeight() && y > getTop())
            return true;
        return false;
    }

    public void step(World world)
    {
        //do NOT insert any code here
    }
}
