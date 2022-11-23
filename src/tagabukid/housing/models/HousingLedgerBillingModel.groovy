package tagabukid.housing.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

class HousingLedgerBillingModel{

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
    
    def amount = 0;
    
    def dtp
    
    def abc
    def total
    def totalamount
    def totalpenalty
    def totalhouseamount
    def totallotamount
    def totalmriamount
    
    def itemHandler = [
        fetchList: { o->
            o.ledgerid = caller.objid;
            o.dtp = dtp
            if( amount > 0 ) o.amount = amount;
            abc = billingService.getBilling( o )
            
            //return billingService.getBilling( o );
            total = abc.linetotal.sum()
            totalamount = abc.amount.sum()
            totalpenalty = abc.penalty.sum()
            totalhouseamount = abc.house.sum()
            totallotamount = abc.lot.sum()
            totalmriamount = abc.mri.sum()
            //println total
           
            return abc
            
        }
    ] as BasicListModel;

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
    
//    def pay() {
////        println total
////        println totalamount
////        println totalpenalty
////        println abc
////        println caller.entity
//        def payor = caller.entity.objid
////        println payor
//
//        def z = hpoService.findCurrentbill(payor)
//        println z
//        
//        def p = [_schemaname: 'temp_housing_ledger_payment_item'];
//        p.findBy = [ 'parent.ledgerid': caller.entity.objid, 'parent.objid': z ];
//        p.select = "objid,parent.objid,parent.ledgerid,parent.billasof,year,month,houseamount,lotamount,mriamount,amount,penalty";
//        p.orderby = 'parent.billasof'
//        def fromtemp = querySvc.getList( p );
//        
//        println fromtemp
//        
//        def xbill = billingService.getPaybill(fromtemp)
//        
////        println xbill
//        
//        def ztotal = xbill.linetotal.sum()
//        def ztotalamount = xbill.amount.sum()
//        def ztotalpenalty = xbill.penalty.sum()
//        def ztotalhouseamount = xbill.house.sum()
//        def ztotallotamount = xbill.lot.sum()
//        def ztotalmriamount = xbill.mri.sum()
//        
//        println ztotal
//        println ztotalamount
//        println ztotalpenalty
//        println ztotalhouseamount
//        println ztotallotamount
//        println ztotalmriamount
//        
//        def order = [
//            person : caller.entity.objid,
//            totamount : ztotalamount,
//            totpenalty : ztotalpenalty,
//            tothouseamount : ztotalhouseamount,
//            totlotamount : ztotallotamount,
//            totmriamount : ztotalmriamount,
//            tot : ztotal,
//            remarks : fromtemp.parent.objid[0]
//        ]
//        
//        println order
//        
//        def x = hpoService.preparePayment(order)
//        
////        println x
//                
//        MsgBox.alert "Payment Order Number : " + x.ordernum
//        
////        getPersistenceService().update([ 
////             _schemaname: 'vetpermit', 
////             objid : entity.objid, 
////             state : 'FLOAT' 
////        ]);
//        //loadData(); 
//
////        def op = Inv.lookupOpener( "paymentorder:housing", [entity: caller.entity.objid] );
////        
////        println op
////        return op;
//
//
//        
//
//       
//    }

    
}