package U3;

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
 * Opens an image window and adds a panel below the image
 */
public class GLDM_U3 implements PlugIn {

    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;

    String[] items = {"Original", "Rot-Kanal", "Graustufen", "Negativ", "Bitonal1", "Bitonal2", "Bitonal3", "Bitonal4", "Sepia", "8-farbig"};


    public static void main(String args[]) {

        IJ.open("C:\\Users\\matth\\OneDrive\\Desktop\\mein Zeug\\Semester 2\\GLDM\\Bild2.jpg");
        //IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

        GLDM_U3 pw = new GLDM_U3();
        pw.imp = IJ.getImage();
        pw.run("");
    }

    public void run(String arg) {
        if (imp == null)
            imp = WindowManager.getCurrentImage();
        if (imp == null) {
            return;
        }
        CustomCanvas cc = new CustomCanvas(imp);

        storePixelValues(imp.getProcessor());

        new CustomWindow(imp, cc);
    }


    private void storePixelValues(ImageProcessor ip) {
        width = ip.getWidth();
        height = ip.getHeight();

        origPixels = ((int[]) ip.getPixels()).clone();
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

            if (method.equals("Rot-Kanal")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;

                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        //int g = (argb >>  8) & 0xff;
                        //int b =  argb        & 0xff;

                        int rn = r;
                        int gn = 0;
                        int bn = 0;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Negativ")) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = 255 - r;
                        int gn = 255 - g;
                        int bn = 255 - b;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Graustufen")) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = (r + g + b) / 3;
                        int gn = (r + g + b) / 3;
                        int bn = (r + g + b) / 3;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Bitonal1")) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = (r + g + b) / 3;
                        int gn = (r + g + b) / 3;
                        int bn = (r + g + b) / 3;

                        if (rn < 128) {
                            rn = 0;
                        } else {
                            rn = 255;
                        }
                        if (gn < 128) {
                            gn = 0;
                        } else {
                            gn = 255;
                        }
                        if (bn < 128) {
                            bn = 0;
                        } else {
                            bn = 255;
                        }

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Bitonal2")) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = (r + g + b) / 3;
                        int gn = (r + g + b) / 3;
                        int bn = (r + g + b) / 3;

                        rn = graustufen1(rn);
                        gn = graustufen1(gn);
                        bn = graustufen1(bn);

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }


            if (method.equals("Bitonal3")) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = (r + g + b) / 3;
                        int gn = (r + g + b) / 3;
                        int bn = (r + g + b) / 3;

                        rn = graustufen2(rn);
                        gn = graustufen2(gn);
                        bn = graustufen2(bn);

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Bitonal3")) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int rn = (r + g + b) / 3;
                        int gn = (r + g + b) / 3;
                        int bn = (r + g + b) / 3;

                        rn = graustufen2(rn);
                        gn = graustufen2(gn);
                        bn = graustufen2(bn);

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Bitonal4")) {
                int d = 0; //Fehler
                int oben = 1;
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int grau = (r + g + b) / 3;

                        int rn;
                        int gn;
                        int bn;

                        if (oben == 0) {
                            rn = grau + d;
                            gn = grau + d;
                            bn = grau + d;
                        } else {
                            rn = grau - d;
                            gn = grau - d;
                            bn = grau - d;
                        }

                        if (rn < 128) {
                            d = rn;

                            rn = 0;
                            gn = 0;
                            bn = 0;

                            oben = 0;
                        } else {
                            d = 255 - rn;

                            rn = 255;
                            gn = 255;
                            bn = 255;

                            oben = 1;
                        }


                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("Sepia")) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        int grau = (r + g + b) / 3;

                        double rx = ((double) grau / 255) * (255 + 40);
                        double gx = ((double) grau / 255) * (191 + 40);
                        double bx = ((double) grau / 255) * (117 + 40);

                        int rn = (int) rx;
                        int gn = (int) gx;
                        int bn = (int) bx;

                        if (rn > 255) {
                            rn = 255;
                        } else if (rn < 0) {
                            rn = 0;
                        }

                        if (gn > 255) {
                            gn = 255;
                        } else if (gn < 0) {
                            gn = 0;
                        }

                        if (bn > 255) {
                            bn = 255;
                        } else if (bn < 0) {
                            bn = 0;
                        }

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }

            if (method.equals("8-farbig")) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;

                        /*
                        Farbe 1: R:255 G:255 B:255 (weiß)
                        Farbe 2: R:180 G:160 B:160 (hellbraun)
                        Farbe 3: R:135 G:110 B:110 (mittelbraun)
                        Farbe 4: R:060 G:050 B:050 (dunkelbraun)
                        Farbe 5: R:155 G:170 B:200 (hellblau)
                        Farbe 6: R:080 G:120 B:190 (dunkelblau)
                        Farbe 7: R:090 G:100 B:100 (baumbraun)
                        Farbe 8: R:155 G:170 B:170 (eisgrau)
                         */

                        int[] weiß = {240, 240, 240};

                        int[] hellbraun = {180, 160, 160};

                        int[] mittelbraun = {135, 110, 110};

                        int[] dunkelbraun = {60, 50, 50};

                        int[] hellblau = {155, 170, 200};

                        int[] dunkelblau = {80, 120, 190};

                        int[] baumbraun = {90, 100, 100};

                        int[] eisgrau = {155, 170, 170};

                        int[][] farben = {weiß, hellbraun, mittelbraun, dunkelbraun, hellblau, dunkelblau, baumbraun, eisgrau};

                        int rn = 0;
                        int gn = 0;
                        int bn = 0;

                        int min = Integer.MAX_VALUE;

                        for (int[] i : farben) {
                            int rx = Math.abs(r - i[0]);
                            int gx = Math.abs(g - i[1]);
                            int bx = Math.abs(b - i[2]);
                            if (rx + gx + bx < min) {
                                min = rx + gx + bx;
                                rn = i[0];
                                gn = i[1];
                                bn = i[2];
                            }
                        }

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
                    }
                }
            }
        }


    } // CustomWindow inner class

    public static int graustufen1(int color) {
        if (color < 255 / 3) {
            return 0;
        } else if (color < 255 / 3 * 2) {
            return 255 / 2;
        }
        return 255;
    }

    public static int graustufen2(int rn) {
        if (rn < 255 / 7) {
            return 0;
        } else if (rn < 255 / 7 * 2) {
            return 255 / 6;
        } else if (rn < 255 / 7 * 3) {
            return 255 / 6 * 2;
        } else if (rn < 255 / 7 * 4) {
            return 255 / 6 * 3;
        } else if (rn < 255 / 7 * 5) {
            return 255 / 6 * 4;
        } else if (rn < 255 / 7 * 6) {
            return 255 / 6 * 5;
        }
        return 255;
    }
}

