package it.rosariocirrito.msOrganiko.domain

import org.springframework.beans.support.MutableSortDefinition
import org.springframework.beans.support.PropertyComparator
import org.springframework.data.repository.CrudRepository
import javax.persistence.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

interface personaAfferenzaRepository: CrudRepository<OpPersonaAfferenza, Int> {
    fun findByOpPersonaFisica(personaFisica: OpPersonaFisica?)
}

@Entity
@Table(name="op_persona_afferenza")
class OpPersonaAfferenza {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idop_persona_afferenza")
    var id: Int? = null

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_inizio")
    var dataInizio: Date? = null


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_fine")
    var dataFine: Date? = null

    //bi-directional many-to-one association to OpPersonaFisica
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "op_persona_fisica_idpersona")
    var opPersonaFisica: OpPersonaFisica? = null

    //bi-directional many-to-one association to OpPersonaGiuridica
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "op_persona_giuridica_idpersona")
    var opPersonaGiuridica: OpPersonaGiuridica? = null

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
}