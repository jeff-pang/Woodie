package droidcon.sg.woodie;

/**
 * Created by jeff on 25/3/18.
 */

public class DxlVector {
    public static String TAG="DxlVector";
    public int id;
    public int position;
    public float power;
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

    public static int PowerToUnit(float percent)
    {
        if(percent>1)
        {
            percent=1;
        }
        return (int)(percent * 1024);
    }

    public static int DegreesToUnit(float degree) {
        float d=degree+150;

        if (d >= 300) {
            d=300;
        }

        if (d < 0) {
            d=0;
        }

        float gp=Math.round((1023.0/300.0) * d);
        return (int)gp;
    }
}