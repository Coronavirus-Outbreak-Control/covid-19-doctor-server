package org.coronaviruscheck.api.doctors.CommonLibs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Domenico Lupinetti
 */
public class ConfigParser {

    public static class Module {

        private String moduleName;
        private LinkedHashMap<String,String> params = new LinkedHashMap<>();
        private HashMap<String,Module> subModules = new HashMap<>(  );
        private HashMap<String,String> attributes = new HashMap<>();

        public String getModuleName() {
            return moduleName;
        }

        void setModuleName( String moduleName ) {
            this.moduleName = moduleName;
        }

        public HashMap<String, String> getParams() {
            return params;
        }

        public String getParam( String param ){
            return params.getOrDefault( param, "" );
        }

        public void setParams( HashMap<String, String> params ) {
            this.params.putAll( params );
        }

        public HashMap<String,Module> getSubModules() {
            return subModules;
        }

        public Module getSubModule( String subModuleName ) {
            return subModules.getOrDefault( subModuleName, new Module() );
        }

        void setSubModule( Module subModule ) {
            this.subModules.put( subModule.getModuleName(),subModule );
        }

        public HashMap<String, String> getAttributes() {
            return attributes;
        }

        void setAttributes( HashMap<String, String> attributes ) {
            this.attributes.putAll( attributes );
        }

        public String getAttribute( String param ){
            return attributes.getOrDefault( param, "" );
        }

    }

    protected Logger                 logger;
    private   HashMap<String,Module> params = new HashMap<>();

    //--------------- Access modules and params

    public Module getModule( String module ){
        if( params.containsKey( module ) ){
            return params.get( module );
        }
        Module _module = new Module();
        _module.setModuleName( module );
        return _module;
    }

    public String getParam( String param ){
        return params.get( "root" ).getParam( param );
    }

    public void setParam( String param, String value ){
        params.get( "root" ).params.put( param, value );
    }

    public void setModule( Module module ){
        params.put( module.getModuleName(), module );
    }

    //---------------

    public ConfigParser( Logger _logger, File confFile ) {

        if ( _logger == null ) {
            logger = LogManager.getLogger( this.getClass() );
        } else logger = _logger;

        this.parse( confFile );

    }

    private void parse( File confFile ) {

        DocumentBuilderFactory DocBuild = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder builder = DocBuild.newDocumentBuilder();
            if( confFile == null ){
                confFile = new File( "config" + File.separator + "config.xml" );
            }

            Document confXml = builder.parse( confFile );

            this.parseRootNode( confXml.getFirstChild() );

        } catch ( ParserConfigurationException | IOException | SAXException ex ) {
            logger.error( ex.getMessage() );
        }

    }

    private void parseRootNode( Node currentNode ) {

        int iChildNumber = currentNode.getChildNodes().getLength();
        NodeList rootChildList = currentNode.getChildNodes();

        Module rootModule = new Module();
        rootModule.setModuleName( currentNode.getNodeName() );
        rootModule.setAttributes( this.getAttributes( currentNode.getAttributes() ) );


        for( int iChild = 0; iChild < iChildNumber; iChild++ ){
            walkDOM( rootChildList.item( iChild ), rootModule );
        }

        HashMap<String, Module> subModules = new HashMap<>( rootModule.getSubModules() );
        rootModule.getSubModules().clear();

        params.put( currentNode.getNodeName(), rootModule );
        params.putAll( subModules );

    }

    private void walkDOM( Node currentNode, Module parentModule ){

        if ( currentNode.getNodeType() == Node.ELEMENT_NODE ) {

            //exit code
            if( currentNode.getNodeName().equals( "param" ) ){
                parentModule.setParams( this.getAttributes( currentNode.getAttributes() ) );
                return;
            }

            Module cModule = new Module();
            cModule.setModuleName( currentNode.getNodeName() );
            parentModule.setSubModule( cModule );
            cModule.setAttributes( this.getAttributes( currentNode.getAttributes() ) );

            int iChildNumber = currentNode.getChildNodes().getLength();
            NodeList nlChilds = currentNode.getChildNodes();
            for( int iChild = 0; iChild < iChildNumber; iChild++ ){
                walkDOM( nlChilds.item( iChild ), cModule );
            }

        }

    }

    private HashMap<String,String> getAttributes( NamedNodeMap nnm ) {

        HashMap<String, String> hmNodeAttr = new HashMap<>();

        if ( nnm != null && nnm.getLength() > 0 ) {

            String name;
            String value;

            for ( int iAttr = 0; iAttr < nnm.getLength(); iAttr++ ) {
                name = nnm.item( iAttr ).getNodeName();
                value = nnm.item( iAttr ).getNodeValue();
                hmNodeAttr.put( name, value );
            }

        } else {
//            logger.debug( "No attributes found" );
        }

        return hmNodeAttr;

    }


}
