package it.rosariocirrito.msOrganiko.domain

import javax.persistence.*

@Entity
@Table(name = "persona")
@Inheritance(strategy = InheritanceType.JOINED)
open class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idpersona")
    open var idpersona: Int? = null
}