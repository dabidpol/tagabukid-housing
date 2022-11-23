package tagabukid.housing.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

class HousingLedgerFullpaymentModel{

    @Caller
    def caller;  
    
    @Binding
    def binding;

    @Service("TagabukidHousingLedgerBillingService")
    def billingService;
    
    @Service("TagabukidHousingPaymentOrderService")
    def hpoService
    
    @Service('QueryService')
    def querySvc
    
    //def entity
    
    def amount = 0;
    
    def dtp
    
    def abc
    
    def receiptdate
    def receiptno
   
    def selectedItem
    def itemHandler = [
        fetchList: { o->
            o.ledgerid = caller.objid;
//            o = caller
//            println o
            o.dtp = dtp
            o.receiptno = receiptno
            o.receiptdate = receiptdate
            if( amount > 0 ) o.amount = amount;
            abc = billingService.getFullPaymentComputation( o )

            return abc
  
        }
    ] as EditorListModel;

    void calc() {
        itemHandler.reload();
        //updateTotal();
    }
    
    def doClose() {
        return "_close";
    }
    
    def bill(){
        def zx = hpoService.tempdbSupport(abc,caller)
        println zx
        
        
        def op = Inv.lookupOpener( "billing:housing", [entity: caller.objid] );
    }
    
    def captureFullPayment() {
        if ( MsgBox.confirm('Capturing a full payment will create another ledger. Do you want to proceed?')) {
            println caller
            println selectedItem
            if(selectedItem.receiptno == null || selectedItem.receiptdate == null){
                MsgBox.alert("Please provide a receipt number and a receiptdate")
            }
            
           
            def spi = caller 
            def selecteditem = selectedItem
            billingService.newLedger(spi,selecteditem)
            
            
            //return Inv.lookupOpener("housing_ledger_capture_full_payment", [entity: caller.objid ] );
            return "_close"
        }
    }

    
}