package tagabukid.housing.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

class HousingLedgerCapturePaymentModel extends CrudFormModel {
    
    @Service("TagabukidHousingLedgerCaptureService")
    def ledSvc;
    
    def parent;
    def selectedItem;
    
    public void afterCreate() {
        entity.items = [];
        entity.ledgerid = parent.objid;
    }
    
    public void beforeSave(o){
        //println entity
        
        //parent.lastdatepaid = entity.receiptdate

        def x = ledSvc.findCapturedlastdatecovered(entity)
        //parent.lastdatecovered = x
        

    }
    
    void updateAmount() {
        entity.amount = 0;
        entity.penalty = 0;
        entity.penalty = entity.items.sum{x-> x.penalty };
        entity.amount = entity.items.sum{x-> x.amount } ;
        entity.totalamount = entity.penalty + entity.amount
        binding.refresh("entity.totalamount");
    }
    
    def itemListModel = [
        fetchList: {
            return entity.items;
        },
        onAddItem: { o->
            addItem("items", o);
            updateAmount();
        },
        onRemoveItem: { o->
            removeItem("items", o);
            updateAmount();
        },
        onColumnUpdate: { o,colName ->
            if(colName=='year' ) {
                if(o.year != null){
                    println parent
                    o.houseamount = ledSvc.findHouseAmount(parent.objid)
                    o.lotamount = ledSvc.findLotAmount(parent.objid)
                    o.mriamount = ledSvc.findMriAmount(parent.objid)
                    o.amount = ledSvc.findMonthlyAmortization(parent.objid)
                    o.penalty = ledSvc.findCapturedPenalty(o,entity.receiptdate)
                    updateAmount();
                }
            }
             
        }
    ] as EditorListModel;
}