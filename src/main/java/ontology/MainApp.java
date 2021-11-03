package ontology;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainApp {
    private static final String DATA_PROPERTY_HAS_VALUE = "hasValue";
    private static final String SQWRL_GET_STATES = "GetStates";
    private static final String SQWRL_GET_RESUME = "GetResume";

    private final OWLOntologyManager manager;
    private final OWLOntology ontology;
    private final IRI ontologyIri;
    private final OWLDataFactory dataFactory;
    private final SQWRLQueryEngine queryEngine;

    MainApp() throws OWLOntologyCreationException, FileNotFoundException {
        final File ontologyFile = new File("./ontology.owl");
        manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(new FileInputStream(ontologyFile));
        ontologyIri = ontology.getOntologyID().getOntologyIRI().get();
        dataFactory = manager.getOWLDataFactory();
        queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);
    }

    private String format(String iri) {
        return iri.trim().replace(" ", "_");
    }

    private IRI getEntityFullIri(String entityName) throws URISyntaxException {
        final String fragment = format(entityName);
        String base = format(ontologyIri.toString());
        if (!base.endsWith("#") && !base.endsWith("/")) {
            base += "#";
        }
        return IRI.create(new URI(base + fragment));
    }

    public void setIndicatorValue(String indicatorName, double indicatorValue) throws URISyntaxException {
        final OWLNamedIndividual indicator = dataFactory.getOWLNamedIndividual(getEntityFullIri(indicatorName));
        final OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(getEntityFullIri(DATA_PROPERTY_HAS_VALUE));
        final OWLDataPropertyAssertionAxiom dataPropertyAssertion = dataFactory.getOWLDataPropertyAssertionAxiom(
                dataProperty, indicator, indicatorValue);
        manager.addAxiom(ontology, dataPropertyAssertion);
    }

    public String executeSqwrlQuery(String name) throws SQWRLException {
        return queryEngine.runSQWRLQuery(name).toString();
    }

    public static void main(String[] args) throws Exception {
        final MainApp mainApp = new MainApp();
        final String areaPower = "AREA_POWER";
        final double areaPowerValue = 144d;
        mainApp.setIndicatorValue(areaPower, areaPowerValue);
        final String employeePower = "EMPLOYEE_POWER";
        final double employeePowerValue = 113610.41d;
        mainApp.setIndicatorValue(employeePower, employeePowerValue);
        final String toolCount = "TOOL_COUNT";
        final double toolCountValue = 18d;
        mainApp.setIndicatorValue(toolCount, toolCountValue);
        final String toolPower = "TOOL_POWER";
        final double toolPowerValue = 2700d;
        mainApp.setIndicatorValue(toolPower, toolPowerValue);
        System.out.println(mainApp.executeSqwrlQuery(SQWRL_GET_STATES));
        System.out.println(mainApp.executeSqwrlQuery(SQWRL_GET_RESUME));
    }
}
