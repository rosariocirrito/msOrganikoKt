package it.rosariocirrito.msOrganiko.domain

import java.util.*
import javax.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

interface IncaricoRepository : CrudRepository<Incarico, Int> {
    //fun findById(id:Long): Incarico? è implicito?
    //fun findBy...(): Incarico?
    //fun findAllByOrderByAddedAtDesc(): Iterable<>

    fun findAllByPersonaFisica(pf: OpPersonaFisica): List<Incarico>
    fun findAllByPersonaGiuridica(pg: OpPersonaGiuridica): List<Incarico>
}

@Entity
@Table(name = "incarico")
class Incarico {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var idIncarico: Int? = null
    //
    @Temporal(TemporalType.TIMESTAMP)
    var dataInizio: Date? = null
    //
    @Temporal(TemporalType.TIMESTAMP)
    var dataFine: Date? = null
    //
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idPF")
    var personaFisica: OpPersonaFisica? = null
    //
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idPG")
    var personaGiuridica: OpPersonaGiuridica? = null
    //
    var interim = false

    // ----------- transient
    @Transient
    var idPF: Int? = null
        get(){
            return personaFisica!!.idpersona!!
        }
    @Transient
    var idDip = 0
        get(){
        return personaFisica!!.idpersona!!
    }
    //
    @Transient
    var idPG: Int? = null
        get(){
            return personaGiuridica!!.idpersona!!
        }
    @Transient
    var idStruttura = 0
        get(){
            return personaGiuridica!!.idpersona!!
        }

    @Transient
    var idDept = 0
        get(){
            val dept = personaGiuridica!!.getDipartimento()
            return dept!!.idpersona!!
        }

    @Transient
    var denominazioneDipartimento: String? = null
        get(){
            val dept = personaGiuridica!!.getDipartimento()
            return dept!!.denominazione
        }

    @Transient
    var codiceStruttura: String? = null
        get(){
            return personaGiuridica!!.codice
        }

    @Transient
    var denominazioneStruttura: String? = null
        get(){
            return personaGiuridica!!.denominazione
        }

    @Transient
    var competenzeStruttura: String? = null
        get(){
            if (personaGiuridica!!.competenze != null) return personaGiuridica!!.competenze
            else return "N/A"
        }
    @Transient
    var nuovo = false

    @Transient
    var stringa: String? = null

    //
    @Transient
    var annoinz = 0
        get(): Int {
            val inizio = Calendar.getInstance()
            if (null != dataInizio) inizio.time = dataInizio
            return inizio[Calendar.YEAR]
        }

    @Transient
    var annofin = 0
        get(): Int {
            val fine = Calendar.getInstance()
            if (null != dataFine) fine.time = dataFine
            return fine[Calendar.YEAR]
        }

    @Transient
    var anno = 0

    //transient private OpPersonaFisica responsabile;
    @Transient
    var order: String? = null
        get(): String ? {
            val strOrder: String =""+personaGiuridica!!.codice + personaFisica!!.cognome
            return strOrder
        }

    @Transient
    var listaIncarichiDaClonare: List<Incarico>? = null

    @Transient
    var idIncaricoDaClonare = 0

    // campi transient per IncaricoDTO
    @Transient
    var responsabile: String? = null
        get() {
            if (personaFisica != null) return personaFisica!!.getCognomeNome()!!
            else return "responsabile sconosciuto"
        }


    @Transient
    var incaricoDipartimentale = false
        get() : Boolean {
            return personaGiuridica!!.isDipartimento()
        }

    @Transient
    var incaricoDirigenziale = false
        get() : Boolean {
            return (personaGiuridica!!.isIntermedia() ||
                    personaGiuridica!!.isUob() ||
                    personaGiuridica!!.isStudio())
        }

    @Transient
    var incaricoPop = false
        get() : Boolean {
            return personaGiuridica!!.isPop()
        }

    @Transient
    var carica: String? = null



}