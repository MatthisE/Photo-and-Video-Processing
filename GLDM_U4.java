import ij.*;
import ij.io.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;


public class GLDM_U4 implements PlugInFilter {

    protected ImagePlus imp;
    final static String[] choices = {"Wischen", "Weiche Blende", "Overlay 1", "Overlay 2", "Schieben", "Chroma-Keying", "Überlagerung", "Überlagerung 2", "Überlagerung 3"};

    public int setup(String arg, ImagePlus imp) {
        this.imp = imp;
        return DOES_RGB+STACK_REQUIRED;
    }

    public static void main(String args[]) {
        ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
        ij.exitWhenQuitting(true);

        IJ.open("insert your own path here /StackB.zip");
        //IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");


        GLDM_U4 sd = new GLDM_U4();
        sd.imp = IJ.getImage();
        ImageProcessor B_ip = sd.imp.getProcessor();
        sd.run(B_ip);
    }

    public void run(ImageProcessor B_ip) {
        // Film B wird uebergeben
        ImageStack stack_B = imp.getStack();

        int length = stack_B.getSize();
        int width  = B_ip.getWidth();
        int height = B_ip.getHeight();

        // ermoeglicht das Laden eines Bildes / Films
        Opener o = new Opener();
        OpenDialog od_A = new OpenDialog("Auswählen des 2. Filmes ...",  "insert your own path here /StackA.zip");

        // Film A wird dazugeladen
        String dateiA = od_A.getFileName();
        if (dateiA == null) return; // Abbruch
        String pfadA = od_A.getDirectory();
        ImagePlus A = o.openImage(pfadA,dateiA);
        if (A == null) return; // Abbruch

        ImageProcessor A_ip = A.getProcessor();
        ImageStack stack_A  = A.getStack();

        if (A_ip.getWidth() != width || A_ip.getHeight() != height)
        {
            IJ.showMessage("Fehler", "Bildgrößen passen nicht zusammen");
            return;
        }

        // Neuen Film (Stack) "Erg" mit der kleineren Laenge von beiden erzeugen
        length = Math.min(length,stack_A.getSize());

        ImagePlus Erg = NewImage.createRGBImage("Ergebnis", width, height, length, NewImage.FILL_BLACK);
        ImageStack stack_Erg  = Erg.getStack();

        // Dialog fuer Auswahl des Ueberlagerungsmodus
        GenericDialog gd = new GenericDialog("Überlagerung");
        gd.addChoice("Methode",choices,"");
        gd.showDialog();

        int methode = 0;
        String s = gd.getNextChoice();
        if (s.equals("Wischen")) methode = 1;
        if (s.equals("Weiche Blende")) methode = 2;
        if (s.equals("Overlay 1")) methode = 3;
        if (s.equals("Overlay 2")) methode = 32;
        if (s.equals("Schieben")) methode = 4;
        if (s.equals("Chroma-Keying")) methode = 5;
        if (s.equals("Überlagerung")) methode = 6;
        if (s.equals("Überlagerung 2")) methode = 62;
        if (s.equals("Überlagerung 3")) methode = 63;


        // Arrays fuer die einzelnen Bilder
        int[] pixels_B;
        int[] pixels_A;
        int[] pixels_Erg;

        // Schleife ueber alle Bilder
        for (int z=1; z<=length; z++)
        {
            pixels_B   = (int[]) stack_B.getPixels(z);
            pixels_A   = (int[]) stack_A.getPixels(z);
            pixels_Erg = (int[]) stack_Erg.getPixels(z);

            int pos = 0;
            for (int y=0; y<height; y++)
                for (int x=0; x<width; x++, pos++)
                {
                    int cA = pixels_A[pos];
                    int rA = (cA & 0xff0000) >> 16;
                    int gA = (cA & 0x00ff00) >> 8;
                    int bA = (cA & 0x0000ff);

                    int cB = pixels_B[pos];
                    int rB = (cB & 0xff0000) >> 16;
                    int gB = (cB & 0x00ff00) >> 8;
                    int bB = (cB & 0x0000ff);

                    if (methode == 1)
                    {
                        if (y+1 > (z-1)*(double)height/(length-1))
                            pixels_Erg[pos] = pixels_B[pos];
                        else
                            pixels_Erg[pos] = pixels_A[pos];
                    }

                    if (methode == 2)
                    {
                        //c(t, x, y) =α(t)⋅ a(t, x, y) + [1−α(t)]⋅ b(t, x, y)

                        int r = (z*rA + (95-z)*rB)/95;
                        int g = (z*gA + (95-z)*gB)/95;
                        int b = (z*bA + (95-z)*bB)/95;

                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
                    }

                    if (methode == 3)
                    {
                        //für Rh <= 128: Re = Rv*Rh/128
                        //sonst:         Re = 255-((255-Rv)*(255-Rh)/128)

                        int r;
                        int g;
                        int b;

                        if(rB<=128){
                            r=rA*rB/128;
                        }
                        else{
                            r=255-((255-rA)*(255-rB)/128);
                        }
                        if(gB<=128){
                            g=gA*gB/128;
                        }
                        else{
                            g=255-((255-gA)*(255-gB)/128);
                        }
                        if(bB<=128){
                            b=bA*bB/128;
                        }
                        else{
                            b=255-((255-bA)*(255-bB)/128);
                        }

                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
                    }

                    if (methode == 32)
                    {
                        //für Rh <= 128: Re = Rv*Rh/128
                        //sonst:         Re = 255-((255-Rv)*(255-Rh)/128)

                        int r;
                        int g;
                        int b;

                        if(rA<=128){
                            r=rA*rB/128;
                        }
                        else{
                            r=255-((255-rA)*(255-rB)/128);
                        }
                        if(gA<=128){
                            g=gA*gB/128;
                        }
                        else{
                            g=255-((255-gA)*(255-gB)/128);
                        }
                        if(bA<=128){
                            b=bA*bB/128;
                        }
                        else{
                            b=255-((255-bA)*(255-bB)/128);
                        }

                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
                    }

                    if (methode == 4)
                    {
                        //(y+1 > (z-1)*(double)height/(length-1))

                        int schieb = (int)((z-1)*(double)width/(length-1));

                        if (x+1 > schieb)
                            pixels_Erg[pos] = pixels_B[pos-schieb];
                        else
                            pixels_Erg[pos] = pixels_A[pos+width-schieb];
                    }

                    if (methode == 5)
                    {
                        // rA 255-150
                        // gA 225-105
                        // bA 155-0

                        int r;
                        int g;
                        int b;

                        if(rA>=150 && (gA>=105||gA<=225) && (bA<=155)){
                            r = rB;
                            b = bB;
                            g = gB;
                        }
                        else{
                            r = rA;
                            b = bA;
                            g = gA;
                        }

                        /*int r = (0*rA + (95-0)*rB)/95;
                        int g = (0*gA + (95-0)*gB)/95;
                        int b = (0*bA + (95-0)*bB)/95;*/

                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
                    }

                    if (methode == 6){

                        int r;
                        int g;
                        int b;

                        if((x-(width/2))*(x-(width/2))+(y-(height/2))*(y-(height/2))<=z/2*z*z){
                        //if(x>2*(z-5) && x<width-2*(z-5) && y>2*(z-5) && y<height-2*(z-5)){
                        //if(y<((-1)*height*x/width +8*z -10)){
                            r = rB;
                            b = bB;
                            g = gB;
                        }
                        else{
                            r = rA;
                            b = bA;
                            g = gA;
                        }
                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
                    }

                    if (methode == 62){

                        int r;
                        int g;
                        int b;

                        //if((x-(width/2))*(x-(width/2))+(y-(height/2))*(y-(height/2))<=z/2*z*z){
                        if(x>2*(z-5) && x<width-2*(z-5) && y>2*(z-5) && y<height-2*(z-5)){
                        //if(y<((-1)*height*x/width +8*z -10)){
                            r = rB;
                            b = bB;
                            g = gB;
                        }
                        else{
                            r = rA;
                            b = bA;
                            g = gA;
                        }
                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
                    }

                    if (methode == 63){

                        int r;
                        int g;
                        int b;

                        //if((x-(width/2))*(x-(width/2))+(y-(height/2))*(y-(height/2))<=z/2*z*z){
                        //if(x>2*(z-5) && x<width-2*(z-5) && y>2*(z-5) && y<height-2*(z-5)){
                        if(y<((-1)*height*x/width +8*z -10)){
                            r = rB;
                            b = bB;
                            g = gB;
                        }
                        else{
                            r = rA;
                            b = bA;
                            g = gA;
                        }
                        pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
                    }
                    
                }
        }

        // neues Bild anzeigen
        Erg.show();
        Erg.updateAndDraw();

    }

}
