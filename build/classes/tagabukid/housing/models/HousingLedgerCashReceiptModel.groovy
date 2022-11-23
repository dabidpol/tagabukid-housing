package tagabukid.housing.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

class HousingLedgerCashReceiptModel extends com.rameses.enterprise.treasury.cashreceipt.AbstractCashReceipt {

    @Caller
    def caller;    

    @Service("TagabukidHousingCashReceiptService")
    def cashReceiptSvc;
    
    def barcodeid;
    String entityName = "misc_cashreceipt"    
    
     String title = "Housing Rental";
     def payOption = [type: "Full"];  
     def selectedItem;
    
     def loadData(acctno) {
        def info = cashReceiptSvc.getInfo(acctno);
        entity.putAll( info );
        listModel.reload();
        //reload();
    }
    
    def loadBarcode() { 
        loadData( barcodeid );
        super.init(); 
        return "default";
    }   
    
    def listModel = [
        fetchList: { o->
            return entity.items;
        }
    ] as BasicListModel;
    
    void reload() {
        def bill =  cashReceiptSvc.getInfo( entity.acctid );
        entity.putAll(bill);
        updateBalances();
        if( binding !=null ) {
            listModel.reload();
            binding.refresh();
        }
        
    }
    
}