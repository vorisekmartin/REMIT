/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static remit.Util.*;

/**
 *
 * @author Martin
 */
public class ToString {

    static String path;

    /**
     *
     * @param inputPath
     */
    public static boolean readAndMoveBack(String inputPath, String index) {
        path = "/Users/Martin/googleocr" + index + "/in/" + inputPath.substring(inputPath.lastIndexOf("/"));
        //System.out.println("path: " + path);
        Path input = Paths.get(inputPath, "");
        Path output = Paths.get(path, "");
        try {
            Files.copy(input, output, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            System.out.println("repeat");
            Logger.getLogger(ToString.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        execCommand(index);

        boolean res = moveBack(inputPath, index);

        emptyFolders(index);
        return res;
        //System.out.println("TRANSLATED: " + inputPath);
    }

    public static void readAndMoveBack(String inputPath) {
        path = "/Users/Martin/googleocr/in/" + inputPath.substring(inputPath.lastIndexOf("/")>=0?inputPath.lastIndexOf("/"):0);
        //System.out.println("path: " + path);
        Path input = Paths.get(inputPath, "");
        Path output = Paths.get(path, "");
        try {
            Files.copy(input, output, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(ToString.class.getName()).log(Level.SEVERE, null, ex);
        }
        execCommand();
        moveBack(inputPath, "");
        emptyFolders("");
        //System.out.println(text);
    }
//============================================================================================//

    /**
     * Will read image via google drive and return string
     *
     * @param imagePath path to image
     * @param index index of googleOcr
     * @return
     */
    public static String imageToString(String inputPath, String index) {
        path = "/Users/Martin/googleocr" + index + "/in/" + inputPath.substring(inputPath.lastIndexOf("/"));
        //System.out.println("path: " + path);
        //String path = "/Users/Martin/googleocr" + index + "/" + inputPath.substring(inputPath.lastIndexOf("/") + 1) + ".txt";
        Path input = Paths.get(inputPath, "");
        Path output = Paths.get(path, "");
        try {
            Files.copy(input, output, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(ToString.class.getName()).log(Level.SEVERE, null, ex);
        }
        execCommand();
        String text = readFile(inputPath, index);
        emptyFolders(index);
        //System.out.println(text);
        return text;
    }

    public static String imageToString(String inputPath) {
        path = "/Users/Martin/googleocr/in/" + inputPath.substring(inputPath.lastIndexOf("/"));

        Path input = Paths.get(inputPath, "");
        Path output = Paths.get(path, "");
        try {
            Files.copy(input, output, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(ToString.class.getName()).log(Level.SEVERE, null, ex);
        }
        execCommand();
        String text = readFile();
        //emptyFolders();
        //System.out.println(text);
        return text;
    }

//=========================================================================================//
    public static String imageToString(BufferedImage im) {
        path = "/Users/Martin/googleocr/in/pix" + Math.round(Math.random() * 1000000000) + ".png";

        savePicture(im);
        Runtime rt = Runtime.getRuntime();
        Process p;
        try {
            p = rt.exec("/usr/local/bin/node /Users/Martin/googleocr/index.js");
            p.waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ToString.class.getName()).log(Level.SEVERE, null, ex);
        }
        String text = readFile();
        deleteFiles();
        System.out.println(text);
        return text;
    }

    private static void savePicture(BufferedImage im) {
        saveImage(im, path);
    }

    private static String readFile() {

        return readTXT(path.replaceAll("/in/", "/out/") + ".txt");
    }

    private static String readFile(String inputPath, String index) {
        String path = "/Users/Martin/googleocr" + index + "/out/" + inputPath.substring(inputPath.lastIndexOf("/") + 1) + ".txt";
        System.out.println("path: " + path);
        return readTXT(path);
    }

    private static void deleteFiles() {
        File file = new File(path);
        file.delete();
        file = new File(path.replaceAll("/in/", "/out/") + ".txt");
        file.delete();

    }

    /**
     * vyprazdni slozky in a out
     */
    public static void emptyFolders(String index) {
        List<String> in = readAllFilePaths("/Users/Martin/googleocr" + index + "/in");
        List<String> out = readAllFilePaths("/Users/Martin/googleocr" + index + "/out");
        for (String s : in) {
            File f = new File(s);
            f.delete();
        }
        for (String s : out) {
            File f = new File(s);
            f.delete();
        }

    }

    public static void execCommand(String index) {

        Runtime rt = Runtime.getRuntime();
        Process p;
        try {
            p = rt.exec("/usr/local/bin/node /Users/Martin/googleocr" + index + "/index.js");
            p.waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ToString.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void execCommand() {

        Runtime rt = Runtime.getRuntime();
        Process p;
        try {
            p = rt.exec("/usr/local/bin/node /Users/Martin/googleocr/index.js");
            p.waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ToString.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void execCommand(Path input, Path output) {
        Runtime rt = Runtime.getRuntime();
        Process p;
        try {
            p = rt.exec("/usr/local/bin/node /Users/Martin/googleocr/index.js");
            p.waitFor();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ToString.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void moveFile(String inputPath) {
        path = "/Users/Martin/googleocr/in/" + inputPath.substring(inputPath.lastIndexOf("/"));
        System.out.println("path: " + path);
        Path input = Paths.get(inputPath, "");
        Path output = Paths.get(path, "");
        try {
            Files.copy(input, output, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(ToString.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static boolean moveBack(String inputPath, String index) {
        //System.out.println("inputPath "+ inputPath);
        path = "/Users/Martin/googleocr" + index + "/in/" + inputPath.substring(inputPath.lastIndexOf("/")>=0?inputPath.lastIndexOf("/"):0);
        //System.out.println("path: " + path);
        Path input = Paths.get(path.replace("/in/", "/out/") + ".txt", "");
        Path output = Paths.get(inputPath + ".txt", "");
        try {
            //System.out.println("input: "+ input.toAbsolutePath());
            //System.out.println("output: "+ output.toAbsolutePath());
            Files.copy(input, output, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(ToString.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("repeat: -" + index + "-" + inputPath);
            emptyFolders(index);
            //System.out.println("repeat: " + inputPath);
            return false;
        }
        return true;
    }

    /**
     *
     * @param inputPath
     *
     * @param index googleOcr index
     * @param outputFolder FOLDER!!!
     */
    public static boolean readAndMove(String inputPath, String index, String outputFolder) {
        outputFolder = outputFolder.replaceAll("/page/", "/page/check");
        File outputFold = new File(outputFolder);
        if (!outputFold.exists()) {
            outputFold.mkdir();
        }
        path = "/Users/Martin/googleocr" + index + "/in/" + inputPath.substring(inputPath.lastIndexOf("/"));
        //System.out.println("path: " + path);
        Path input = Paths.get(inputPath, "");
        Path output = Paths.get(path, "");
        try {
            Files.copy(input, output, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(ToString.class.getName()).log(Level.SEVERE, null, ex);
            emptyFolders(index);
            System.out.println("repeat: -" + index + "-" + inputPath);
            return false;
        }
        execCommand(index);
        String addit = inputPath.substring(inputPath.lastIndexOf("/"));
        //System.out.println("addit: "+ addit);
        //System.out.println("outputFodler " + outputFolder);
        moveBack(outputFolder + addit, index);
        emptyFolders(index);
        //System.out.println("TRANSLATED in Thread " + index + ": " + inputPath);
        return true;
    }

}
