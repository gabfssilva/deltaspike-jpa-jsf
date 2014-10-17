Link do artigo: http://wehavescience.com/2014/10/16/criando-uma-aplicacao-completa-utilizando-o-deltaspike-um-framework-baseado-em-extensoes-do-cdi/

O Apache Deltaspike é um framework baseado em extensões do CDI, que funciona muito bem tanto com a implementação referência, que é a Weld, quanto a do próprio Apache, a OpenWebBeans.
O Deltaspike provê certas características, como por exemplo, controle de transações, que, em conjunto com o CDI, faz você não precisar de um container mais robusto, como por exemplo algum JEE ou o Spring.
Para ficar ainda mais simples, vamos utilizar um Jetty Embedded. Exatamente, ao invés de colocarmos nossa aplicação em um servlet container, vamos colocar um servlet container na nossa aplicação e rodar um main dentro de um jar que tudo irá funcionar como deveria. ;) <!--more-->
Para esse artigo, eu vou refazer essa aplicação que eu fiz em Spring: http://wehavescience.com/2012/12/20/criando-uma-aplicacao-utilizando-spring-framework-3-e-hibernate-4-e-jsf-2/, só que dessa vez utilizando CDI, Deltaspike e Hibernate, rodando em um Jetty 9.

Outras informações sobre a aplicação:
- Será utilizado o MySQL como banco de dados
- Utilizaremos JSF 2.2 com PrimeFaces 5.


Vamos utilizar Gradle nessa aplicação para baixar os jars e gerar o build da aplicação.

Abaixo o build.gradle, contendo todas as dependêndias e plugins para o nosso projeto:

```groovy
apply plugin: 'java'
apply plugin: 'maven'
apply plugin: 'application'

group = 'com.wehavescience'
version = '1.0-SNAPSHOT'

sourceCompatibility = 1.8
targetCompatibility = 1.8

repositories {
    mavenCentral()
    mavenLocal()
}

sourceSets {
    main {
        output.resourcesDir = "build/classes/main"
    }
}

mainClassName = "com.wehavescience.jetty.ApplicationRunner"

dependencies {
    compile group: 'org.jboss.weld', name: 'weld-core', version:'2.2.5.Final'
    compile group: 'org.jboss.weld.servlet', name: 'weld-servlet-core', version:'2.2.5.Final'
    compile group: 'org.hibernate', name: 'hibernate-entitymanager', version:'4.3.6.Final'
    compile group: 'org.hibernate', name: 'hibernate-core', version:'4.3.6.Final'
    compile group: 'org.hibernate', name: 'hibernate-c3p0', version:'4.3.6.Final'
    compile group: 'org.hibernate', name: 'hibernate-validator', version:'5.1.2.Final'
    compile group: 'com.mchange', name: 'c3p0', version:'0.9.2.1'
    compile group: 'org.apache.deltaspike.core', name: 'deltaspike-core-api', version:'1.0.3'
    compile group: 'org.apache.deltaspike.modules', name: 'deltaspike-jpa-module-api', version:'1.0.3'
    compile group: 'org.apache.deltaspike.modules', name: 'deltaspike-data-module-api', version:'1.0.3'
    compile group: 'org.eclipse.jetty', name: 'jetty-server', version:'9.2.3.v20140905'
    compile group: 'org.eclipse.jetty', name: 'jetty-webapp', version:'9.2.3.v20140905'
    compile group: 'org.eclipse.jetty', name: 'jetty-jsp', version:'9.2.3.v20140905'
    compile group: 'org.eclipse.jetty', name: 'jetty-plus', version:'9.2.3.v20140905'
    compile group: 'com.sun.faces', name: 'jsf-api', version:'2.2.8'
    compile group: 'com.sun.faces', name: 'jsf-impl', version:'2.2.8'
    compile group: 'org.primefaces', name: 'primefaces', version:'5.1'

    runtime group: 'org.jboss', name: 'jandex', version:'1.2.1.Final'
    runtime group: 'mysql', name: 'mysql-connector-java', version:'5.1.31'
    runtime group: 'org.apache.deltaspike.core', name: 'deltaspike-core-impl', version:'1.0.3'
    runtime group: 'org.apache.deltaspike.modules', name: 'deltaspike-jpa-module-impl', version:'1.0.3'
    runtime group: 'org.apache.deltaspike.modules', name: 'deltaspike-data-module-impl', version:'1.0.3'
}
```


<h2>Entidades</h2>

Nessa aplicação, para afim de facilitar o entendimento, teremos apenas uma entidade, que é a entidade Person:

```java
package com.wehavescience.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
@Entity
@Table(name = "people")
public class Person {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private Integer age;
    private String email;
    private String information;

    public Person(Long id, String name, Integer age, String email, String information) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.information = information;
    }

    public Person() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", information='" + information + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (age != null ? !age.equals(person.age) : person.age != null) return false;
        if (email != null ? !email.equals(person.email) : person.email != null) return false;
        if (id != null ? !id.equals(person.id) : person.id != null) return false;
        if (information != null ? !information.equals(person.information) : person.information != null) return false;
        if (name != null ? !name.equals(person.name) : person.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (information != null ? information.hashCode() : 0);
        return result;
    }
}
```


Bem simples, certo? Ela aponta para uma tabela chamada "people" e possui cinco atributos, sendo que apenas um é único, que é o ID.

<h2>Repositories</h2>

Vamos utilizar o módulo Deltaspike Data, que é um plugin do Deltaspike baseado no JPA que provê algumas features para criação de repositories na nossa aplicação, de uma forma bem simples, por exemplo:

```java
package com.wehavescience.repositories;

import com.wehavescience.entities.Person;
import org.apache.deltaspike.data.api.EntityRepository;
import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.Repository;

import java.util.List;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
@Repository
public interface PersonRepository extends EntityRepository<Person, Long> {
    @Query("SELECT p FROM Person p where age = ?1")
    List<Person> findAllByAge(Integer age);
}
```

Não precisamos criar uma implementação para essa classe. Iremos apenas injetar essa interface utilizando @Inject que automaticamente uma implementação será criada em tempo de execução para essa classe. Incrível, certo? Podemos apenas herdar de EntityRepository para herdarmos métodos básicos, então, é sempre interessante fazê-lo.

<h2>JPA e Hibernate</h2>

Dessa vez vamos fazer um pouco diferente, vamos setar os atributos do nosso EntityManager via código. É bem interessante, porque, diminuimos o XML da nossa aplicação, algo que ninguém gosta. :P
Para isso, vamos definir os atributos via arquivo properties, para não deixar hardcoded:


<strong>jpa-mysql.properties:</strong>

```
javax.persistence.jdbc.driver = com.mysql.jdbc.Driver
javax.persistence.jdbc.url = jdbc:mysql://localhost:3306/wehavescience
javax.persistence.jdbc.user = root
javax.persistence.jdbc.password =

hibernate.dialect = org.hibernate.dialect.MySQL5InnoDBDialect
hibernate.hbm2ddl.auto = create-drop
hibernate.connection.provider_class = org.hibernate.connection.C3P0ConnectionProvider
hibernate.c3p0.max_size = 10
hibernate.c3p0.min_size = 5
hibernate.c3p0.acquire_increment = 1
hibernate.c3p0.idle_test_period = 300
hibernate.c3p0.max_statements = 0
hibernate.c3p0.timeout = 100
```

Note que estamos utilizando o C3P0 para definirmos um connection pool para o Hibernate.

<h2>Métodos produtores</h2>

Antes de seguir, vamos criar dois qualifiers, futuramente você entenderá para que cada um serve:

```java
package com.wehavescience.qualifiers;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectableProperties {
    @Nonbinding String file();
}
```


```java
package com.wehavescience.qualifiers;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Startup {
}
```

Voltando ao assunto, o CDI provê a feature de criar objetos através de métodos produtores (anotados com um @Produces), então, vamos criar alguns produtores:

```java
package com.wehavescience.producers;

import com.wehavescience.qualifiers.InjectableProperties;
import com.wehavescience.qualifiers.Startup;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
public class DefaultProducer {
    @Produces
    @ApplicationScoped
    @Default
    @Startup
    public EntityManagerFactory entityManagerFactoryMySQL(@InjectableProperties(file = "jpa-mysql") Map<String, String> properties) {
        PersistenceProvider persistenceProvider = new HibernatePersistenceProvider();
        return persistenceProvider.createEntityManagerFactory("MySQL", properties);
    }

    @Produces
    @Default
    @RequestScoped
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.createEntityManager();
    }

    public void close(@Disposes @Any EntityManager entityManager) {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    @Produces
    @InjectableProperties(file = "")
    public Map<String, String> properties(InjectionPoint injectionPoint) throws IOException {
        InjectableProperties annotation = injectionPoint.getAnnotated().getAnnotation(InjectableProperties.class);
        Map<String, String> map = new HashMap<String, String>();
        final ResourceBundle bundle = ResourceBundle.getBundle(annotation.file());
        bundle.keySet().forEach(key -> map.put(key, bundle.getString(key)));
        return map;
    }
}
```

Estamos utilizando um método para obter arquivo properties como Map, que é o produtor @InjectableProperties. Estamos injetando o arquivo properties citado acima no método da criação de um EntityManagerFactory application scoped, ou seja, um singleton, passando todas as propriedades do nosso arquivo jpa-mysql.properties.
O último método produtor é o do nosso EntityManager, que basicamente injeta o singleton do EntityManagerFactory e cria um novo EntityManager.
Você deve ter notado que existe um método com as anotações @Disposes @Any dentro dessa classe. É para que, sempre que acabarmos de usar um EntityManager em um request, o mesmo ser fechado e retornar a conexão para o pool.
Relembrando que, estamos criando o EntityManager porque ele é utilizado pelo Deltaspike Data Module, o qual estamos utilizando para criar repositórios.


<h2>Services</h2>

Não há uma necessidade na verdade para criarmos essa camada, mas, para termos uma camada transacional, eu resolvi criá-la.

Vamos possuir apenas um service. Temos uma interface:

```java
package com.wehavescience.service;

import com.wehavescience.entities.Person;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
public interface PersonService {
    void save(Person person);
    Person fetch(Long id);
    Long size();
}
```

E a implementação:

```java
package com.wehavescience.service.impl;

import com.wehavescience.entities.Person;
import com.wehavescience.repositories.PersonRepository;
import com.wehavescience.service.PersonService;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import javax.inject.Inject;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
public class PersonServiceImpl implements PersonService {
    @Inject
    private PersonRepository personRepository;

    @Override
    @Transactional
    public void save(Person person) {
        personRepository.save(person);
    }

    @Override
    public Person fetch(Long id){
        return personRepository.findBy(id);
    }

    @Override
    public Long size(){
        return personRepository.count();
    }
}
```

Estamos utilizando também o módulo JPA do Deltaspike, o qual provê um interceptor que cuida do ciclo das transações, bem simples mas provendo o que precisamos. O interceptor é chamado sempre que você anota alguma classe ou método com a interface org.apache.deltaspike.jpa.api.transaction.Transactional. Dessa forma, não precisamos dar um begin transaction nem commitar nada, o interceptor fará isso para nós.

<h2>Managed Beans</h2>

Como estamos utilizando JSF, possuimos um ManagedBean para cuidar da comunicação da página com o serviço:

```java
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
```

Relembrando das anotações do CDI para JSF e dos escopos do CDI. Não podemos utilizar escopos de outros pacotes.

<h2>Runner</h2>

Por fim, nossa classe que starta o Jetty 9 em um método main.

```java
package com.wehavescience.jetty;

import com.sun.faces.config.ConfigureListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jboss.weld.environment.servlet.BeanManagerResourceBindingListener;
import org.jboss.weld.environment.servlet.Listener;

import java.net.URL;
import java.security.ProtectionDomain;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
public class ApplicationRunner {
    private static Server server;

    private ApplicationRunner() {
    }

    public static void main(String[] args) {
        run(ApplicationRunner.class, "/deltaspike-cdi-jpa", 8080, true);
    }

    public static void run(Class<?> clazz, String applicationRoot, int port, boolean join) {
        try {
            if(isRunning()){
                throw new IllegalStateException("Server already running!");
            }

            server = new Server(port);

            ProtectionDomain domain = clazz.getProtectionDomain();
            URL location = domain.getCodeSource().getLocation();

            WebAppContext webAppContext = new WebAppContext();
            webAppContext.setContextPath(applicationRoot);
            webAppContext.setWar(location.toExternalForm());

            webAppContext.addEventListener(new ConfigureListener());
            webAppContext.addEventListener(new BeanManagerResourceBindingListener());
            webAppContext.addEventListener(new Listener());

            server.setHandler(webAppContext);

            server.start();

            if (join) {
                server.join();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isRunning() {
        return server != null && server.isRunning();
    }

    public static void stop() {
        if (!isRunning()) {
            throw new IllegalStateException("Server not running...");
        }

        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

Após executar o método main, o servidor subirá junto com a sua aplicação, que estará disponível na porta 8080.

<h2>Startup Extension</h2>

O CDI não possui nenhum mecanismo para criar objetos no startup da aplicação, infelizmente. Logo, podemos criar esse workaround, que é simplesmente um qualifier e uma classe que starta toda e qualquer classe application scoped que possui o @Startup como qualifier:

```java
package com.wehavescience.cdi.extensions;

import com.wehavescience.qualifiers.Startup;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.AnnotationLiteral;
import java.util.Set;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
public class StartupBeanExtension implements Extension {
    public void afterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
        Set<Bean<?>> beans = beanManager.getBeans(Object.class, new AnnotationLiteral<Startup>() {
        });

        for (Bean<?> bean : beans) {
            beanManager.getReference(bean, bean.getTypes().iterator().next(), beanManager.createCreationalContext(bean)).toString();
        }
    }
}
```

<h2>Resources e XMLs de configuração</h2>

Tentei fazer com que a aplicação possuisse o mínimo de configuração possível, para ficar bem simples. Infelizmente, por conta do JSF, acabei precisando criar o web.xml e o faces-config.xml e colocando na pasta /resources/WEB-INF da aplicação. Aliás, quem souber subir uma aplicação com JSF apenas configurando no ApplicationRunner, como eu defini os listeners do CDI, pode mandar um comentário explicando que eu coloco aqui no artigo.

src/main/resources/WEB-INF/faces-config.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<faces-config version="2.2"
              xmlns="http://xmlns.jcp.org/xml/ns/javaee"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-facesconfig_2_2.xsd">
</faces-config>
```

src/main/resources/WEB-INF/web.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee" xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd" id="WebApp_ID" version="3.0">
    <display-name>CadastroClientesWEB</display-name>
    <welcome-file-list>
        <welcome-file>index.html</welcome-file>
    </welcome-file-list>
    <servlet>
        <servlet-name>Faces Servlet</servlet-name>
        <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.jsf</url-pattern>
    </servlet-mapping>

    <context-param>
        <param-name>primefaces.THEME</param-name>
        <param-value>cupertino</param-value>
    </context-param>
</web-app>
```

src/main/resources/META-INF/beans.xml:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://docs.jboss.org/cdi/beans_1_0.xsd">

    <interceptors>
        <class>org.apache.deltaspike.jpa.impl.transaction.TransactionalInterceptor</class>
    </interceptors>
</beans>
```

Note que, estamos ativando o interceptor de transações do módulo JPA do Deltaspike.


src/main/resources/META-INF/persistence.xml:

```xml
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd"
             version="1.0">
    <persistence-unit name="MySQL"/>
</persistence>
```

Não tenho a mínima ideia porque o JPA ainda pede para criarmos um persistence.xml com o persistent-unit que você criou no seu producer, mas é necessário.

src/main/resources/META-INF/services/javax.enterprise.inject.spi.Extension:

``
com.wehavescience.cdi.extensions.StartupBeanExtension
```

Para a nossa classe StartupBeanExtension funcionar, precisamos adicionar essa linha em um arquivo chamado javax.enterprise.inject.spi.Extension, dentro de META-INF/services.

<h2>Página JSF</h2>

Finalizando com a página JSF:

```html
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core"
      xmlns:p="http://primefaces.org/ui">

<h:head>
    <title>Página de Cadastro</title>
</h:head>
<h:body>
    <h:form>
        <p:messages />

        <h:panelGrid columns="2">
            <h:outputText value="Nome: " />
            <p:inputText value="#{personMBean.person.name}" />
            <h:outputText value="E-mail: " />
            <p:inputText value="#{personMBean.person.email}" />
            <h:outputText value="Idade: " />
            <p:inputText value="#{personMBean.person.age}" />
            <h:outputText value="Informações " />
            <p:inputText value="#{personMBean.person.information}" />
        </h:panelGrid>

        <p:commandButton value="Cadastrar!" action="#{personMBean.save}" ajax="false" />
    </h:form>
</h:body>
</html>
```

<h2>Utilizando o Gradle para rodar seu projeto</h2>

Para rodar seu projeto utilizando o Gradle, é só, dentro do seu projeto, rodar o comando:

````
gradle run
```

Após isso, sua aplicação estará disponível em: http://localhost:8080/deltaspike-cdi-jpa/pages/index.jsf

<h2>Finalizando...</h2>

O Deltaspike possui vários módulos, você pode dar uma olhada em: <a href="https://deltaspike.apache.org/" target="_blank">https://deltaspike.apache.org/</a>

Espero que eu tenha ajudado, logo mais eu estarei postando o github do projeto, caso vocês deparem com algum problema para startar a aplicação aqui. 

Abraços e bons estudos!
