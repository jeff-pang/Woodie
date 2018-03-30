package droidcon.sg.woodie;

/**
 * Created by jeff on 29/3/18.
 */

public class DxlQuery {
    int _dxlId;
    int _targetPos;

    public DxlQuery(int dxlId,int targetPos)
    {
        _dxlId=dxlId;
        _targetPos=targetPos;
    }

    public int getDxlId() {
        return _dxlId;
    }

    public  int getTargetPos(){
        return _targetPos;
    }
}