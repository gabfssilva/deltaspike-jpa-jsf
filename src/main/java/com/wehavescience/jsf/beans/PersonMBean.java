package com.wehavescience.jsf.beans;

import com.wehavescience.entities.Person;
import com.wehavescience.service.PersonService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
@Named
@RequestScoped
public class PersonMBean implements Serializable {
    private Person person;

    @Inject
    private PersonService service;

    @PostConstruct
    private void init() {
        person = new Person();
    }

    public String save() {
        try {
            service.save(person);
            message("Cadastro realizado com sucesso! Tamanho atual da tabela: " + service.size());
        } catch (IllegalArgumentException e) {
            message(e.getMessage());
        }
        return "";
    }

    private void message(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(msg, ""));
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public PersonService getService() {
        return service;
    }

    public void setService(PersonService service) {
        this.service = service;
    }
}