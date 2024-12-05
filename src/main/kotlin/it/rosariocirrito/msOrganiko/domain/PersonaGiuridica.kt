package it.rosariocirrito.msOrganiko.domain

import org.springframework.beans.support.MutableSortDefinition
import org.springframework.beans.support.PropertyComparator
import org.springframework.data.repository.CrudRepository
import java.util.*
import javax.persistence.*


// ********** 1. PersonaGiuridicaRepository *******

interface PersonaGiuridicaRepository  : CrudRepository<OpPersonaGiuridica, Int> {
    fun findAllByOpPersonaGiuridicaTipo(tipo: Optional<OpPersonaGiuridicaTipo>): List<OpPersonaGiuridica>
    fun findAllByOpPersonaGiuridica(padre: OpPersonaGiuridica): List<OpPersonaGiuridica>
}

@Entity
@Table(name="op_persona_giuridica")
class OpPersonaGiuridica : Persona() {

    enum class OpPersonaGiuridicaStatoEnum {
        CANCELLATO, ATTIVO, NASCOSTO
    }

    // --------- campi dal db
    // l'Id viene ereditato da Persona
    var denominazione: String? = null
    var codice: String? = null
    var competenze: String? = null
    @Enumerated
    var stato: OpPersonaGiuridicaStatoEnum? = null
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cambio_stato")
    var dataCambioStato: Date? = null
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_inserimento")
    var dataInserimento: Date? = null
    @Column(name = "carica")
    var carica: String? = null

    // ----------- relazioni chiave esterna fk
    // bi-directional many-to-one association to PersonaGiuridicaTipo
    // @ManyToOne(fetch = FetchType.EAGER) 2015/07/20
    @ManyToOne
    @JoinColumn(name = "op_persona_giuridica_tipo_idop_persona_giuridica_tipo")
    var opPersonaGiuridicaTipo: OpPersonaGiuridicaTipo? = null
    @ManyToOne
    @JoinColumn(name = "padre")
    var opPersonaGiuridica: OpPersonaGiuridica? = null

    // --------- relazioni su entità subordinate
    // strutture figlie
    @OneToMany(mappedBy = "opPersonaGiuridica", fetch = FetchType.EAGER)
    val opPersonaGiuridicas: Set<OpPersonaGiuridica> = HashSet()
    // afferenze
    @OneToMany(mappedBy = "opPersonaGiuridica")
    val opPersonaAfferenzas: Set<OpPersonaAfferenza> = HashSet<OpPersonaAfferenza>()
    // dipendenti
    // bi-directional one-to-many association
    @OneToMany(mappedBy = "opPersonaGiuridica")
    val dipendenti: Set<OpPersonaFisica> = HashSet<OpPersonaFisica>()


    // --------- metodi per gestire estrazione da possibili campi nulli
    @Transient
    var competenzeStruttura: String? = null
        get(){
            if (competenze != null) return competenze
            else return "N/A"
        }
    //
    fun decodeCarica() : String {
        if (carica != null) return carica!! // casi particolari Segretario Avvocato Ragioniere Generale
        else {
            when (opPersonaGiuridicaTipo!!.id) {
                1 -> return "Capo Amministrazione"
                2 -> return "Dirig. Generale"
                3 -> return "Dirigente"
                4 -> return "Dirigente"
                5 -> return ""
                6 -> return "Dirigente"
                7 -> return "Assessore"
                8 -> return "Dirigente"
                9 -> return "Responsabile"
                else -> return "???"
            }
        }
    }

    // --------- metodi per gestire campi ausiliari utili
    @Transient
    var annoCancellazione = 0
        get(): Int {
            val cambioStato = Calendar.getInstance()
            if (null != dataCambioStato) cambioStato.time = dataCambioStato
            return cambioStato[Calendar.YEAR]
        }

    //
    @Transient
    var attiva = false
        get():Boolean {
            return  stato == OpPersonaGiuridicaStatoEnum.ATTIVO
        }
    @Transient
    var cancellata = false
        get():Boolean {
            return stato == OpPersonaGiuridicaStatoEnum.CANCELLATO
        }

    fun isAssessorato(): Boolean {
        val codice = opPersonaGiuridicaTipo!!.codice
        return codice.equals("ASS")
    }

    fun isDipartimento(): Boolean {
        val codice = opPersonaGiuridicaTipo!!.codice
        return codice.equals("DIP") || codice.equals("AMM") || codice.equals("UFF")
    }

    fun isIntermedia(): Boolean {
        val codice = opPersonaGiuridicaTipo!!.codice
        return codice.equals("INT")
    }

    fun isUob(): Boolean {
        val codice = opPersonaGiuridicaTipo!!.codice
        return codice.equals("UO")
    }

    fun isPop(): Boolean {
        val codice = opPersonaGiuridicaTipo!!.codice
        return codice.equals("POP")
    }

    fun isStudio(): Boolean {
        val codice = opPersonaGiuridicaTipo!!.codice
        return codice.equals("STU")
    }

    // ------------------ metodi usati direttamente da altri servizi e dal RestController
    fun getStruttureDiscendentiAttiveOCancellateAnno(anno: Int): List<OpPersonaGiuridica> {
        var lista : MutableList<OpPersonaGiuridica> = ArrayList<OpPersonaGiuridica>()
        val listaLev1 : List<OpPersonaGiuridica> = opPersonaGiuridicas.toList()
        if(!listaLev1.isNullOrEmpty()){
            for (struLev1 in listaLev1) {
                //println("struttura codice-> ${struLev1.codice} tipo -> ${struLev1.opPersonaGiuridicaTipo!!.nome}")
                if (struLev1.attiva || (struLev1.cancellata && struLev1.annoCancellazione >= anno)){
                    //println("struttura intermedia: ${struLev1.codice} attiva-> struLev1.attiva AGGIUNTA!!" )
                    lista.add(struLev1)
                }
                // aggiungiamo eventuali strutture figlie
                val listaLev2 : List<OpPersonaGiuridica> = struLev1.opPersonaGiuridicas.toList()
                if(!listaLev2.isNullOrEmpty()){
                    for (struLev2 in listaLev2) {
                        //println("struttura codice-> ${struLev2.codice} tipo -> ${struLev2.opPersonaGiuridicaTipo!!.nome}")
                        if (struLev2.attiva || (struLev2.cancellata && struLev2.annoCancellazione >= anno)){
                            //println("struttura intermedia: ${struLev2.codice} attiva-> struLev2.attiva AGGIUNTA!!" )
                            lista.add(struLev2)
                        }
                        // aggiungiamo eventuali strutture nipoti
                        val listaLev3 : List<OpPersonaGiuridica> = struLev2.opPersonaGiuridicas.toList()
                        if(!listaLev3.isNullOrEmpty()){
                            for (struLev3 in listaLev3) {
                                //println("struttura codice-> ${struLev3.codice} tipo -> ${struLev3.opPersonaGiuridicaTipo!!.nome}")
                                if (struLev3.attiva || (struLev3.cancellata && struLev3.annoCancellazione >= anno)){
                                    //println("struttura intermedia: ${struLev3.codice} attiva-> struLev3.attiva AGGIUNTA!!" )
                                    lista.add(struLev3)
                                }
                                // aggiungiamo eventuali strutture pro-nipoti
                                val listaLev4 : List<OpPersonaGiuridica> = struLev3.opPersonaGiuridicas.toList()
                                if(!listaLev4.isNullOrEmpty()){
                                    for (struLev4 in listaLev4) {
                                        //println("struttura codice-> ${struLev4.codice} tipo -> ${struLev4.opPersonaGiuridicaTipo!!.nome}")
                                        if (struLev4.attiva || (struLev4.cancellata && struLev4.annoCancellazione >= anno)){
                                            //println("struttura intermedia: ${struLev4.codice} attiva-> struLev4.attiva AGGIUNTA!!" )
                                            lista.add(struLev4)
                                        }
                                        // non aggiungiamo ulteriori discendenti abbiamo coperto perfino assessorati
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //
        PropertyComparator.sort(lista, MutableSortDefinition("codice", true, true))
        return lista
    }

    fun getStruttureFiglieAttiveOCancellateAnno(anno: Int): List<OpPersonaGiuridica> {
        var lista : MutableList<OpPersonaGiuridica> = ArrayList<OpPersonaGiuridica>()
        val listaLev1 : List<OpPersonaGiuridica> = opPersonaGiuridicas.toList()
        if(!listaLev1.isNullOrEmpty()){
            for (struLev1 in listaLev1) {
                //println("struttura codice-> ${struLev1.codice} tipo -> ${struLev1.opPersonaGiuridicaTipo!!.nome}")
                if (struLev1.attiva || (struLev1.cancellata && struLev1.annoCancellazione >= anno)){
                    //println("struttura intermedia: ${struLev1.codice} attiva-> struLev1.attiva AGGIUNTA!!" )
                    lista.add(struLev1)
                }
            }
        }
        //
        PropertyComparator.sort(lista, MutableSortDefinition("codice", true, true))
        return lista
    }

    fun getPersonaGiuridicas(): List<OpPersonaGiuridica> {
        val sortedPersonaGiuridicas = ArrayList<OpPersonaGiuridica>(this.opPersonaGiuridicas)
        PropertyComparator.sort(sortedPersonaGiuridicas, MutableSortDefinition("codice", true, true))
        return sortedPersonaGiuridicas
    }

    fun getPersonaGiuridicasAttive(): List<OpPersonaGiuridica> {
        val attiveStrutture = ArrayList<OpPersonaGiuridica>()
        for (struttura in this.opPersonaGiuridicas) {
            if (struttura.stato!!.equals(OpPersonaGiuridicaStatoEnum.ATTIVO)) attiveStrutture.add(struttura)
        }
        PropertyComparator.sort(attiveStrutture, MutableSortDefinition("denominazione", true, true))
        return attiveStrutture
    }

    fun getPersonaGiuridicasCancellate(): List<OpPersonaGiuridica> {
        val deletedStrutture = ArrayList<OpPersonaGiuridica>()
        for (struttura in this.opPersonaGiuridicas) {
            if (struttura.stato!!.equals(OpPersonaGiuridicaStatoEnum.CANCELLATO)) deletedStrutture.add(struttura)
        }
        PropertyComparator.sort(deletedStrutture, MutableSortDefinition("denominazione", true, true))
        return deletedStrutture
    }

    override fun toString(): String {
        return ""+denominazione
    }

    fun getMapSelectStruttureAttive(dipartimento: OpPersonaGiuridica): Map<Int, String>? {
        // costruisco la map per la select strutture
        val mapSelectStru: MutableMap<Int, String> = LinkedHashMap<Int, String>()
        // prima il dipartimento
        mapSelectStru[dipartimento.idpersona!!] = dipartimento.denominazione.toString()
        for (stru in dipartimento.getPersonaGiuridicasAttive()) {
            mapSelectStru[stru.idpersona!!] = stru.denominazione.toString()
        }
        return mapSelectStru
    }



    // ____________ metodi business ____________________________________
    //


    //
    fun getDipartimento(): OpPersonaGiuridica? {
        var dipartimento: OpPersonaGiuridica? = null
        var struttura: OpPersonaGiuridica = this
        if (isDipartimento()) return this
        //System.out.println("UserServiceImpl.findDipartimentoOfLoggedUser() struttura= "+struttura.getDenominazione());
        var maxLevel = 5
        while (maxLevel > 0) {
            //System.out.println("UserServiceImpl.findDipartimentoOfLoggedUser() dip?= "+dipartimento.getDenominazione());
            maxLevel--
            if (struttura.isDipartimento()) {
                dipartimento = struttura
                break
            } else struttura = struttura.opPersonaGiuridica!!
        }
        return dipartimento
    }

}


// ********** 2. PersonaGiuridicaTipo *******
interface PersonaGiuridicaTipoRepository: CrudRepository<OpPersonaGiuridicaTipo, Int> {}

@Entity
@Table(name="op_persona_giuridica_tipo")
class OpPersonaGiuridicaTipo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idop_persona_giuridica_tipo")
    var id: Int? = null
    var codice: String? = null
    var nome: String? = null

    @Transient
    private var idTipo = 0
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as OpPersonaGiuridicaTipo
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {return id ?: 0}
}