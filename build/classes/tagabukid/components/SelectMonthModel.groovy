package tagabukid.components;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

class SelectMonthModel extends ComponentBean {
    
    def monthList = [ 
        [id:1, title:'JAN'], 
        [id:2, title:'FEB'], 
        [id:3, title:'MAR'], 
        [id:4, title:'APR'], 
        [id:5, title:'MAY'], 
        [id:6, title:'JUN'], 
        [id:7, title:'JUL'], 
        [id:8, title:'AUG'], 
        [id:9, title:'SEP'], 
        [id:10, title:'OCT'], 
        [id:11, title:'NOV'], 
        [id:12, title:'DEC']
    ];
    
    
    public int getMonth() {
        return getValue();
    }
    
    public void setMonth( int o ) {
        setValue( o );
    }
    
}