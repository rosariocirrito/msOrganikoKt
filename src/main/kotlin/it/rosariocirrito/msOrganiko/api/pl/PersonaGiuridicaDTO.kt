package it.rosariocirrito.msOrganiko.api.pl

import it.rosariocirrito.msOrganiko.domain.OpPersonaGiuridica
import java.util.*

class PersonaGiuridicaDTO(pg: OpPersonaGiuridica) {
    val idPersona: Int
    val denominazione: String
    val codice: String
    val competenze: String
    val stato: OpPersonaGiuridica.OpPersonaGiuridicaStatoEnum
    val dataCambioStato: Date
    val dataInserimento: Date
    val pgTipoID: Int
    val pgPadreID: Int
    val pgDeptID: Int
    val assessorato: Boolean
    val dipartimento: Boolean
    val intermedia: Boolean
    val uob: Boolean
    val pop: Boolean
    val studio: Boolean
    val cancellata: Boolean
    val carica: String

    init {
        idPersona = pg.idpersona!!
        denominazione = pg.denominazione!!
        codice = pg.codice!!
        competenze =  pg.competenzeStruttura!!
        stato = pg.stato!!
        dataCambioStato = pg.dataCambioStato!!
        dataInserimento = pg.dataInserimento!!
        pgTipoID = pg.opPersonaGiuridicaTipo!!.id!!
        pgPadreID = pg.opPersonaGiuridica!!.idpersona!!
        assessorato = pg.isAssessorato()
        dipartimento = pg.isDipartimento()
        intermedia = pg.isIntermedia()
        uob = pg.isUob()
        pop = pg.isPop()
        studio = pg.isStudio()
        cancellata = pg.cancellata
        pgDeptID = pg.getDipartimento()!!.idpersona!!
        carica = pg.decodeCarica()
    }
}