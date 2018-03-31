package droidcon.sg.woodie;

import android.os.SystemClock;
import android.util.Log;

import com.google.android.things.pio.UartDevice;

import java.util.LinkedList;
import java.util.Queue;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

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

    private PublishSubject<DxlStatus> publisher = PublishSubject.create();

    public DxlSync(UartDevice device)
    {
        _queries=new LinkedList<>();
        _device=device;

        DxlStatusBus statusBus = new DxlStatusBus(device);

        statusBus.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s-> {

                    if(_current==null && !_queries.isEmpty())
                    {
                        _current = _queries.remove();
                        Log.i(TAG, "pop dxl" + _current.getDxlId());
                        getDxlInfo(_current.getDxlId());
                    }

                    if(s.dxlid == 0)
                    {
                        Log.i(TAG, "skip");
                    }
                    else
                    {
                        Log.i(TAG, "dxl" + s.dxlid + " position:" + s.position + " moving:" + s.isMoving);

                        if(_current!=null)
                        {
                            Log.i(TAG, "current dxl" + _current.getDxlId() + " target position:" + _current.getTargetPos());
                        }
                        else
                        {
                            Log.i(TAG, "not current dxl");
                        }
                    }

                    if(_current!=null)
                    {
                        if(s.dxlid==_current.getDxlId() && !s.isMoving)
                        {
                            if(DxlHelper.approximatePosition(s.position,_current.getTargetPos()))
                            {
                                _current = null;
                                byte[] buffer = {2};
                                DxlHelper.writeUartData(_device, buffer);
                                publisher.onNext(s);
                            }
                        }

                    }

                    SystemClock.sleep(10);
                });
    }

    public Observable<DxlStatus> toObservable() {
        return publisher;
    }

    public void AddQueue(DxlQuery query)
    {
        _queries.add(query);

        if(_current==null && !_queries.isEmpty())
        {
            _current = _queries.remove();
            Log.i(TAG, "pop dxl" + _current.getDxlId());
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