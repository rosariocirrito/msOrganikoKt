package it.rosariocirrito.msOrganiko.services

import it.rosariocirrito.msOrganiko.domain.Incarico
import it.rosariocirrito.msOrganiko.domain.IncaricoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.support.MutableSortDefinition
import org.springframework.beans.support.PropertyComparator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface IncaricoService {
    fun findById(id:Int) : Incarico? //1
    fun findIncarichiApicaliByAssessoratoIDAndAnno(assrId: Int, anno: Int): List<Incarico> //2
    fun findIncarichiApicaliByDipartimentoIDAndAnno(assrId: Int, anno: Int): List<Incarico> //3
    fun findIncarichiByDipartimentoIDAndAnno(deptId: Int, anno: Int): List<Incarico> //4a
    fun findIncarichiDirigenzialiByDipartimentoIDAndAnno(deptId: Int, anno: Int): List<Incarico> //4a
    fun findIncarichiPopByDipartimentoIDAndAnno(deptId: Int, anno: Int): List<Incarico> //4b
    fun findIncarichiPopByIntermediaIDAndAnno(struId: Int, anno: Int): List<Incarico> //4b
    fun findIncarichiByStrutturaIDAndAnno(struPadreId: Int, anno: Int): List<Incarico> //5
    fun findIncarichiByDirigenteIDAndAnno(pfId: Int, anno: Int): List<Incarico> //7
}

@Service("incaricoService")
class IncaricoServiceImpl : IncaricoService{

    @Autowired
    private lateinit var repo: IncaricoRepository
    @Autowired
    private lateinit var pgServizi: PersonaGiuridicaQryService
    @Autowired
    private lateinit var pfServizi: PersonaFisicaQryService

    @Transactional(readOnly = true)
    override fun findById(id:Int) : Incarico? {
        var inc = repo.findByIdOrNull(id)
        return inc
    }



    @Transactional(readOnly = true)
    override fun findIncarichiApicaliByAssessoratoIDAndAnno(assrId: Int, anno: Int): List<Incarico> {//2
        var lista : MutableList<Incarico> = ArrayList<Incarico>()
        val assr = pgServizi.findById(assrId)
        val listaDipartimenti = assr!!.getStruttureFiglieAttiveOCancellateAnno(anno)
        if(!listaDipartimenti.isNullOrEmpty()) {
            for (dept in listaDipartimenti) {
            val listaIncDept : List<Incarico> = repo.findAllByPersonaGiuridica(dept)
                if(!listaIncDept.isNullOrEmpty()) {
                    for (inc in listaIncDept) {
                        if(anno >= inc.annoinz && anno <= inc.annofin){
                            if(!lista.contains(inc) && inc.incaricoDipartimentale) lista.add(inc)
                        }
                    }
                }
            }
        }
        PropertyComparator.sort(lista, MutableSortDefinition("order",true,true));
        return lista
    }

    @Transactional(readOnly = true)
    override fun findIncarichiApicaliByDipartimentoIDAndAnno(deptId: Int, anno: Int): List<Incarico> {//3
        var lista : MutableList<Incarico> = ArrayList<Incarico>()
        val listaIncDept : List<Incarico> = this.findIncarichiByStrutturaIDAndAnno(deptId,anno)
        if(!listaIncDept.isNullOrEmpty()) {
            for (inc in listaIncDept) {
                if(anno >= inc.annoinz && anno <= inc.annofin){
                    if(!lista.contains(inc) && inc.incaricoDipartimentale) lista.add(inc)
                }
            }
        }
        PropertyComparator.sort(lista, MutableSortDefinition("order",true,true));
        return lista
    }

    @Transactional(readOnly = true)
    override fun findIncarichiByDipartimentoIDAndAnno(deptId: Int, anno: Int): List<Incarico> {//4a
        var lista : MutableList<Incarico> = ArrayList<Incarico>()
        val dept = pgServizi.findById(deptId)
        val listaStrutture = dept!!.getStruttureDiscendentiAttiveOCancellateAnno(anno)
        if(!listaStrutture.isNullOrEmpty()) {
            for (stru in listaStrutture) {
                val listaIncDept : List<Incarico> = this.findIncarichiByStrutturaIDAndAnno(stru.idpersona!!,anno)
                if(!listaIncDept.isNullOrEmpty()) {
                    for (inc in listaIncDept) {
                        if(anno >= inc.annoinz && anno <= inc.annofin){
                            if(!lista.contains(inc)) lista.add(inc)
                        }
                    }
                }
            }
        }
        else { // aggiunta per uffici senza strutture ex Uff Cerimoniale
            val listaIncApicDept: List<Incarico> = this.findIncarichiByStrutturaIDAndAnno(deptId, anno)
            if (!listaIncApicDept.isNullOrEmpty()) {
                for (inc in listaIncApicDept) {
                    if (anno >= inc.annoinz && anno <= inc.annofin) {
                        if (!lista.contains(inc) && inc.incaricoDipartimentale) lista.add(inc)
                    }
                }
            }
        }
        PropertyComparator.sort(lista, MutableSortDefinition("order",true,true));
        return lista
    }

    @Transactional(readOnly = true)
    override fun findIncarichiDirigenzialiByDipartimentoIDAndAnno(deptId: Int, anno: Int): List<Incarico> {//4a
        var lista : MutableList<Incarico> = ArrayList<Incarico>()
        val dept = pgServizi.findById(deptId)
        val listaStrutture = dept!!.getStruttureDiscendentiAttiveOCancellateAnno(anno)
        if(!listaStrutture.isNullOrEmpty()) {
            for (stru in listaStrutture) {
                val listaIncDept : List<Incarico> = this.findIncarichiByStrutturaIDAndAnno(stru.idpersona!!,anno)
                if(!listaIncDept.isNullOrEmpty()) {
                    for (inc in listaIncDept) {
                        if(anno >= inc.annoinz && anno <= inc.annofin){
                            if(!lista.contains(inc) && inc.incaricoDirigenziale) lista.add(inc)
                        }
                    }
                }
            }
        }
        PropertyComparator.sort(lista, MutableSortDefinition("order",true,true));
        return lista
    }

    @Transactional(readOnly = true)
    override fun findIncarichiPopByDipartimentoIDAndAnno(deptId: Int, anno: Int): List<Incarico> {//4b
        var lista : MutableList<Incarico> = ArrayList<Incarico>()
        val dept = pgServizi.findById(deptId)
        val listaStrutture = dept!!.getStruttureDiscendentiAttiveOCancellateAnno(anno)
        if(!listaStrutture.isNullOrEmpty()) {
            for (stru in listaStrutture) {
                if(stru.isPop()) {
                    val listaIncStru = this.findIncarichiByStrutturaIDAndAnno(stru.idpersona!!,anno)
                    if(!listaIncStru.isNullOrEmpty()) lista.addAll(listaIncStru)
                }
            }
        }
        PropertyComparator.sort(lista, MutableSortDefinition("order",true,true));
        return lista
    }

    @Transactional(readOnly = true)
    override fun findIncarichiPopByIntermediaIDAndAnno(struId: Int, anno: Int): List<Incarico> {//4b
        var lista : MutableList<Incarico> = ArrayList<Incarico>()
        val struInt = pgServizi.findById(struId)
        val listaStrutture = struInt!!.getStruttureDiscendentiAttiveOCancellateAnno(anno)
        if(!listaStrutture.isNullOrEmpty()) {
            for (stru in listaStrutture) {
                if(stru.isPop()) {
                    val listaIncStru = this.findIncarichiByStrutturaIDAndAnno(stru.idpersona!!,anno)
                    if(!listaIncStru.isNullOrEmpty()) lista.addAll(listaIncStru)
                }
            }
        }
        PropertyComparator.sort(lista, MutableSortDefinition("order",true,true));
        return lista
    }

    @Transactional(readOnly = true)
    override fun findIncarichiByStrutturaIDAndAnno(struId: Int, anno: Int): List<Incarico> {//5
        var lista : MutableList<Incarico> = ArrayList<Incarico>()
        val stru = pgServizi.findById(struId)
            val listaInc: List<Incarico> = repo.findAllByPersonaGiuridica(stru!!)
            if(!listaInc.isNullOrEmpty()) {
                for (inc in listaInc) {
                    val pf = pfServizi.findById(inc.idPF!!)
                    if(anno >= inc.annoinz && anno <= inc.annofin) lista.add(inc)
            }
        }
        PropertyComparator.sort(lista, MutableSortDefinition("order",true,true));
        return lista
    }


    @Transactional(readOnly = true)
    override fun findIncarichiByDirigenteIDAndAnno(pfId: Int, anno: Int): List<Incarico> {//5
        var lista : MutableList<Incarico> = ArrayList<Incarico>()
        val dirig = pfServizi.findById(pfId)
        val listaInc : List<Incarico> = repo.findAllByPersonaFisica(dirig!!)
        if(!listaInc.isNullOrEmpty()) {
            for (inc in listaInc) {
                if(anno >= inc.annoinz && anno <= inc.annofin ) lista.add(inc)
            }
        }
        PropertyComparator.sort(lista, MutableSortDefinition("order",true,true));
        return lista
    }

} // -------------------------------
