/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package phyllotaxisspiral;

import processing.core.*;

/**
 *
 * @author simonkenny
 */
public class ProcessingApplet extends PApplet {
    int NUM_POINTS = 1000;
    float CIRCLE_SIZE = 5.f;
    int NUM_COLOURS = 32;
    
    /*
    private float RADIUS_RATE = 2.f;
    private float ANGLE_RATE = 0.5f;
    private float RADIUS_INC_RATE = 1.f;
    private float ANGLE_INC_RATE = 0.5f;
    */
    private float RADIUS_RATE_DEFAULT = 0.01f;
    private float ANGLE_RATE_DEFAULT = 0.01f;
    private float RADIUS_INC_RATE_DEFAULT = 0.001f;
    private float ANGLE_INC_RATE_DEFAULT = 0.001f;
    
    private float RADIUS_RATE_MIN = 0.0001f;
    private float ANGLE_RATE_MIN = 0.0001f;
    private float RADIUS_INC_RATE_MIN = 0.00001f;
    private float ANGLE_INC_RATE_MIN = 0.00001f;
    
    private float RADIUS_RATE_MAX = 2.f;
    private float ANGLE_RATE_MAX = 0.5f;
    private float RADIUS_INC_RATE_MAX = 1.f;
    private float ANGLE_INC_RATE_MAX = 0.5f;
    
    float radius = 30.f;		
    float angle = 0.f;
    float radiusInc = 0.5f;
    float angleInc = 1.61803398f;
    
    MovingVariable radiusMV;
    MovingVariable angleMV;
    MovingVariable radiusIncMV;
    MovingVariable angleIncMV;
    
    int curColourTable[];
    int colourLength = 20;
    
    boolean useCol = true;
    boolean bgIsBlack = true;
    int screenBlur = 40;
    
    float entropyFactor = 0.f;
    boolean entropyOn = true;
    
    public void setup() {
	size(710,400);
        //noLoop();
        
        radiusMV = new MovingVariable(radius,radius,RADIUS_RATE_DEFAULT);
        angleMV = new MovingVariable(radius,radius,ANGLE_RATE_DEFAULT);
        radiusIncMV = new MovingVariable(radius,radius,RADIUS_INC_RATE_DEFAULT);
        angleIncMV = new MovingVariable(radius,radius,ANGLE_INC_RATE_DEFAULT);
        
        curColourTable = createColourTable(curColourTable,NUM_COLOURS);
        
        lastTime = (float)millis()/1000.f;
    }
    
    float lastTime;

    public void draw() {
        // time
        float curTime = (float)millis()/1000.f;
        float elapsedTime = curTime - lastTime;
        
        radiusMV.applyTime(elapsedTime);
        angleMV.applyTime(elapsedTime);
        radiusIncMV.applyTime(elapsedTime);
        angleIncMV.applyTime(elapsedTime);
  
        float useAngle = (float)angleMV.getVal();
        float useRadius = (float)radiusMV.getVal();
        if( !entropyOn ) {
            useAngle = (float)angleMV.getTarget();
            useRadius = (float)radiusMV.getTarget();
        }
        //angleInc /= 8.f;
        //background(255);
        if( bgIsBlack ) {
            fill(0,0,0,screenBlur);
        } else {
            fill(255,255,255,screenBlur);
        }
        rect(0,0,width,height);
        noStroke();
        if( !useCol ) {
            if( bgIsBlack ) {
                fill(255);
            } else {
                fill(0);
            }
        }
        int curColour = 0;
        int colourCount = 0;
        for( int i = 0 ; i < NUM_POINTS ; i++ ) {
            float x = cos(useAngle) * useRadius;
            float y = sin(useAngle) * useRadius;
            x += width/2;
            y += height/2;
            //if( ++colourCount >= colourLength ) {
                if( ++curColour >= NUM_COLOURS ) {
                    curColour = 0;
                }
                //colourCount = 0;
                
            //}
            if( useCol ) {
                fill(curColourTable[curColour]);
            }
            //fill(255);
            
            ellipse( x, y, CIRCLE_SIZE, CIRCLE_SIZE );
            // inc
            if( entropyOn ) {
                useAngle += (float)angleIncMV.getVal();
                useRadius += (float)radiusIncMV.getVal();
            } else {
                useAngle += (float)angleIncMV.getTarget();
                useRadius += (float)radiusIncMV.getTarget();
            }
            
        }
    }
    
    public void setScreenBlur(float val) {
        screenBlur = (int)(val * 255.f);
    }
    
    public void setRadius(float val) {
        //radius = val;
        radiusMV.setTarget((float)val);
    }
    
    public void setRadiusInc(float val) {
        //radiusInc = val;
        radiusIncMV.setTarget((float)val);
    }
    
    public void setAngle(float val) {
        //angle = val;
        angleMV.setTarget((float)val);
    }
    
    public void setAngleInc(float val) {
        //angleInc = val;
        angleIncMV.setTarget((float)val);
    }
    
    public void setUseCol(boolean state) {
        useCol = state;
    }
    
    public void setEntropy(float val) {
        entropyFactor = val;
        radiusMV.setRate(mapRange(RADIUS_RATE_MIN, RADIUS_RATE_MAX, entropyFactor));
        angleMV.setRate(mapRange(ANGLE_RATE_MIN, ANGLE_RATE_MAX, entropyFactor));
        radiusIncMV.setRate(mapRange(RADIUS_INC_RATE_MIN, RADIUS_INC_RATE_MAX, entropyFactor));
        angleIncMV.setRate(mapRange(ANGLE_INC_RATE_MIN, ANGLE_INC_RATE_MAX, entropyFactor));
    }
    
    public void setEntropyState(boolean state) {
        entropyOn = state;
    }
    
    public void setBackgroundBlackOrWhite(boolean isBlack) {
        bgIsBlack = isBlack;
    }
    
    public float mapRange( float start, float end, float pos ) {
        if( start < end ) {
            return ((end - start) * pos) + start;
        } else if( start > end ) {
            return ((start - end) * (1.f-pos)) + end;
        }
        return start;
    }
    
    private class MovingVariable {
        private final double MOVING_VARIABLE_DEFAULT_RATE = 1;
        
	private double rate;     // unit change per second
	private double target;   // target value for val to reach
        private boolean atTarget;
    
        public double val;
    
        public MovingVariable() {
            rate = MOVING_VARIABLE_DEFAULT_RATE;
            val = 0.f;
            target = 0.f;
            atTarget = true;
        }

        public MovingVariable( double _val ) {
            rate = MOVING_VARIABLE_DEFAULT_RATE;
            val = _val;
            target = 0.f;
            atTarget = false;
        }

        public MovingVariable( double _val, double _target ){
            rate = MOVING_VARIABLE_DEFAULT_RATE;
            val = _val;
            target = _target;
            atTarget = false;
        }

        public MovingVariable( double _val, double _target, double _rate ){
            rate = _rate;
            val = _val;
            target = _target;
            atTarget = false;
        }

        public void setVal( double _val ) { val = _val; }
        public double getVal() { return val; }
        public void setRate( double _rate ) { rate = _rate; }
        public double getRate() { return rate; }
        public void setTarget( double _target ) { target = _target; atTarget = false; }
        public double getTarget() { return target; }
        public boolean isAtTarget() { return atTarget; }

        public void applyTime( double deltaTime ) {
            double direction = 0.f;
            if( target < val )
                direction = -1.f;
            else if( target > val )
                direction = 1.f;
            if( direction != 0.f ) {
                val += rate * direction * deltaTime;
                if( direction == -1.f && val < target )
                    val = target;
                else if( direction == 1.f && val > target )
                    val = target;
            }
            if( val == target )
                atTarget = true;
        }
    }
    
    int [][]colours = {
        { 0x5D5C4A,0x686752,0x8D8B6B,0xCEC8B7,0x6B7257,0x7D866C,0x302E4A,0x313046,0x313345,0x373748,0x3A3A4B,0x49445A,0x817A85,0xB2A7AB,0xA49EA5 },                    // Pennys & Polaroids - SeeFig
        { 0x433D29,0x50442C,0x5C4E2C,0x7B5D2E,0x836231,0x88744B,0x8D7A4E,0xA18847,0xAA8F54,0xB39B5C },                                                            // Emers Garden (wood) - SeeFig
        { 0x947D72,0xA08E8D,0xA38280,0x977977,0xAB8F8D,0xA18582,0xB2ABA9,0xCAD0C5,0x919195,0x998694,0x8B6E7B,0x846F7C,0x7E6B77,0x9B808C,0xA08F9B,0xA78896,0xAB939C,0x6B4852,0xBA9EA3,0xB19196,0xBA9599,0x9C7374,0x976C6B }, //Blossoms - SeeFig
        { 0x21431C,0x103113,0x144217,0x736E39,0x867238,0x588D35,0x7E9644,0xB0A345,0xD0B93E,0xDFCE46,0xEDE951,0xE2D597 },                                            // Spring - SeeFig
        { 0xE5B9B9,0xB78C78,0xCC9F82,0xE1B57F,0xC69547,0xE6D1A6,0xD8BB7C,0xE1C679,0xE6D488,0xEADEAE,0xEEE8C4,0xF5F5EC,0xABA9BC,0xDA3B88,0xDD7FA2,0xDB9DA9,0xDF8D95 },    // Colourblocc (ice cream) - SeeFig
        { 0x120602,0x220B03,0x360E02,0x3C1102,0x431302,0x4E1401,0x5F2A07,0x742F07,0x7D2D01,0x843A07,0xA1621D },                                                    // Galway Cathedral (dark fire) - SeeFig
        { 0x532700,0x914F0B,0x512C04,0x994710,0x503010,0x6B451A,0xA27D4A,0xCAAA7D,0xBA8840,0xE2B46D,0x6A5017,0x89681F,0x989A40,0x7E7B12,0x5C5C19,0x4A5117,0x343A1E },    // Salad Fingers(wood and grass) - SeeFig
        { 0xAF2E35,0xC35132,0xB43E18,0xB76E3C,0x724524,0xC69060,0xD58E1D,0xA27C2C,0x907121,0x997B24,0x988D49,0xCCC071 },                                            // Untitled (natural tasty) - Alison
        { 0x671C2C,0x73162A,0xB33A54,0xC4526B,0xC16175,0xCC7487,0xC7798A,0xE0768D,0xFE6686,0xEA7A92,0xD8899A,0xF37B95,0xE4889B,0xDA91A1,0xE593A5,0xDC98A6,0xDC98A7,
          0xE197A7,0xEA94A6,0xDE9DAB,0xDE9FAC,0xE2ABB7,0xE8B3BE,0xEEB1BE,0xE9BAC4,0xE8C1CA,0xFEBAC9,0xFEC1CE,0xFEEEF2,0xF9F9F9,0xFEFEFE},                             // Samuari Hack
        { 0x946D52,0x776746,0x63612E,0x4D5528,0x37420F,0x4E5B22,0x465826,0x374D18,0x576841,0x354D29,0x71A559,0x457A4F,0x538F60 },                                    // Moschops
        { 0x333933,0x444D47,0x48534F,0x515D59,0xABB7AC,0xCDDAD2,0xD0DBD3,0xDCE6DF,0xE4EFE7,0xF6F0EE,0xF8FBF9,0xFCFEFC,0xFDFFFD },                                    // Poster on poll
        { 0x6E4A38,0xC3A795,0x886B58,0x342418,0x7E5C42,0xBAA38F,0x4C3928,0x4F4134,0x5F5A55,0x9A988E,0x5D5B4E,0x62675A,0x64675E,0x6A6F6A },                            // Anne
        { 0x141B90,0x12E940,0x123750,0x2104E0,0x20307B,0x2E3468,0x104350,0x505340,0x202320,0x48456A,0x363253,0x514462,0x3C3147,0x7C6469 },                                   // Jellyfish
        { 0xC08814,0xD69E13,0xD09910,0xD49C10,0xCD9812,0x9C750D,0x956D01,0x957106,0x434501,0xC4C681,0x9DCFFE,0x9AC6CD,0xC9D2B4,0xB1BFAD },                            // Buttercup
        { 0xF5A630,0x4EBBC9,0x64C4CF,0x65C4D0,0x69C5D0,0x6BC3CE,0x6EC7D2,0x8DD4DC,0x89CCD4,0x90D5DD,0xA5DDE3,0xABDFE5,0xB7E4E8,0xCAEAEC },                             // Ice
        { 0xF97C44,0xF77C38,0xF57D2B,0xF68131,0xF48326,0xF78E37,0xF68D30,0xF69232,0xF6982E,0xF59729,0xF3921D,0xF3981E,0xF49C24,0xF4AE1D }                             // Sun
  };


    int[] createColourTable_better( int []colourTable, int numCols ) {
      colourTable = new int[numCols];
      int table = (int)random(colours.length);
      int tableLen = colours[table].length;
      for( int i = 0 ; i < numCols ; i++ ) {
        colourTable[i] = colours[table][(int)(((float)i/(float)numCols)*(float)tableLen)];
      }
      return colourTable;
    }
    
    
    int[] createColourTable( int []colourTable, int numCols ) {
        colourTable = new int[numCols];
        for( int i = 0 ; i < numCols ; i++ ) {
          colourTable[i] = getRainbowColourFromLinearNumber( (1.f/(float)numCols) * (float)i );
        }
        return colourTable;
}



int INC_RED = 1;
int INC_GREEN = 2;
int INC_BLUE = 4;
int DEC_RED = 8;
int DEC_GREEN = 16;
int DEC_BLUE = 32;

int []interpolateColourTable = { color(255,0,0), color(255,255,0), color(0,255,0), color(0,255,255),
                          color(0,0,255), color(255,0,255) };
int []colourMovementTable = { INC_GREEN, DEC_RED, INC_BLUE, DEC_GREEN, INC_RED, DEC_BLUE };                         
                          
int getRainbowColourFromLinearNumber( float num ) {
  int idx = -1;
  float scaledNum = 0.f;
  for( int i = 1 ; i <= 6 ; i++ ) {
    if( num < ((1.f / 6.f)*((float)i)) ) {
      scaledNum = num - ((1.f / 6.f)*((float)i-1));
      scaledNum *= 6f;
      //println( num + " is in group "+i+", "+scaledNum+" in it" );
      idx = i - 1;
      break;
    }
  }
  if( idx >= 0 ) {
    return color( red(interpolateColourTable[idx])
                        + (((colourMovementTable[idx]&INC_RED)==INC_RED) ? (int)(255.f * scaledNum) : 0)
                        - (((colourMovementTable[idx]&DEC_RED)==DEC_RED) ? (int)(255.f-(255.f * (1.f-scaledNum))) : 0),
                  green(interpolateColourTable[idx])
                        + (((colourMovementTable[idx]&INC_GREEN)==INC_GREEN) ? (int)(255.f * scaledNum) : 0)
                        - (((colourMovementTable[idx]&DEC_GREEN)==DEC_GREEN) ? (int)(255.f-(255.f * (1.f-scaledNum))) : 0),
                  blue(interpolateColourTable[idx])
                        + (((colourMovementTable[idx]&INC_BLUE)==INC_BLUE) ? (int)(255.f * scaledNum) : 0)
                        - (((colourMovementTable[idx]&DEC_BLUE)==DEC_BLUE) ? (int)(255.f-(255.f * (1.f-scaledNum))) : 0) );
  }
  return color(255);
}


}
