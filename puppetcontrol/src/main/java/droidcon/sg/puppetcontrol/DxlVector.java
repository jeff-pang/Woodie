package droidcon.sg.puppetcontrol;

/**
 * Created by jeff on 4/4/18.
 */
public class DxlVector {
    public static String TAG="DxlVector";
    public int id;
    public int position;
    public int power;
    public int postWait;
    public int preWait;

    public static int CalculateDuration(int dPosUnit,int powerUnit)
    {
        //114 rpm = 1.9 rps = 0.5263 sec per rev
        //300 radians = (0.526 / 360) * 300 = 0.44
        float rps = (float)((powerUnit / 1024f) * 1.9);
        float radian = dPosUnit / 1024f;
        float radianPerSec= rps * radian;

        return (int)(radianPerSec*1000);
    }

    public static int PowerBySpeed(int dP,int ms)
    {
        int rpms = (int)((1.9f * (dP / 1024)) * 1000);
        int power= (ms/rpms) * 1024;

        if(power>1024)
        {
            power =1024;
        }
        return  power;
    }

    public static int PowerToUnit(int percent)
    {
        if(percent>100)
        {
            percent=100;
        }

        float pc = (float)percent/100f;
        return (int)(pc * 1024);
    }

    public static int DegreesToUnit(float degree) {
        float d=degree+150;

        if (d >= 300) {
            d=300;
        }

        if (d < 0) {
            d=0;
        }

        float constant = 3.41f;
        float gp = 3.41f * d;
        return (int)gp;
    }

    public static int UnitToDegrees(int goal)
    {
        float g=(float)goal;
        if(goal==65535)
        {
            g=0;
        }

        float pc = g / 1023f;
        int degrees= (int)roundUp(pc * 300);

        if(degrees!=0) {
            return degrees - 150;
        }
        else
        {
            return 0;
        }
    }

    private static float roundDown (float value) {
        float v = Math.round(value - 0.05f);
        return v;
    }

    private static float roundUp (float value) {
        float v = Math.round(value + 0.05f);
        return v;
    }
}