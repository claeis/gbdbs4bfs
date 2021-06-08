package ch.ehi.gbdbs4bfs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.logging.FileListener;
import ch.ehi.basics.logging.StdListener;
import ch.ehi.basics.settings.Settings;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.InhaltNatuerlichePersonGBType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasisid._2_1.PersonIdDefType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.AnmeldungType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.BergwerkType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.EigentumAnteilType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.GemeinschaftType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.GewoehnlichesMiteigentumType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.GewoehnlichesSDRType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.GrundstueckType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.InhaltGemeinschaftType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.InhaltJuristischePersonGBType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.InhaltPersonGBType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.JuristischePersonGBType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.KonzessionType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.LiegenschaftType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.MiteigentumsAnteilType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.NatuerlichePersonGBType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.PersonGBType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.PersonRefBegruendet;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.RechtType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.SelbstaendigesDauerndesRechtType;
import ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.StockwerksEinheitType;


public class Gbdbs4bfs {
    /** the main folder of program.
     */
    public static final String SETTING_APPHOME="ch.ehi.bfs4gbdbs.appHome";
    /** Name of the log file that receives the conversion results.
     */
    public static final String SETTING_LOGFILE = "ch.ehi.bfs4gbdbs.log";
    public static final Class JAXB_CONTEXT_PATH[]={
            ch.ehi.gbdbs4bfs.jaxb.gbdbsegvt._2_0.ObjectFactory.class,
            ch.ehi.gbdbs4bfs.jaxb.gbdbsegvt._2_1.ObjectFactory.class,
            ch.ehi.gbdbs4bfs.jaxb.gbdbs._2_0.ObjectFactory.class,
            ch.ehi.gbdbs4bfs.jaxb.gbdbs._2_1.ObjectFactory.class,
            ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_0.ObjectFactory.class,
            ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.ObjectFactory.class,
            ch.ehi.gbdbs4bfs.jaxb.gbdbsdatei._2_0.ObjectFactory.class,
            ch.ehi.gbdbs4bfs.jaxb.gbdbsdatei._2_1.ObjectFactory.class,
            ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_0.ext1.ObjectFactory.class,
            ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_0.ext2.ObjectFactory.class,
            ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.ext1.ObjectFactory.class,
            ch.ehi.gbdbs4bfs.jaxb.versioning._1_0.ObjectFactory.class,
            ch.ehi.gbdbs4bfs.jaxb.versioning._1_1.ObjectFactory.class,
            };
    public static final String soapNs = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String gbdbsNs = "http://schemas.terravis.ch/GBDBS/2.0";
    public static final String gbdbsDateiNs = "http://schemas.terravis.ch/GBDBS-Datei/2.0";
    public static final String gbdbs21Ns = "http://schemas.geo.admin.ch/BJ/TGBV/GBDBS/2.1";
    public static final String gbdbsTypen21Ns = "http://schemas.geo.admin.ch/BJ/TGBV/GBBasisTypen/2.1";
    public static final String gbdbsDatei21Ns = "http://schemas.geo.admin.ch/BJ/TGBV/GBDBS-Datei/2.1";
    private static final QName GBDBS_20 = new QName(gbdbsDateiNs,"GBDBS");
    private static final QName HEADERSECTION_20 = new QName(gbdbsDateiNs,"HEADERSECTION");
    private static final QName DATASECTION_20 = new QName(gbdbsDateiNs,"DATASECTION");
    private static final QName VOLLBESTAND_20 = new QName(gbdbsDateiNs,"Vollbestand");
    public static final QName GBDBS_21 = new QName(gbdbsDatei21Ns,"GBDBS");
    public static final QName HEADERSECTION_21 = new QName(gbdbsDatei21Ns,"HEADERSECTION");
    public static final QName DATASECTION_21 = new QName(gbdbsDatei21Ns,"DATASECTION");
    public static final QName VOLLBESTAND_21 = new QName(gbdbsDatei21Ns,"Vollbestand");
    private Marshaller ms = null;
    private ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.ObjectFactory of=null;
    private long personIdx=0;
    private Set<String> egbpids=new java.util.HashSet<String>();
	public static boolean convert(
			File fullFile,
			File reducedFile,
			Settings settings
		) {
		return new Gbdbs4bfs().doConversion(fullFile,reducedFile,settings);
	}
	/** main workhorse function.
	 */
	public boolean doConversion(
            File fullFile,
            File reducedFile,
            Settings settings
		) {
		if(fullFile==null){
			EhiLogger.logError("no Full-GBDBS file given");
			return false;
		}
        if(reducedFile==null){
            EhiLogger.logError("no Reduced-GBDBS file given");
            return false;
        }
		if(settings==null){
			settings=new Settings();
		}
		of=new ch.ehi.gbdbs4bfs.jaxb.gbbasistypen._2_1.ObjectFactory();
	    String logFilename=settings.getValue(Gbdbs4bfs.SETTING_LOGFILE);
		FileListener logfile=null;
		boolean ret=false;
		try{
			// setup logging of conversion results
			if(logFilename!=null){
				logfile=new FileListener(new java.io.File(logFilename));
				EhiLogger.getInstance().addListener(logfile);
			}
		    String appHome=settings.getValue(Gbdbs4bfs.SETTING_APPHOME);
		    		    
		    // give user important info (such as input files or program version)
			EhiLogger.logState(Main.APP_NAME+"-"+Main.getVersion());
			EhiLogger.logState("maxMemory "+java.lang.Runtime.getRuntime().maxMemory()/1024L+" KB");
			EhiLogger.logState("fullFile <"+fullFile.getPath()+">");
            EhiLogger.logState("reducedFile <"+reducedFile.getPath()+">");
		
			// process data files
            EhiLogger.logState("convert data...");
            try {
                pass1(fullFile);
                ret = pass2(fullFile, reducedFile);
                // check for errors
                if(StdListener.getInstance().hasSeenErrors()){
                    EhiLogger.logState("...conversion failed");
                }else{
                    EhiLogger.logState("...conversion done");
                    ret=true;
                }
            }catch(Throwable ex){
                EhiLogger.logError(ex);
                EhiLogger.logState("...conversion failed");
            }
		}finally{
			if(logfile!=null){
				logfile.close();
				EhiLogger.getInstance().removeListener(logfile);
				logfile=null;
			}
		}
		return ret;
	}
    private void pass1(File fullFile) throws XMLStreamException, IOException, JAXBException {
        XMLInputFactory xmlif=null;
        XMLEventFactory xmlef=null;
        Unmarshaller um=null;
        XMLEventReader xmlr = null;
        java.io.InputStream fullStream = null;
        try{
        	
            xmlif = XMLInputFactory.newInstance();
            xmlef = XMLEventFactory.newInstance();
              try {
                  JAXBContext jaxbContext = JAXBContext
                  .newInstance(JAXB_CONTEXT_PATH);
                  um = jaxbContext.createUnmarshaller();
              } catch (JAXBException e) {
                  throw new IllegalStateException(e);
              }
            fullStream = new java.io.BufferedInputStream(new java.io.FileInputStream(fullFile));
            xmlr = xmlif.createXMLEventReader(fullStream);
            XMLEvent ev = null;
            // move to the root element and check its name.
            do{
                ev=xmlr.nextEvent();
            }while(!(ev instanceof StartElement));
            if(!ev.asStartElement().getName().equals(GBDBS_21)) { 
                throw new JAXBException("not a GBDBS 2.1 transfer file (" +ev.asStartElement().getName()+")");
            }else{
                ev=xmlr.nextEvent();
                ev=skipSpacesAndGetNextEvent(xmlr, ev);
                if(ev.asStartElement().getName().equals(HEADERSECTION_21)) {
                    skip(xmlr);
                }
                ev=xmlr.nextEvent();
                ev=skipSpacesAndGetNextEvent(xmlr, ev);
                if(ev.asStartElement().getName().equals(DATASECTION_21)) {
                    ev=xmlr.nextEvent();
                    ev=skipSpacesAndGetNextEvent(xmlr, ev);
                    if(ev.asStartElement().getName().equals(VOLLBESTAND_21)) {
                        skipSpaces(xmlr);
                        while(xmlr.peek().isStartElement()) {
                            JAXBElement ele=(JAXBElement) um.unmarshal(xmlr);
                            if(ele.getValue() instanceof PersonGBType){
                                String gbdbsId=((PersonGBType)ele.getValue()).getNummer();
                                String egbpid=getEGBPID(gbdbsId);
                                egbpids.add(egbpid);
                            }
                            skipSpaces(xmlr);
                        }
                    }
                }
            }
        }finally {
            if(xmlr!=null) {
                try {
                    xmlr.close();
                } catch (XMLStreamException e) {
                }
                xmlr=null;
            }
            if(fullStream!=null) {
                try {
                    fullStream.close();
                } catch (IOException e) {
                }
                fullStream=null;
            }
        }
    }
    
    private boolean pass2(File fullFile, File reducedFile) throws XMLStreamException, IOException, JAXBException {
        boolean ret=false;
        XMLInputFactory xmlif=null;
        XMLOutputFactory xmlof = null;
        XMLEventFactory xmlef=null;
        Unmarshaller um=null;
        XMLEventReader xmlr = null;
        java.io.OutputStream reducedStream = null;
        java.io.InputStream fullStream = null;
        XMLEventWriter out = null;
        try{
            
            xmlif = XMLInputFactory.newInstance();
            xmlof = XMLOutputFactory.newInstance();
            xmlef = XMLEventFactory.newInstance();
              try {
                  JAXBContext jaxbContext = JAXBContext
                  .newInstance(JAXB_CONTEXT_PATH);
                  um = jaxbContext.createUnmarshaller();
                  ms = jaxbContext.createMarshaller();
                  ms.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
              } catch (JAXBException e) {
                  throw new IllegalStateException(e);
              }
            reducedStream = new java.io.BufferedOutputStream(new java.io.FileOutputStream(reducedFile));
            out = xmlof.createXMLEventWriter(reducedStream, "UTF-8");
            fullStream = new java.io.BufferedInputStream(new java.io.FileInputStream(fullFile));
            xmlr = xmlif.createXMLEventReader(fullStream);
            XMLEvent ev = null;
            // move to the root element and check its name.
            do{
                ev=xmlr.nextEvent();
            }while(!(ev instanceof StartElement));
            if(!ev.asStartElement().getName().equals(GBDBS_21)) { 
                EhiLogger.logError("not a GBDBS 2.1 transfer file (" +ev.asStartElement().getName()+")");
            }else{
                out.add(xmlef.createStartDocument());
                out.add(ev);
                ev=xmlr.nextEvent();
                ev=skipSpacesAndGetNextEvent(xmlr, ev);
                if(ev.asStartElement().getName().equals(HEADERSECTION_21)) {
                    out.add(ev);
                    skip(xmlr);
                    out.add(xmlef.createEndElement(HEADERSECTION_21,null));
                }
                ev=xmlr.nextEvent();
                ev=skipSpacesAndGetNextEvent(xmlr, ev);
                if(ev.asStartElement().getName().equals(DATASECTION_21)) {
                    out.add(ev);
                    ev=xmlr.nextEvent();
                    ev=skipSpacesAndGetNextEvent(xmlr, ev);
                    if(ev.asStartElement().getName().equals(VOLLBESTAND_21)) {
                        out.add(ev);
                        skipSpaces(xmlr);
                        while(xmlr.peek().isStartElement()) {
                            JAXBElement ele=(JAXBElement) um.unmarshal(xmlr);
                            convertGbdbsEle(ele,out);
                            skipSpaces(xmlr);
                        }
                        out.add(xmlef.createEndElement(VOLLBESTAND_21,null));
                    }
                    out.add(xmlef.createEndElement(DATASECTION_21,null));
                }
                out.add(xmlef.createEndElement(GBDBS_21,null));
                out.add(xmlef.createEndDocument());
            }
        }finally {
            if(out!=null) {
                try {
                    out.close();
                } catch (XMLStreamException e) {
                }
                out=null;
            }
            if(xmlr!=null) {
                try {
                    xmlr.close();
                } catch (XMLStreamException e) {
                }
                xmlr=null;
            }
            if(fullStream!=null) {
                try {
                    fullStream.close();
                } catch (IOException e) {
                }
                fullStream=null;
            }
            if(reducedStream!=null) {
                try {
                    reducedStream.close();
                } catch (IOException e) {
                }
                reducedStream=null;
            }
        }
        return ret;
    }
    private void convertGbdbsEle(JAXBElement ele,XMLEventWriter out) throws JAXBException {
        if(ele.getValue() instanceof GrundstueckType){
            convertGrundstueck(ele,out);
        }else if(ele.getValue() instanceof RechtType){
            convertRecht(ele,out);
        }else if(ele.getValue() instanceof PersonGBType){
            convertPerson(ele,out);
        }else if(ele.getValue() instanceof AnmeldungType){
        }else{
            throw new IllegalArgumentException("unexpected type "+ele.getValue().getClass());
        }
    }
    private void convertPerson(JAXBElement<? extends PersonGBType> ele,XMLEventWriter out) throws JAXBException {
        personIdx++;
        JAXBElement<? extends PersonGBType> newele=null;
        if(ele.getValue() instanceof GemeinschaftType) {
            GemeinschaftType val=new GemeinschaftType();
            for(PersonRefBegruendet mitglied:((GemeinschaftType)ele.getValue()).getMitglieder()) {
                String nummer=mitglied.getRef();
                PersonIdDefType berechtigteId = parseGbdbsPersId(nummer);
                berechtigteId.setKantPersNr(null);
                nummer=toString(berechtigteId);
                mitglied.setRef(nummer);
                val.getMitglieder().add(mitglied);
            }
            newele=of.createGemeinschaft(val);
        }else if(ele.getValue() instanceof JuristischePersonGBType) {
            JuristischePersonGBType val=new JuristischePersonGBType();
            newele=of.createJuristischePersonGB(val);
            val.setPersonStamm(ele.getValue().getPersonStamm());
        }else if(ele.getValue() instanceof NatuerlichePersonGBType) {
            NatuerlichePersonGBType val=new NatuerlichePersonGBType();
            newele=of.createNatuerlichePersonGB(val);
        }
        if(newele!=null) {
            for(JAXBElement<? extends InhaltPersonGBType> inhaltEle:ele.getValue().getInhaltPersonGB()) {
                JAXBElement<? extends InhaltPersonGBType> newinhalt=null;
                final InhaltPersonGBType inhalt = inhaltEle.getValue();
                if(inhalt instanceof InhaltGemeinschaftType) {
                    InhaltGemeinschaftType val=new InhaltGemeinschaftType();
                    val.setVonEGBTBID(inhalt.getVonEGBTBID());
                    val.setVonTagebuchDatumZeit(inhalt.getVonTagebuchDatumZeit());
                    val.setVonTagebuchNummer(inhalt.getVonTagebuchNummer());
                    val.setVonIdx(inhalt.getVonIdx());
                    val.setBisEGBTBID(inhalt.getBisEGBTBID());
                    val.setBisTagebuchDatumZeit(inhalt.getBisTagebuchDatumZeit());
                    val.setBisTagebuchNummer(inhalt.getBisTagebuchNummer());
                    val.setBisIdx(inhalt.getBisIdx());
                    val.setName(((InhaltGemeinschaftType) inhalt).getName());
                    val.setArt(((InhaltGemeinschaftType) inhalt).getArt());
                    newinhalt=of.createInhaltGemeinschaft(val);
                }else if(inhalt instanceof InhaltJuristischePersonGBType) {
                    InhaltJuristischePersonGBType val=new InhaltJuristischePersonGBType();
                    val.setVonEGBTBID(inhalt.getVonEGBTBID());
                    val.setVonTagebuchDatumZeit(inhalt.getVonTagebuchDatumZeit());
                    val.setVonTagebuchNummer(inhalt.getVonTagebuchNummer());
                    val.setVonIdx(inhalt.getVonIdx());
                    val.setBisEGBTBID(inhalt.getBisEGBTBID());
                    val.setBisTagebuchDatumZeit(inhalt.getBisTagebuchDatumZeit());
                    val.setBisTagebuchNummer(inhalt.getBisTagebuchNummer());
                    val.setBisIdx(inhalt.getBisIdx());
                    InhaltJuristischePersonGBType jp=(InhaltJuristischePersonGBType)inhalt;
                    val.setUID(jp.getUID());
                    val.setNameFirma(jp.getNameFirma());
                    val.setRechtsformStichwort(jp.getRechtsformStichwort());
                    val.setRechtsformZusatz(jp.getRechtsformZusatz());
                    val.setSitz(jp.getSitz());
                    val.setFirmennummer(jp.getFirmennummer());
                    newinhalt=of.createInhaltJuristischePersonGB(val);
                }else if(inhalt instanceof InhaltNatuerlichePersonGBType) {
                    InhaltNatuerlichePersonGBType val=new InhaltNatuerlichePersonGBType();
                    val.setVonEGBTBID(inhalt.getVonEGBTBID());
                    val.setVonTagebuchDatumZeit(inhalt.getVonTagebuchDatumZeit());
                    val.setVonTagebuchNummer(inhalt.getVonTagebuchNummer());
                    val.setVonIdx(inhalt.getVonIdx());
                    val.setBisEGBTBID(inhalt.getBisEGBTBID());
                    val.setBisTagebuchDatumZeit(inhalt.getBisTagebuchDatumZeit());
                    val.setBisTagebuchNummer(inhalt.getBisTagebuchNummer());
                    val.setBisIdx(inhalt.getBisIdx());
                    InhaltNatuerlichePersonGBType np=(InhaltNatuerlichePersonGBType)inhalt;
                    val.setName(np.getName());
                    val.setVornamen(np.getVornamen());
                    val.setGeburtsjahr(np.getGeburtsjahr());
                    val.setGeburtsmonat(np.getGeburtsmonat());
                    val.setGeburtstag(np.getGeburtstag());
                    //val.setGeschlecht(np.getGeschlecht());
                    val.setHeimatort(np.getHeimatort());
                    val.setStaatsangehoerigkeit(np.getStaatsangehoerigkeit());

                    newinhalt=of.createInhaltNatuerlichePersonGB(val);
                }
                newele.getValue().getInhaltPersonGB().add(newinhalt);
            }
            String nummer=ele.getValue().getNummer();
            PersonIdDefType berechtigteId = parseGbdbsPersId(nummer);
            if(egbpids.contains(berechtigteId.getEGBPID())) {
                berechtigteId.setKantPersNr(null);
                nummer=toString(berechtigteId);
            }
            newele.getValue().setNummer(nummer);
            ms.marshal(newele, out);
        }
    }
    private void convertRecht(JAXBElement<? extends RechtType> value,XMLEventWriter out) throws JAXBException {
        if(value.getValue() instanceof EigentumAnteilType) {
            EigentumAnteilType eig=(EigentumAnteilType)value.getValue();
            String berechtigteGbdbsId=eig.getBerechtigte();
            PersonIdDefType berechtigteId = parseGbdbsPersId(berechtigteGbdbsId);
            if(egbpids.contains(berechtigteId.getEGBPID())) {
                berechtigteId.setKantPersNr(null);
                eig.setBerechtigte(toString(berechtigteId));
            }
            ms.marshal(value, out);
        }
    }
    private void convertGrundstueck(JAXBElement<? extends GrundstueckType> ele,XMLEventWriter out) throws JAXBException {
        JAXBElement<? extends GrundstueckType> newele=null;
        if(ele.getValue() instanceof BergwerkType) {
            BergwerkType val=new BergwerkType();
            newele=of.createBergwerk(val);
        }else if(ele.getValue() instanceof LiegenschaftType) {
            LiegenschaftType val=new LiegenschaftType();
            newele=of.createLiegenschaft(val);
        }else if(ele.getValue() instanceof MiteigentumsAnteilType) {
            if(ele.getValue() instanceof GewoehnlichesMiteigentumType) {
                GewoehnlichesMiteigentumType val=new GewoehnlichesMiteigentumType();
                newele=of.createGewoehnlichesMiteigentum(val);
            }else if(ele.getValue() instanceof StockwerksEinheitType) {
                StockwerksEinheitType val=new StockwerksEinheitType();
                newele=of.createStockwerksEinheit(val);
            }else {
                MiteigentumsAnteilType val=new MiteigentumsAnteilType();
                newele=of.createMiteigentumsAnteil(val);
            }
        }else if(ele.getValue() instanceof SelbstaendigesDauerndesRechtType) {
            if(ele.getValue() instanceof GewoehnlichesSDRType) {
                GewoehnlichesSDRType val=new GewoehnlichesSDRType();
                newele=of.createGewoehnlichesSDR(val);
            }else if(ele.getValue() instanceof KonzessionType) {
                KonzessionType val=new KonzessionType();
                newele=of.createKonzession(val);
            }else {
                SelbstaendigesDauerndesRechtType val=new SelbstaendigesDauerndesRechtType();
                newele=of.createSelbstaendigesDauerndesRecht(val);
            }
        }
        if(newele!=null) {
            newele.getValue().setNummer(ele.getValue().getNummer());
            newele.getValue().setIstKopie(ele.getValue().isIstKopie());
            newele.getValue().setGemeinde(ele.getValue().getGemeinde());
            ms.marshal(newele, out);
        }
    }
    private String getEGBPID(String gbdbsId) {
        String egbpid =parseGbdbsPersId(gbdbsId).getEGBPID();
        return egbpid;
    }
    static public PersonIdDefType parseGbdbsPersId(String gbdbsId) {
        PersonIdDefType ret = new PersonIdDefType();
        String idv[] = gbdbsId.split(":", 6);
        String egbpid = idv[0].trim();
        if (egbpid.length() > 0) {
            ret.setEGBPID(idv[0]);
        }
        String kantPersNr = idv[1].trim();
        if (kantPersNr.length() > 0) {
            ret.setKantPersNr(kantPersNr);
        }
        String uid = idv[2].trim();
        if (uid.length() > 0) {
            ret.setUID(uid);
        }
        String ahvn13 = idv[3].trim();
        if (ahvn13.length() > 0) {
            ret.setAHVN13(ahvn13);
        }
        String localId = idv[4].trim();
        if (localId.length() > 0) {
            ret.setLocalId(localId);
        }
        return ret;
    }
    public static String toString(PersonIdDefType gbdbsId) {
        return (gbdbsId.getEGBPID()!=null?gbdbsId.getEGBPID():"")
        +":"+(gbdbsId.getKantPersNr()!=null?gbdbsId.getKantPersNr():"")
        +":"+(gbdbsId.getUID()!=null?gbdbsId.getUID():"")
        +":"+(gbdbsId.getAHVN13()!=null?gbdbsId.getAHVN13():"")
        +":"+(gbdbsId.getLocalId()!=null?gbdbsId.getLocalId():"")
        ;
    }
    private XMLEvent skipSpacesAndGetNextEvent(XMLEventReader reader,XMLEvent event) throws XMLStreamException{
        while(event.isCharacters() || event.getEventType()==XMLEvent.COMMENT){
            if(event.isCharacters() && event.getEventType()!=XMLEvent.COMMENT) {
                Characters characters = (Characters) event;
                if(!characters.isWhiteSpace()){
                    throw new IllegalStateException("unexpected non-whitespace in XML");
                }
            }
            event=reader.nextEvent();
        }
        return event;
    }
    private void skipSpaces(XMLEventReader reader) throws XMLStreamException{
        XMLEvent event=reader.peek();
        while(event.isCharacters() || event.getEventType()==XMLEvent.COMMENT){
            if(event.isCharacters() && event.getEventType()!=XMLEvent.COMMENT) {
                Characters characters = (Characters) event;
                if(!characters.isWhiteSpace()){
                    throw new IllegalStateException("unexpected non-whitespace in XML");
                }
            }
            reader.nextEvent();
            event=reader.peek();
        }
    }
    static void skip(XMLEventReader parser) throws XMLStreamException {
        int inHeader = 0;
        for (XMLEvent event = parser.nextEvent(); !event.isEndDocument(); event = parser
                .nextEvent()) {
            switch (event.getEventType()) {
            case XMLStreamConstants.START_ELEMENT:
                inHeader++;
                break;
            case XMLStreamConstants.END_ELEMENT:
                inHeader--;
                break;
            } // end switch
            if (inHeader < 0) {
                break;
            }
        } // end while
    }

}
