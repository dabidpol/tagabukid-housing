package tagabukid.housing.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*;
import com.rameses.util.*;
import java.text.SimpleDateFormat
import java.lang.Math

class HousingBeneficiaryController extends CrudFormModel{

    @Service("HousingInfoService")
    def infoSvc;
    
    @Service("TagabukidHousingLedgerBillingService")
    def ledSvc;
    
    @Service('DateService')
    def dtSvc;
    
    @Service("PersistenceService")
    def persistenceSvc;
    
    def financingtypes = ['PRIVATE', 'GOVERNMENT', 'PROVINCE']
    def ptoaccounts = ['houseandlot', 'lotonly', 'houseonly']
    def states = ['ACTIVE', 'CLOSED']
    def withmris = ['YES', 'NO']
 
    boolean isAllowApprove() {
         return ( mode=='read' && entity.state.toString().matches('DRAFT|ACTIVE') ); 
    }
    
    public void afterCreate(){
        //entity.txndate = dtSvc.getBasicServerDate();
        //entity.expdate = dtSvc.getBasicServerDate() + 3;
        entity.state = "DRAFT"
//        entity.houseamortization = 0.00 
//        entity.lotamortization = 0.00
//        entity.mri = 0.00
        
//        entity.projectitems.each{
//            it.houseamortization = 0.00 
//            it.lotamortization = 0.00
//            it.mri = 0.00
//            }
            
//        entity.projectitems = [
//            houseamortization : 0.00,
//            lotamortization : 0.00,
//            mri : 0.00
//        ];

        entity.projectitems = [];
//         entity.projectitems.ledgeritems = [];
            
         projectitemHandler.reload();
        
    }
    
    public void beforeSave(o){
        entity.recordlog_datecreated = dtSvc.getServerDate();
        entity.recordlog_createdbyuser = OsirisContext.env.FULLNAME;
        entity.recordlog_createdbyuserid = OsirisContext.env.USERID;
        entity.recordlog_dateupdated = dtSvc.getServerDate();
        entity.recordlog_lastupdatedbyuser = OsirisContext.env.FULLNAME;
        entity.recordlog_lastupdatedbyuserid = OsirisContext.env.USERID;
        
//        entity.projectitems.each{
//            if(it.principal){
//                it.mri = ((it.principal/1000)*0.76);
//            }else{
//                it.mri = 0.00
//            }
//            
//            if(it.interest){
//                it.totalpayable = (it.interest/100) * it.principal * it.duration 
//            }else{
//                it.totalpayable = 0.00
//            }
//            
//            it.monthlyamortization = it.houseamortization + it.lotamortization + it.mri 
//            }
            
        projectitemHandler.reload();
        
        //entity.monthlyamortization = entity.houseamortization + entity.lotamortization + entity.mri

    }

    public void afterEdit(){
        entity.recordlog_dateupdated = dtSvc.getServerDate();
        entity.recordlog_lastupdatedbyuser = OsirisContext.env.FULLNAME;
        entity.recordlog_lastupdatedbyuserid = OsirisContext.env.USERID;
    }
    
    public void afterOpen(){
        
        entity.projectitems.each {
            def ledid = persistenceSvc.read([_schemaname:'housing_beneficiary_ledger', objid:it.objid])
            it.totalamountpaid = ledSvc.findTotalAmountPaid(ledid.objid);
        }
        projectitemHandler.reload();
    }
    
    def ledgerEntryHandler = [
        fetchList: { o->
            if (!selectedProjectitem) return []
            
            def p = [_schemaname: 'housing_ledger_payment_item'];
            p.findBy = [ 'parent.ledgerid': selectedProjectitem.objid ];
            p.select = "objid,parent.receiptno,parent.receiptdate,year,month,houseamount,lotamount,mriamount,amount,penalty,parent.totalamount";
            p.orderBy = "year DESC,month DESC"
            return queryService.getList( p );
        }
    ] as BasicListModel;

    def capturePayment() {
        return Inv.lookupOpener("housing_ledger_capture_payment", [parent: selectedProjectitem ] );
    }

    void refreshItem() {
        ledgerEntryHandler.reload();
    }
    
   def restructure(){
       // MsgBox.alert "Testing Successful"
        if (selectedProjectitem.state == 'ACTIVE'){
            if ( MsgBox.confirm('You are about to restructure this Ledger. Do you want to proceed?')) {
            def spi = selectedProjectitem
            //def lp = ledSvc.getLedgerPayments(selectedProjectitem)
            //def lpi = ledSvc.getLedgerPaymentItems(lp)
            
//            println spi
//            println "================="
//            println lp
//            println "================="
//            println lpi
            
            ledSvc.restructureLedger(spi)
        }
        projectitemHandler.reload();
        }else{
            MsgBox.alert "The ledger you are trying to RESTRUCTURE is already CLOSED. Please try another ledger or contact your collection officer."
        }
        
        
    }
    
    def bill() {
        
//        if (selectedProjectitem.state == 'ACTIVE'){
//            def op = Inv.lookupOpener( "housing_beneficiary_ledger:form:formActions", [caller: selectedProjectitem] );
//            //println selectedProjectitem
//            return op
//        }else if(selectedProjectitem.isfullypaid == true){
//            MsgBox.alert "The ledger you are trying to BILL is already FULLY PAID. Please try another ledger or contact your collection officer."      
//        }else{
//            MsgBox.alert "The ledger you are trying to BILL is already CLOSED. Please try another ledger or contact your collection officer."
//        }
        
        if(selectedProjectitem.state == 'CLOSED' || selectedProjectitem.isfullypaid == true){
            MsgBox.alert "The ledger you are trying to BILL is either CLOSED or FULLY PAID. Please try another ledger or contact your collection officer."
        }else{
            def op = Inv.lookupOpener( "housing_beneficiary_ledger:form:formActions", [caller: selectedProjectitem] );
            //println selectedProjectitem
            return op
        }
        
    }
    
        def fullpayment() {
        
//        if (selectedProjectitem.state == 'ACTIVE'){
//            def op = Inv.lookupOpener( "housing_beneficiary_ledger:form:formActions", [caller: selectedProjectitem] );
//            //println selectedProjectitem
//            return op
//        }else if(selectedProjectitem.isfullypaid == true){
//            MsgBox.alert "The ledger you are trying to BILL is already FULLY PAID. Please try another ledger or contact your collection officer."      
//        }else{
//            MsgBox.alert "The ledger you are trying to BILL is already CLOSED. Please try another ledger or contact your collection officer."
//        }
        
        if(selectedProjectitem.state == 'CLOSED' || selectedProjectitem.isfullypaid == true){
            MsgBox.alert "The ledger you are trying to BILL is either CLOSED or FULLY PAID. Please try another ledger or contact your collection officer."
        }else{
            def op = Inv.lookupOpener( "housing_beneficiary_ledger_fullpayment:form:formActions", [caller: selectedProjectitem] );
            //println selectedProjectitem
            return op
        }
        
    }
    
//    def getFormControls() {
//        def list = [];
//        if( entity.infos ) {
//            entity.infos.each {
//                def m = [ name:it.fieldid, caption:it.caption, type:it.fieldtype ];
//                if( m.type == 'text' ) m.value = it.stringvalue;
//                else if( m.type == 'date' ) m.value =  it.datevalue;
//                else if( m.type == 'decimal' ) m.value = it.decimalvalue;
//                else if( m.type == 'integer' ) m.value = it.intvalue;
//                else m.value = it.info;
//                list << m;
//            }
//            return list;
//        }
//        list.addAll( infoSvc.getInfos( [type:entity.type.objid] ) );
//        list.each {
//            println it;
//        }
//        
//        /*
//        if(!entity.type?.objid) return list;
//        switch( entity.type.objid ) {
//            case 'A':
//                list << [ name:'fld1', caption:'Field 1', type:'text' ];
//                list << [ name:'fld2', caption:'Field 2', type:'date' ];
//                list << [ name:'fld3', caption:'Field 3', type:'decimal' ];
//                //list << [ name:'eng', caption:'Engine', type:'engine' ];
//                break;
//            case 'B':
//                list << [ name:'xfld1', caption:'XField 1', type:'text' ];
//                list << [ name:'xfld2', caption:'XField 2', type:'date' ];
//                list << [ name:'xfld3', caption:'XField 3', type:'decimal' ];
//                break;
//        }
//        */
//        entity.infos = [];
//        list.each {
//            
//            entity.infos << [ fieldid: it.name, caption: it.caption, fieldtype: it.type  ];
//        }
//        return list;
//    }
//    
//    def formControlList = [
//        updateBean: { name,value,item->
//            println "updating " + name + " " + value;
//            def f = entity.infos.find{ it.fieldid == name };
//            if( item.type == 'text' ) f.stringvalue = value;
//            else if( item.type == 'date' ) f.datevalue = value;
//            else if( item.type == 'decimal' ) f.decimalvalue = value;
//            else if( item.type == 'integer' ) f.intvalue = value;
//            else if( item.type == 'lookup' && item.handler == 'revenueitem:lookup' ) {
//                f.lookup = [objid: value.objid, title: value.title ];
//            }
//        },
//        getControlList: {
//            return getFormControls();
//        }
//    ] as FormPanelModel;

    def selectedProjectitem;
    
     def projectitemHandler = [
        fetchList: { o->
            def p = [_schemaname: 'housing_beneficiary_ledger'];
            p.findBy = [ 'parentid': entity.objid];
            p.select = "objid,parentid,projecttype_objid,projecttype_name,state,blockno,duration,houseinterest,lotinterest,lotarea,startdate,houseprincipal,lotprincipal,totalpayable,totalamountpaid,houseamortization,lotamortization,mri,monthlyamortization,lastdatepaid,lastdatecovered,remarks,isoccupant,isfullypaid,ispayrolldeduct,nopenalty,financingtype,accountused";
            if(!entity.projectitems){
                entity.projectitems = queryService.getList( p );
            }
            return entity.projectitems;
        },
        createItem : {
            return[
                objid : 'LED' + new java.rmi.server.UID(),
                newitem : false,
                houseamortization : 0.00,
                houseprincipal: 0.00,
                lotprincipal: 0.00,
                houseinterest: 0,
                lotinterest: 0,
                lotamortization : 0.00,
                mri : 0.00,
                ledgerpayments : [],
            ]
        },
        onColumnUpdate: { o,col-> 
            
            if(col == 'houseprincipal'){
                
                
                if(o.houseinterest == 0){
                    o.houseamortization = o.houseprincipal/(o.duration*12)
//                    def housefactor = ((( (o.houseinterest/100) / 12 ) * (((Math.pow(( (o.houseinterest/100) / 12 )+1,o.duration*12)) / (((Math.pow(( (o.houseinterest/100) / 12 )+1, o.duration *12) -1)))))) * 1000)
//                    o.houseamortization = ((o.houseprincipal/1000)*housefactor)
                }else{
                    //o.houseamortization = o.houseprincipal/(o.duration/12)
                    def housefactor = ((( (o.houseinterest/100) / 12 ) * (((Math.pow(( (o.houseinterest/100) / 12 )+1,o.duration*12)) / (((Math.pow(( (o.houseinterest/100) / 12 )+1, o.duration *12) -1)))))) * 1000)
                    o.houseamortization = ((o.houseprincipal/1000)*housefactor)
                }
                
            }
            
            if(col == 'lotprincipal'){
                
                if(o.lotinterest == 0){
                    o.lotamortization = o.lotprincipal/(o.duration*12)
//                    def lotfactor = ((( (o.lotinterest/100) / 12 ) * (((Math.pow(( (o.lotinterest/100) / 12 )+1,o.duration*12)) / (((Math.pow(( (o.lotinterest/100) / 12 )+1, o.duration *12) -1)))))) * 1000)
//                    o.lotamortization = (o.lotprincipal/1000)*lotfactor
                }else{
                    //o.lotamortization = o.lotprincipal/(o.duration/12)
                    def lotfactor = ((( (o.lotinterest/100) / 12 ) * (((Math.pow(( (o.lotinterest/100) / 12 )+1,o.duration*12)) / (((Math.pow(( (o.lotinterest/100) / 12 )+1, o.duration *12) -1)))))) * 1000)
                    o.lotamortization = (o.lotprincipal/1000)*lotfactor
                } 
                
            }
            
             
            
//            if(col == 'mri'){
//                if(o.principal){
//                    o.mri = ((o.principal/1000)*0.76);
//                }else{
//                    o.mri = 0.00
//                }
//            }
            
//            if(col == 'houseamortization'){
//                if(o.houseinterest){
//                    def housefactor = ((( o.houseinterest / 12 ) * (((pow(( o.houseinterest / 12 )+1,o.duration*12)) / (((pow(( o.houseinterest / 12 )+1, o.duration *12) -1)))))) * 1000)
//                    o.houseamortization = ((principal/1000)*housefactor)
//                }else{
//                    o.houseamortization = 0.00
//                }
//            }
            
//            if(col == 'lotamortization'){
//                if(lotinterest){
//                    def lotfactor = ((( o.lotinterest / 12 ) * (((pow(( o.lotinterest / 12 )+1,o.duration*12)) / (((pow(( o.lotinterest / 12 )+1, o.duration *12) -1)))))) * 1000)
//                    o.lotamortization = (principal/1000)*factor
//                }else{
//                    o.lotamortization = 0.00
//                }
//            }
        
            
//            if(col == 'totalpayable'){
//                o.totalpayable = (o.houseamortization * 12 * o.duration) + (o.lotamortization * 12 * o.duration)
//            }
            
//            if(col == 'monthlyamortization'){
//                o.monthlyamortization = o.houseamortization + o.lotamortization + o.mri
//            }
            
            
            
        },
        
        onCommitItem:{ o ->
              if(o.hasmri == 'YES'){
                  if(o.lotprincipal || o.houseprincipal){
                    o.mri = ((o.lotprincipal/1000)*0.76) + ((o.houseprincipal/1000)*0.76);
                }else{
                    o.mri = 0.00
                }
              }else{
                  o.mri = 0.00
              }  
              
              o.totalpayable = (o.houseamortization * 12 * o.duration) + (o.lotamortization * 12 * o.duration)
              o.monthlyamortization = o.houseamortization + o.lotamortization + o.mri

        },
        
        onAddItem : {
            entity.projectitems << it;
        },
        onRemoveItem : {
            if (MsgBox.confirm('Delete item?')) {
                //service.deleteFarmerItems(it)
                entity.projectitems.remove(it)
                //persistenceSvc.removeEntity([_schemaname:'hrmis_appointmentjoborderitems',objid:it.objid])
                projectitemHandler.reload();
                return true;
            }
            return false;
        },
        
        validate:{li->
            def item=li.item;
            //checkDuplicateIPCR(selectedDPCR.ipcrlist,item);
        }
        
            
    ] as EditorListModel;

    
    
    def getLookupProjecttype(){
       return Inv.lookupOpener('projecttype:lookup',[
               onselect :{
                   selectedProjectitem.projecttype = it
//                   entity.specie.objid = it.objid;
//                   entity.specie.code = it.code;
//                   entity.specie.name = it.name;
                   binding.refresh(); 
               },
           ])
   }
   
    
    void approve() { 
        if ( MsgBox.confirm('You are about to approve this information. Proceed?')) { 
            getPersistenceService().update([ 
               _schemaname: 'housing_beneficiary', 
               objid : entity.objid, 
               state : 'APPROVED' 
            ]); 
            loadData(); 
        }
    }
   
    

    
}