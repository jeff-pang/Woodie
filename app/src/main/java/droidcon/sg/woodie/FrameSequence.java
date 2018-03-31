package droidcon.sg.woodie;

import android.util.Log;

import com.google.android.things.pio.UartDevice;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by jeff on 31/3/18.
 */

public class FrameSequence {

    private static final String TAG = "FrameSequence";

    List<Integer> dxlIds=new ArrayList<>();
    ActionFrame _frame;
    DxlSync _sync;

    private PublishSubject<ActionFrame> publisher = PublishSubject.create();

    public FrameSequence(DxlSync sync, ActionFrame frame)
    {
        _frame = frame;
        _sync = sync;

        _sync.toObservable().subscribe(s-> {
            if(!dxlIds.contains(s.dxlid))
            {
                boolean found = false;

                for(int x=0;x<_frame.vectors.length;x++)
                {
                    if(_frame.vectors[x].id == s.dxlid)
                    {
                        found = true;
                        break;
                    }
                }

                if(found)
                {
                    dxlIds.add(s.dxlid);
                }
            }

            Log.i(TAG, dxlIds.size()+" of "+ _frame.vectors.length +" moves completed");

            if(dxlIds.size() == _frame.vectors.length)
            {
                Log.i(TAG, "frame completed");
                publisher.onNext(_frame);
            }
        });
    }

    public void Reset(ActionFrame frame)
    {
        _frame=frame;
        dxlIds.clear();
    }

    public Observable<ActionFrame> toObservable() {
        return publisher;
    }

}
