package it.rosariocirrito.msOrganiko.services

import it.rosariocirrito.msOrganiko.domain.OpPersonaFisica
import it.rosariocirrito.msOrganiko.domain.OpPersonaGiuridica
import it.rosariocirrito.msOrganiko.domain.PersonaFisicaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.support.MutableSortDefinition
import org.springframework.beans.support.PropertyComparator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface PersonaFisicaQryService {
    fun findById(id: Int): OpPersonaFisica? // 1
    fun findDipendentiStrictByStrutturaIDAndAnno(struId: Int, anno: Int): List<OpPersonaFisica> //2
    fun findDipendentiGlobalByStrutturaIDAndAnno(struId: Int, anno: Int): List<OpPersonaFisica> //3
    fun findDipendentiByDipartimentoIDAndAnno(struId: Int, anno: Int): List<OpPersonaFisica> //4
}

@Service("personaFisicaQryService")
class PersonaFisicaQryServiceImpl : PersonaFisicaQryService{

    @Autowired
    private lateinit var repo: PersonaFisicaRepository
    @Autowired
    private lateinit var pgServizi: PersonaGiuridicaQryService

    //1
    @Transactional(readOnly = true)
    override fun findById(id: Int): OpPersonaFisica? {
        return repo.findByIdOrNull(id)
    }

    //2
    @Transactional(readOnly = true)
    override fun findDipendentiStrictByStrutturaIDAndAnno(struId: Int, anno: Int): List<OpPersonaFisica> {//2
        var pg : OpPersonaGiuridica = pgServizi .findById(struId)!!
        var lista : MutableList<OpPersonaFisica> = ArrayList<OpPersonaFisica>()
        lista.addAll(pg.dipendenti.toList())
        //
        for (aff in pg.opPersonaAfferenzas) {
            //println("aff"+aff.opPersonaFisica.toString()+"inizio: "+aff.annoinz+"fine: "+aff.annofin)
            val dipRipescato = aff.opPersonaFisica!!
            if(!lista.contains(dipRipescato) && anno>=aff.annoinz && anno<=aff.annofin) lista.add(dipRipescato)
        }
        PropertyComparator.sort(lista, MutableSortDefinition("order",true,true));
        return lista
    }

    //3
    @Transactional(readOnly = true)
    override fun findDipendentiGlobalByStrutturaIDAndAnno(struId: Int, anno: Int): List<OpPersonaFisica> {//3
        var pg : OpPersonaGiuridica = pgServizi .findById(struId)!!
        var lista : MutableList<OpPersonaFisica> = ArrayList<OpPersonaFisica>(this.findDipendentiStrictByStrutturaIDAndAnno(struId,anno))
        var listaTot = pg.dipendenti.toList()
        for (stru in pg.opPersonaGiuridicas) {
            var struDipendenti = this.findDipendentiStrictByStrutturaIDAndAnno(stru.idpersona!!,anno)
            if(!struDipendenti.isEmpty()) {
                for (dip2 in struDipendenti) {
                    if(!lista.contains(dip2)) lista.add(dip2)
                }
            }
            var lstSubStr : MutableList<OpPersonaGiuridica> = ArrayList<OpPersonaGiuridica>(stru.opPersonaGiuridicas)
            if(!lstSubStr.isNullOrEmpty()) {
                for (subStru in lstSubStr) {
                    var lstDipSub = this.findDipendentiStrictByStrutturaIDAndAnno(subStru.idpersona!!,anno)
                    if(!lstDipSub.isNullOrEmpty()) {
                        for (dip3 in lstDipSub) {
                            if(!lista.contains(dip3)) lista.add(dip3)
                        }
                    }
                }
            }

        }
        PropertyComparator.sort(lista, MutableSortDefinition("order",true,true));
        return lista
    }

    // 4
    @Transactional(readOnly = true)
    override fun findDipendentiByDipartimentoIDAndAnno(deptId: Int, anno: Int): List<OpPersonaFisica> {//4
        var lista : MutableList<OpPersonaFisica> = ArrayList<OpPersonaFisica>()
        var dept: OpPersonaGiuridica = pgServizi .findById(deptId)!!
        for (stru in dept.opPersonaGiuridicas) {
            var listaDipStru : MutableList<OpPersonaFisica> = ArrayList<OpPersonaFisica>(this.findDipendentiGlobalByStrutturaIDAndAnno(stru.idpersona!!,anno))
            if(!listaDipStru.isNullOrEmpty()) {
                for (dip in listaDipStru) {
                    if(!lista.contains(dip)) lista.add(dip)
                }
            }
        }
        PropertyComparator.sort(lista, MutableSortDefinition("order",true,true));
        return lista
    }

} // ----------------------------------