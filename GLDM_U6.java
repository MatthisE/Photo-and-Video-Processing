import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class GLDM_U6 implements PlugInFilter {

    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;

    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about")) {
            showAbout();
            return DONE;
        }
        return DOES_RGB + NO_CHANGES;
        // kann RGB-Bilder und veraendert das Original nicht
    }

    public static void main(String args[]) {

        IJ.open("insert your own path here");
        //IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

        GLDM_U6 pw = new GLDM_U6();
        pw.imp = IJ.getImage();
        pw.run(pw.imp.getProcessor());
    }

    public void run(ImageProcessor ip) {

        String[] dropdownmenue = {"Kopie", "Pixelwiederholung", "Bilinear"};

        GenericDialog gd = new GenericDialog("scale");
        gd.addChoice("Methode", dropdownmenue, dropdownmenue[0]);
        gd.addNumericField("Hoehe:", 500, 0);
        gd.addNumericField("Breite:", 400, 0);

        gd.showDialog();

        int height_n = (int) gd.getNextNumber(); // _n fuer das neue skalierte Bild
        int width_n = (int) gd.getNextNumber();

        String choice = gd.getNextChoice();

        int width = ip.getWidth();  // Breite bestimmen
        int height = ip.getHeight(); // Hoehe bestimmen

        //height_n = height;
        //width_n  = width;

        double s_height = (double) height / height_n;
        double s_width = (double) width / width_n;

        ImagePlus neu = NewImage.createRGBImage("Skaliertes Bild",
                width_n, height_n, 1, NewImage.FILL_BLACK);

        ImageProcessor ip_n = neu.getProcessor();


        int[] pix = (int[]) ip.getPixels();
        int[] pix_n = (int[]) ip_n.getPixels();

        if (choice.equals("Kopie")) {

            // Schleife ueber das neue Bild
            for (int y_n = 0; y_n < height_n; y_n++) {
                for (int x_n = 0; x_n < width_n; x_n++) {
                    int y = y_n;
                    int x = x_n;

                    if (y < height && x < width) {
                        int pos_n = y_n * width_n + x_n;
                        int pos = y * width + x;

                        pix_n[pos_n] = pix[pos];
                    }
                }
            }

        }

        if (choice.equals("Pixelwiederholung")) {
            for (int y_n = 0; y_n < height_n; y_n++) {
                for (int x_n = 0; x_n < width_n; x_n++) {
                    int y = Math.round((long) (y_n * s_height));
                    int x = Math.round((long) (x_n * s_width));

                    int pos_n = y_n * width_n + x_n;
                    int pos = y * width + x;

                    pix_n[pos_n] = pix[pos];
                }
            }
        }

        if (choice.equals("Bilinear")) {
            for (int y_n = 0; y_n < height_n; y_n++) {
                for (int x_n = 0; x_n < width_n; x_n++) {
                    double y = y_n * s_height;
                    double x = x_n * s_width;

                    int yi = (int) y;
                    int xi = (int) x;

                    double v = y - yi;
                    double h = x - xi;

                    int pos_n = y_n * width_n + x_n;

                    /*
                    int pos1 = yi * width + xi;
                    int pos2 = yi * width + xi+1;
                    int pos3 = (yi+1) * width + xi;
                    int pos4 = (yi+1) * width + xi+1;
                    }*/

                    int pos1 = yi * width + xi;

                    int pos2;
                    if (xi == width - 1) {
                        pos2 = pos1;
                    } else {
                        pos2 = yi * width + xi + 1;
                    }

                    int pos3;
                    if (yi == height - 1) {
                        pos3 = pos1;
                    } else {
                        pos3 = (yi + 1) * width + xi;
                    }

                    int pos4;
                    if (xi == width - 1 && yi != height - 1) {
                        pos4 = pos3;
                    } else if (xi != width - 1 && yi == height - 1) {
                        pos4 = pos2;
                    } else if (xi == width - 1 && yi == height - 1) {
                        pos4 = pos1;
                    } else {
                        pos4 = (yi + 1) * width + xi + 1;
                    }

                    int A = check(pix[pos1], (1 - h), (1 - v));
                    int B = check(pix[pos2], h, (1 - v));
                    int C = check(pix[pos3], (1 - h), v);
                    int D = check(pix[pos4], h, v);

                    int farbe = A + B + C + D;

                    pix_n[pos_n] = farbe;
                }
            }
        }

        // neues Bild anzeigen
        neu.show();
        neu.updateAndDraw();
    }

    int check(int argb, double x, double y) {
        int r = (argb >> 16) & 0xff;
        int g = (argb >> 8) & 0xff;
        int b = argb & 0xff;

        int rn = ueberlauf(Math.round((long) (r * x * y)));
        int gn = ueberlauf(Math.round((long) (g * x * y)));
        int bn = ueberlauf(Math.round((long) (b * x * y)));

        return (0xFF << 24) | (rn << 16) | (gn << 8) | bn;
    }

    int ueberlauf(int f) {
        if (f > 255) {
            return 255;
        } else if (f < 0) {
            return 0;
        } else {
            return f;
        }
    }

    void showAbout() {
        IJ.showMessage("");
    }
}
