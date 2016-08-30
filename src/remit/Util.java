/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

/**
 *
 * @author Martin
 */
public class Util {

    public static int IMG_HEIGHT = 30;
    public static int IMG_EXPAND_TOP = 6;
    public static int IMG_EXPAND_LEFT = 10;
    public static String TEMPLATE_PATH = "/Users/Martin/Documents/zakony/pismena/_Templates";
    public static int MATCH_PERCENT = 80;

    public static final String FOLDER_FOOTNOTE = "/footnote";
    public static final String FOLDER_OTHERS = "/other";
    public static final String FOLDER_TITLE = "/title";
    public final static String FOLDER_PNG = "/png";
    public final static String FOLDER_PARA = "/para";
    public final static String FOLDER_COLUMN = "/column";
    public final static String PATH_RULES = "rules.txt";
    public final static String RULES_REGEX = " -> ";

    public static void splitAndSavePDF(String pdfPath) {
        File f = new File(pdfPath.substring(0, pdfPath.lastIndexOf("/") + 1) + "split/");
        if (!f.exists()) {
            f.mkdir();
        }
        String outputPath = pdfPath.substring(0, pdfPath.lastIndexOf("/") + 1) + "split/";
        PDDocument document;
        List<PDDocument> splittedDocuments = null;
        try {
            document = PDDocument.load(new File(pdfPath));

            Splitter splitter = new Splitter();

            splittedDocuments = splitter.split(document);

        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("pdfPath: " + pdfPath);
        if (splittedDocuments != null) {
            int index = 1;
            for (PDDocument page : splittedDocuments) {

                try {
                    page.save(outputPath + "text_" + index + ".pdf");
                } catch (IOException ex) {
                    Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
                }
                index++;
            }
        }
        //System.exit(0);
    }

    //Najde cestu ke všem soubor;m ve slozce a vrátí List s nimi
    public static List<String> readAllFileNames(String s) {
        List<String> files = new ArrayList();
        File folder = new File(s);

        File[] listOfFiles = folder.listFiles();
        //System.out.println("folder " + listOfFiles.length);
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains("Store")) {
                files.add(listOfFiles[i].getName());
                //System.out.println(listOfFiles[i].getName());
            }
        }
        return files;
    }

    public static List<String> getFolders(String directoryName) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        List<String> result = new ArrayList();
        for (File file : fList) {
            if (file.isDirectory()) {
                result.add(file.getAbsolutePath());

            }
        }

        return result;

    }

    //Zadá se složka, vrátí to list všech soubroů včetně jejich kompletní cesty k souboru
    public static List<String> readAllFilePaths(String folderPath) {
        List<String> files = new ArrayList();
        File folder = new File(folderPath);

        File[] listOfFiles = folder.listFiles();
        //System.out.println("folder " + listOfFiles.length);
        if (listOfFiles == null) {
            return files;
        }
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && !listOfFiles[i].getName().contains("Store")) {
                files.add(folderPath + "/" + listOfFiles[i].getName());
                //System.out.println(listOfFiles[i].getName());
            }
        }
        return files;
    }

    //Zadá se složka, vrátí to list všech soubroů včetně jejich kompletní cesty k souboru
    public static List<String> readAllFilePathsCondition(String folderPath, String condition) {
        List<String> files = new ArrayList();
        File folder = new File(folderPath);

        File[] listOfFiles = folder.listFiles();
        //System.out.println("folder " + listOfFiles.length);
        if (listOfFiles == null) {
            return files;
        }
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()
                    && listOfFiles[i].getName().contains("Store")
                    && listOfFiles[i].getName().contains(condition)) {
                files.add(folderPath + "/" + listOfFiles[i].getName());
                //System.out.println(listOfFiles[i].getName());
            }
        }
        return files;
    }

    public static String readTXT(String path) {
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            System.out.println("PATH NOT EXIST: " + path + " ");
            return "";
        }
        BufferedReader br = null;

        try {
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileDir), "UTF8"));
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }

        String everything = "";
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
            line = null;
            sb = null;
        } catch (IOException ex) {

        } finally {
            try {
                br.close();
            } catch (IOException ex) {

            }
        }
        fileDir = null;
        //System.out.println(everything);
        return everything;
    }

    public static String readPDF(String filepath) {
        String st = "";
        try {
            PDDocument document = null;
            try {
                document = PDDocument.load(new File(filepath));

            } catch (IOException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            }
            document.getClass();
            PDFTextStripperByArea stripper = null;
            st = "";
            stripper = new PDFTextStripperByArea();
            stripper.setSortByPosition(true);
            PDFTextStripper Tstripper;
            Tstripper = new PDFTextStripper();
            st = Tstripper.getText(document);
            //System.out.println("Text:" + st);

        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return st;

    }

    public static void saveTXT(String path, String text) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(path, "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        writer.println(text);
        writer.close();

    }

    // Vrátí barvy 
    public static int[][] getPixelArray(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();
        final boolean hasAlphaChannel = image.getAlphaRaster() != null;

        int[][] result = new int[height][width];
        if (hasAlphaChannel) {
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // blue
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
                result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        } else {
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; // 255 alpha
                argb += ((int) pixels[pixel] & 0xff); // blue
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
                //System.out.println(row+ " " + col+"  " +result.length + " "+ result[0].length);
                try {
                    result[row][col] = argb;
                } catch (ArrayIndexOutOfBoundsException e) {
                    //System.out.println("============================= Catch ===========================");
                    //System.out.println(row + " " + col + "  " + result.length + " " + result[0].length + " pixels.length " + pixels.length + " piels " + pixel);
                    //System.out.println("============================= Catch ===========================");
                    Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, e);
                    //System.exit(0);
                }
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }
        }

        return result;
    }

    public static void saveImage(BufferedImage img, String path) {

        System.out.println(path);
        File outputfile = new File(path);
        if (!outputfile.exists()) {
            File dir = new File(path.substring(0, path.lastIndexOf("/")));
            //System.out.println(path.substring(0, path.lastIndexOf("/")));
            dir.mkdir();
        }

        try {
            ImageIO.write(img, "png", outputfile);
        } catch (Exception ex) {
            System.out.println("ERROR PATH: " + path);
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void saveImage(BufferedImage img, String path, boolean noticePath) {
        if (noticePath) {
            System.out.println(path);
        }

        File outputfile = new File(path);
        if (!outputfile.exists()) {
            File dir = new File(path.substring(0, path.lastIndexOf("/")));
            //System.out.println(path.substring(0, path.lastIndexOf("/")));
            dir.mkdir();
        }
        try {
            ImageIO.write(img, "png", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static BufferedImage readImage(String path) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
        } catch (IOException ex) {
            System.out.println("=============PATH BROKEN" + path);
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return img;
    }

    public static void drawLine(BufferedImage img, int x1, int y1, int x2, int y2) {
        Dimension imgDim = new Dimension(200, 200);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.red);
        g2d.drawLine(x1, y1, x2, y2);
    }

    /**
     * scale image
     *
     * @param sbi image to scale
     * @param imageType type of image
     * @param p
     * @param dWidth width of destination image
     * @param dHeight height of destination image
     * @param fWidth x-factor for transformation / scaling
     * @param fHeight y-factor for transformation / scaling
     * @return scaled image
     */
    public static BufferedImage scaleToWidth(BufferedImage sbi, int imageType, int dWidth) {
        double scaleFactor = (double) dWidth / sbi.getWidth();
        //System.out.println(scaleFactor);
        int dHeight = (int) (sbi.getHeight() * scaleFactor);
        BufferedImage dbi = null;
        if (sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
            g.drawRenderedImage(sbi, at);

        }
        return dbi;
    }

    /**
     * scale image
     *
     * @param sbi image to scale
     * @param imageType type of image
     * @param dWidth width of destination image
     * @param dHeight height of destination image
     * @param fWidth x-factor for transformation / scaling
     * @param fHeight y-factor for transformation / scaling
     * @return scaled image
     */
    public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
        BufferedImage dbi = null;
        if (sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
            g.drawRenderedImage(sbi, at);

        }
        return dbi;
    }

    /**
     * scale image
     *
     * @param sbi image to scale
     * @param imageType type of image
     * @param dWidth width of destination image
     * @param dHeight height of destination image
     * @param fWidth x-factor for transformation / scaling
     * @param fHeight y-factor for transformation / scaling
     * @return scaled image
     */
    public static BufferedImage scale(BufferedImage sbi, int imageType, double fWidth, double fHeight) {
        BufferedImage dbi = null;
        int dWidth = (int) ((int) sbi.getWidth() * fWidth);
        int dHeight = (int) ((int) sbi.getHeight() * fHeight);
        if (sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
            g.drawRenderedImage(sbi, at);

        }
        return dbi;
    }

    public static BufferedImage scale(BufferedImage sbi, int imageType, int dHeight) {
        double scaleFactor = (double) dHeight / sbi.getHeight();
        //System.out.println(scaleFactor);
        int dWidth = (int) (sbi.getWidth() * scaleFactor);
        BufferedImage dbi = null;
        if (sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
            g.drawRenderedImage(sbi, at);

        }
        return dbi;
    }

    public static BufferedImage scale(BufferedImage sbi, int imageType, int dHeight, int dWidth) {
        double scaleFactor = (double) dHeight / sbi.getHeight();
        //System.out.println(scaleFactor);
        BufferedImage dbi = null;
        if (sbi != null) {
            dbi = new BufferedImage(dWidth, dHeight, imageType);
            Graphics2D g = dbi.createGraphics();
            AffineTransform at = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
            g.drawRenderedImage(sbi, at);

        }
        return dbi;
    }

    public static int[][] getArrayOfBlack(BufferedImage im) {
        Util.saveImage(im, "/Users/Martin/test.png", false);
        im = Util.readImage("/Users/Martin/test.png");

        int pixels[][] = Util.getPixelArray(im);
        int result[][] = new int[pixels.length][pixels[0].length];
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                if (pixels[i][j] != -1) {
                    result[i][j] = 1;
                }
            }
        }
        return result;

    }

    public static int[][] getArrayOfBlack(int[][] pixels) {

        int result[][] = new int[pixels.length][pixels[0].length];
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                if (pixels[i][j] != -1) {
                    result[i][j] = 1;
                }
            }
        }
        return result;

    }

    public static int[][] enlargeImage(int[][] imagePixel, int topExpand, int leftExpand, int top, int left) {

        int[][] pixels = new int[imagePixel.length + topExpand][imagePixel[0].length + leftExpand];
        // Postupne pridava radky resp prazdne sloupce
        for (int i = 0; i < top; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                pixels[i][j] = 0;
            }
        }
        for (int i = 0; i < topExpand - top; i++) {
            for (int j = 0; j < pixels[pixels.length - i - 1].length; j++) {
                pixels[pixels.length - i - 1][j] = 0;
            }
        }
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < left; j++) {
                pixels[i][j] = 0;
            }
        }
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < leftExpand - left; j++) {
                pixels[i][pixels[i].length - j - 1] = 0;
            }
        }
        for (int i = 0; i < imagePixel.length; i++) {
            for (int j = 0; j < imagePixel[i].length; j++) {
                pixels[i + top][j + left] = imagePixel[i][j];
            }
        }

        return pixels;
    }

    public static int[][] enlargeImage(BufferedImage img, int topExpand, int leftExpand, int top, int left) {
        //int expand = 10;
        Util.saveImage(img, "/Users/Martin/test.png", false);
        img = Util.readImage("/Users/Martin/test.png");
        int[][] imagePixel = Util.getPixelArray(img);
        int[][] pixels = new int[imagePixel.length + topExpand][imagePixel[0].length + leftExpand];
        // Postupne pridava radky resp prazdne sloupce
        for (int i = 0; i < top; i++) {
            for (int j = 0; j < pixels[i].length; j++) {
                pixels[i][j] = -1;
            }
        }
        for (int i = 0; i < topExpand - top; i++) {
            for (int j = 0; j < pixels[pixels.length - i - 1].length; j++) {
                pixels[pixels.length - i - 1][j] = -1;
            }
        }
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < left; j++) {
                pixels[i][j] = -1;
            }
        }
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < leftExpand - left; j++) {
                pixels[i][pixels[i].length - j - 1] = -1;
            }
        }
        for (int i = 0; i < imagePixel.length; i++) {
            for (int j = 0; j < imagePixel[i].length; j++) {
                pixels[i + top][j + left] = imagePixel[i][j];
            }
        }

        return pixels;
    }

    public static BufferedImage arrayToImg(int[][] input) {
        BufferedImage result = new BufferedImage(input[0].length, input.length, BufferedImage.TYPE_3BYTE_BGR);
        for (int i = 0; i < input[0].length; i++) {
            for (int j = 0; j < input.length; j++) {
                if (input[j][i] != 0) {
                    result.setRGB(i, j, 16777215);
                }

            }
        }
        return result;

    }

    public static HashMap<String, HashMap> readHash(String input) {
        HashMap<String, HashMap> result = new HashMap();
        String[] split = input.split("----");

        HashMap<String, int[][]> tempHash = new HashMap();
        HashMap<String, Dimension> dimHash = new HashMap();
        HashMap<String, String> letterHash = new HashMap();
        HashMap<String, Integer> sizeHash = new HashMap();

        tempHash.put("template", templateReadText(split[1]));

        dimHash.put("dimension", getDimension(split[2]));
        //System.out.println("saving-letter: "+split[i].substring(split[i].indexOf("\n")).replace("\n", ""));

        letterHash.put("letter", split[3].replaceAll("\n", ""));
        //System.out.println("saving-letter: " + split[3].substring(split[3].indexOf("\n")).replace("\n", ""));

        sizeHash.put("size", Integer.parseInt(split[4].replaceAll("\n", "")));
        //System.out.println("saving: "+split[i].substring(split[i].indexOf("\n")).replace("\n", ""));

        result.put("template", tempHash);
        result.put("dimension", dimHash);
        result.put("letter", letterHash);
        result.put("size", sizeHash);

        //System.out.println("-------------------------");
        return result;
    }

    private static Dimension getDimension(String string) {

        int width = Integer.parseInt(string.substring(string.indexOf("width=") + "width=".length(), string.lastIndexOf(",")));
        int height = Integer.parseInt(string.substring(string.indexOf("height=") + "height=".length(), string.lastIndexOf("]")));
        Dimension result = new Dimension(width, height);
        //System.out.println("dim: " + result);
        return result;
    }

    private static int[][] templateReadText(String input) {
        String[] split = input.split("\n");
        int height = 0;
        int width = 0;
        List<String> list = new ArrayList<String>(Arrays.asList(split));

        for (int i = 0; i < split.length; i++) {
            if (split[i].contains("[")) {

            } else {
                list.remove(split[i]);

            }
        }
        split = list.toArray(new String[0]);

        //System.exit(width);
        int[][] result = new int[split.length][split[0].split(", ").length];
        for (int i = 0; i < split.length; i++) {
            String[] row = split[i].split(", ");
            //System.out.println(split.length + "--" + height);

            for (int j = 0; j < row.length; j++) {
                //System.out.println(j + "  " + i);
                //System.out.println(Arrays.toString(row));
                result[i][j] = Integer.parseInt(row[j].replace("[", "").replace("]", ""));
            }

        }
        for (int i = 0; i < result.length; i++) {
            //System.out.println(Arrays.toString(result[i]));
        }
        return result;
    }

    public static BufferedImage cropImage(BufferedImage img1) {
        Util.saveImage(img1, "/Users/Martin/test.png", false);
        img1 = Util.readImage("/Users/Martin/test.png");
        int[][] pix = Util.getPixelArray(img1);
        int start = 0;
        int end = 0;
        for (int i = 0; i < pix.length; i++) {
            boolean check = false;
            for (int j = 0; j < pix[i].length; j++) {
                if (pix[i][j] != -1) {
                    check = true;
                    break;
                }
            }
            if (check) {
                start = i;
                break;
            }
        }
        //Cropne spodek
        for (int i = pix.length - 1; i >= 0; i--) {
            boolean check = false;
            for (int j = 0; j < pix[i].length; j++) {
                if (pix[i][j] != -1) {
                    check = true;
                    break;
                }
            }
            if (check) {
                end = i;
                break;
            }
        }
        BufferedImage result = img1;
        if (end - start > 0) {
            result = img1.getSubimage(0, start, img1.getWidth(), end - start);
        } else {

            result = img1.getSubimage(0, 0, img1.getWidth(), img1.getHeight());
        }

        Util.saveImage(result, "/Users/Martin/temp.png", false);
        result = Util.readImage("/Users/Martin/temp.png");
        return result;
    }
    /*
     Upraví obrázky písmen do takové formy, aby je mohla přečíst metoda ReadLetter.main
     výstup z SeparateWords, vstup pro ReadLetter
     */

    public static BufferedImage prepareLetters(BufferedImage img) {
        img = Util.cropImage(img);
        img = Util.scale(img, BufferedImage.TYPE_3BYTE_BGR, Util.IMG_HEIGHT);
        return img;
    }

    public static List<BufferedImage> prepareLetterList(List<BufferedImage> imgs) {
        List<BufferedImage> result = new ArrayList();
        for (int i = 0; i < imgs.size(); i++) {
            result.add(Util.prepareLetters(imgs.get(i)));
        }
        return result;

    }

    /**
     * Method read all templates with parametrs - Hashmap in list have params
     * "dimension", "template","size" and "letter"
     *
     *
     * @return list with HashMap for every template
     */
    public static List<HashMap<String, HashMap>> readTemplates() {
        List<HashMap<String, HashMap>> templates = new ArrayList();

        List<String> filePaths = Util.readAllFilePaths(Util.TEMPLATE_PATH);
        List<BufferedImage> imgs = new ArrayList();

        for (int i = 0; i < filePaths.size(); i++) {
            HashMap<String, HashMap> template = Util.readHash(Util.readTXT(filePaths.get(i)));
            //int[][] a = (int[][]) template.get("template").get("template");
            templates.add(template);

        }
        return templates;
    }

    public static BufferedImage scaleLetters(BufferedImage img) {
        img = Util.scale(img, BufferedImage.TYPE_3BYTE_BGR, Util.IMG_HEIGHT);
        return img;
    }

    public static List<String> sort(List<String> filePaths) {
        HashMap<Integer, String> h = new HashMap();
        for (int i = 0; i < filePaths.size(); i++) {
            String key = filePaths.get(i).substring(filePaths.get(i).lastIndexOf("page") + "page".length(), filePaths.get(i).lastIndexOf(".png"));
            //System.out.println(key);
            h.put(Integer.parseInt(key), filePaths.get(i));
        }
        List<String> result = new ArrayList();
        List<Integer> list = new ArrayList<Integer>(h.keySet());
        Collections.sort(list);
        for (Integer i : list) {
            result.add(h.get(i));
        }

        return result;
    }

    /**
     * Oddeli priponu souboru, veme číslo těsně před příponou a podle toho čísla
     * to setřídí
     *
     * @param filePaths
     * @return
     */
    public static List<String> sortByLastNumber(List<String> filePaths) {
        HashMap<Integer, String> h = new HashMap();
        //System.out.println("size"+ filePaths.size());
        for (String path : filePaths) {
            //System.out.println(path);
            String key = path.substring(path.lastIndexOf("/") + "/".length(), path.lastIndexOf("."));
            int index = 0;
            for (int i = key.length() - 1; i >= 0; i--) {
                if (!Character.isDigit(key.charAt(i))) {
                    index = i;
                }
            }
            key = key.substring(index);
            //System.out.println("key "+ key);
            h.put(Integer.parseInt(key), path);
        }
        List<String> result = new ArrayList();
        List<Integer> list = new ArrayList<Integer>(h.keySet());
        Collections.sort(list);
        for (Integer i : list) {
            //System.out.println(h.get(i));
            result.add(h.get(i));
        }

        return result;
    }

    public static BufferedImage rectangleCropImage(BufferedImage img, Rectangle rect) {
        return img.getSubimage(rect.x, rect.y, rect.width, rect.height);
    }

    public static BufferedImage mergeImages(BufferedImage img1, BufferedImage img2) {

        int width = Math.max(img1.getWidth(), img2.getWidth());
        int height = img1.getHeight() + img2.getHeight();
        BufferedImage combination = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = combination.getGraphics();
        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, 0, img1.getHeight(), null);
        return combination;
    }

    /**
     * Index nejakeho znaku a jeho poradi / napr chci druhou mezeru
     *
     *
     * @param str
     * @param c
     * @param n
     * @return
     */
    public static int ordinalIndexOf(String str, char c, int n) {
        int pos = str.indexOf(c, 0);
        while (n-- > 0 && pos != -1) {
            pos = str.indexOf(c, pos + 1);
        }
        return pos;
    }

    /**
     * Index nejakeho znaku a jeho poradi / napr chci druhou mezeru
     *
     *
     * @param str
     * @param c
     * @param n
     * @return
     */
    public static int ordinalIndexOf(String str, String s, int n) {
        int pos = str.indexOf(s, 0);
        while (n-- > 0 && pos != -1) {
            pos = str.indexOf(s, pos + 1);
        }
        return pos;
    }

    /**
     *
     *
     * @param path - path k pdfku
     *
     */
    public static void convertPDFtoPng(String path) {

        File dir = new File(path.substring(0, path.lastIndexOf("/")) + "/page/");
        if (!dir.exists()) {
            dir.mkdir();
        }

        //List<Integer> relevantPages = getRelevantPages(path);
        try {
            PDDocument document = PDDocument.load(new File(path));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB); //or renderImageWithDPI()
                bim = modifyPage(bim);
                ImageIOUtil.writeImage(bim, path.substring(0, path.lastIndexOf("/")) + "/page/page" + (page) + ".png", 300);
            }
            document.close();
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static BufferedImage modifyPage(BufferedImage input) {

        //int pixels[][] = Util.getPixelArray(input);
        int count = 0;
        int begin = 0;
        int limit = 100;
        for (int i = 0; i < input.getWidth(); i++) {
            for (int j = 0; j < input.getHeight(); j++) {

                if (input.getRGB(i, j) != -1) {
                    count++;
                }
                if (count > limit) {

                    break;
                }
            }
            if (count > limit) {
                begin = i;
                break;

            }
        }
        count = 0;
        int end = 0;
        for (int i = input.getWidth() - 1; i >= 0; i--) {
            for (int j = 0; j < input.getHeight(); j++) {
                if (input.getRGB(i, j) != -1) {
                    count++;
                }
                if (count > limit) {
                    break;
                }
            }
            if (count > limit) {
                end = i;
                break;
            }
        }

        BufferedImage output = null;
        if ((end + begin >= input.getWidth())) {
            return input;
        }
        //System.out.println("ChangedWidth = "+ (end+begin));
        if (end + begin <= 0) {

        }
        return input.getSubimage(0, 0, (end + begin) <= 0 ? 1 : (end + begin), input.getHeight());
    }

    /**
     * Precte vsechnz soubory vcetne souboru v podslozkach
     *
     * @param directoryName
     * @param files
     * @return
     */
    public static List<String> readAllFilePathsSubfolders(String directoryName, ArrayList<String> files) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file.getPath());
            } else if (file.isDirectory()) {
                readAllFilePathsSubfolders(file.getAbsolutePath(), files);
            }
        }
        return files;
    }

    /**
     * Precte vsechnz soubory vcetne souboru v podslozkach
     *
     * @param directoryName
     * @param mustContain nuntý znak v naźvu souboru
     * @param files
     * @return
     */
    public static List<String> readFilesInSubfolderCondition(String directoryName, String mustContain, ArrayList<String> files) {
        File directory = new File(directoryName);
        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile() && file.getPath().contains(mustContain)) {
                //System.out.println("path UTIL: " + file.getPath());
                files.add(file.getPath());
            } else if (file.isDirectory()) {
                readFilesInSubfolderCondition(file.getAbsolutePath(), mustContain, files);
            }
        }
        return files;
    }

    /**
     * check, if in string are only numbers
     *
     * @param input Input string
     * @return
     */
    public static boolean onlyNumbers(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static int occurenceNumber(String text, String part) {
        int count = 0;
        while (text.contains(part)) {
            text = text.substring(text.indexOf(part) + part.length());
            count++;

        }

        return count;
    }

    public static boolean isUpperCase(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLowerCase(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }

    public static List<String> readFilesInSubfolderCondition(String directoryName, String[] mustContain, ArrayList<String> files) {
        File directory = new File(directoryName);
        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isDirectory()) {
                readFilesInSubfolderCondition(file.getAbsolutePath(), mustContain, files);
                continue;
            }
            boolean check = true;
            for (String must : mustContain) {
                if (!file.getPath().contains(must)) {
                    check = false;
                    break;
                }
            }
            if (file.isFile() && check) {
                files.add(file.getPath());
            }

        }
        return files;
    }

    public static List<String> removeDuplicate(List<String> input) {
        Set<String> hs = new LinkedHashSet<>(input);
        hs.addAll(input);
        input.clear();
        input.addAll(hs);
        return input;
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static BufferedImage getSubImage(BufferedImage source, Rectangle rect) {
        BufferedImage b = new BufferedImage(rect.width, rect.height, source.getType());
        source = source.getSubimage(rect.x, rect.y, rect.width, rect.height);
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public static BufferedImage rotate90ToLeft(BufferedImage inputImage) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage returnImage = new BufferedImage(height, width, inputImage.getType());
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                returnImage.setRGB(y, width - x - 1, inputImage.getRGB(x, y)
                );
            }
        }
        return returnImage;

    }

    static void getMemory() {
        System.out.println("Available processors (cores): "
                + Runtime.getRuntime().availableProcessors());

        /* Total amount of free memory available to the JVM */
        System.out.println("Free memory (MB): "
                + Runtime.getRuntime().freeMemory()/1000000);

        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (MB): "
                + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory/1000000));

        /* Total memory currently in use by the JVM */
        System.out.println("Total memory (MB): "
                + Runtime.getRuntime().totalMemory()/1000000);
    }

}
