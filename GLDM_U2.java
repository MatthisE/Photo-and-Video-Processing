package U2;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

    /**
     Opens an image window and adds a panel below the image
     */
    public class GLDM_U2 implements PlugIn {

        ImagePlus imp; // ImagePlus object
        private int[] origPixels;
        private int width;
        private int height;


        public static void main(String args[]) {
            //new ImageJ();
            //IJ.open("/users/barthel/applications/ImageJ/_images/orchid.jpg");
            IJ.open("C:\\Users\\matth\\OneDrive\\Desktop\\mein Zeug\\Semester 2\\GLDM\\Bild.jpeg");

            GLDM_U2 pw = new GLDM_U2();
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


        class CustomWindow extends ImageWindow implements ChangeListener {

            private JSlider jSliderBrightness;
            private JSlider jSliderKontrast;
            private JSlider jSliderSättigung;
            private JSlider jSliderHue;

            private double brightness=0;
            private double kontrast=1;
            private double saettigung=1;
            private double hue=0;


            CustomWindow(ImagePlus imp, ImageCanvas ic) {
                super(imp, ic);
                addPanel();
            }

            void addPanel() {
                //JPanel panel = new JPanel();
                Panel panel = new Panel();

                panel.setLayout(new GridLayout(4, 1));
                jSliderBrightness = makeTitledSilder("Helligkeit", -128, 128, 0);
                jSliderKontrast = makeTitledSilder("Kontrast", 0, 10, 5);
                jSliderSättigung = makeTitledSilder("Sättigung", 0, 8, 4);
                jSliderHue = makeTitledSilder("Farbe", 0, 360, 0);
                panel.add(jSliderBrightness);
                panel.add(jSliderKontrast);
                panel.add(jSliderSättigung);
                panel.add(jSliderHue);

                add(panel);

                pack();
            }

            private JSlider makeTitledSilder(String string, int minVal, int maxVal, int val) {

                JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val );
                Dimension preferredSize = new Dimension(width, 50);
                slider.setPreferredSize(preferredSize);
                TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
                        string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
                        new Font("Sans", Font.PLAIN, 11));
                slider.setBorder(tb);
                slider.setMajorTickSpacing((maxVal - minVal)/10 );
                slider.setPaintTicks(true);
                slider.addChangeListener(this);
                slider.getSnapToTicks();
                return slider;
            }

            private void setSliderTitle(JSlider slider, String str) {
                TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
                        str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
                        new Font("Sans", Font.PLAIN, 11));
                slider.setBorder(tb);
            }

            public void stateChanged( ChangeEvent e ){
                JSlider slider = (JSlider)e.getSource();

                if (slider == jSliderBrightness) {
                    brightness = slider.getValue();
                    String str = "Helligkeit " + brightness;
                    setSliderTitle(jSliderBrightness, str);
                }

                if (slider == jSliderKontrast) {
                    double[] zahlen= {0, 0.2, 0.4, 0.6, 0.8, 1, 2, 4, 6, 8, 10};
                    kontrast = zahlen[slider.getValue()];
                    String str = "Kontrast " + kontrast;
                    setSliderTitle(jSliderKontrast, str);
                }

                if (slider == jSliderSättigung) {
                    double[] zahlen = {0, 0.25, 0.5, 0.75, 1, 2, 3, 4, 5};
                    saettigung = zahlen[slider.getValue()];
                    String str = "Sättigung " + saettigung;
                    setSliderTitle(jSliderSättigung, str);
                }

                if (slider == jSliderHue) {
                    hue = slider.getValue();
                    String str = "Farbe " + hue;
                    setSliderTitle(jSliderHue, str);
                }

                changePixelValues(imp.getProcessor());

                imp.updateAndDraw();
            }


            private void changePixelValues(ImageProcessor ip) {

                // Array fuer den Zugriff auf die Pixelwerte
                int[] pixels = (int[])ip.getPixels();

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        // anstelle dieser drei Zeilen später hier die Farbtransformation durchführen,
                        // die Y Cb Cr -Werte verändern und dann wieder zurücktransformieren

                        int Y = (int)(0.299*r + 0.587*g + 0.114*b);
                        int U = (int)((b-Y)*0.493);
                        int V = (int)((r-Y)*0.877);

                        Y = (int)(Y + brightness);

                        Y = (int)((kontrast) * (Y-128)+128);
                        U = (int)((kontrast) * U);
                        V = (int)((kontrast) * V);

                        U = (int)(U * saettigung);
                        V = (int)(V * saettigung);

                        int rn = (int)(Y + V/0.877);
                        int bn = (int)(Y + U/0.493);
                        int gn = (int)(1/0.587 * Y - 0.299/0.587 * rn - 0.114/0.587 * bn);

                        if(rn>255){
                            rn=255;
                        }
                        else if(rn<0){
                            rn=0;
                        }

                        if(gn>255){
                            gn=255;
                        }
                        else if(gn<0){
                            gn=0;
                        }

                        if(bn>255){
                            bn=255;
                        }
                        else if(bn<0){
                            bn=0;
                        }

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;

                    }
                }
            }

        } // CustomWindow inner class
    }
