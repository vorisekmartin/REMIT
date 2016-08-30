/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remit;

import com.snowtide.PDF;
import com.snowtide.pdf.Document;
import com.snowtide.pdf.OutputTarget;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin
 */
public class REMIT {

    /**
     * @param args the command line arguments
     */
    public static String basePath = "/Users/Martin/NetBeansProjects/REMIT/";
    public static final int GENERATION = 0;
    public static final int PLAN_OFF = 1;
    //public final int UNPLAN_OFF = 2;

    public static final int NUCLEAR = 3;

    public static void main(String[] args) {
        Handler h = new Handler();
        h.get(GENERATION, NUCLEAR);
        h.get(PLAN_OFF,NUCLEAR);

    }

}
