package droidcon.sg.woodie;

import android.os.SystemClock;
import android.util.Log;
import com.google.android.things.pio.UartDevice;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jeff on 25/3/18.
 */

public class DxlRunner implements Runnable{

    private static final String TAG = "DxlRunner";

    UartDevice mUsb;
    ActionFrame mFrame;

    DxlRunner(UartDevice usbDevice,ActionFrame frame) {

        mUsb=usbDevice;
        mFrame = frame;
    }

    public void run() {
        runScript(mFrame);
    }

    private int x=0;
    void runScript(ActionFrame frame) {

        if (frame != null) {
            x=0;
            ArrayList<Integer> completed=new ArrayList<>();
            runVectors(frame.vectors);
        }
        else
        {
            Log.i(TAG,"runScript:node is null");
        }
    }

    void runVectors(DxlVector[] vectors) {
        if(vectors!=null) {
            for (DxlVector dxl : vectors) {

                int powerUnit = DxlVector.PowerToUnit(dxl.power);
                int posUnit = DxlVector.DegreesToUnit(dxl.position);
                moveDxl(dxl.id, posUnit, powerUnit);
                SystemClock.sleep(100);
                SystemClock.sleep(100);
            }
        }
    }

    void moveDxl(int id,int positionUnit,int powerUnit)
    {
        if(mUsb!=null) {

            byte cmd =0;
            byte reqid=0;

            byte[] bPos = DxlHelper.toBytes(positionUnit);
            byte[] bPower = DxlHelper.toBytes(powerUnit);
            byte[] buffer = {cmd,reqid,(byte) id, bPos[0], bPos[1], bPower[0], bPower[1]};

            Log.i(TAG, "b[0]="+buffer[0]+",b[1]="+buffer[1]+",b[2]="+buffer[2]+",b[3]="+buffer[3]+",b[4]="+buffer[4]+",b[5]="+buffer[5]+",b[6]="+buffer[6]);

            int pos= ((buffer[4] & 0xff) << 8) | (buffer[3] & 0xff);
            int speed= ((buffer[6] & 0xff) << 8) | (buffer[5] & 0xff);
            Log.i(TAG, "pos="+pos+",speed"+speed);
            DxlHelper.writeUartData(mUsb, buffer);
        }
        else {
            Log.i(TAG, "Usb device is null");
        }
    }
}