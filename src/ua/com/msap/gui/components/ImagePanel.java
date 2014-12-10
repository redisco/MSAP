package ua.com.msap.gui.components;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 *
 * @version 0.0.0.1 23.02.2014 
 * @author Neredko Mick
 */
public class ImagePanel extends JPanel {
    private Image image;
    private boolean tile;
    private String fileName;
    
    public ImagePanel() {
        this.tile = false;
    };

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(this.image == null){
            return;
        }
        int width = this.image.getWidth(this);
        int height = this.image.getHeight(this);
        if(this.getHeight() < height){
            double k = (double)this.getHeight()/height;
            width = (int)(k * width);
            height = this.getHeight();
        }
        if(this.getWidth() < width){
            double k = (double)this.getWidth()/width;
            height = (int)(k * height);
            width = this.getWidth();
        }
        int x = (this.getWidth() - width)/2;
        int y = (this.getHeight() - height)/2;
            g.drawImage(image, x, y, width, height, this);

    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
        try{
            File file = new File(fileName);
            this.image = ImageIO.read(file);
        }catch(Exception ex){
            return;
        }
    }
}
