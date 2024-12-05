package it.rosariocirrito.msOrganiko.domain

import org.springframework.data.repository.CrudRepository
import java.util.*
import javax.persistence.*


// ********** 1. PersonaFisica *******
interface PersonaFisicaRepository : CrudRepository<OpPersonaFisica, Int> {
    //fun findById(id:Long): Incarico? è implicito?
    //fun findBy...(): Incarico?
    //fun findAllByOrderByAddedAtDesc(): Iterable<>

    fun findAllByCognome(cognome: String): Iterable<OpPersonaFisica>
    fun findAllByMatricola(matricola: String): Iterable<OpPersonaFisica>
    fun findAllByCognomeAndNome(cognome: String, nome: String): Iterable<OpPersonaFisica>
    //fun findByPersonaGiuridica(personaGiuridica: PersonaGiuridica): List<PersonaFisica>
}

@Entity
@Table(name = "op_persona_fisica")
class OpPersonaFisica : Persona() {
    enum class PersonaFisicaSexEnum {
        M, F
    }

    enum class PersonaFisicaStatoEnum {
        ATTIVO,
        CANCELLATO, // Cancellato e non visibile in anagrafica
        TRASFERITO,
        PENSIONTATO,
        LICENZIATO,
        NASCOSTO // Visibile solo da particolari utenti
    }

    // l'Id viene ereditato da Persona

    var cognome: String? = null
    var nome: String? = null
    var matricola: String? = null
    var datanascita: String? = null
    var codicefiscale: String? = null

    @Enumerated
    var stato: PersonaFisicaStatoEnum? = null

    // bi-directional many-to-one association to OpPersonaFisicaTipo
    @ManyToOne(fetch = FetchType.EAGER) // non modificare senn� sposta dipendente va in errore
    @JoinColumn(name = "op_persona_fisica_tipo_idop_persona_fisica_tipo")
    var opPersonaFisicaTipo: opPersonaFisicaTipo? = null

    // bi-directional many-to-one association to OpPersonaGiuridica
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "op_persona_giuridica_idpersona")
    var opPersonaGiuridica: OpPersonaGiuridica? = null

    // bi-directional many-to-one association to OpPersonaAfferenza
    @OneToMany(mappedBy = "opPersonaFisica", fetch = FetchType.EAGER)
    var opPersonaAfferenzas: MutableSet<OpPersonaAfferenza?>? = null


    // ----------- transient
    @Transient
    var stringa: String? = null
        get(){
            return this.opPersonaFisicaTipo!!.descrizione + " " + this.opPersonaFisicaTipo!!.livello+ " " + this.cognome + " " + this.nome;
        }
    @Transient
    var order: String? = null
        get(){
            return cognome + " " + nome + " " + opPersonaFisicaTipo!!.descrizione
        }

    @Transient
    var idPersonaGiuridica = 0

    @Transient
    var idPersonaFisicaTipo = 0

    @Transient
    var anno = 0

    @Transient
    var dataCambioStruttura: Date? = null

    @Transient
    private var cognomeNome: String? = null

    @Transient
    var incarichiAnno: List<Incarico>? = null

    @Transient
    var dipartimento = false

    @Transient
    var categoria: String? = null
        get(){
            return this.opPersonaFisicaTipo!!.categoria
        }



    override fun toString(): String {
        return this.opPersonaFisicaTipo!!.descrizione + " " + this.opPersonaFisicaTipo!!.livello + " " + this.cognome + " " + this.nome
    }

    fun getCognomeNome(): String? {
        return cognome + " " + nome
    }

    fun isDirigente(): Boolean {
        val idTipo: Int? = this.opPersonaFisicaTipo!!.id
        return idTipo == 13 ||  // 13 Dirigente F3
                idTipo == 114 ||  // 114 Dirigente F2
                idTipo == 26 // 26 Dirigente esterno F2
    }
    fun isDirigenteApicale(): Boolean {
        val idTipo: Int? = this.opPersonaFisicaTipo!!.id
        return if (idTipo == 14 ||  // 14 Dirigente Generale
            idTipo == 24 ||  // 24 Segretario Generale F2
            idTipo == 28 ||  // 28 Ragioniere Generale
            idTipo == 206 ||  // Dirigente generale F2
            idTipo == 207 ||  // Segretario Generale F3
            idTipo == 30 // 30 Avvocato Generale
        ) true else false
    }



    // ____________________ business methods ---------------------

} // end

// 2. ********** PersonaFisicaTipo ******
interface PersonaFisicaTipoRepository: CrudRepository<opPersonaFisicaTipo, Int> {
}

@Entity
@Table(name="op_persona_fisica_tipo")
class opPersonaFisicaTipo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idop_persona_fisica_tipo")
    var id: Int? = null
    var codice: String? = null
    var descrizione: String? = null
    var livello: String? = null
    var categoria: String? = null

    @Transient
    var liv_descr: String? = null

    //bi-directional many-to-one association to OpPersonaFisica
    //@OneToMany(mappedBy = "opPersonaFisicaTipo", fetch = FetchType.LAZY)
    //var opPersonaFisicas: List<OpPersonaFisica>? = null
    @OneToMany(mappedBy = "opPersonaFisicaTipo", fetch = FetchType.LAZY)
    var opPersonaFisicas: MutableList<OpPersonaFisica>? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as opPersonaFisicaTipo

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id ?: 0
    }

}