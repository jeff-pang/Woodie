package droidcon.sg.puppetcontrol;

import android.util.Log;

import com.google.android.things.pio.UartDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeff on 4/4/18.
 */
public class DxlHelper {

    private static final String TAG = "DxlHelper";

    public static byte[] toBytes(int i)
    {
        byte[] result = new byte[2];

        result[0] = (byte) i;
        result[1] = (byte) (i >> 8);

        return result;
    }

    public static int fromBytes(byte[] buffer)
    {
        int r = buffer[0] | buffer[1] << 8;
        return r;
    }

    public static void writeUartData(UartDevice uart, byte[] buffer) {
        try
        {
            int count = uart.write(buffer, buffer.length);
        } catch (IOException ex) {
            String usbName = uart.getName();
            Log.i(TAG, "Error writing to usb " + usbName);
        }
    }

    public static byte[] readUartBuffer(UartDevice uart) throws IOException {
        // Maximum amount of data to read at one time

        byte[] buffer = new byte[20];

        int count = uart.read(buffer, buffer.length);
        Log.d(TAG, "Read " + count + " bytes, data:"+bytesToHex(buffer));

        return buffer;
    }

    public static List<DxlStatus> BytesToStatuses(byte[] buffer)
    {
        List<DxlStatus> statuses = new ArrayList<>();

        for(int x=0;x<5;x++) {

            int offset=x*4;
            DxlStatus status = new DxlStatus();
            status.dxlid = buffer[offset];
            status.isMoving = (buffer[offset+1] == 1);

            int p1 = ((buffer[offset+3] & 0xFF) << 8) | (buffer[offset+2] & 0xFF);
            status.position = DxlVector.UnitToDegrees(p1);

            statuses.add(status);
            Log.d(TAG, "dxl" + status.dxlid + " position:" + status.position + "(p1=" + p1 + ")");
        }
        return statuses;
    }

    public static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static boolean approximatePosition(int position,int compareWith)
    {
        int lower = position - 1 ;
        int upper = position + 1;

        return (compareWith>=lower && compareWith<=upper);
    }
}
