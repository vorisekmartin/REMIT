/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remit.generation;

import com.snowtide.PDF;
import com.snowtide.pdf.Document;
import com.snowtide.pdf.OutputTarget;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import remit.REMIT;
import remit.ToString;
import remit.Util;

/**
 *
 * @author Martin
 */
public class GenerationNuclear {

    static String path = "https://eportal.cezdata.cz/mtprovider/genfile?dt=sv&tp=je&from=[DATE]&lang=cs";
    static String outputPath = "PDF/";
    static boolean downloadAll = true;

    public static void update() {
        if (downloadAll) {
            List<String> paths = getPaths();
            downloadPDF(paths);
            translate();
        }
        checkIfAllTranslated();
        List<HashMap> data = getData();
        saveData(data);
    }

    //URL adresy pro jednotlivé dny
    private static List<String> getPaths() {
        List<String> result = new ArrayList();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
        Calendar cal = Calendar.getInstance();
        //System.out.println("TODAY: " + cal.getTime());
        Date today = cal.getTime();
        cal.add(Calendar.DATE, -30);

        for (int i = 0; true; i++) {

            String d = (cal.getTime().getDate()) + "." + (cal.getTime().getMonth() + 1) + "." + (cal.getTime().getYear() + 1900);
            result.add(path.replace("[DATE]", d));
            //System.out.println(path.replace("[DATE]", d));
            if (today.getDate() == cal.getTime().getDate()
                    && today.getYear() == cal.getTime().getYear()
                    && today.getMonth() == cal.getTime().getMonth()) {
                break;
            }
            cal.add(Calendar.DATE, 1);
        }
        return result;
    }

    private static void downloadPDF(List<String> paths) {
        for (String path : paths) {
            try {
                URL website = new URL(path);
                try (InputStream in = website.openStream()) {
                    Path output = Paths.get(outputPath + path.substring(path.indexOf("from=") + "from=".length(), path.lastIndexOf("&lang")) + ".pdf", "");
                    Files.copy(in, output, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    Logger.getLogger(GenerationNuclear.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (MalformedURLException ex) {
                Logger.getLogger(GenerationNuclear.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("DOWNLOADED: " + path);
        }

    }

    private static void translate() {
        List<String> paths = Util.readAllFilePaths("PDF");
        for (String path : paths) {
            if (path.contains(".pdf.txt")) {
                continue;
            }
            //SimpleDateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
            Calendar cal = Calendar.getInstance();
            //System.out.println("TODAY: " + cal.getTime());
            Date today = cal.getTime();
            String t = (cal.getTime().getDate()) + "." + (cal.getTime().getMonth() + 1) + "." + (cal.getTime().getYear() + 1900);
            cal.add(Calendar.DATE, -1);
            String y = (cal.getTime().getDate()) + "." + (cal.getTime().getMonth() + 1) + "." + (cal.getTime().getYear() + 1900);
            if (paths.contains(path + ".txt")
                    && !path.contains(t)
                    && !path.contains(y)) {
                System.out.println("skipped: " + path);
                continue;
            }
            Document pdf = PDF.open(System.getProperty("user.dir") + "/" + path);
            StringBuilder text = new StringBuilder(1024);
            pdf.pipe(new OutputTarget(text));
            try {
                pdf.close();
            } catch (IOException ex) {
                Logger.getLogger(REMIT.class.getName()).log(Level.SEVERE, null, ex);
            }
            String tex = text.toString();
            tex = tex.replace("  ", " ").replace("  ", " ").replace("  ", " ").replace("  ", " ").replace("  ", " ").replace("  ", " ").replace("\n ", "\n ").replace("\n\n", "\n").replace("\n\n", "\n").replace("\n\n", "\n").replace("\n\n", "\n").replace("\n\n", "\n").replace("\n\n", "\n").replace("\n\n", "\n").replace("\n\n", "\n").replace("\n\n", "\n").replace("\n\n", "\n").replace("\n", "").replace("Report byl", "\nReport byl").replace("Elektrárna Blok", "\nElektrárna Blok");
            System.out.println(tex);
            Util.saveTXT(System.getProperty("user.dir") + "/" + path + ".txt", tex);
        }
    }

    private static void checkIfAllTranslated() {
        List<String> paths = Util.readAllFilePaths("PDF");
        for (String path : paths) {
            if (path.contains(".txt")) {
                continue;
            }
            if (!paths.contains(path + ".txt")) {
                ToString.readAndMoveBack(System.getProperty("user.dir") + "/" + path);
            }
        }
    }

    private static List<HashMap> getData() {
        String readPath = System.getProperty("user.dir") + "/PDF/";
        //System.out.println("readPath " + readPath);
        List<String> paths = Util.readAllFilePaths(readPath);
        //System.out.println("pat: " + paths.size());
        List<HashMap> result = new ArrayList();
        for (String path : paths) {
            if (path.contains(".txt") && !path.contains("2012") && !path.contains("2013")) {
                String maintext = getMainText(path);
                String date = path
                        .substring(path.lastIndexOf("/") + 1).replace(".pdf.txt", "");
                System.out.println("date: " + getDate(date));
                HashMap dukovany = getDukovany(maintext);
                HashMap temelin = getTemelin(maintext);

                //System.out.println("date: " + date);
                dukovany.put("date", getDate(date));
                dukovany.put("plant", "Dukovany");
                temelin.put("date", getDate(date));
                temelin.put("plant", "Temelín");
                result.add(dukovany);
                result.add(temelin);
            }
        }

        return result;
    }

    private static String getMainText(String path) {
        String full = Util.readTXT(path);
        full = full.substring(full.indexOf("Dukovany"));
        full = full.substring(0, full.indexOf("Report byl"));
        full = full.replace("Dukovany", "\nDukovany")
                .replace("Temelín", "\nTemelín")
                .replace("B1", "\nB1")
                .replace("B2", "\nB2")
                .replace("B3", "\nB3")
                .replace("B4", "\nB4");
        return full;
    }

    private static HashMap getDukovany(String input) {
        input = input.substring(input.indexOf("Dukovany"), input.indexOf("Temelín"));
        HashMap out = new HashMap();
        String[] split = input.split("\n");
        for (String s : split) {
            if (s.startsWith("B1")) {
                out.put("B1", getHourly(s));
                //out.put("hourly", );
            } else if (s.startsWith("B2")) {
                out.put("B2", getHourly(s));
                //out.put("hourly", getHourly(s));
            } else if (s.startsWith("B3")) {
                out.put("B3", getHourly(s));
                //out.put("hourly", getHourly(s));
            } else if (s.startsWith("B4")) {
                out.put("B4", getHourly(s));
                //out.put("hourly", getHourly(s));
            } else {
                //System.out.println("START WITH OTHER: " + s);
            }
        }
        return out;
    }
        private static HashMap getTemelin(String input) {
        //System.out.println("input: "+input);
        input = input.substring(input.indexOf("Temelín"));
        HashMap out = new HashMap();
        String[] split = input.split("\n");
        for (String s : split) {
            if (s.startsWith("B1")) {
                out.put("B1", getHourly(s));
                //out.put("hourly", getHourly(s));
            } else if (s.startsWith("B2")) {
                out.put("B2", getHourly(s));
                //out.put("hourly", getHourly(s));
            } else if (s.startsWith("B3")) {
                out.put("B3", getLine(s));
                //out.put("hourly", getHourly(s));
            } else if (s.startsWith("B4")) {
                out.put("B4", getLine(s));
                //out.put("hourly", getHourly(s));
            } else {
                //System.out.println("START WITH OTHER: " + s);
            }
        }
        return out;
    }

    private static int getLine(String s) {
        String[] split = {};
        if (s.length() > 2) {
            split = s.substring(3).split(" ");
        } else {
            return 0;
        }
        int total = 0;
        for (String nr : split) {
            try {
                total += Integer.parseInt(nr);
            } catch (Exception e) {
                System.out.println("s.size: " + Arrays.toString(split));
                System.out.println("s: " + s);
            }
        }
        return total;
    }

    private static String getHourly(String s) {
        String[] split = {};
        if (s.length() > 2) {
            split = s.substring(3).split(" ");
        } else {
            return "";
        }
        String json = "{";
        int hour = 1;
        for (String nr : split) {
            json += "\"" + hour + "\":" + nr + ", ";
            hour++;
        }
        if (json.endsWith(", ")) {
            json = json.substring(0, json.length() - 2);
        }
        json += "}";
        //System.out.println("json: " + json);
        return json;
    }

    private static void saveData(List<HashMap> data) {
        Collections.sort(data, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                HashMap one = (HashMap) o1;
                HashMap two = (HashMap) o2;
                String[] dOne = one.get("date")
                        .toString()
                        .split("-");
                String[] dTwo = two.get("date")
                        .toString()
                        .split("-");
                //seřazení pokud jsou odlišné roky
                if (Integer.parseInt(dOne[0]) != Integer.parseInt(dTwo[0])) {
                    int iOne = Integer.parseInt(dOne[0]);
                    int iTwo = Integer.parseInt(dTwo[0]);
                    return -Integer.valueOf(iOne).compareTo(iTwo);
                }
                //seřazení pokud jsou odlišné měsíce
                if (Integer.parseInt(dOne[1]) != Integer.parseInt(dTwo[1])) {
                    int iOne = Integer.parseInt(dOne[1]);
                    int iTwo = Integer.parseInt(dTwo[1]);
                    return -Integer.valueOf(iOne).compareTo(iTwo);
                }
                //seřazení pokud jsou odlišné dny
                if (Integer.parseInt(dOne[2]) != Integer.parseInt(dTwo[2])) {
                    int iOne = Integer.parseInt(dOne[2]);
                    int iTwo = Integer.parseInt(dTwo[2]);
                    return -Integer.valueOf(iOne).compareTo(iTwo);
                }

                return 0;
            }
        });
        int count = 0;
        for (HashMap h : data) {
            System.out.println("date: " + h.get("date"));
            if (h.get("date").toString().contains("2015")
                    || h.get("date").toString().contains("2014")) {
                continue;
            }
            //Omezeni poctu updatů na 15 dni
            if (count > 30) {
                break;
            }
            JSONObject obj = new JSONObject(h);
            sendPost(obj.toJSONString());
            count++;
        }
    }

    // HTTP POST request
    private static void sendPost(String data) {
        data = data.replace("\":\"{", "\":{")
                .replace("}\",\"", "},\"")
                .replace("}\"}", "}}")
                .replace("\\\"", "\"");
        String url = "http://www.oenergetice.cz/wp-content/themes/SmartNews/custom/update_remit_data.php";
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("json", data));
        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));
//            post.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GenerationNuclear.class.getName()).log(Level.SEVERE, null, ex);
        }
        HttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException ex) {
            //Logger.getLogger(GenerationNuclear.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("LOG: Repeat ");
            sendPost(data);
        }
        if (response == null) {
            return;
        }

        BufferedReader rd = null;
        try {
            rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException ex) {
            Logger.getLogger(GenerationNuclear.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(GenerationNuclear.class.getName()).log(Level.SEVERE, null, ex);
        }
        StringBuffer result = new StringBuffer();
        String line = "";
        try {
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(GenerationNuclear.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(result.toString());
    }

    private static Object getDate(String date) {
        date = date.replace(".", "-");
        String[] pom = date
                .split("-");
        String result = "";
        for (String s : pom) {
            result = s + "-" + result;
        }
        if (result.endsWith("-")) {
            result = result.substring(0, result.length() - 1);
        }
        //System.out.println("resultdate: "+result );
        return result;
    }



}
