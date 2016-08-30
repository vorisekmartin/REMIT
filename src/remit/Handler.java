/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package remit;

import remit.generation.GenerationNuclear;

/**
 *
 * @author Martin
 */
class Handler {

    void get(int type, int source) {
        if (type == REMIT.GENERATION) {
            if (source == REMIT.NUCLEAR) {
                GenerationNuclear.update();
            }
        }
       if (type == REMIT.PLAN_OFF) {
            if (source == REMIT.NUCLEAR) {
                PlanOffNuclear.update();
            }
        }

    }

}
