/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package phyllotaxisspiral;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;

/**
 *
 * @author simonkenny
 */
public class LeapListener extends Listener {
    private final float MAX_HEIGHT = 400.f;
    private final float MAX_DEPTH = 200.f;
    private final float MAX_WIDTH = 200.f;
    
    private final PhyJFrame phyframe;
    
    LeapListener(PhyJFrame frame) {
        this.phyframe = frame;
    }
    
    public void onFrame(Controller controller) {
        Frame frame = controller.frame();
        int count = 0;
        for( Hand hand : frame.hands() ) {
            if( count++ == 0 ) {
                phyframe.externalChange(2,rescaleVal(hand.palmPosition().getX(),-MAX_WIDTH,MAX_WIDTH,
                        0.f,1.f));
                phyframe.externalChange(1,rescaleVal(hand.palmPosition().getZ(),-MAX_DEPTH,MAX_DEPTH,
                        0.f,1.f));
                phyframe.externalChange(0,rescaleVal(hand.palmPosition().getY(),0,MAX_HEIGHT,
                        0.f,1.f));
                //phyframe.externalChange(3,rescaleVal((float)hand.fingers().count(),0,5,
                //       0.f,1.f));
                phyframe.externalChange(3,(float)Math.log10(rescaleVal(Math.abs(hand.palmPosition().getX()),0.f,MAX_WIDTH,
                        1.f,10.f)));
            }
        }
    }
    
    private float rescaleVal(float val, float origMin, float origMax, float newMin, float newMax) {
        return (((val-origMin)/(origMax-origMin)) * (newMax-newMin)) + newMin;
    }
}
