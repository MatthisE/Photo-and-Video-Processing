package U5;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

/**
 Opens an image window and adds a panel below the image
 */
public class GLDM_U5 implements PlugIn {

    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;

    String[] items = {"Original", "Filter 1", "Filter 2", "Filter 3", "Filter 4"};


    public static void main(String args[]) {

        IJ.open("C:\\Users\\matth\\OneDrive\\Desktop\\mein Zeug\\Semester 2\\GLDM\\Bild3.jpg");
        //IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

        GLDM_U5 pw = new GLDM_U5();
        pw.imp = IJ.getImage();
        pw.run("");
    }

    public void run(String arg) {
        if (imp==null)
            imp = WindowManager.getCurrentImage();
        if (imp==null) {
            return;
        }
        CustomCanvas cc = new CustomCanvas(imp);

        storePixelValues(imp.getProcessor());

        new CustomWindow(imp, cc);
    }


    private void storePixelValues(ImageProcessor ip) {
        width = ip.getWidth();
        height = ip.getHeight();

        origPixels = ((int []) ip.getPixels()).clone();
    }


    class CustomCanvas extends ImageCanvas {

        CustomCanvas(ImagePlus imp) {
            super(imp);
        }

    } // CustomCanvas inner class


    class CustomWindow extends ImageWindow implements ItemListener {

        private String method;

        CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }

        void addPanel() {
            //JPanel panel = new JPanel();
            Panel panel = new Panel();

            JComboBox cb = new JComboBox(items);
            panel.add(cb);
            cb.addItemListener(this);

            add(panel);
            pack();
        }

        public void itemStateChanged(ItemEvent evt) {

            // Get the affected item
            Object item = evt.getItem();

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("Selected: " + item.toString());
                method = item.toString();
                changePixelValues(imp.getProcessor());
                imp.updateAndDraw();
            }

        }


        private void changePixelValues(ImageProcessor ip) {

            // Array zum Zurückschreiben der Pixelwerte
            int[] pixels = (int[]) ip.getPixels();

            if (method.equals("Original")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;

                        pixels[pos] = origPixels[pos];
                    }
                }
            }

            if (method.equals("Filter 1")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = r / 2;
                        int gn = g / 2;
                        int bn = b / 2;

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Filter 2")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;

                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = r/9 + getR(getPix(pos+1))/9 + getR(getPix(pos-1))/9 +
                                getR(getPix(pos-width))/9 + getR(getPix(pos-width-1))/9 + getR(getPix(pos-width+1))/9 +
                                getR(getPix(pos+width))/9 + getR(getPix(pos+width-1))/9 + getR(getPix(pos+width+1))/9;

                        int gn = g/9 + getG(getPix(pos+1))/9 + getG(getPix(pos-1))/9 +
                                getG(getPix(pos-width))/9 + getG(getPix(pos-width-1))/9 + getG(getPix(pos-width+1))/9 +
                                getG(getPix(pos+width))/9 + getG(getPix(pos+width-1))/9 + getG(getPix(pos+width+1))/9;

                        int bn = b/9 + getB(getPix(pos+1))/9 + getB(getPix(pos-1))/9 +
                                getB(getPix(pos-width))/9 + getB(getPix(pos-width-1))/9 + getB(getPix(pos-width+1))/9 +
                                getB(getPix(pos+width))/9 + getB(getPix(pos+width-1))/9 + getB(getPix(pos+width+1))/9;

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Filter 3")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;

                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = r*8/9 + getR(getPix(pos+1))*(-1)/9 + getR(getPix(pos-1))*(-1)/9 +
                                getR(getPix(pos-width))*(-1)/9 + getR(getPix(pos-width-1))*(-1)/9 + getR(getPix(pos-width+1))*(-1)/9 +
                                getR(getPix(pos+width))*(-1)/9 + getR(getPix(pos+width-1))*(-1)/9 + getR(getPix(pos+width+1))*(-1)/9 +128;

                        int gn = g*8/9 + getG(getPix(pos+1))*(-1)/9 + getG(getPix(pos-1))*(-1)/9 +
                                getG(getPix(pos-width))*(-1)/9 + getG(getPix(pos-width-1))*(-1)/9 + getG(getPix(pos-width+1))*(-1)/9 +
                                getG(getPix(pos+width))*(-1)/9 + getG(getPix(pos+width-1))*(-1)/9 + getG(getPix(pos+width+1))*(-1)/9 +128;

                        int bn = b*8/9 + getB(getPix(pos+1))*(-1)/9 + getB(getPix(pos-1))*(-1)/9 +
                                getB(getPix(pos-width))*(-1)/9 + getB(getPix(pos-width-1))*(-1)/9 + getB(getPix(pos-width+1))*(-1)/9 +
                                getB(getPix(pos+width))*(-1)/9 + getB(getPix(pos+width-1))*(-1)/9 + getB(getPix(pos+width+1))*(-1)/9 +128;

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Filter 4")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;

                        int argb = origPixels[pos];

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = r*17/9 + getR(getPix(pos+1))*(-1)/9 + getR(getPix(pos-1))*(-1)/9 +
                                getR(getPix(pos-width))*(-1)/9 + getR(getPix(pos-width-1))*(-1)/9 + getR(getPix(pos-width+1))*(-1)/9 +
                                getR(getPix(pos+width))*(-1)/9 + getR(getPix(pos+width-1))*(-1)/9 + getR(getPix(pos+width+1))*(-1)/9 ;

                        int gn = g*17/9 + getG(getPix(pos+1))*(-1)/9 + getG(getPix(pos-1))*(-1)/9 +
                                getG(getPix(pos-width))*(-1)/9 + getG(getPix(pos-width-1))*(-1)/9 + getG(getPix(pos-width+1))*(-1)/9 +
                                getG(getPix(pos+width))*(-1)/9 + getG(getPix(pos+width-1))*(-1)/9 + getG(getPix(pos+width+1))*(-1)/9 ;

                        int bn = b*17/9 + getB(getPix(pos+1))*(-1)/9 + getB(getPix(pos-1))*(-1)/9 +
                                getB(getPix(pos-width))*(-1)/9 + getB(getPix(pos-width-1))*(-1)/9 + getB(getPix(pos-width+1))*(-1)/9 +
                                getB(getPix(pos+width))*(-1)/9 + getB(getPix(pos+width-1))*(-1)/9 + getB(getPix(pos+width+1))*(-1)/9 ;

                        if(rn<0){
                            rn=0;
                        }
                        if(gn<0){
                            gn=0;
                        }
                        if(bn<0){
                            bn=0;
                        }
                        if(rn>255){
                            rn=255;
                        }
                        if(gn>255){
                            gn=255;
                        }
                        if(bn>255){
                            bn=255;
                        }

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

        }

        public int getPix(int pos){
            if(pos<0){
                pos=0;
            }
            if(pos>origPixels.length-1){
                pos=origPixels.length-1;
            }
            return origPixels[pos];
        }

        public int getR(int argb){
            return (argb >> 16) & 0xff;
        }

        public int getG(int argb){
            return (argb >> 8) & 0xff;
        }

        public int getB(int argb){
            return argb & 0xff;
        }


    } // CustomWindow inner class
}