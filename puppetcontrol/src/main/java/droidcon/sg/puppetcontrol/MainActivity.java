package droidcon.sg.puppetcontrol;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
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

    private static final String TAG = "MainActivity";

    UartDevice mDevice;
    private FirebaseDatabase mDatabase;

    HashMap<Integer,DxlStatus> lastStatuses=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDevice = initialiseUsb();
        mDatabase = FirebaseDatabase.getInstance();

        final DatabaseReference puppet = mDatabase.getReference("puppet");

        DxlStatusBus statusBus = new DxlStatusBus(mDevice);

        statusBus.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(statuses-> {
                    for (DxlStatus status : statuses)
                    {
                        if(!lastStatuses.containsKey(status.dxlid))
                        {
                            lastStatuses.put(status.dxlid,status);
                            puppet.child("dxl"+status.dxlid).setValue(status.position);
                        }
                        else
                        {
                            DxlStatus lastStatus=lastStatuses.get(status.dxlid);
                            if(lastStatus.position != status.position)
                            {
                                puppet.child("dxl"+status.dxlid).setValue(status.position);
                                lastStatuses.put(status.dxlid,status);

                                Log.i(TAG,"Setting Firebase puppet dxl:"+status.dxlid+" value:"+status.position);
                            }
                        }

                        Log.d(TAG, "dxl" + status.dxlid + " position:" + status.position);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
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
