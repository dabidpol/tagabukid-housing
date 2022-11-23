package tagabukid.housing.models;

import com.rameses.rcp.annotations.*;
import com.rameses.rcp.common.*;
import com.rameses.seti2.models.*;
import com.rameses.osiris2.client.*
import com.rameses.osiris2.common.*;
import com.rameses.util.*;

class HousingBeneficiaryModel extends CrudFormModel{

    @Service("HousingInfoService")
    def infoSvc;
    
    def ledgerEntryHandler = [
        fetchList: { o->
            def p = [_schemaname: 'housing_ledger_payment_item'];
            p.findBy = [ 'parent.ledgerid': entity.objid ];
            p.select = "objid,parent.receiptno,parent.receiptdate,year,month,amount,penalty";
            return queryService.getList( p );
        }
    ] as BasicListModel;

    def capturePayment() {
        return Inv.lookupOpener("housing_ledger_capture_payment", [parent: entity ] );
    }

    void refreshItem() {
        ledgerEntryHandler.reload();
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

    def getLookupProjecttype(){
       return Inv.lookupOpener('projecttype:lookup',[
               onselect :{
                    entity.projecttype = it
//                   entity.specie.objid = it.objid;
//                   entity.specie.code = it.code;
//                   entity.specie.name = it.name;
                   binding.refresh(); 
               },
           ])
   }

    
}