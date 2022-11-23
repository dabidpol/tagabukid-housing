package tagabukid.housing.reports;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.reports.*;
import com.rameses.gov.etracs.rpt.common.*;
import com.rameses.etracs.shared.*;

public class housingBillingController extends ReportController
{
   @Service('TagabukidHousingBillingReportService')
   def svc;
   
   @Service("TagabukidHousingLedgerBillingService")
   def billingService;
    
   @Service("TagabukidHousingPaymentOrderService")
   def hpoService
    
   @Service('QueryService')
   def querySvc
   
   @Caller
   def caller; 
   
   //@Service("ReportParameterService")
   //def paramSvc;
   
  // def query = [:];
  
   @Service("ReportParameterService")
   def paramSvc;
   
   
   String title = 'Print Bill';
   String reportPath = 'tagabukid/housing/reports/';
   String reportName = reportPath + 'housingbillingreport.jasper';
   
   
   
   public def getReportData(){
       //entity is personnel info akin to the platform
//       MsgBox.alert(svc.getReportData(entity))
       return svc.getHousingReportData(entity);
       
   }
   
    Map getParameters (){
        def params = paramSvc.getStandardParameter()
       
        //params.signature = EtracsReportUtil.getInputStream("sirJoelSignature.png")
        params.logos = ReportUtil.getImageAsStream("buk.png")
        //params.pvetlogo = ReportUtil.getImageAsStream("pvetlogo.png")
        return params 
   }
   
   SubReport[] getSubReports(){
       return[
           new SubReport('BILLITEMS', reportPath+'housingbillingreportitems.jasper'), 
       ]
   }
   
    
     def pay() {
//        println total
//        println totalamount
//        println totalpenalty
//        println abc
//        println caller.entity
        //def payor = caller.entity.objid
//        println payor

        def z = hpoService.findCurrentbill(entity)
        println z
        
        def p = [_schemaname: 'temp_housing_ledger_payment_item'];
        p.findBy = [ 'parent.ledgerid': entity, 'parent.objid': z ];
        p.select = "objid,parent.objid,parent.ledgerid,parent.billasof,year,month,houseamount,lotamount,mriamount,amount,penalty";
        p.orderby = 'parent.billasof'
        def fromtemp = querySvc.getList( p );
        
        println fromtemp
        
        def xbill = billingService.getPaybill(fromtemp)
        
//        println xbill
        
        def ztotal = xbill.linetotal.sum()
        def ztotalamount = xbill.amount.sum()
        def ztotalpenalty = xbill.penalty.sum()
        def ztotalhouseamount = xbill.house.sum()
        def ztotallotamount = xbill.lot.sum()
        def ztotalmriamount = xbill.mri.sum()
        
        println ztotal
        println ztotalamount
        println ztotalpenalty
        println ztotalhouseamount
        println ztotallotamount
        println ztotalmriamount
        
        def order = [
            person : entity,
            totamount : ztotalamount,
            totpenalty : ztotalpenalty,
            tothouseamount : ztotalhouseamount,
            totlotamount : ztotallotamount,
            totmriamount : ztotalmriamount,
            tot : ztotal,
            remarks : fromtemp.parent.objid[0]
        ]
        
        println order
        
        def x = hpoService.preparePayment(order)
        
//        println x
                
        MsgBox.alert "Payment Order Number : " + x.ordernum
        
//        getPersistenceService().update([ 
//             _schemaname: 'vetpermit', 
//             objid : entity.objid, 
//             state : 'FLOAT' 
//        ]);
        //loadData(); 

//        def op = Inv.lookupOpener( "paymentorder:housing", [entity: caller.entity.objid] );
//        
//        println op
//        return op;


        

       
    }
}

