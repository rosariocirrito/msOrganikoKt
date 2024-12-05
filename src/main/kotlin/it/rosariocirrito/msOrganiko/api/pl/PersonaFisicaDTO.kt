package it.rosariocirrito.msOrganiko.api.pl

import it.rosariocirrito.msOrganiko.domain.OpPersonaFisica
import javax.persistence.Enumerated

class PersonaFisicaDTO (pf: OpPersonaFisica){
    val idPersona: Int
    val cognome: String
    val nome: String
    val stringa: String
    val matricola: String
    @Enumerated
    val stato: OpPersonaFisica.PersonaFisicaStatoEnum
    val pfTipoID: Int
    val pgID: Int
    val dirigente:Boolean
    val dirigenteApicale :Boolean
    val categoria: String

    init {
        idPersona = pf.idpersona!!
        cognome = pf.cognome!!
        nome = pf.nome!!
        matricola = pf.matricola!!
        stato = pf.stato!!
        pfTipoID = pf.opPersonaFisicaTipo!!.id!!
        pgID = pf.opPersonaGiuridica!!.idpersona!!
        dirigente = pf.isDirigente()
        dirigenteApicale = pf.isDirigenteApicale()
        stringa = pf.stringa!!
        categoria = pf.categoria!!
    }
}