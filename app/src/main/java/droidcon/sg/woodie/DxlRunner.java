package droidcon.sg.woodie;

import android.os.SystemClock;
import android.util.Log;
import com.google.android.things.pio.UartDevice;
import java.io.IOException;

/**
 * Created by jeff on 25/3/18.
 */

public class DxlRunner implements Runnable{

    private static final String TAG = "DxlRunner";

    UartDevice mUsb;
    ActionGroup[] mScript;

    DxlRunner(UartDevice usbDevice,ActionGroup[] script) {

        mUsb=usbDevice;
        mScript = script;
    }

    public void run() {
        runScript(mScript);
    }

    void runScript(ActionGroup[] group) {

        if (group != null) {

            for(int x=0;x<group.length;x++)
            {
                DxlVector[] vect =group[x].vectors;
                for (DxlVector dxl : vect) {

                    runVectors(vect);
                }
            }
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
                moveDxl(mUsb, dxl.id, posUnit, powerUnit);
                SystemClock.sleep(100);
            }
        }
    }
    void moveDxl(UartDevice device,int id,int positionUnit,int powerUnit)
    {
        if(device!=null) {
            try {

                byte cmd =0;
                byte reqid=0;

                byte[] bPos = toBytes(positionUnit);
                byte[] bPower = toBytes(powerUnit);
                byte[] buffer = {cmd,reqid,(byte) id, bPos[0], bPos[1], bPower[0], bPower[1]};

                Log.i(TAG, "b[0]="+buffer[0]+",b[1]="+buffer[1]+",b[2]="+buffer[2]+",b[3]="+buffer[3]+",b[4]="+buffer[4]+",b[5]="+buffer[5]+",b[6]="+buffer[6]);

                int pos= buffer[3] | buffer[4] << 8;
                int speed= buffer[5] | buffer[6] << 8;
                Log.i(TAG, "pos="+pos+",speed"+speed);

                writeUartData(device, buffer);
            } catch (IOException ex) {
                String usbName = device.getName();
                Log.i(TAG, "Error writing to usb " + usbName);
            }
        }
        else {
            Log.i(TAG, "Usb device is null");
        }
    }

    byte[] toBytes(int i)
    {
        byte[] result = new byte[2];

        result[0] = (byte) i;
        result[1] = (byte) (i >> 8);

        return result;
    }

    int fromBytes(byte[] buffer)
    {
        int r = buffer[0] | buffer[1] << 8;
        return r;
    }

    public void writeUartData(UartDevice uart, byte[] buffer) throws IOException {
        int count = uart.write(buffer, buffer.length);
    }
}
