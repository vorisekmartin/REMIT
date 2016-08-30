/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remit;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import objects.Odstavka;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import remit.generation.GenerationNuclear;

/**
 *
 * @author Martin
 */
class PlanOffNuclear {

    static final String savePath = "PDF/PLAN_OFF_NUC/";

    static void update() {
        List<String> paths = getPaths();
        downloadData(paths);
        List<String> rows = translateData();
        List<Odstavka> data = readData(rows);
        data = mergeData(data);
        System.out.println("===============");
        saveData(data);
        System.out.println("------");

    }

    /**
     * Přidá všechny potřebný URL ke stažení, kde jsou odstávky
     *
     * @return
     */
    private static List<String> getPaths() {
        List<String> result = new ArrayList();
        result.add("https://eportal.cezdata.cz/mtprovider/genfile?dt=po&tp=je&from=1.1.2016&to=30.12.2016&lang=cs");
        result.add("https://eportal.cezdata.cz/mtprovider/genfile?dt=po&tp=je&from=20.12.2016&to=19.12.2017&lang=cs");
        result.add("https://eportal.cezdata.cz/mtprovider/genfile?dt=po&tp=je&from=10.12.2017&to=09.12.2018&lang=cs");
        result.add("https://eportal.cezdata.cz/mtprovider/genfile?dt=po&tp=je&from=02.12.2018&to=01.12.2019&lang=cs");
        result.add("https://eportal.cezdata.cz/mtprovider/genfile?dt=po&tp=je&from=28.11.2019&to=31.12.2019&lang=cs");
        return result;
    }

    private static void downloadData(List<String> paths) {
        for (String path : paths) {
            try {
                URL website = new URL(path);
                try (InputStream in = website.openStream()) {
                    Path output = Paths
                            .get(savePath + path.substring(path.indexOf("from=") + "from=".length(),
                                    path.lastIndexOf("&to")) + ".pdf", "");
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

    private static List<String> translateData() {
        List<String> paths = Util.readAllFilePaths(savePath);
        List<String> rows = new ArrayList();
        for (String path : paths) {
            if (path.contains("pdf.txt")) {
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
            String input = text.toString();
            input = input.contains("Dukovany") ? input.substring(input.indexOf("Dukovany")) : input;
            rows.addAll(repairText(input));
        }
        return rows;
    }

    private static List<Odstavka> readData(List<String> rows) {
        List<Odstavka> result = new ArrayList();
        for (String row : rows) {
            if (row.startsWith("Dukovany")) {
                Odstavka.curr_plant = "Dukovany";
                continue;
            } else if (row.startsWith("Temelín")) {
                Odstavka.curr_plant = "Temelín";
                continue;
            }
            Odstavka o = new Odstavka(row);

            result.add(o);
        }
        return result;
    }

    private static void saveData(List<Odstavka> data) {
        Collections.shuffle(data);
        HashMap<String, List<String>> h = sortData(data);
        HashMap<String, List<String>> important = getImportant(data);
        for (Object s : important.keySet().toArray()) {
            String json = listToJson(important.get(s));
            String[] split = s.toString().split("_");
            sendPost(json, split[0], split[1], split[2], "important");
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(PlanOffNuclear.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        for (Object s : h.keySet().toArray()) {
            //System.out.println("o: " + s.toString());
            String json = listToJson(h.get(s));
            String[] split = s.toString().split("_");
            sendPost(json, split[0], split[1], split[2], "total");
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(PlanOffNuclear.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }

    }

    private static List<String> repairText(String input) {
        if (!input.contains("Dukovany") && !input.contains("Temelín")) {
            List<String> res = new ArrayList();

            return res;
        }
        input = input.replace("Dukovany", "Dukovany\n").replace("Temelín", "Temelín\n");
        String[] split = input.split("\n");
        List<String> result = new ArrayList();
        for (String s : split) {
            if (!s.startsWith("Report byl vygenerován")
                    && !s.startsWith("Poslední aktualizace dat:")
                    && !s.startsWith("Elektrárna")
                    && !s.startsWith("Plánované odstávky")
                    && s.length() > 0) {
                while (s.startsWith(" ")) {
                    s = s.substring(1);
                }
                while (s.contains("  ")) {
                    s = s.replace("  ", " ");
                }
                result.add(s);
            }
        }
        return result;
    }

    private static List<Odstavka> mergeData(List<Odstavka> data) {
        System.out.println("data: " + data.size());
        for (int i = 0; i < data.size(); i++) {
            Odstavka one = data.get(i);
            for (int j = i + 1; j < data.size(); j++) {
                Odstavka two = data.get(j);
                if (one.getAmount() == two.getAmount()
                        && one.getPlant().equals(two.getPlant())
                        && one.getUnit().equals(two.getUnit())
                        && isOverlap(one, two)) {

//                    System.out.println("======================== MERGE ========================");
//                    System.out.println(one);
//                    System.out.println(two);
//                    System.out.println("========================");
                    mergeOdstavka(one, two);
//                    System.out.println("======================== MERGE 2 ========================");
//                    System.out.println(one);
//                    System.out.println(two);
//                    System.out.println("========================");
                    i = -1;
                }
            }
        }
        data = removeDuplicity(data);
        Collections.sort(data, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Odstavka one = (Odstavka) o1;
                Odstavka two = (Odstavka) o2;
                if (one.getPlant().compareTo(two.getPlant()) != 0) {
                    return one.getPlant().compareTo(two.getPlant());
                }
                if (one.getUnit().compareTo(two.getUnit()) != 0) {
                    return one.getUnit().compareTo(two.getUnit());
                }
                return one.getBegin().compareTo(two.getBegin());
            }
        });

        return data;
    }

    private static boolean isOverlap(Odstavka one, Odstavka two) {
        Date oneBegin = one.getBegin();
        Date oneEnd = one.getEnd();
        Date twoBegin = two.getBegin();
        Date twoEnd = two.getEnd();
        if (oneBegin.compareTo(twoBegin) < 0
                && twoBegin.compareTo(oneEnd) < 0
                && oneEnd.compareTo(twoEnd) < 0) {
//            System.out.println(one);
//            System.out.println(two);
//            System.out.println("========================");
            return true;
        }

        if (oneEnd.compareTo(twoBegin) == 0 || twoEnd.compareTo(oneBegin) == 0) {
            if (one.getReason().equals(two.getReason())) {
                return true;
            }
        }

        return false;
    }
    //                 B                     E
    // ONE =============|=====================|===================
    // TWO ==============================|=====================|====
    //                                   B                     E

    private static void mergeOdstavka(Odstavka one, Odstavka two) {

        if (one.getBegin().compareTo(two.getBegin()) < 0) {
            two.setBegin(one.getBegin());
        } else {
            one.setBegin(two.getBegin());
        }
        if (one.getEnd().compareTo(two.getEnd()) < 0) {
            one.setEnd(two.getEnd());
        } else {
            two.setEnd(one.getEnd());
        }
        one.updateDuration();
        two.updateDuration();
    }

    private static List<Odstavka> removeDuplicity(List<Odstavka> input) {
        List<Odstavka> output = new ArrayList();
        for (int i = 0; i < input.size(); i++) {
            Odstavka one = input.get(i);
            boolean add = true;
            for (int j = i + 1; j < input.size(); j++) {
                Odstavka two = input.get(j);
                if (one.toString().equals(two.toString())) {
                    add = false;
                    break;
                }
                if (one.getPlant().equals(two.getPlant())
                        && one.getUnit().equals(two.getUnit())
                        && one.getReason().equals(two.getReason())
                        && one.getAmount() == two.getAmount()
                        && one.updateDuration() < two.updateDuration()
                        && (one.getEnd() == two.getEnd() || one.getBegin() == two.getBegin())) {
                    add = false;
                    System.out.println("one: " + one.toString());
                    System.out.println("two: " + two.toString());
                    break;
                }
            }
            if (add) {
                output.add(one);
            } else {
            }
        }
        return output;
    }

    private static void sendPost(String data, String plant, String unit, String year, String meta) {
        String url = "http://www.oenergetice.cz/wp-content/themes/SmartNews/custom/update_remit_data_offtime.php";

        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        HttpConnectionParams.setSoTimeout(httpParams, 3000);

        client = new DefaultHttpClient(httpParams);

        post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("json", data));
        urlParameters.add(new BasicNameValuePair("plant", plant));
        urlParameters.add(new BasicNameValuePair("unit", unit));
        urlParameters.add(new BasicNameValuePair("year", year));
        urlParameters.add(new BasicNameValuePair("meta", meta));

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
            System.out.println("FAILED");
            System.out.println("===data: " + data);
            System.out.println("===meta: " + meta);
            System.out.println("===plant: " + plant);
            System.out.println("===unit: " + unit);
            System.out.println("===year: " + year);
            System.out.println("ENTITY: " + urlParameters);
            System.out.println("=============new Send");
            sendPost(data, plant, unit, year, meta);
            return;
        }
        //System.out.println("\nSending 'POST' request to URL : " + url);
        //System.out.println("Post parameters : " + post.getEntity());
        //System.out.println("Response Code : "
        //      + response.getStatusLine().getStatusCode());
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

    private static HashMap<String, List<String>> sortData(List<Odstavka> data) {
        HashMap<String, List<String>> h = new HashMap<String, List<String>>();
        for (Odstavka o : data) {
            String key = o.getPlant() + "_" + o.getUnit() + "_" + (o.getBegin().getYear() + 1900);
            if (h.get(key) == null) {
                List<String> objects = new ArrayList();
                objects.add(o.getJson());
                h.put(key, objects);
            } else {
                h.get(key).add(o.getJson());
            }
        }
        return h;
    }

    private static String listToJson(List<String> input) {
        String json = "{";
        int count = 0;
        if (input == null) {
            return json + "}";
        }

        for (String j : input) {
            json += "\"" + count + "\":" + j + ",";
            count++;
        }
        if (json.endsWith(",")) {
            json = json.substring(0, json.length() - 1);
        }
        json += "}";
        return json;
    }
// V tomhle se nikdo nevyzná

    private static HashMap<String, List<String>> getImportant(List<Odstavka> input) {
        HashMap<String, List<String>> h = new HashMap<String, List<String>>();
        for (Odstavka o : input) {
            String key_beg = o.getPlant() + "_" + o.getUnit() + "_" + (o.getBegin().getYear() + 1900);
            String key_end = o.getPlant() + "_" + o.getUnit() + "_" + (o.getEnd().getYear() + 1900);
            if (o.updateDuration() > 24
                    && ((o.getAmount() > 300 && "Dukovany".equals(o.getPlant()))
                    || (o.getAmount() > 600 && "Temelín".equals(o.getPlant())))) {
                if (h.get(key_beg) == null) {
                    List<String> objects = new ArrayList();
                    objects.add(o.getJson());
                    h.put(key_beg, objects);
                } else if (!h.get(key_beg).contains(o.getJson())) {
                    h.get(key_beg).add(o.getJson());
                }
                if (o.getEnd().getDate() == 01 && o.getEnd().getMonth() == 0 && o.getEnd().getHours() == 00 && o.getEnd().getMinutes() == 00) {

                    continue;
                }
                if (h.get(key_end) == null) {
                    List<String> objects = new ArrayList();
                    objects.add(o.getJson());
                    h.put(key_end, objects);
                } else if (!h.get(key_beg).contains(o.getJson())) {
                    h.get(key_beg).add(o.getJson());
                }
            }
        }
        return h;
    }

}
