package com.distocraft.dc5000.etl.xml3GPP32435DYN;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.Parser;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.distocraft.dc5000.etl.parser.ZipEntrySource;
import com.ericsson.eniq.common.ENIQEntityResolver;


/**
 * 3GPP TS 32.435 Parser <br>
 * <br>
 * Configuration: <br>
 * <br>
 * Database usage: Not directly <br>
 * <br>
 * <br>
 * Version supported: v 7.20 <br>
 * <br>
 * Copyright Ericsson 2008 <br>
 * <br>
 * $id$ <br>
 * 
 * <br>
 * <br>
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="4"><font size="+2"><b>Parameter Summary</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Name</b></td>
 * <td><b>Key</b></td>
 * <td><b>Description</b></td>
 * <td><b>Default</b></td>
 * </tr>
 * <tr>
 * <td>Vendor ID mask</td>
 * <td>3GPP32435DYNParser.vendorIDMask</td>
 * <td>Defines how to parse the vendorID</td>
 * <td>.+,(.+)=.+</td>
 * </tr>
 * <tr>
 * <td>Vendor ID from</td>
 * <td>3GPP32435DYNParser.readVendorIDFrom</td>
 * <td>Defines where to parse vendor ID (file/data supported)</td>
 * <td>data</td>
 * </tr>
 * <tr>
 * <td>Fill empty MOID</td>
 * <td>3GPP32435DYNParser.FillEmptyMOID</td>
 * <td>Defines whether empty moid is filled or not (true/ false)</td>
 * <td>true</td>
 * </tr>
 * <tr>
 * <td>Fill empty MOID style</td>
 * <td>3GPP32435DYNParser.FillEmptyMOIDStyle</td>
 * <td>Defines the style how moid is filled (static/inc supported)</td>
 * <td>inc</td>
 * </tr>
 * <tr>
 * <td>Fill empty MOID value</td>
 * <td>3GPP32435DYNParser.FillEmptyMOIDValue</td>
 * <td>Defines the value for the moid that is filled</td>
 * <td>0</td>
 * </tr>
 * </table> <br>
 * <br>
 * <table border="1" width="100%" cellpadding="3" cellspacing="0">
 * <tr bgcolor="#CCCCFF" class="TableHeasingColor">
 * <td colspan="2"><font size="+2"><b>Added DataColumns</b></font></td>
 * </tr>
 * <tr>
 * <td><b>Column name</b></td>
 * <td><b>Description</b></td>
 * </tr>
 * <tr>
 * <td>collectionBeginTime</td>
 * <td>contains the begin time of the whole collection</td>
 * </tr>
 * <tr>
 * <td>objectClass</td>
 * <td>contains the vendor id parsed from MOID</td>
 * </tr>
 * <tr>
 * <td>MOID</td>
 * <td>contains the measured object id</td>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td> contains the filename of the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>PERIOD_DURATION</td>
 * <td>contains the parsed duration of this measurement</td>
 * </tr>
 * <tr>
 * <td>DATETIME_ID</td>
 * <td>contains the counted starttime of this measurement</td>
 * </tr>
 * <tr>
 * <td>DC_SUSPECTFLAG</td>
 * <td>contains the suspected flag value</td>
 * </tr>
 * <tr>
 * <td>DIRNAME</td>
 * <td>Contains full path to the inputdatafile.</td>
 * </tr>
 * <tr>
 * <td>JVM_TIMEZONE</td>
 * <td>contains the JVM timezone (example. +0200) </td>
 * </tr>
 * <tr>
 * <td>vendorName</td>
 * <td>contains the vendor name</td>
 * </tr> 
 * <tr>
 * <td>fileFormatVersion</td>
 * <td>contains the version of file format</td>
 * </tr>
 * <tr>
 * <td>dnPrefix</td>
 * <td>contains the dn prefix</td>
 * </tr>
 * <tr>
 * <td>localDn</td>
 * <td>contains the local dn</td>
 * </tr>
 * <tr>
 * <td>managedElementLocalDn</td>
 * <td>contains the local dn of managedElement element</td>
 * </tr>
 * <tr>
 * <td>elementType</td>
 * <td>contains the element type</td>
 * </tr>
 * <tr>
 * <td>userLabel</td>
 * <td>contains the user label</td>
 * </tr>
 * <tr>
 * <td>swVersion</td>
 * <td>contains the software version</td>
 * </tr>
 * <tr>
 * <td>endTime</td>
 * <td>contains the granularity period end time</td>
 * </tr>
 * <tr>
 * <td>measInfoId</td>
 * <td>contains the measInfoId</td>
 * </tr>
 * <tr>
 * <td>jobId</td>
 * <td>contains the jobId</td>
 * </tr>
 * <tr>
 * <td>&lt;measType&gt; (amount varies based on measurement executed)</td>
 * <td>&lt;measValue&gt; (amount varies based on measurement executed)</td>
 * </tr>
 * </table> <br>
 * <br>
 * 
 * @author pylkkänen <br>
 *         <br>
 * 
 */


public class Xml3GPP32435DYNParser extends DefaultHandler implements Parser{

	// Virtual machine timezone unlikely changes during execution of JVM
	  private static final String JVM_TIMEZONE = (new SimpleDateFormat("Z")).format(new Date());
	  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	  private static String strPropertiesFilePath="/eniq/sw/conf/";
	  //private static String strPropertiesFilePath="C:/Users/ebrifol/Documents/Projects/Paser_Test_Bench_OLD/runtime/OCC/";
	  
	  private static String strCounterPropertiesFileName="3GPPDYN_CounterList.properties";
	  private static String strPropertiesFileName = "3gppdynmdcmapping.properties";
	  
	  private Logger log;

	  private String techPack;

	  private String setType;

	  private String setName;

	  private int status = 0;

	  private Main mainParserObject = null;

	  private String workerName = "";
	  
	  private SourceFile sourceFile;
	  
	  private MeasurementFile measFile = null;
	 	  
	  final private List errorList = new ArrayList();
	  
	  private String objectMask;

	  private String readVendorIDFrom;
	  	  
	  private String charValue;
	  private String fileFormatVersion;
	  private String collectionBeginTime;	  
	  private HashMap<String, String> counterNameList;	  
	  private Map counterKeyList;
	 
	  private String dyn1;
	  private String dyn2;
	  private String dyn3;
	  private String dyn4;
	  private Map measurement;
	  private String melocalDn;
	  private String jobId;
	  private String vendorName;
	  private String dnPrefix;
	  private String fsLocalDN;
	  private String elementType;
	  private String userLabel;
	  private String swVersion;
	  private String measInfoId;
	  private String granularityPeriodDuration;
	  private String granularityPeriodEndTime;
	  private String repPeriodDuration;
	  private HashMap dynmeasurementMap;
	  private String measObjLdn;
	  private String suspectFlag = "";
	  private String objectClass;	  
	  private String measTypeIndex;
	  private String measValueIndex;
	  private Set<String> counterlist;
	  private Properties mobjProperties = null;
	  private Map<String, String> counterMapping;
	  private String counterAppendFlag;
	  private boolean counterInLocalDn;
	  private Map counterInLoacalDnmeasurementMap;
	  private boolean firstTime; // reset the firstTime flag to handle a new sample file that may require a different parsing style
	  private HashMap<Object, Object> temp_compute_nova = new HashMap<Object, Object>();
	  private HashMap<String, String> temp_compute_nova_mapping = new HashMap<String, String>();
	  private boolean GySessionCountercounterflag=false;
	  //Minilink Nodes
	  private HashMap<String,String> counterNamesForMinilink;
	  private ArrayList measNameMap;
	  private boolean minilinkMeasFile = false;
	  private boolean dynamicVendorID = false;
	  private HashMap<String, String> measValues;

	  private String newMeLocalDN ="";
	  private String keyCounterValue;
	  /**
	   * Parameters for throughput measurement.
	   */
	  private long parseStartTime;
	  private long totalParseTime;
	  private long fileSize;
	  private int fileCount;
	  
	  
	  //variables introduced for consolidating dynamic counters
	  private Map<String, Map<String, Map<String, List <String>>>> consolidationKeyMap;
	  private Map<String, Map<String,String>> consolidationValueMap;
	  private Map<String, MeasurementFile> measMapForConsolidated = new HashMap<>();
	  private List<String> sortedCounterList;
	  
	  private boolean isConsolidateOnRightCounter;
	  private boolean isConsolidateOnLeftCounter;
	  private boolean requireConsolidation;
	  
	  private String spid="";
	  private Pattern spCounterPattern;
	  private static final String SP_ID = "SP_ID";
	  //End
	  
	  private static final String LEFT_COUNTER = "leftCounterName";
	  private static final String RIGHT_COUNTER = "rightCounterName";
	  private static final String OBJECT_CLASS = "objectClass";
	
	  
	  public int status() {
		    return status;
		  }

		  public List errors() {
		    return errorList;
		  }
	  
	  public void init(final Main main, final String techPack, final String setType, final String setName,
		      final String workerName) {
		    this.mainParserObject = main;
		    this.techPack = techPack;
		    this.setType = setType;
		    this.setName = setName;
		    this.status = 1;
		    this.workerName = workerName;

		    String logWorkerName = "";
		    if (workerName.length() > 0) {
		      logWorkerName = "." + workerName;
		    }

		    log = Logger.getLogger("etl." + techPack + "." + setType + "." + setName + ".parser.Xml3GPP32435DYNParser" + logWorkerName);
		  }
	  
	public void run() {

		try {

			this.status = 2;
			SourceFile sf = null;
			parseStartTime = System.currentTimeMillis();
			while ((sf = mainParserObject.nextSourceFile()) != null) {

				try {
					fileCount++;
					fileSize += sf.fileSize();
					mainParserObject.preParse(sf);
					parse(sf, techPack, setType, setName);
					mainParserObject.postParse(sf);
				} catch (Exception e) {
					mainParserObject.errorParse(e, sf);
				} finally {
					mainParserObject.finallyParse(sf);
				}
			}
			totalParseTime = System.currentTimeMillis() - parseStartTime;
			if (totalParseTime != 0) {
				log.info("Parsing Performance :: " + fileCount + " files parsed in " + totalParseTime
						+ " milliseconds, filesize is " + fileSize + " bytes and throughput : "
						+ (fileSize / totalParseTime) + " bytes/ms.");
			}
		} catch (Exception e) {
			// Exception catched at top level. No good.
			log.log(Level.WARNING, "Worker parser failed to exception", e);
			errorList.add(e);
		} finally {
			if (!measMapForConsolidated.isEmpty()) {
				closeMeasFiles(measMapForConsolidated.values());
				measMapForConsolidated.clear();
			}
			this.status = 3;
		}
	}
	  
	  public void parse(final SourceFile sf, final String techPack, final String setType, final String setName)
			  throws Exception {
			    this.measFile = null;
			    final long start = System.currentTimeMillis();
			    this.sourceFile = sf;
			    String fileNamePattern = sf.getProperty("fileNameFormat", "");
			    if (fileNamePattern != null && !fileNamePattern.isEmpty()) {
			    	 if (parseFileName(sf.getName(), fileNamePattern).isEmpty()) {
			    		 throw new Exception ("File name pattern mismatch");
			    	 }
			    }
			    objectMask = sf.getProperty("x3GPPDynParser.vendorIDMask", ".+,(.+)=.+");
			    readVendorIDFrom = sf.getProperty("x3GPPDynParser.readVendorIDFrom", "data");
			    // To check if the same counter name exist in 2 tables 
			    counterAppendFlag = sf.getProperty("x3GPPDynParser.counterAppendFlag", "false");
				//To append Extra value to the counter
			    keyCounterValue = sf.getProperty("x3GPPDynParser.keyCounterValue", "");
			    
			    isConsolidateOnRightCounter = ("true").equalsIgnoreCase(sf.getProperty("x3GPPDynParser.isConsolidateOnRightCounter","false"));
			    isConsolidateOnLeftCounter = ("true").equalsIgnoreCase(sf.getProperty("x3GPPDynParser.isConsolidateOnLeftCounter","false"));
			    if (isConsolidateOnLeftCounter && isConsolidateOnRightCounter) {
			    	consolidationKeyMap = new HashMap<>();
			    	consolidationValueMap = new HashMap<>();
			    	requireConsolidation = true;
			    	
			    	spCounterPattern = Pattern.compile(sf.getProperty("x3GPPDynParser.spCounterPattern",".*PER_SP"));
			    	spid="";
			    }
			    SAXParserFactory spf = SAXParserFactory.newInstance();
			    //spf.setValidating(validate);

			    SAXParser parser = spf.newSAXParser();
			    final XMLReader xmlReader = parser.getXMLReader();
			    xmlReader.setContentHandler(this);
			    xmlReader.setErrorHandler(this);

			    xmlReader.setEntityResolver(new ENIQEntityResolver(log.getName()));
			    final long middle = System.currentTimeMillis();
			    xmlReader.parse(new InputSource(sourceFile.getFileInputStream()));
			    final long end = System.currentTimeMillis();
			    log.log(Level.FINER, "Data parsed. Parser initialization took "+ (middle-start)+ " ms, parsing "+(end-middle)+ " ms. Total: "+(end-start)+ " ms.");
			  }
	  	   
		  /**
		   * Event handlers
		   */
	  public void startDocument() {
		  try{
			    DynamicParserCache cache = DynamicParserCache.getInstance(log);
			    counterlist = cache.getCounterList();
			    log.log(Level.FINEST, "counter list in parser: " + counterlist);
			    counterMapping = cache.getCounterMap();
			  	log.log(Level.FINEST, "counterMapping in parser : " + counterMapping); 
			}
			catch(Exception e){
				log.log(Level.INFO, "Problem getting info from DynamicParserCache:", e);
			}
		  }

	  public void endDocument() throws SAXException {
			    if (measFile != null) {
			      try {
			        measFile.close();
			      } catch (Exception e) {
			        log.log(Level.FINEST, "Worker parser failed to exception", e);
			        throw new SAXException("Error closing measurement file");
			      }
			    }
			  }
	  public void startElement(final String uri, final String name, final String qName, final Attributes atts)
				  throws SAXException {
		  
		  charValue = "";
		  
		  if (qName.equals("measCollecFile")) {
			  counterInLoacalDnmeasurementMap = new HashMap();
			  counterInLocalDn = false;
			  firstTime = true;
		  }else if (qName.equals("fileHeader")) {
		      this.fileFormatVersion = atts.getValue("fileFormatVersion");
		      this.vendorName = atts.getValue("vendorName");
		      this.dnPrefix = atts.getValue("dnPrefix");
		      
		    } else if (qName.equals("fileSender")) {
		      this.fsLocalDN = atts.getValue("localDn");
		      this.elementType = atts.getValue("elementType");
		    }else if (qName.equals("measCollec")) {
		      if (atts.getValue("beginTime") != null) {
		        // Begining of the file
		        collectionBeginTime = atts.getValue("beginTime");
		      } 		   
		    } else if (qName.equals("measInfo")) {
		    	counterNameList = new HashMap();
		    	counterKeyList = new HashMap();
		    	counterNamesForMinilink = new HashMap<String,String>();
		    }else if (qName.equals("measData")) {
		     	measNameMap = new ArrayList<String>();
		    } else if (qName.equals("measTypes")) {  	
		    }else if (qName.equals("managedElement")) {
		    	this.melocalDn = atts.getValue("localDn");
		    	this.userLabel = atts.getValue("userLabel");
		        this.swVersion = atts.getValue("swVersion");
		    	if (melocalDn == null)
		    		melocalDn = "";
		    	handleTAGmoid(melocalDn);
		    } else if (qName.equals("measInfo")) {
		        this.measInfoId = atts.getValue("measInfoId");
		    } else if (qName.equals("job")) {
		      this.jobId = atts.getValue("jobId");
		    } else if (qName.equals("granPeriod")) {
		      granularityPeriodDuration = getSeconds(atts.getValue("duration"));
		      granularityPeriodEndTime = atts.getValue("endTime");
		    } else if (qName.equals("repPeriod")) {
		      repPeriodDuration = getSeconds(atts.getValue("duration"));
		    } else if (qName.equals("measType")) {
		      measTypeIndex = atts.getValue("p");
		      //System.out.println("meastype p:"+measIndex);
		    }  else if (qName.equals("measResults")) {
		    	measValues = new HashMap<String, String>();
			} else if (qName.equals("measValue")) {	
		    	dynmeasurementMap = new HashMap();
		      //this.measurement = new HashMap();
		      this.suspectFlag = "";
		      this.measObjLdn = atts.getValue("measObjLdn");
		      measurement = new HashMap(); 
	          measurement.put("PERIOD_DURATION", granularityPeriodDuration);
	          log.log(Level.FINEST, "PERIOD_DURATION: "+ granularityPeriodDuration);
	          measurement.put("repPeriodDuration", repPeriodDuration);
	          log.log(Level.FINEST, "repPeriodDuration: "+ repPeriodDuration);
	          //DATETIME_ID calculated from end time
	          String begin = calculateBegintime();
	          if (begin != null) {
	            measurement.put("DATETIME_ID", begin);
	            log.log(Level.FINEST, "DATETIME_ID: "+ begin);
	          }
	          measurement.put("collectionBeginTime", collectionBeginTime);
	          log.log(Level.FINEST, "collectionBeginTime: "+ collectionBeginTime);
	          
	          measurement.put("filename", (sourceFile == null ? "dummyfile" : sourceFile.getName()));
	          log.log(Level.FINEST, "filename: "+ (sourceFile == null ? "dummyfile" : sourceFile.getName()));
	          measurement.put("JVM_TIMEZONE", JVM_TIMEZONE);
	          if (sourceFile instanceof ZipEntrySource) {
	        	  measurement.put("parentFile", ((ZipEntrySource)sourceFile).getParentName());
	          }
	          log.log(Level.FINEST, "JVM_TIMEZONE: "+ JVM_TIMEZONE);
	          measurement.put("DIRNAME", (sourceFile == null ? "dummydir" : sourceFile.getDir()));
	          log.log(Level.FINEST, "DIRNAME: "+ (sourceFile == null ? "dummydir" : sourceFile.getDir()));
	          //measurement.put("MOID", measObjLdn);    
	          measurement.put("measObjLdn", measObjLdn);  
	          log.log(Level.FINEST, "measObjLdn/MOID: "+ measObjLdn);
	          measurement.put(OBJECT_CLASS, objectClass);
	          log.log(Level.FINEST, "objectClass: "+ objectClass);
	          measurement.put("fileFormatVersion",fileFormatVersion);
	          log.log(Level.FINEST, "fileFormatVersion: "+ fileFormatVersion);
	          //collectionEndTime received so late, that migth not be used
	          measurement.put("endTime", granularityPeriodEndTime);
	          log.log(Level.FINEST, "endTime: "+ granularityPeriodEndTime);
	          measurement.put("jobId", jobId);
	          log.log(Level.FINEST, "jobId: "+ jobId);	          
	          measurement.put("vendorName", vendorName);
	          log.log(Level.FINEST, "vendorName: "+ vendorName);
	          measurement.put("dnPrefix", dnPrefix);
	          log.log(Level.FINEST, "dnPrefix: "+ dnPrefix);
	          measurement.put("fsLocalDN", fsLocalDN);
	          log.log(Level.FINEST, "fsLocalDN: "+ fsLocalDN);
	          measurement.put("elementType", elementType);
	          log.log(Level.FINEST, "elementType: "+ elementType);
	          measurement.put("userLabel", userLabel);
	          log.log(Level.FINEST, "userLabel: "+ userLabel);
	          measurement.put("swVersion", swVersion);
	          log.log(Level.FINEST, "swVersion: "+ swVersion);
	          measurement.put("measInfoId", measInfoId);
	          log.log(Level.FINEST, "measInfoId: "+ measInfoId);
	          measurement.put("melocalDn", melocalDn);
	          log.log(Level.FINEST, "melocalDn: "+ melocalDn);	
		    } else if (qName.equals("r")) { 
		    	measValueIndex = atts.getValue("p");
		    }
		  }
	  
	  
	  
	  public void endElement(final String uri, final String name, final String qName) throws SAXException {
		  // log.log(Level.FINEST, "endElement(" + uri + "," + name + "," + qName + "," + charValue + ")");

		if (qName.equals("measData")) {
			if(minilinkMeasFile){
				minilinkMeasFile =false;
			}
		}
		else if (qName.equals("measType")) {
			// to check if the counter name exist in the localDn of
			// managedElement tab
			if (firstTime && melocalDn.contains(charValue)) {
				log.log(Level.FINEST, "localDn contain the counter name");
				counterInLocalDn = true;
				firstTime = false;
			}
			if (counterInLocalDn) {
				newMeLocalDN = (melocalDn != null) ? this.melocalDn.substring(
						0, this.melocalDn.lastIndexOf('=') + 1) : "";
				melocalDn = newMeLocalDN.concat("Default");
				log.log(Level.FINEST,
						"melocalDn value after replacing with default  "
								+ melocalDn);

			}

			// to Split the counters based on dynamic
			// key(HOST_NAME,SERVER_NAME,CONFIGURATION_NAME,COUNTER_OWNER,ACTIVITY_TYPE,RESULT_TYPE)
			formatDYNcounter(charValue);
		}
		else if (qName.equals("measValue")) {
			try {
				measurement.put("DC_SUSPECTFLAG", suspectFlag);
		        log.log(Level.FINEST, "DC_SUSPECTFLAG: "+ suspectFlag);

				if (!counterInLocalDn){
					if(minilinkMeasFile){
						log.finest("Creating a dynamic measurement file for minilink under measvalue end tag ");
						CreateDynmeasurementForMinilink(dynmeasurementMap);
				  }else if (requireConsolidation){
					  createDynMeasForConsolidatedData();
				  } else {
					  CreateDynmeasurement(dynmeasurementMap);
				  }

				}
			} catch (Exception e) {
				log.log(Level.FINEST, "Error saving measurement data", e);
				throw new SAXException("Error saving measurement data: "
						+ e.getMessage(), e);
			}
		}
		 else if (qName.equals("measTypes")) {
		    	counterNamesForMinilink = (HashMap<String, String>) strToMap(charValue);
		    	if(dynamicVendorID){
		    		minilinkMeasFile = true;
		    		formatcounterforMinilink(counterNamesForMinilink);
		    	}else{
		    		log.finest("It is a normal file in 3GPP format...");
		    		minilinkMeasFile = true;
		    		counterNameList = counterNamesForMinilink ;
		    		measNameMap.add(counterNamesForMinilink.keySet());
		    	}
	 
		    }
		else if (qName.equals("r")) {
			if (!counterNameList.containsKey(measValueIndex)) {
				log.warning("Data contains one or more r-tags than mt-tags");
			}
			try {
				if (counterInLocalDn) {
					Dynmeasurement(counterInLoacalDnmeasurementMap);
				} else {
					Dynmeasurement(dynmeasurementMap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (qName.equals("suspect")) {
			this.suspectFlag = charValue;
		}
		else if (qName.equals("measResults")) {
			measValues =(HashMap<String, String>) strToMap(charValue);
			if (measValues.keySet().size() == counterNameList.keySet().size()) {
				if (measurement == null) {
				} else {
					try {
						log.finest("Calling the dyameasurementlink &&&&");
						DynmeasurementMinilink(dynmeasurementMap);
					} catch (Exception e) {
						log.info("Exception occured while writing to measfiler"+e.getMessage());
					}
				}
			}else {
				log.log(Level.INFO,"The size of keyset of values and the counter name is not same");
			}
		}else if (qName.equals("measInfo")) {
			
			
		}
		else if (qName.equals("measCollecFile")) {
			
			if (counterInLocalDn) {
				
				if(!dynamicVendorID){
					
				try {
					CreateDynmeasurement(counterInLoacalDnmeasurementMap);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			}
		}
		
		
	}
	  
	  public void formatDYNcounter(String counter){
		log.log(Level.FINEST, "counter information in file    : " + counter);
		Map<String, String> keyMap = new HashMap<>();
		String counterKeyName;
		String counterName = "";
		String leftCounterName = "";
		String rightCounterName = "";
		String resultCode = "";
		String host = "";
		String service = "";
		
		//String objClass = "";
		
		if (counter.contains("--")) {
			String[] CounetSplit = counter.split("--");
			dyn1 = CounetSplit[0];
			dyn2 = CounetSplit[1];
			dyn3 = CounetSplit[2];
			dyn4 = CounetSplit[3];
			counterKeyName = CounetSplit[4];
			
			keyMap.put("dyn1", dyn1);
			keyMap.put("dyn2", dyn2);
			keyMap.put("dyn3", dyn3);
			keyMap.put("dyn4", dyn4);
			log.log(Level.FINEST, "dyn1       : " + dyn1);
			log.log(Level.FINEST, "dyn2     : " + dyn2);
			log.log(Level.FINEST, "dyn3     : " + dyn3);
			log.log(Level.FINEST, "dyn4   : " + dyn4);
			log.log(Level.FINEST, "counter after  splitting --    : "+ counterKeyName);
			
		}else if(counter.contains(" ")){
			//SECTION ADDED TO SUPPORT CEE
			counterKeyName = counter;
//			KeyList.put("dyn1", "");
			
			if(counter.startsWith("HAProxy")){
				String[] CounetSplit = counter.split(" ");
				dyn1 = CounetSplit[2];
				
				counterKeyName = "";
				for(int i=0; i<CounetSplit.length; i++){
					if(i!=2 && i!=1){
						if(!counterKeyName.equals("")){
							counterKeyName = counterKeyName + "_";
						}
						counterKeyName = counterKeyName+CounetSplit[i];
					}
				}
				counterKeyName = counterKeyName.toUpperCase();

				keyMap.put("dyn1", dyn1);
			}
			
			else if(counter.startsWith("Incoming network") || counter.startsWith("Outgoing network")){
				String[] CounetSplit = counter.split(" ");
				dyn1 = CounetSplit[CounetSplit.length - 1];
				
				counterKeyName = "";
				for(int i=0; i<(CounetSplit.length-2); i++){
					if(!counterKeyName.equals("")){
						counterKeyName = counterKeyName +"_";
					}
					counterKeyName = counterKeyName +CounetSplit[i];
				}
				counterKeyName = counterKeyName.toUpperCase();

				keyMap.put("dyn1", dyn1);
			}
			
			else if(counter.startsWith("Percentage")){
				String[] CounetSplit = counter.split(" ");
				dyn1 = CounetSplit[CounetSplit.length - 1];
				keyMap.put("dyn1", dyn1);
				
				counterKeyName = "PERCENTAGE_USED_DISK_SPACE";
				
			}
			
			else if(counter.contains("for interface")){
				String[] CounetSplit = counter.split(" ");

				dyn1 = "";
				for(int i=3; i<=(CounetSplit.length-1); i++){
					if(!dyn1.equals("")){
						dyn1 = dyn1 +" ";
					}
					dyn1 = dyn1 +CounetSplit[i];
				}

				counterKeyName = CounetSplit[0].toUpperCase();
				keyMap.put("dyn1", dyn1);
			}
			
			else if(counter.startsWith("Disk")){
				String[] CounetSplit = counter.split(" ");
				dyn1 = CounetSplit[1];
				counterKeyName = CounetSplit[0]+"_"+CounetSplit[2]+"_"+CounetSplit[3];
				counterKeyName = counterKeyName.toUpperCase();
				
				keyMap.put("dyn1", dyn1);
			}
			
			else if(counter.startsWith("CPU idle time (5 min)")){
				counterKeyName = "CPU idle time 5 min";
			}
			
			else{
				String[] otherOptions = new String[]{"Free disk space on","Total disk space on", "Free inodes on", "Used disk space on", "Percentage of space used on disk"};
				for(String option : otherOptions){
					if(counter.startsWith(option)){
						String[] CounetSplit = counter.split(" ");
						
						counterKeyName = "";
						Boolean end = false;
						for(String part : CounetSplit){
							if(!part.equals("on") && !end){
								if(!counterKeyName.equals("")){
									counterKeyName = counterKeyName +"_";
								}
								counterKeyName = counterKeyName + part;
							}else{
								end = true;
							}
						}
						counterKeyName = counterKeyName.toUpperCase();
						
						if(CounetSplit[CounetSplit.length-1].equalsIgnoreCase("(percentage)")){
							dyn1 = CounetSplit[CounetSplit.length-2];
							counterKeyName = counterKeyName +"_PERCENTAGE";
						}else{
							dyn1 = CounetSplit[CounetSplit.length-1];
						}
						
						keyMap.put("dyn1", dyn1);
					}
				}
			}
			counterKeyName=counterKeyName.replaceAll(" ", "_");
			log.info("The countername after replacing the space is " + counterKeyName);
		}
		
		else if(counter.startsWith("instance:")){
			String[] CounetSplit = counter.split(":");
			dyn1 = CounetSplit[1];
			keyMap.put("dyn1", dyn1);
			
			counterKeyName = "INSTANCE_EXISTENCE_TYPE";
		}
		
		else{
			
			counterKeyName = counter;
		}
		if(counterKeyName.startsWith("GySessionCounter")){
			StringTokenizer st=new StringTokenizer(counterKeyName,".");
			String[] splittcounter=new String[st.countTokens()];
			int i=0;
			while(st.hasMoreTokens())
			{
				splittcounter[i]=st.nextElement().toString();
				i++;
			}
			keyMap.put("leftCounterName", splittcounter[0]);
			String ccrcounter = splittcounter[1];
			String ccrcounters[] = ccrcounter.split("-");
			String MSCC = ccrcounters[2]+splittcounter[2];
			keyMap.put("MSCC", MSCC);
			GySessionCountercounterflag=true;
			counterKeyName = ccrcounters[0]+"-"+ccrcounters[1]+"."+splittcounter[3];
		}
		if(!counterKeyName.toLowerCase().startsWith("haproxy") && !requireConsolidation)
		{
		for (String s : counterlist) {
			if (counterKeyName.contains(s)) {
				counterName = s;
				
				break;
			} else if (counterKeyName.contains(".") && s.contains(".") && counterName=="" ) {
				if (counterKeyName.substring(0, counterKeyName.lastIndexOf(".")).contains(s.substring(0, s.lastIndexOf("."))) 
						&& counterKeyName.substring(counterKeyName.lastIndexOf(".")).equals(s.substring(s.lastIndexOf(".")))) {
					// counterName = s;
					counterName = s.substring(0, s.lastIndexOf("."));
					log.log(Level.FINEST, "counterName : " + counterName);
				}
			}
		}
		}
		if (counterName != "" && !counterKeyName.equals(counterName) && !GySessionCountercounterflag) {

			if (!counterKeyName.startsWith(counterName))
				leftCounterName = counterKeyName.substring(0, counterKeyName.indexOf(counterName) - 1);

			if (leftCounterName == "")
				rightCounterName = counterKeyName.substring(counterName.length() + 1);
			else if (((leftCounterName + counterName).length() + 1) != counterKeyName.length())
				rightCounterName = counterKeyName.substring(counterName.length() + leftCounterName.length() + 2);

			if (rightCounterName.length() != 0) {
				//rightCounterName = removeDelimiter(rightCounterName, counterRightDelimiter, true);
				if ("true".equalsIgnoreCase(counterAppendFlag) && rightCounterName != "") {
					counterName = counterName + "dyn";
				}
				if (rightCounterName.contains(".")) {
					String extracCounter = rightCounterName.substring(rightCounterName.lastIndexOf("."));
					log.log(Level.FINEST, "extracCounter   : " + extracCounter);
					rightCounterName = rightCounterName.substring(0, rightCounterName.indexOf(extracCounter));
					if (rightCounterName.contains("-")) {

						String[] dynValuesSplit = rightCounterName.split("-");
						resultCode = dynValuesSplit[0];
						host = dynValuesSplit[1];

						keyMap.put("resultCode", resultCode);
						keyMap.put("host", host);
						log.log(Level.FINEST, "resultCode : " + resultCode);
						log.log(Level.FINEST, "host : " + host);
					} else if (rightCounterName.contains("/")) {

						rightCounterName = rightCounterName.substring(rightCounterName.indexOf("/"));
					} else {

						service = rightCounterName;

					}
					keyMap.put("service", service);
					log.log(Level.FINEST, "service : " + service);
					counterName = counterName + extracCounter;
					log.log(Level.FINEST, "Counter name after joining the right part of the counter : " + counterName);
				}
				keyMap.put(RIGHT_COUNTER, rightCounterName);
				//log.log(Level.FINEST,
						//"leftCounterName = " + leftCounterName + " :: rightCounterName = " + rightCounterName);
			}
			
			keyMap.put(LEFT_COUNTER, leftCounterName);
		} else {
			counterName = counterKeyName;
		}
		//To append extra value to the counter
			if(keyMap.containsValue(keyCounterValue)){
				counterName = counterName + keyCounterValue;
			}

		
		counterName = counterName.replace(" ", "_");
		if (requireConsolidation) {
			
			Map <String, String> resultMap = extractFromCounterName(counterKeyName, measTypeIndex);
			counterName = resultMap.get("counterName");
			keyMap.put(LEFT_COUNTER, resultMap.get(LEFT_COUNTER));
			keyMap.put(RIGHT_COUNTER, resultMap.get(RIGHT_COUNTER));
			
			if (spid.isEmpty()) {
				Matcher matcher = spCounterPattern.matcher(counterName);
				if(matcher.matches()) {
					spid = resultMap.get(RIGHT_COUNTER);
				}
			}
			
			log.log(Level.FINEST,"counterKeyName = " + counterKeyName + "counterName = " + counterName);		
		}
		if (counterName != null) {
			counterNameList.put(measTypeIndex, counterName);
		}
		counterKeyList.put(measTypeIndex, keyMap);
		
		
		
		log.log(Level.FINEST,"Counter name after spliting the key information   : "+ counterName);
		if(GySessionCountercounterflag){
			GySessionCountercounterflag = false;
		}
	}
	  
	private Map<String, String> extractFromCounterName(String counterKeyName,String measTypeIndex) {
		String counterName = null;
		Map<String, String> resultMap = new HashMap<>();
		for (String s : counterlist) {
			if (counterKeyName.contains(s)) {
				counterName = s;
				break;
			}
		}
		if (counterName != null) {
			String leftCounterName = counterKeyName.substring(0, counterKeyName.indexOf(counterName) - 1);
			String rightCounterName = counterKeyName.substring(counterName.length() + leftCounterName.length() + 2);
			log.log(Level.FINEST,
					"leftCounterName = " + leftCounterName + " :: rightCounterName = " + rightCounterName);
			
			resultMap.put(LEFT_COUNTER, leftCounterName);
			resultMap.put(RIGHT_COUNTER, rightCounterName);
			resultMap.put("counterName", counterName);
			
			String oc = getObjectClassFromCounterMap(counterName);
			log.log(Level.FINEST, "ObjectClass from Counter Map = " + oc);
			populateConsolidatedMap(oc, leftCounterName, rightCounterName, measTypeIndex);
		}
		
		return resultMap;
		
	}
	  
	private void populateConsolidatedMap(String objectClass, String leftCN, String rightCN, String index) {
		Map<String, Map<String, List<String>>> leftMap = consolidationKeyMap.get(objectClass);
		if (leftMap == null) {
			leftMap = new HashMap<>();
		}
		Map<String, List<String>> rightMap = leftMap.get(leftCN);
		if (rightMap == null) {
			rightMap = new HashMap<>();
		}
		List<String> indexList = rightMap.get(rightCN);
		if (indexList == null) {
			indexList = new ArrayList<>();
		}
		indexList.add(index);
		rightMap.put(rightCN, indexList);
		leftMap.put(leftCN, rightMap);
		consolidationKeyMap.put(objectClass, leftMap);
	}
	  
	private String removeDelimiter(String input, String delimiter, boolean fromFirst) {
		if (!delimiter.isEmpty()) {
			int delimIndex;
			if (fromFirst) {
				delimIndex = input.indexOf(delimiter);
			} else {
				delimIndex = input.lastIndexOf(delimiter);
			}
			if (delimIndex != -1) {
				input = new StringBuilder(input).deleteCharAt(delimIndex).toString();
			}
		}
		return input;
	}
	  
	  //Will return whether the counter is present in the counterlist file 
	  public void formatcounterforMinilink(final HashMap<String,String> counterValuesMapforMinilink) {
		  String countername="";
		  String key = "";
		  for (String indexvalue :  counterValuesMapforMinilink.keySet()){
		  for (final String checkCounter : this.counterlist) {
			String value = (String) counterValuesMapforMinilink.get(indexvalue);
			if (value.contains(checkCounter)){
		         countername = checkCounter;
		         key=value.replace(checkCounter, "");
		         break;
		      }
		   }
		  counterNameList.put(indexvalue, countername);
		  counterKeyList.put(indexvalue,key);
		  this.measNameMap.add(countername);
		}
	  }
	  
	  private String getObjectClassFromCounterMap(String counterName) {
		  String objectClass = null;
		  if (!counterMapping.isEmpty()) {
				try {
					objectClass = counterMapping.get(counterName);
				} catch (Exception e) {
					log.log(Level.INFO, "error while fetching from counterMapping : " + e.toString());
				}
			}
		  return objectClass;
	  }
	  
	  
	  public void Dynmeasurement(Map measurementMap) throws Exception{		  
		String objClass = "";
		Map measurementcopy;

		boolean notFound = false;
		HashMap keyValue = (HashMap) counterKeyList.get(measValueIndex);		
		if (keyValue.isEmpty()) {
			objClass = objectClass;
		} else {
			notFound=true;
		}
		
		if(notFound || objClass.equals("") || objClass.equals("null")){
			
			if (!counterMapping.isEmpty()) {
				try {
					objClass = counterMapping.get(counterNameList.get(measValueIndex));
				} catch (Exception e) {
					log.log(Level.INFO, "error while fetching from counterMapping:" + e.toString());
				}
			}
		}
		
		if(objClass != null && objClass.equals("compute_nova")){
			Map tmpMap;
			if(temp_compute_nova.containsKey(measObjLdn)){
				tmpMap = (Map) temp_compute_nova.get(measObjLdn);
				tmpMap.put(counterNameList.get(measValueIndex), charValue);
				if(keyValue.containsKey("dyn1")){
					tmpMap.putAll((Map) counterKeyList.get(measValueIndex));
					if(measurementMap.containsKey(temp_compute_nova_mapping.get(measObjLdn))){
						measurementMap.remove(temp_compute_nova_mapping.get(measObjLdn));
						temp_compute_nova_mapping.put(measObjLdn, objClass + keyValue + melocalDn);
					}
				}
				temp_compute_nova.put(measObjLdn, tmpMap);
			}else{
				tmpMap = new HashMap(measurement);
				tmpMap.put(OBJECT_CLASS, objClass);
				tmpMap.put(counterNameList.get(measValueIndex), charValue);
				tmpMap.putAll((Map) counterKeyList.get(measValueIndex));
				temp_compute_nova.put(measObjLdn, tmpMap);
				temp_compute_nova_mapping.put(measObjLdn, objClass + keyValue + melocalDn);
			}
			measurementMap.put(temp_compute_nova_mapping.get(measObjLdn), tmpMap);
		}
		
		
		else if (measurementMap != null && measurementMap.containsKey(objClass + keyValue + melocalDn)) { 
			
			measurementcopy = (Map) measurementMap.get(objClass + keyValue+ melocalDn);
			
			measurementcopy.put(counterNameList.get(measValueIndex), charValue);
			
		} else {
			     
			log.log(Level.FINEST, "objClass in Dynmeasurement Function "+ objClass);
			// System.out.println("objClass   : "+objClass);
			
			measurementcopy = new HashMap(measurement);
			measurementcopy.put(OBJECT_CLASS, objClass);
			
			String rValue = charValue;
			rValue = validationCheckforCounterVal(rValue);
			
			measurementcopy.put(counterNameList.get(measValueIndex), rValue);
			measurementcopy.putAll((Map) counterKeyList.get(measValueIndex));
			
			if (requireConsolidation) {
				consolidationValueMap.put(measValueIndex, measurementcopy);
			} else {
				measurementMap.put(objClass + keyValue + melocalDn, measurementcopy);
			}
			
		}

	}

	    
	  public  void DynmeasurementMinilink(HashMap measurementMap) {	
		  measurement.put("MOID", measObjLdn);    
		  if(!dynamicVendorID) {
			normalFileData(measurementMap);
		  } else{
			 for( Map.Entry<String,String> entry : counterNameList.entrySet()) {
		 		 String indexC = entry.getKey();
		 		  if(!counterMapping.isEmpty()) {
					counterMappingNotEmpty(measurementMap, indexC);
		 		  }else{
		 			  	log.warning("Counter is not present in the Counter Mapping property file .... Please check the property file");
		           }
	            }
		  }
		  log.finest("Size of the measurement map is outer map "+measurementMap.size()+" data- "+measurementMap);
        }

	private void counterMappingNotEmpty(HashMap<String, ArrayList<HashMap<String, String>>> measurementMap, String indexC) {
		String objClass;
		HashMap<String, String> tmp;
		ArrayList<HashMap<String, String>> measurementList;
		HashMap<String, String> innerMap;
			 objClass=counterMapping.get(counterNameList.get(indexC)); 
			 String keyCounter =  (String) counterKeyList.get(indexC);			  
		 	 tmp = new HashMap<>(measurement);
		 	 measurementList = new ArrayList<>();
		 	String valueAtIndexC = measValues.get(indexC);
		 	valueAtIndexC = validationCheckforCounterVal(valueAtIndexC);
		 	 if(measurementMap.isEmpty()) {
				emptyMeasMap(measurementMap, objClass, tmp, measurementList, indexC, keyCounter, valueAtIndexC);
		 	 }else {
		 		 if(!measurementMap.containsKey(objClass)){
		 			tmp.put(OBJECT_CLASS, objClass);
		 			tmp.put(counterNameList.get(indexC), valueAtIndexC);
		 			tmp.put("Trans_Mode", keyCounter);
		 			measurementList.add(tmp);
		 			measurementMap.put(objClass, measurementList);
		 			log.finest("outermap has some value and not empty.. outermap doenot containi key  "+objClass+"   adding to it newly ");
		 		 }else{
				  log.finest("outermap has some value and not empty..  outermap  containi key  "+objClass);
				  ArrayList list =  measurementMap.get(objClass);
				  list.listIterator();
				  Boolean flag = false;
				  for(int i=0; i<list.size();i++){
					  innerMap = (HashMap<String, String>) list.get(i);
				
					  String transMode =(String) innerMap.get("Trans_Mode");
					  if(transMode.equalsIgnoreCase(keyCounter)){
						 list.remove(i);
						 innerMap.put(counterNameList.get(indexC), valueAtIndexC);
						 list.add(innerMap);
						 measurementMap.put(objClass, list);
						 flag = true ; 
						 break;
					 }
				 }
				 if(Boolean.FALSE.equals(flag)){
					 log.finest("transMode and keycounter are not same in the inner map .. hence adding to it.. newly  "+keyCounter+ counterNameList.get(indexC));
					  ArrayList list2 =  measurementMap.get(objClass);
					  tmp.put(OBJECT_CLASS, objClass);
				  	tmp.put(counterNameList.get(indexC), valueAtIndexC);
				  	tmp.put("Trans_Mode", keyCounter);
				  	list2.add(tmp);
				  	measurementMap.put(objClass, list2);
				  	log.finest("Size of lilst for the objclass  "+objClass+"   "+list2.size());
				 }
			 }
		}
}

	private void normalFileData(HashMap<String, ArrayList<HashMap<String, String>>> measurementMap) {
		ArrayList<HashMap<String, String>> measurementList;
		HashMap<String, String> innerMap;
			  log.finest("It is a normal file... hence ading it directly to measfile");
			  innerMap = new HashMap<String, String>(measurement);
			  for(String counterIndex : counterNameList.keySet()){
				  counterNameList.get(counterIndex);
				  String valueAtCounterIndex = measValues.get(counterIndex);
				  valueAtCounterIndex = validationCheckforCounterVal(valueAtCounterIndex);
				  innerMap.put(counterNameList.get(counterIndex), valueAtCounterIndex);  
			  }
			  log.finest("Size of innermap "+innerMap.size());
			  measurementList = new ArrayList<HashMap<String,String>>();
			  measurementList.add(innerMap);
			  measurementMap.put(objectClass, measurementList);
	}

	private void emptyMeasMap(HashMap<String, ArrayList<HashMap<String, String>>> measurementMap, String objClass, HashMap<String, String> tmp,
			ArrayList<HashMap<String, String>> measurementList, String indexC, String keyCounter,
			String valueAtIndexC) {
			 tmp.put(OBJECT_CLASS, objClass);
			 tmp.put(counterNameList.get(indexC), valueAtIndexC);
			 tmp.put("Trans_Mode", keyCounter);
			 measurementList.add(tmp);
			 log.finest("outermap is empty.. hence adding it directly "+"    "+tmp.size());
			 measurementMap.put(objClass, measurementList); 
	}

	private String validationCheckforCounterVal(String valueAtIndexC) {
		if ((valueAtIndexC!= null)
				&& (valueAtIndexC.equalsIgnoreCase("NIL") || valueAtIndexC
						.trim().equalsIgnoreCase("") || valueAtIndexC.equalsIgnoreCase("NULL"))) {
			valueAtIndexC = "";
		}
		return valueAtIndexC;
	}
	  
	  
	  public void CreateDynmeasurement(Map measurementMap)throws Exception{	
		  		  
		  measFile = null;
			List keyList = new ArrayList(measurementMap.keySet());
			for(int index=0;index<keyList.size();index++){
				
				final String key = (String) keyList.get(index);
				final Map datarow = (Map) measurementMap.get(key);
				
				final String objClass = (String) datarow.get(OBJECT_CLASS);
				
				if(objClass != null){
					if(objClass.equals("compute_nova")){
						System.out.println();
						System.out.println("ObjClass: " + objClass);
						List temp = new ArrayList(datarow.keySet());
						System.out.println("print datarow");
						for(int i = 0; i < temp.size(); i++)
						{
							String k = (String) temp.get(i);
							String v = (String) datarow.get(k);
							System.out.println(k + "\t\t" + v);
						}
						System.out.println();
					}
				}
				
				log.log(Level.FINEST,"CreateDynmeasurement");
				log.log(Level.FINEST, "KEY:" + key + " ObjectClass:" + objClass);
				log.log(Level.FINEST, "sourceFile:" + sourceFile + "techPack:" + techPack + "setType:" + setType + "setName:" + setName + "workerName:" + workerName + "log:" + log);
		         measFile = Main.createMeasurementFile(sourceFile, objClass, techPack, setType, setName, workerName, log);          
			if (measFile != null) {
				if (datarow != null) {
					datarow.put("DC_SUSPECTFLAG", suspectFlag);
				}
				measFile.setData(datarow);
				measFile.saveData();
				log.log(Level.FINE, "Saved file");
			}	
				if (measFile != null) {
		            measFile.close();
		          }	       
		    
			}
		}
	  
	private void createDynMeasForConsolidatedData() throws Exception {
		log.log(Level.FINEST, "consolidation key Map : " + consolidationKeyMap);
		log.log(Level.FINEST, "consolidation value Map : " + consolidationValueMap);
		try {
			for (String objClass : consolidationKeyMap.keySet()) {
				Map<String, Map<String, List<String>>> leftMap = consolidationKeyMap.get(objClass);
				MeasurementFile measFile = Main.createMeasurementFile(sourceFile, objClass, techPack, setType, setName,
						workerName, log);
				try {
					for (String leftCounter : leftMap.keySet()) {
						Map<String, List<String>> rightMap = leftMap.get(leftCounter);
						Map<String, String> dataRow = new HashMap<>();
						for (String rightCounter : rightMap.keySet()) {
							List<String> indexList = rightMap.get(rightCounter);
							for (String index : indexList) {
								Map<String, String> tempMap = consolidationValueMap.get(index);
								if (tempMap != null) {
									dataRow.putAll(tempMap);// consolidation happens here
								} else {
									log.warning("No value present for the index : " + index);
								}
							}
							if (objClass != null && measFile != null && !dataRow.isEmpty()) {
								log.log(Level.FINEST, "tagId=" + measFile.getTagID() + " : sourceFile="
										+ sourceFile);
								dataRow.put("DC_SUSPECTFLAG", suspectFlag);
								if (spid != null && !spid.isEmpty()) {
									dataRow.put(SP_ID, spid);
								}
								
								measFile.setData(dataRow);
								log.log(Level.FINEST, "data to be saved : " + dataRow);
								measFile.saveData();
							}
						}
					}
				} catch (Exception e) {
					log.log(Level.WARNING, "Exception occurred while creating consolidated dynamic measurementfile : "
							+ e);
					e.printStackTrace();
					throw new Exception(e.getMessage());
				} finally {
					if (measFile != null) {
						measFile.close();
					}
				}
			}
		} finally {
			if (consolidationKeyMap != null) {
				consolidationKeyMap.clear();
			}
			if (consolidationValueMap != null) {
				consolidationValueMap.clear();
			}
		}
	}
	  
	  private void closeMeasFiles(Collection<MeasurementFile> measFiles) {
		  measFiles.forEach(file -> {
			try {
				if (file != null) {
					file.close();
				}
			} catch (Exception e) {
				log.warning("Exception while closing measurementfile , "+e.getMessage());
			}
		});
	  }
	  
	  
	  public void CreateDynmeasurementForMinilink(Map MeasdynmeasurementMap) throws Exception{	

		  HashMap<String, ArrayList<HashMap<String,String>>>  measMap = new HashMap<String, ArrayList<HashMap<String,String>>>();
			measMap.putAll(MeasdynmeasurementMap);
			log.finest("Minilink measfile data - "+measMap.keySet());
			for(String vendoroutput : measMap.keySet()){
				log.finest("==================="+vendoroutput+"===========");
				List<HashMap<String, String>> keyList =  measMap.get(vendoroutput);
				if(vendoroutput != null ){
						measFile = Main.createMeasurementFile(sourceFile, vendoroutput, techPack, setType, setName, workerName, log);
				}
				for(int index=0;index < keyList.size(); index++){		
					if(vendoroutput != null ){
						Map<String, String> dataRow;
						if (measFile != null) {
							dataRow = keyList.get(index);
							if (dataRow != null) {
								dataRow.put("DC_SUSPECTFLAG", suspectFlag);
							}
							measFile.setData(dataRow);
							log.finest("size of measfile for "+vendoroutput +"  is  "+measFile.getRowCount());
							measFile.saveData();
							log.log(Level.FINE,"Saved file");
						}
					}else{
							log.finest("No info has been found for objclass  "+vendoroutput);
						}
				}		
			}
			if ((measFile != null) && measFile.isOpen()) {
				measFile.close();
			}
	  }
			
	  private String calculateBegintime() {
		    String result = null;
		    try {
			      String granPeriodETime = granularityPeriodEndTime;
			      if (granPeriodETime.matches(".+[\\+|-]\\d\\d(:)\\d\\d")) {
			        granPeriodETime = granularityPeriodEndTime.substring(0,granularityPeriodEndTime.lastIndexOf(":")) + granularityPeriodEndTime.substring(granularityPeriodEndTime.lastIndexOf(":") + 1);
			      }
			      granPeriodETime = granPeriodETime.replaceAll("[.]\\d+","");
			      if(granPeriodETime.endsWith("Z")){
	
	                  // Kludge for handling IS0 8601 time format -
	                  // The time zone formats available to 'SimpleDateFormat' are not ISO8601 compliant
	                  // 'Z' is the zone designator for the zero UTC offset. Replace the last "Z" with "UTC"
	                  // so that the time string can be handled by SimpleDateFormat.parse()
	
	                  granPeriodETime=granPeriodETime.replaceAll("Z", "UTC");
			      }
			      String timeZone =  granPeriodETime.substring(granPeriodETime.length()-6);
			      Date end = simpleDateFormat.parse(granPeriodETime);
			      Calendar cal = Calendar.getInstance();
			      cal.setTime(end);
			      int period = Integer.parseInt(granularityPeriodDuration);
			      cal.add(Calendar.SECOND, - period);
			      result = simpleDateFormat.format(cal.getTime());
			      result += timeZone;

		    } catch (ParseException e) {
		      log.log(Level.WARNING, "Worker parser failed to exception", e);
		    } catch (NumberFormatException e) {
		      log.log(Level.WARNING, "Worker parser failed to exception", e);
		    } catch (NullPointerException e) {
		      log.log(Level.WARNING, "Worker parser failed to exception", e);
		    }
		    return result;
		  }  
	   
	  private void handleTAGmoid(String value) {
		    //  TypeClassID is determined from the moid
		    // of the first mv of the md

		    this.objectClass = "";
		    
		    // where to read objectClass (moid)
		    if ("file".equalsIgnoreCase(readVendorIDFrom)) {
		      // read vendor id from file
		      objectClass = parseFileName(sourceFile.getName(), objectMask);

		    } else if ("data".equalsIgnoreCase(readVendorIDFrom)) {
		      // if moid is empty and empty moids are filled.
		    	// read vendor id from data
		      objectClass = parseFileName(value, objectMask); 
		    } else if ("minilink".equalsIgnoreCase(readVendorIDFrom) ){
		    	String fileName =sourceFile.getName();
		    	if(fileName.contains("ethsoam")){
		    		//objectClass =  parseFileName(sourceFile.getName(), objectMask);
		    		objectClass = "ethsoam";
		    		log.finest("objectClass--> "+objectClass);
		    		dynamicVendorID = false;
		    	}else{
		    		objectClass = "MINI-LINK"; 
		    		dynamicVendorID = true;
		    	}
		    }
		  }
	  
	  /**
	   * Rips PT and S values off from the value.
	   * 
	   * @param value Contains the duration value
	   * @return the duration in seconds
	   */
	private String getSeconds(String value) {
		String result = null;
		if (value != null) {
			result = value.substring(2, value.indexOf('S'));
		}
		return result;
	}

	public String parseFileName(final String str, final String regExp) {

		final Pattern pattern = Pattern.compile(regExp);
		final Matcher matcher = pattern.matcher(str);

		if (matcher.matches()) {
			final String result = matcher.group(1);
			log.finest(" regExp (" + regExp + ") found from " + str + "  :"+ result);
			return result;
		} else {
			log.log(Level.FINEST,"String " + str + " doesn't match defined regExp "+ regExp);
		}
		return "";
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		for (int i = start; i < start + length; i++) {
			// If no control char
			if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t') {
				charValue += ch[i];
			}
		}
	}
    
	 //handle counters which are under a single <meastypes>
	 public Map strToMap(final String str) {
		 	log.log(Level.FINEST,"Adding all the counters/Values to a hashmap");
			final HashMap<String,String> frameMap = new HashMap();
			int index = 1;
			if (str != null) {
				log.finest("Actual string  "+str);
				 StringTokenizer triggerTokens = new StringTokenizer(str," ");  
				while (triggerTokens.hasMoreTokens()) {
					String value =triggerTokens.nextToken();
					String indexvalue=Integer.toString(index);
					frameMap.put(indexvalue, value);
					index++;
				}
			}
			log.log(Level.FINEST,"Size of the counters/Values from the hashmap read from the file from the starttag of meastypes "+frameMap.size());
			return frameMap;
		}

	  
}
