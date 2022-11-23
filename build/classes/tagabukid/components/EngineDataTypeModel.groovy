package tagabukid.components;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

class EngineDataTypeModel extends ComponentBean {

    def makeList = [ 'HANSHIN', 'MITSUBISHI', 'TOYOTA' ]
    
    public def getEngine() {
        if( getValue()==null ) setValue([:]);
        return getValue();
    }
    
    public void setEngine( def o ) {
        if(o==null) o = [:];
        setValue( o );
    }
    
}