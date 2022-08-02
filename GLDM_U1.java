package U1;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

//erste Uebung (elementare Bilderzeugung)

public class GLDM_U1 implements PlugIn {

    final static String[] choices = {
            "Schwarzes Bild",
            "Netherlands",
            "Tropical",
            "Disco",
            "Bhutan",
            "Grönland",

    };

    private String choice;

    public static void main(String args[]) {
        ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
        ij.exitWhenQuitting(true);

        GLDM_U1 imageGeneration = new GLDM_U1();
        imageGeneration.run("");
    }

    public void run(String arg) {

        int width = 566; // Breite
        int height = 400; // Hoehe

// RGB-Bild erzeugen
        ImagePlus imagePlus = NewImage.createRGBImage("GLDM_U1_582101", width, height, 1, NewImage.FILL_BLACK);
        ImageProcessor ip = imagePlus.getProcessor();

// Arrays fuer den Zugriff auf die Pixelwerte
        int[] pixels = (int[])ip.getPixels();

        dialog();

////////////////////////////////////////////////////////////////
// Hier bitte Ihre Aenderungen / Erweiterungen

        if ( choice.equals("Schwarzes Bild") ) {
            generateBlackImage(width, height, pixels);
        }

        if ( choice.equals("Netherlands") ) {
            generateNetherlands(width, height, pixels);
        }

        if ( choice.equals("Tropical") ) {
            generateTropical(width, height, pixels);
        }

        if ( choice.equals("Disco") ) {
            generateDisco(width, height, pixels);
        }

        if ( choice.equals("Bhutan") ) {
            generateBhutan(width, height, pixels);
        }

        if ( choice.equals("Grönland") ) {
            generateGroenland(width, height, pixels);
        }
////////////////////////////////////////////////////////////////////

// neues Bild anzeigen
        imagePlus.show();
        imagePlus.updateAndDraw();
    }

    private void generateBlackImage(int width, int height, int[] pixels) {
// Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
// Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                int r = 0;
                int g = 0;
                int b = 0;

// Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }
    }

    private void generateNetherlands(int width, int height, int[] pixels) {
// Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
// Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                int r = 255;
                int g = 255;
                int b = 255;

                if (y <= 133) {
                    r = 255;
                    g = 0;
                    b = 0;
                }

                if (y >= 266) {
                    r=0;
                    g=0;
                    b=255;
                }
// Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }
    }

    private void generateTropical(int width, int height, int[] pixels) {
// Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
// Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                int r = 255*x/(width-1);
                int g = 255;
                int b = 255-r;


// Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }
    }

    private void generateDisco(int width, int height, int[] pixels) {
// Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
// Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                int r = 255*x/(width-1);
                int g = 0;
                int b = 255*y/(height-1);

// Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }
    }

    private void generateBhutan(int width, int height, int[] pixels) {
// Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
// Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                int d = (-1)*height*x/width+height;

                int r;
                int g;
                int b;

                if(y<d){
                    r = 255;
                    g = 255;
                    b = 0;
                }
                else{
                    r = 255;
                    g = 125;
                    b = 0;
                }

// Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }
    }

    private void generateGroenland(int width, int height, int[] pixels) {
// Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
// Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                int r;
                int g;
                int b;

                if(y<height/2){
                    if((x-(width/3))*(x-(width/3))+(y-(height/2))*(y-(height/2))<=(height/3)*(height/3)){
                        r = 255;
                        g = 0;
                        b = 0;
                    }
                    else{
                        r = 255;
                        g = 255;
                        b = 255;
                    }
                }
                else{
                    if((x-(width/3))*(x-(width/3))+(y-(height/2))*(y-(height/2))<=(height/3)*(height/3)){
                        r = 255;
                        g = 255;
                        b = 255;
                    }
                    else{
                        r = 255;
                        g = 0;
                        b = 0;
                    }
                }

// Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
            }
        }
    }

    private void dialog() {
// Dialog fuer Auswahl der Bilderzeugung
        GenericDialog gd = new GenericDialog("Bildart");

        gd.addChoice("Bildtyp", choices, choices[0]);


        gd.showDialog(); // generiere Eingabefenster

        choice = gd.getNextChoice(); // Auswahl uebernehmen

        if (gd.wasCanceled())
            System.exit(0);
    }
}