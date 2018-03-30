package droidcon.sg.woodie;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {

    UartDevice mDevice;
    Scripts mScripts;
    private static final String TAG = "MainActivity";

    DxlSync mSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScripts=loadJSONFromAsset();

        mDevice = initialiseUsb();
        mSync=new DxlSync(mDevice);
        if(mScripts !=null && mScripts.scripts!=null)
        {
            Log.i(TAG,"Running script 'wavehello'");
            ActionFrame[] frames = mScripts.scripts.get("wavehello");

            DxlRunner runner = new DxlRunner(mDevice,frames[0]);
            new Thread(runner).start();

            DxlVector[] vectors=frames[0].vectors;

            if(vectors!=null) {
                for (DxlVector dxl : vectors) {
                    DxlQuery q=new DxlQuery(dxl.id,dxl.position);
                    mSync.AddQueue(q);
                }
            }
        }
        else
        {
            Log.i(TAG,"Script is null");
        }
    }

    Scripts loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("script.json");

            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

            JsonParser parser = new JsonParser();
            JsonElement mJson =  parser.parse(json);
            Gson gson = new Gson();

            Scripts object = gson.fromJson(mJson,Scripts.class);
            return object;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    UartDevice initialiseUsb()
    {
        PeripheralManager service = PeripheralManager.getInstance();
        List<String> deviceList = service.getUartDeviceList();

        if (deviceList.isEmpty()) {
            Log.i(TAG, "No UART port available on this device.");
        }
        else {
            Log.i(TAG, "List of available devices: " + deviceList);
            String usbName = "";
            for (int x = 0; x < deviceList.size(); x++) {
                String dev = deviceList.get(x);
                if (!dev.equals("MINIUART") && !dev.equals("UART0")) {
                    usbName = dev;
                    break;
                }
            }

            if (usbName != null && !usbName.isEmpty()) {
                try {
                    Log.i(TAG, "Aquiring usb " + usbName);
                    UartDevice device = service.openUartDevice(usbName);
                    configureUartFrame(device);
                    //device.registerUartDeviceCallback(new UsbReaderCallback(device));
                    return device;
                } catch (IOException ex) {
                    Log.i(TAG, "Error acquiring to usb " + usbName);
                }
            }
        }
        return null;
    }

    public void configureUartFrame(UartDevice uart) throws IOException {
        // Configure the UART port
        uart.setBaudrate(115200);
        uart.setDataSize(8);
        uart.setParity(UartDevice.PARITY_NONE);
        uart.setStopBits(1);
    }
}
