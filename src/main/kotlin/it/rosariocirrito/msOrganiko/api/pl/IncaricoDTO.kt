package it.rosariocirrito.msOrganiko.api.pl

import it.rosariocirrito.msOrganiko.domain.Incarico
import java.util.*
import javax.persistence.*


// import java.util.*

class IncaricoDTO (inc: Incarico){
    val idIncarico: Int
    val dataInizio: Date
    val dataFine: Date
    val pfID: Int
    val pgID: Int
    val interim: Boolean

    val responsabile: String
    val codiceStruttura: String
    val denominazioneStruttura: String
    val competenzeStruttura: String
    val idDept: Int
    val denominazioneDipartimento: String
    val incaricoDipartimentale: Boolean
    val incaricoDirigenziale: Boolean
    val incaricoPop: Boolean
    val carica: String

    init {
        idIncarico = inc.idIncarico!!
        dataInizio = inc.dataInizio!!
        dataFine = inc.dataFine!!
        pfID = inc.idPF!!
        pgID = inc.idPG!!
        interim = inc.interim!!
        responsabile = inc.responsabile!!
        codiceStruttura = inc.codiceStruttura!!
        denominazioneStruttura = inc.denominazioneStruttura!!
        competenzeStruttura = inc.competenzeStruttura!!
        idDept = inc.idDept!!
        denominazioneDipartimento = inc.denominazioneDipartimento!!
        incaricoDipartimentale = inc.incaricoDipartimentale
        incaricoDirigenziale = inc.incaricoDirigenziale
        incaricoPop = inc.incaricoPop
        carica = inc.personaGiuridica!!.decodeCarica()
    }
}