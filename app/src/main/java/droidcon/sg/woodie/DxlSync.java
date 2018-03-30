package droidcon.sg.woodie;

import android.os.SystemClock;
import android.util.Log;

import com.google.android.things.pio.UartDevice;

import java.util.LinkedList;
import java.util.Queue;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by jeff on 27/3/18.
 */

public class DxlSync {

    private static final String TAG = "DxlSync";
    private int _dxlId;
    private int _targetPos;
    private  UartDevice _device;
    private Queue<DxlQuery> _queries;
    private DxlQuery _current;

    public DxlSync(UartDevice device)
    {
        _queries=new LinkedList<>();
        _device=device;

        DxlStatusBus bus = new DxlStatusBus(device);

        bus.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s-> {

                    if(s.dxlid == 0)
                    {
                        Log.i(TAG, "skip");
                    }
                    else
                    {
                        Log.i(TAG, "dxl" + s.dxlid + " position:" + s.position + " moving:" + s.isMoving);
                    }

                    if(_current!=null)
                    {

                        if(s.dxlid==_current.getDxlId())
                        {
                            if(DxlHelper.approximatePosition(s.position,_current.getTargetPos()))
                            {
                                if(!_queries.isEmpty())
                                {
                                    _current = _queries.remove();
                                    byte[] buffer = {2};
                                    DxlHelper.writeUartData(_device, buffer);
                                }
                                else
                                {
                                    _current=null;
                                }
                            }
                        }

                        Log.i(TAG, "getting info for dxl" + _current.getDxlId());
                        getDxlInfo(_current.getDxlId());
                    }

                    SystemClock.sleep(100);
                });
    }

    public void AddQueue(DxlQuery query)
    {
        _queries.add(query);

        if(_current==null)
        {
            _current=_queries.remove();
        }

        Log.i(TAG, "dxl"+query.getDxlId()+" query added");
        getDxlInfo(_current.getDxlId());
    }

    void getDxlInfo(int dxlid)
    {
        byte[] buffer = {1,1,(byte) dxlid};
        DxlHelper.writeUartData(_device, buffer);
    }
}