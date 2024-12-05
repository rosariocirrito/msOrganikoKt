package it.rosariocirrito.msOrganiko.api

import it.rosariocirrito.msOrganiko.api.pl.IncaricoDTO
import it.rosariocirrito.msOrganiko.api.pl.PersonaFisicaDTO
import it.rosariocirrito.msOrganiko.api.pl.PersonaGiuridicaDTO
import it.rosariocirrito.msOrganiko.domain.Incarico
import it.rosariocirrito.msOrganiko.domain.OpPersonaFisica
import it.rosariocirrito.msOrganiko.domain.OpPersonaGiuridica
import it.rosariocirrito.msOrganiko.services.IncaricoService
import it.rosariocirrito.msOrganiko.services.PersonaFisicaQryService
import it.rosariocirrito.msOrganiko.services.PersonaGiuridicaQryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import java.io.FileNotFoundException
import java.io.IOException

@RestController
@RequestMapping(value = ["/api"])
class RestController {

    @Autowired
    lateinit var incServizi: IncaricoService
    @Autowired
    lateinit var pfServizi: PersonaFisicaQryService
    @Autowired
    lateinit var pgServizi: PersonaGiuridicaQryService


    @GetMapping(value = ["/findPersonaGiuridicaById/{id}"])
    fun findPersonaGiuridicaById(@PathVariable id: Int): ResponseEntity<out Any> {
        //println("/findPersonaGiuridicaById/"+id)
        val pg = pgServizi.findById(id)
        return if (pg != null) ResponseEntity(PersonaGiuridicaDTO(pg!!) ,OK)
        else ResponseEntity(ErrorResponse("Struttura non trovata","struttura con id '$id'non trovata") , NOT_FOUND)
    }

    @GetMapping(value = ["/findDipartimentoByPersonaGiuridicaId/{id}"])
    fun findDipartimentoByPersonaGiuridicaID(@PathVariable id: Int) : ResponseEntity<out Any> {
        //println("/findDipartimentoByPersonaGiuridicaId/"+id)
        val stru = pgServizi.findById(id)
        val dept = stru!!.getDipartimento()
        return if (dept != null) ResponseEntity(PersonaGiuridicaDTO(dept!!) ,OK)
        else ResponseEntity(ErrorResponse("Dipartimento non trovato","Dipartimento della struttura con id '$id'non trovata") , NOT_FOUND)
    }

    @GetMapping(value = ["/findDipartimentoByPersonaFisicaId/{id}"])
    fun findDipartimentoByPersonaFisicaId(@PathVariable id: Int) : ResponseEntity<out Any> {
        println("/findDipartimentoByPersonaFisicaId/"+id)
        val dept = pgServizi.findDipartimentoByPersonaFisicaId(id)
        return if (dept != null) ResponseEntity(PersonaGiuridicaDTO(dept!!) ,OK)
        else ResponseEntity(ErrorResponse("Dipartimento non trovato","Dipartimento della persona con id '$id'non trovata") , NOT_FOUND)
    }


    @GetMapping(value = ["/findAssessoratoByPersonaFisicaId/{id}"])
    fun findAssessoratoByPersonaFisicaId(@PathVariable id: Int) : ResponseEntity<out Any> {
        println("/findAssessoratoByPersonaFisicaId/"+id)
        val assr = pgServizi.findAssessoratoByPersonaFisicaId(id)
        return if (assr != null) ResponseEntity(PersonaGiuridicaDTO(assr!!) ,OK)
        else ResponseEntity(ErrorResponse("Assessorato non trovato","Dipartimento della persona con id '$id'non trovata") , NOT_FOUND)
    }

    @GetMapping(value = ["/listSubStruttureByDipartimentoIDAndAnno/{padreID}/{anno}"])
    fun listSubStruttureByDipartimentoIDAndAnno(
        @PathVariable(value = "padreID") padreID: Int,
        @PathVariable(value = "anno") anno: Int
        ): ResponseEntity<MutableList<out Any>> {
        println("/listSubStruttureByDipartimentoIDAndAnno/"+padreID+"/"+anno)
        var listaReq: MutableList<PersonaGiuridicaDTO> = ArrayList<PersonaGiuridicaDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessuna sottostruttura per padre con id '$padreID' e anno '$anno'") )
        val padre = pgServizi.findById(padreID)
        val lista: List<OpPersonaGiuridica> = padre!!.getStruttureDiscendentiAttiveOCancellateAnno(anno)
        for (pg: OpPersonaGiuridica in lista) {
            val pgDTO : PersonaGiuridicaDTO =  PersonaGiuridicaDTO(pg)
            listaReq.add(pgDTO)
        }
        //return if (listaReq.isEmpty()) throw FileNotFoundException("/listSubStruttureByDipartimentoIDAndAnno/$padreID/$anno") else listaReq
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    @GetMapping(value = ["/listAssessoratiAndAnno/{anno}"])
    fun listAssessoratiAndAnno(@PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        println("/listAssessoratiAndAnno/"+anno)
        var listaReq: MutableList<PersonaGiuridicaDTO> = ArrayList<PersonaGiuridicaDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessun assessorato per anno '$anno'") )
        val lista: List<OpPersonaGiuridica> = pgServizi.listAssessoratiAndAnno(anno)
        for (pg: OpPersonaGiuridica in lista) {
            val pgDTO : PersonaGiuridicaDTO =  PersonaGiuridicaDTO(pg)
            listaReq.add(pgDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    @GetMapping(value = ["/listDipartimentiByAssessoratoIDAndAnno/{id}/{anno}"])
    fun listDipartimentiByAssessoratoIDAndAnno(@PathVariable(value = "id") id: Int,
                        @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        println("/listDipartimentiByAssessoratoIDAndAnno/"+id+"/"+anno)
        var listaReq: MutableList<PersonaGiuridicaDTO> = ArrayList<PersonaGiuridicaDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessun dipartimento per assessorato con id '$id' nell'anno '$anno'") )
        val padre = pgServizi.findById(id)
        //val lista: List<OpPersonaGiuridica> = pgServizi.listDipartimentiByAssessoratoIDAndAnno(id,anno)
        val lista: List<OpPersonaGiuridica> = padre!!.getStruttureFiglieAttiveOCancellateAnno(anno)
        for (pg: OpPersonaGiuridica in lista) {
            val pgDTO : PersonaGiuridicaDTO =  PersonaGiuridicaDTO(pg)
            listaReq.add(pgDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    @GetMapping(value = ["/findStruttureByStrutturaPadreIDAndAnno/{id}/{anno}"])
    fun findStruttureByStrutturaPadreIDAndAnno(@PathVariable(value = "id") id: Int,
                                               @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        // println("/findStruttureByStrutturaPadreIDAndAnno/"+id+"/"+anno)
        var listaReq: MutableList<PersonaGiuridicaDTO> = ArrayList<PersonaGiuridicaDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessuna sottostruttura per padre con id '$id' e anno '$anno'") )
        val padre = pgServizi.findById(id)
        //val lista: List<OpPersonaGiuridica> = pgServizi.findStruttureByStrutturaPadreIDAndAnno(id,anno)
        val lista: List<OpPersonaGiuridica> = padre!!.getStruttureFiglieAttiveOCancellateAnno(anno)
        for (pg: OpPersonaGiuridica in lista) {
            val pgDTO : PersonaGiuridicaDTO =  PersonaGiuridicaDTO(pg)
            listaReq.add(pgDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    @GetMapping(value = ["/getStruttureFiglieAttiveOCancellateAnno/{id}/{anno}"])
    fun getStruttureFiglieAttiveOCancellateAnno(@PathVariable(value = "id") id: Int,
                                @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        println("/getStruttureFiglieAttiveOCancellateAnno/"+id+"/"+anno)
        var listaReq: MutableList<PersonaGiuridicaDTO> = ArrayList<PersonaGiuridicaDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessuna sottostruttura per padre con id '$id' e anno '$anno'") )
        val padre = pgServizi.findById(id)
        val lista: List<OpPersonaGiuridica> = padre!!.getStruttureFiglieAttiveOCancellateAnno(anno)
        for (pg: OpPersonaGiuridica in lista) {
            val pgDTO : PersonaGiuridicaDTO =  PersonaGiuridicaDTO(pg)
            listaReq.add(pgDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    @GetMapping(value = ["/getStruttureDiscendentiAttiveOCancellateAnno/{id}/{anno}"])
    fun getStruttureDiscendentiAttiveOCancellateAnno(@PathVariable(value = "id") id: Int,
                                               @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        println("/getStruttureDiscendentiAttiveOCancellateAnno/"+id+"/"+anno)
        var listaReq: MutableList<PersonaGiuridicaDTO> = ArrayList<PersonaGiuridicaDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessuna sottostruttura per padre con id '$id' e anno '$anno'") )
        val padre = pgServizi.findById(id)
        val lista: List<OpPersonaGiuridica> = padre!!.getStruttureDiscendentiAttiveOCancellateAnno(anno)
        for (pg: OpPersonaGiuridica in lista) {
            val pgDTO : PersonaGiuridicaDTO =  PersonaGiuridicaDTO(pg)
            listaReq.add(pgDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    // ----------------------------------------------------------------------------------------
    // 1
    @GetMapping(value = ["/findPersonaFisicaById/{id}"])
    fun getPersonaFisicaById(@PathVariable id: Int) : ResponseEntity<out Any> {
        // println("/findPersonaFisicaById/"+id)
        val pf: OpPersonaFisica? = pfServizi.findById(id)
        return if (pf != null) ResponseEntity(PersonaFisicaDTO(pf!!) ,OK)
        else ResponseEntity(ErrorResponse("Persona fisica non trovata","Persona fisica con id '$id'non trovata") , NOT_FOUND)
    }

    // 2 findDipendentiStrictByStrutturaIDAndAnno
    @GetMapping(value = ["/findDipendentiStrictByStrutturaIDAndAnno/{id}/{anno}"])
    fun findDipendentiStrictByStrutturaIDAndAnno(@PathVariable(value = "id") id: Int,
                                               @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        // println("/findDipendentiStrictByStrutturaIDAndAnno/"+id+"/"+anno)
        var listaReq: MutableList<PersonaFisicaDTO> = ArrayList<PersonaFisicaDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessun Dipendente per struttura con id '$id' e anno '$anno'") )
        val lista: List<OpPersonaFisica> = pfServizi.findDipendentiStrictByStrutturaIDAndAnno(id,anno)
        for (pf: OpPersonaFisica in lista) {
            val pfDTO : PersonaFisicaDTO =  PersonaFisicaDTO(pf)
            listaReq.add(pfDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    // 3
    @GetMapping(value = ["/findDipendentiGlobalByStrutturaIDAndAnno/{id}/{anno}"])
    fun findDipendentiGlobalByStrutturaIDAndAnno(@PathVariable(value = "id") id: Int,
                                                 @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        println("/findDipendentiGlobalByStrutturaIDAndAnno/"+id+"/"+anno)
        var listaReq: MutableList<PersonaFisicaDTO> = ArrayList<PersonaFisicaDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessun Dipendente per struttura con id '$id' e anno '$anno'") )
        val lista: List<OpPersonaFisica> = pfServizi.findDipendentiGlobalByStrutturaIDAndAnno(id,anno)
        for (pf: OpPersonaFisica in lista) {
            val pfDTO : PersonaFisicaDTO =  PersonaFisicaDTO(pf)
            listaReq.add(pfDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    // 4
    @GetMapping(value = ["/findDipendentiByDipartimentoIDAndAnno/{id}/{anno}"])
    fun findDipendentiByDipartimentoIDAndAnno(@PathVariable(value = "id") id: Int,
                                                 @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        //println("/findDipendentiByDipartimentoIDAndAnno/"+id+"/"+anno)
        var listaReq: MutableList<PersonaFisicaDTO> = ArrayList<PersonaFisicaDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessun Dipendente per dipartimento con id '$id' e anno '$anno'") )
        val lista: List<OpPersonaFisica> = pfServizi.findDipendentiByDipartimentoIDAndAnno(id,anno)
        for (pf: OpPersonaFisica in lista) {
            val pfDTO : PersonaFisicaDTO =  PersonaFisicaDTO(pf)
            listaReq.add(pfDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    // ----------------------------------------------------------------------------------------
    // ********************* incarico **************************
    // 1
    @GetMapping(value = ["/findIncaricoById/{id}"])
    fun getIncaricoById(@PathVariable id: Int) : ResponseEntity<out Any> {
        //val incarico = IncaricoDTO(id,531,56)
        println("/findIncaricoById/"+id)
        val inc = incServizi.findById(id)
        return if (inc != null) ResponseEntity(IncaricoDTO(inc!!) ,OK)
        else ResponseEntity(ErrorResponse("Incarico non trovato","Incarico con id '$id'non trovato") , NOT_FOUND)
    }

    //2 findIncarichiApicaliByAssessoratoIDAndAnno
    @GetMapping(value = ["/findIncarichiApicaliByAssessoratoIDAndAnno/{id}/{anno}"])
    fun findIncarichiApicaliByAssessoratoIDAndAnno(@PathVariable(value = "id") id: Int,
                                              @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        println("/findIncarichiApicaliByAssessoratoIDAndAnno/$id/$anno")
        var listaReq: MutableList<IncaricoDTO> = ArrayList<IncaricoDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessun incarico apicale per assessorato con id '$id' e anno '$anno'") )
        val lista: List<Incarico> = incServizi.findIncarichiApicaliByAssessoratoIDAndAnno(id,anno)
        for (inc in lista) {
            val incDTO : IncaricoDTO =  IncaricoDTO(inc)
            listaReq.add(incDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    //3 findIncarichiApicaliByDipartimentoIDAndAnno
    @GetMapping(value = ["/findIncarichiApicaliByDipartimentoIDAndAnno/{id}/{anno}"])
    fun findIncarichiApicaliByDipartimentoIDAndAnno(@PathVariable(value = "id") id: Int,
                                                   @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        println("/findIncarichiApicaliByDipartimentoIDAndAnno/$id/$anno")
        var listaReq: MutableList<IncaricoDTO> = ArrayList<IncaricoDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessun incarico apicale per dipartimento con id '$id' e anno '$anno'") )
        val lista: List<Incarico> = incServizi.findIncarichiApicaliByDipartimentoIDAndAnno(id,anno)
        for (inc in lista) {
            val incDTO : IncaricoDTO =  IncaricoDTO(inc)
            listaReq.add(incDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    // 4a
    @GetMapping(value = ["/findIncarichiByDipartimentoIDAndAnno/{id}/{anno}"])
    fun findIncarichiByDipartimentoIDAndAnno(@PathVariable(value = "id") id: Int,
                                             @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        // println("/findIncarichiByDipartimentoIDAndAnno/"+id+"/"+anno)
        var listaReq: MutableList<IncaricoDTO> = ArrayList<IncaricoDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessun incarico per dipartimento con id '$id' e anno '$anno'") )
        val lista: List<Incarico> = incServizi.findIncarichiByDipartimentoIDAndAnno(id,anno)
        for (inc in lista) {
            val incDTO : IncaricoDTO =  IncaricoDTO(inc)
            listaReq.add(incDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    @GetMapping(value = ["/findIncarichiDirigenzialiByDipartimentoIDAndAnno/{id}/{anno}"])
    fun findIncarichiDirigenzialiByDipartimentoIDAndAnno(@PathVariable(value = "id") id: Int,
                                             @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        // println("/findIncarichiByDipartimentoIDAndAnno/"+id+"/"+anno)
        var listaReq: MutableList<IncaricoDTO> = ArrayList<IncaricoDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessun incarico dirigenziale per dipartimento con id '$id' e anno '$anno'") )
        val lista: List<Incarico> = incServizi.findIncarichiDirigenzialiByDipartimentoIDAndAnno(id,anno)
        for (inc in lista) {
            val incDTO : IncaricoDTO =  IncaricoDTO(inc)
            listaReq.add(incDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    // 4b
    @GetMapping(value = ["/findIncarichiPopByDipartimentoIDAndAnno/{id}/{anno}"])
    fun findIncarichiPopByDipartimentoIDAndAnno(@PathVariable(value = "id") id: Int,
                                             @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        // println("/findIncarichiPopByDipartimentoIDAndAnno/"+id+"/"+anno)
        var listaReq: MutableList<IncaricoDTO> = ArrayList<IncaricoDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessun incarico pop per dipartimento con id '$id' e anno '$anno'") )
        val lista: List<Incarico> = incServizi.findIncarichiPopByDipartimentoIDAndAnno(id,anno)
        for (inc in lista) {
            val incDTO : IncaricoDTO =  IncaricoDTO(inc)
            listaReq.add(incDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    // 4c
    @GetMapping(value = ["/findIncarichiPopByIntermediaIDAndAnno/{id}/{anno}"])
    fun findIncarichiPopByIntermediaIDAndAnno(@PathVariable(value = "id") id: Int,
                                                @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        //println("/findIncarichiPopByIntermediaIDAndAnno/"+id+"/"+anno)
        var listaReq: MutableList<IncaricoDTO> = ArrayList<IncaricoDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessun incarico pop per intermedia con id '$id' e anno '$anno'") )
        val lista: List<Incarico> = incServizi.findIncarichiPopByIntermediaIDAndAnno(id,anno)
        for (inc in lista) {
            val incDTO : IncaricoDTO =  IncaricoDTO(inc)
            listaReq.add(incDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    // 5
    @GetMapping(value = ["/findIncarichiByStrutturaIDAndAnno/{id}/{anno}"])
    fun findIncarichiByStrutturaIDAndAnno(@PathVariable(value = "id") id: Int,
                                             @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        //println("/findIncarichiByStrutturaIDAndAnno/"+id+"/"+anno)
        var listaReq: MutableList<IncaricoDTO> = ArrayList<IncaricoDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessun incarico per struttura con id '$id' e anno '$anno'") )
        val lista: List<Incarico> = incServizi.findIncarichiByStrutturaIDAndAnno(id,anno)
        for (inc in lista) {
            val incDTO : IncaricoDTO =  IncaricoDTO(inc)
            listaReq.add(incDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    // 7
    @GetMapping(value = ["/findIncarichiByDirigenteIDAndAnno/{id}/{anno}"])
    fun findIncarichiByDirigenteIDAndAnno(@PathVariable(value = "id") id: Int,
                                             @PathVariable(value = "anno") anno: Int): ResponseEntity<MutableList<out Any>> {
        //println("/findIncarichiByDirigenteIDAndAnno/"+id+"/"+anno)
        var listaReq: MutableList<IncaricoDTO> = ArrayList<IncaricoDTO>()
        var listaErr: MutableList<ErrorResponse> = ArrayList<ErrorResponse>()
        listaErr.add(ErrorResponse("Lista nulla o vuota","Nessun Incarico per dirigente con id '$id' e anno '$anno'") )
        val lista: List<Incarico> = incServizi.findIncarichiByDirigenteIDAndAnno(id,anno)
        for (inc in lista) {
            val incDTO : IncaricoDTO =  IncaricoDTO(inc)
            listaReq.add(incDTO)
        }
        return if (!listaReq.isNullOrEmpty()) ResponseEntity(listaReq ,OK)
        else ResponseEntity(listaErr, NO_CONTENT)
    }

    // --------------- Error Handling
    data class ErrorResponse(val error: String, val message: String)

    class PersonaFisicaNotFoundException(message: String) : Exception(message)
    class PersonaGiuridicaNotFoundException(message: String) : Exception(message)
    class IncaricoNotFoundException(message: String) : Exception(message)

} // ------ fine RestController