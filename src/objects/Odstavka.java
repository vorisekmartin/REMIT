/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objects;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Martin
 */
public class Odstavka {

    public static String curr_plant = "";

    Date begin, end;
    String reason, unit, plant;
    int amount;
    int duration;

    public Odstavka(String row) {
        parseRow(row);
    }

    public Odstavka(String plant, String unit, String reason, int amount, Date begin, Date end) {
        this.plant = plant;
        this.unit = unit;
        this.reason = reason;
        this.amount = amount;
        this.begin = begin;
        this.end = end;
        this.duration = updateDuration();
    }

    public static Odstavka odstavkaFromJson(String json) {
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(json);
        } catch (org.json.simple.parser.ParseException ex) {
            Logger.getLogger(Odstavka.class.getName()).log(Level.SEVERE, null, ex);
        }
        JSONObject js = (JSONObject) obj;
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date beg = null;
        Date end = null;
        try {
            beg = sdf.parse(js.get("begin") + "");
            end = sdf.parse(js.get("end") + "");
        } catch (ParseException ex) {
            Logger.getLogger(Odstavka.class.getName()).log(Level.SEVERE, null, ex);
        }

        Odstavka o = new Odstavka(js.get("plant") + ""
                , js.get("unit") + ""
                , js.get("reason") + ""
                , Integer.parseInt(js.get("amount") + "")
                , beg
                , end);
        return o;

    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    private void parseRow(String row) {
        String[] split = row.split(" ");
        int count = 0;
        String temp = "";
        //System.out.println("row: "+row);
        plant = Odstavka.curr_plant;
        for (String item : split) {
            switch (count) {

                case 0: {
                    unit = item;
                    break;
                }
                case 1: {
                    reason = item;
                    break;
                }
                case 2: {
                    amount = Integer.parseInt(item);
                    break;
                }
                case 3: {
                    temp = item;
                    break;
                }
                case 4: {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.M.yyyy HH:mm");
                    try {
                        begin = sdf.parse(temp + " " + item);
                    } catch (ParseException ex) {
                        Logger.getLogger(Odstavka.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
                case 5: {
                    temp = item;
                    break;
                }
                case 6: {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.M.yyyy HH:mm");
                    try {
                        end = sdf.parse(temp + " " + item);
                    } catch (ParseException ex) {
                        Logger.getLogger(Odstavka.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
            }

            count++;
        }
        duration = updateDuration();
    }

    public int updateDuration() {
        long secs = (this.begin.getTime() - this.end.getTime()) / 1000;
        int hours = (int) (secs / 3600);
        duration = Math.abs(hours);
        return Math.abs(hours);
    }
    

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String ret = "Plant: " + plant + " Unit: " + unit + " Reason: " + reason
                + " Amount: " + amount + " FROM: " + sdf.format(begin)
                + " End: " + sdf.format(end) + " Duration: " + duration;
        return ret;
    }

    public String getJson() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String json = "{\"amount\":" + amount + ",\"begin\":\"" + sdf.format(begin) + "\", \"end\": \"" + sdf.format(end) + "\", \"duration\": " + duration + "}";

        return json;
    }

}
