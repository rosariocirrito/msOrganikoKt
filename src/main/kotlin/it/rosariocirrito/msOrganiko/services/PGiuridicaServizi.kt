package it.rosariocirrito.msOrganiko.services

import it.rosariocirrito.msOrganiko.domain.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.support.MutableSortDefinition
import org.springframework.beans.support.PropertyComparator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


// .1 ******************* interfaccia dei servizi *********************
interface PersonaGiuridicaQryService {
fun findById(id: Int): OpPersonaGiuridica? // 1
fun findDipartimentoByPersonaFisicaId(pfID: Int): OpPersonaGiuridica? // 3
fun findAssessoratoByPersonaFisicaId(pfID: Int): OpPersonaGiuridica? // 4
fun listAssessoratiAndAnno(anno: Int): List<OpPersonaGiuridica> //6
/*


//-----------------------------------


fun findStruttureByStrutturaPadreIDAndAnno(padre: PersonaGiuridica?, anno: Int): List<PersonaGiuridica?>?


		*/
}  // fine 1. ------------------------------------------------

// .2 ******************* implementazione dei servizi *********************
@Service("personaGiuridicaQryService")
class PersonaGiuridicaQryServiceImpl : PersonaGiuridicaQryService{ // fine 2. ------------------------------------------------

    @Autowired
    private lateinit var repo: PersonaGiuridicaRepository
    @Autowired
    private lateinit var pfServizi: PersonaFisicaQryService
    @Autowired
    private lateinit var pgTipoRepo: PersonaGiuridicaTipoRepository

    // metodo 1
    @Transactional(readOnly = true)
    override fun findById(id: Int): OpPersonaGiuridica? {
        return repo.findByIdOrNull(id)
    }



    // metodo 3
    @Transactional(readOnly = true)
    override fun findDipartimentoByPersonaFisicaId(pfID: Int): OpPersonaGiuridica?{
        val pf = pfServizi.findById(pfID)
        val pg = pf!!.opPersonaGiuridica
        val dept = pg!!.getDipartimento()
        return dept
    }

    // metodo 4
    @Transactional(readOnly = true)
    override fun findAssessoratoByPersonaFisicaId(pfID: Int): OpPersonaGiuridica?{
        val pf = pfServizi.findById(pfID)
        val dept = findDipartimentoByPersonaFisicaId(pf!!.idpersona!!)
        val assr = findById(dept!!.opPersonaGiuridica!!.idpersona!!)
        return assr
    }



    // metodo 6
    @Transactional(readOnly = true)
    override fun listAssessoratiAndAnno(anno: Int): List<OpPersonaGiuridica>{
        var lista : MutableList<OpPersonaGiuridica> = ArrayList<OpPersonaGiuridica>()
        val tipoAss  = pgTipoRepo.findById(7)
        val listaTot: List<OpPersonaGiuridica> = repo.findAllByOpPersonaGiuridicaTipo(tipoAss)
        if(!listaTot.isEmpty()){
            for (assr in listaTot) {
                if(!assr.cancellata || assr.annoCancellazione>=anno ) lista.add(assr)
            }
        }
        return lista
    }






}