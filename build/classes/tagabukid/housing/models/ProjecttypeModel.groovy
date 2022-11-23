package tagabukid.housing.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

class ProjecttypeModel extends CrudFormModel{
    
    public void beforeSave(o){
        
        entity.state = "DRAFT"
    }

   
}