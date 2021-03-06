package droidcon.sg.puppetcontrol;

import android.util.Log;

import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by jeff on 4/4/18.
 */

public class DxlStatusBus {

    private PublishSubject<DxlStatus[]> bus = PublishSubject.create();
    private static final String TAG = "DxlStatusBus";
    private UartDevice _device;

    public DxlStatusBus(UartDevice device) {
        _device = device;

        try {
            _device.registerUartDeviceCallback(mUartCallback);
        } catch (IOException e) {
            Log.w(TAG, "Unable to access UART device", e);
        }
    }

    public Observable<DxlStatus[]> toObservable() {
        return bus;
    }

    public UartDeviceCallback mUartCallback = new UartDeviceCallback() {
        @Override
        public boolean onUartDeviceDataAvailable(UartDevice uart) {
            // Read available data from the UART device
            try {
                byte[] buffer=DxlHelper.readUartBuffer(uart);
                List<DxlStatus> statuses = DxlHelper.BytesToStatuses(buffer);
                DxlStatus[] s = new DxlStatus[statuses.size()];
                bus.onNext(statuses.toArray(s));

            } catch (IOException e) {
                Log.w(TAG, "Unable to access UART device", e);
            }

            // Continue listening for more interrupts
            return true;
        }

        @Override
        public void onUartDeviceError(UartDevice uart, int error) {
            Log.w(TAG, uart + ": Error event " + error);
        }
    };

}
