/*
 *  Copyright 2011 GT webMarque Ltd
 * 
 *  This file is part of JasperEmail.
 *
 *  JasperEmail is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  JasperEmail is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JasperEmail.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ilopez.jasperemail;

import com.ilopez.jasperemail.OptionValues.OutputType;
import com.ilopez.jasperemail.OptionValues.ParamType;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperFillManager;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Transport;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.activation.FileDataSource;
import javax.activation.DataSource;
import javax.activation.DataHandler;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Properties;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.PasswordAuthentication;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.io.FilenameUtils;

/**
 * Copyright Israel Lopez 2014
 * Command line application to generate reports from JasperReports XML report
 * definition files, e.g. those designed with iReport
 
 One or more report definitions can be specified on the command line and a PDF
 or HTML file will be generated from each. If email details are specified,
 then they'll be attached to an email and sent. If not, they'll just be
 written to disk
 
 Example Usage: java -jar JasperEmail.jar -dbtype postgresql -dbname
 accountsdb -dbuser fd -dbpass secret -reports p_and_l.jrxml,sales.jrxml
 -folder /var/www/financial/ -emailto directors@gtwm.co.uk -emailfrom
 accounts@gtwm.co.uk -emailsubject FinancialReports -output pdf
 * 
 * @author Oliver Kohll
 * @version 1.1.3
 * @see http://www.agilebase.co.uk/opensource
 */
public class JasperEmail {

	public JasperEmail() {
	}

	/**
         * @author Israel Lopez
	 * @author Charly Clairmont
	 */
	public void generateOutputReport(String reportDefinitionFile, OptionValues.OutputType outputType, String outputFileName,
			String jdbcClass, String jdbcURL, String databaseUsername, String databasePassword, 
                        Map parameters) throws FileNotFoundException, JRException,
			SQLException, IOException, ClassNotFoundException, Exception {

		JasperPrint print = generateReport(reportDefinitionFile, jdbcClass, jdbcURL, databaseUsername, databasePassword, jdbcURL, parameters);                

                if (outputType == OptionValues.OutputType.PDF) {                    
                    JRPdfExporter e = new JRPdfExporter();
                    e.setParameters(parameters);
                    e.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFileName);
                    e.exportReport();
                  } else if (outputType == OptionValues.OutputType.XLS) {                    
                    JRXlsExporter e = new JRXlsExporter();
                    e.setParameters(parameters);
                    e.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFileName);
                  }
                   else if (outputType == OptionValues.OutputType.XLSX) {                    
                    JRXlsxExporter e = new JRXlsxExporter();
                    e.setParameters(parameters);
                    e.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFileName);
                  }else if (outputType == OptionValues.OutputType.CSV) {                    
                    JRCsvExporter e = new JRCsvExporter();
                    e.setParameters(parameters);
                    e.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFileName);
                  } else if (outputType == OptionValues.OutputType.HTML) {                    
                    JRHtmlExporter e = new JRHtmlExporter();
                    e.setParameters(parameters);
                    e.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFileName);
                  } else {
                    throw new Exception("Unsupported output type");
                  }
		
                Logger.getLogger(JasperEmail.class.getName()).log(Level.INFO , null, "Output Written: " + outputFileName);
	}

	/**
	 * Perform a liberal check of boolean representation. Any string that starts
	 * with 't', 'y' or '1', case-insensitively, will return true - anything
	 * else, false, including null. Less stringent than Boolean.valueOf() and
	 * will never throw an Exception
	 */
	private boolean valueRepresentsBooleanTrue(String value) {
		if (value == null) {
			return false;
		}
		if (value.toLowerCase().startsWith("t")
				|| value.toLowerCase().startsWith("y") || value.startsWith("1")) {
			return true;
		} else {
			return false;
		}
	}

	private Map prepareParameters(String parametersString) {
		Map parameters = new HashMap();
		if (parametersString == null) {
			return parameters;
		}
		List<String> parameterList = Arrays.asList(parametersString.split(","));
		for (String parameter : parameterList) {
			String paramName = parameter.split("=", 2)[0];
			String paramTypeAndValue = parameter.split("=", 2)[1];
			String paramTypeString = paramTypeAndValue.split(":", 2)[0];
			ParamType paramType = ParamType.valueOf(paramTypeString.toUpperCase());
			String paramValueString = paramTypeAndValue.split(":", 2)[1];
			switch (paramType) {
			case BOOLEAN:
				parameters.put(paramName, valueRepresentsBooleanTrue(paramValueString));
				break;
			case STRING:
				parameters.put(paramName, paramValueString);
				break;
			case DOUBLE:
				parameters.put(paramName, Double.valueOf(paramValueString));
				break;
			case INTEGER:
				parameters.put(paramName, Integer.valueOf(paramValueString));
				break;
			}
		}		
                Logger.getLogger(JasperEmail.class.getName()).log(Level.INFO , null, "Report Parameters Are: " + parameters);
		return parameters;
	}

	private JasperPrint generateReport(String reportDefinitionFile, String jdbcClass,
			String jdbcURL, String databaseUsername, String databasePassword, String dbHost,
			Map parameters) throws FileNotFoundException, JRException, SQLException,
			ClassNotFoundException, Exception {
            // Get the file Extension of the RDF
            String ext = FilenameUtils.getExtension(reportDefinitionFile).toUpperCase();
            JasperReport report;
            if(ext.contains("JASPER")){
                // If it is a JASPER file, then we do not need to compile it, so load it in.
                report = (JasperReport)JRLoader.loadObject((new File(reportDefinitionFile)));
            }else if(ext.contains("JRXML")){
                // If it is a JRXML file we will need to compile the report
                Logger.getLogger(JasperEmail.class.getName()).log(Level.INFO , null, "Reading Input File " + reportDefinitionFile);
                InputStream input = new FileInputStream(new File(reportDefinitionFile));
                JasperDesign design = JRXmlLoader.load(input);
                report = JasperCompileManager.compileReport(design);		
                Logger.getLogger(JasperEmail.class.getName()).log(Level.INFO , null, "Compiled Report From File " + reportDefinitionFile);
            }else{
                throw new Exception("Unknown Jasper Report Extensions - JRXML or JASPER allowed only.");
            }

            // Connect to the Database with the supplied jdbcClass and jdbcURL, and Username/Password.
            Class.forName(jdbcClass);
            String connectionStatement = jdbcURL;
            Logger.getLogger(JasperEmail.class.getName()).log(Level.INFO , "Connecting to Database " + jdbcURL);
            Properties connectionProperties = new Properties();
            if (databaseUsername != null) {
                    connectionProperties.setProperty("user", databaseUsername);
            }
            if (databasePassword != null) {
                    connectionProperties.setProperty("password", databasePassword);
            }

            Connection conn = DriverManager.getConnection(connectionStatement, connectionProperties);

            Logger.getLogger(JasperEmail.class.getName()).log(Level.INFO ,"Database Connected " + jdbcURL);
            conn.setAutoCommit(false);
            // run report and write output
            JasperPrint print = JasperFillManager.fillReport(report, parameters, conn);
            Logger.getLogger(JasperEmail.class.getName()).log(Level.INFO , "Jasper Print Prepared");
            conn.close();
            return print;
	}

	public void emailReport(String emailHost, final String emailUser, final String emailPass,
			Set<String> emailRecipients, String emailSender, String emailSubject,
			List<String> attachmentFileNames, Boolean smtpauth,  OptionValues.SMTPType smtpenc , Integer smtpport) throws MessagingException {
            
                Logger.getLogger(JasperEmail.class.getName()).log(Level.INFO ,  
                        emailHost + " " + emailUser 
                         + " " + emailPass  + " " +  emailSender  + " " +  emailSubject
                         + " " + smtpauth  + " " +  smtpenc  + " " + smtpport
                );
		Properties props = new Properties();
                
		
                // Setup Email Settings
		props.setProperty("mail.smtp.host", emailHost);
                props.setProperty("mail.smtp.port",smtpport.toString());
		props.setProperty("mail.smtp.auth", smtpauth.toString());
                
                if(smtpenc == OptionValues.SMTPType.SSL){
                    // SSL settings
                    props.put("mail.smtp.socketFactory.port", smtpport.toString() );
                    props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
                }else if(smtpenc == OptionValues.SMTPType.TLS){
                    // TLS Settings
                    props.put("mail.smtp.starttls.enable", "true");
                }else{
                    // Plain
                }
                
                
                
                
                // Setup and Apply the Email Authentication
		
		Session mailSession = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(emailUser,emailPass);
                    }
                });
		
                MimeMessage message = new MimeMessage(mailSession);
		message.setSubject(emailSubject);
		for (String emailRecipient : emailRecipients) {
			Address toAddress = new InternetAddress(emailRecipient);
			message.addRecipient(Message.RecipientType.TO, toAddress);
		}
		Address fromAddress = new InternetAddress(emailSender);
		message.setFrom(fromAddress);
		// Message text
		Multipart multipart = new MimeMultipart();
		BodyPart textBodyPart = new MimeBodyPart();
		textBodyPart.setText("Database report attached\n\n");
		multipart.addBodyPart(textBodyPart);
		// Attachments
		for (String attachmentFileName : attachmentFileNames) {
			BodyPart attachmentBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(attachmentFileName);
			attachmentBodyPart.setDataHandler(new DataHandler(source));
			String fileNameWithoutPath = attachmentFileName.replaceAll("^.*\\/", "");
			fileNameWithoutPath = fileNameWithoutPath.replaceAll("^.*\\\\", "");
			attachmentBodyPart.setFileName(fileNameWithoutPath);
			multipart.addBodyPart(attachmentBodyPart);
		}
		// add parts to message
		message.setContent(multipart);
		// send via SMTP
		Transport transport = mailSession.getTransport("smtp");		
                
		transport.connect();
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

	public static void main(String[] args) throws Exception {
		JasperEmail runJasperReports = new JasperEmail();
		// Set up command line parser
		Options options = new Options();
                
		Option reports = OptionBuilder.withArgName("reportlist").hasArg().withDescription(
				"Comma separated list of JasperReport XML input files").create("reports");                                       
		options.addOption(reports);
                
		Option emailTo = OptionBuilder.withArgName("emailaddress").hasArg().withDescription(
				"Email address to send generated reports to").create("emailto");                
		options.addOption(emailTo);
                
		Option emailFrom = OptionBuilder.withArgName("emailaddress").hasArg().withDescription(
				"Sender email address").create("emailfrom");                
		options.addOption(emailFrom);
                
		Option emailSubjectLine = OptionBuilder.withArgName("emailsubject").hasArg()
				.withDescription("Subject line of email").create("emailsubject");                
		options.addOption(emailSubjectLine);
                
		Option smtpHostOption = OptionBuilder.withArgName("smtphost").hasArg().withDescription(
				"Address of email server").create("smtphost");                                
		options.addOption(smtpHostOption);
                
		Option smtpUserOption = OptionBuilder.withArgName("smtpuser").hasArg()
				.withDescription("Username if email server requires authentication").create(
						"smtpuser");                
		options.addOption(smtpUserOption);
                
		Option smtpPassOption = OptionBuilder.withArgName("smtppass").hasArg()
				.withDescription("Password if email server requires authentication").create(
						"smtppass");                
		options.addOption(smtpPassOption);
                
                Option smtpAuthOption = OptionBuilder.withArgName("smtpauth").hasArg()
				.withDescription("Set SMTP Authentication").create(
						"smtpauth");                
		options.addOption(smtpAuthOption);
                
                Option smtpPortOption = OptionBuilder.withArgName("smtpport").hasArg()
                                .withDescription("Port for the SMTP server 25/468/587/2525/2526").create("smtpport");
                options.addOption(smtpPortOption);
                
                Option smtpTypeOption = OptionBuilder.withArgName("smtptype").hasArg().withDescription("Define SMTP Type, one of: " + Arrays.asList(OptionValues.SMTPType.values()) ).create("smtptype");                
                options.addOption(smtpTypeOption);
                
		Option outputFolder = OptionBuilder
				.withArgName("foldername")
				.hasArg()
				.withDescription(
						"Folder to write generated reports to, with trailing separator (slash or backslash)")
				.create("folder");               
		options.addOption(outputFolder);
                
		Option dbJDBCClass = OptionBuilder.withArgName("jdbcclass").hasArg().withDescription(
				"Provide the JDBC Database Class ").create(
				"jdbcclass");
		options.addOption(dbJDBCClass);
                
		Option dbJDBCURL = OptionBuilder.withArgName("jdbcurl").hasArg().withDescription(
				"Provide the JDBC Database URL").create("jdbcurl");
		options.addOption(dbJDBCURL);

		Option dbUserOption = OptionBuilder.withArgName("username").hasArg().withDescription(
				"Username to connect to databasewith").create("dbuser");
		options.addOption(dbUserOption);
                
		Option dbPassOption = OptionBuilder.withArgName("password").hasArg().withDescription(
				"Database password").create("dbpass");
		options.addOption(dbPassOption);
                
		Option outputTypeOption = OptionBuilder.withArgName("outputtype").hasArg().withDescription(
				"Output type, one of: " + Arrays.asList(OutputType.values())).create("output");
		options.addOption(outputTypeOption);
                
		Option outputFilenameOption = OptionBuilder.withArgName("outputfilename").hasArg()
				.withDescription("Output filename (excluding filetype suffix)").create("filename");
		options.addOption(outputFilenameOption);

		Option paramsOption = OptionBuilder
				.withArgName("parameters")
				.hasArg()
				.withDescription(
						"Parameters, e.g. param1=boolean:true,param2=string:ABC,param3=double:134.2,param4=integer:85")
				.create("params");
		options.addOption(paramsOption);
                
		// Parse command line
		CommandLineParser parser = new GnuParser();
                
		CommandLine commandLine = parser.parse(options, args);
                
		String reportsDefinitionFileNamesCvs = commandLine.getOptionValue("reports");
		if (reportsDefinitionFileNamesCvs == null) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar JasperEmail.jar", options);
			System.out.println();
			System.out.println("See http://github.com/ilopez/JasperEmail for further documentation");
			System.out.println();
			throw new IllegalArgumentException("No reports specified");
		}
                
		String outputPath = commandLine.getOptionValue("folder");
		List<String> reportDefinitionFileNames = Arrays.asList(reportsDefinitionFileNamesCvs
				.split(","));
		List<String> outputFileNames = new ArrayList<String>();
		
		String jdbcClass = commandLine.getOptionValue("jdbcclass");
		
		String jdbcURL = commandLine.getOptionValue("jdbcurl");
		String databaseUsername = commandLine.getOptionValue("dbuser");
		String databasePassword = commandLine.getOptionValue("dbpass");
		
		
		OutputType outputType = OutputType.PDF;
		String outputTypeString = commandLine.getOptionValue("output");
		if (outputTypeString != null) {
			outputType = OutputType.valueOf(outputTypeString.toUpperCase());
		}
                
		String parametersString = commandLine.getOptionValue("params");                
		Map parameters = runJasperReports.prepareParameters(parametersString);
		String outputFilenameSpecified = commandLine.getOptionValue("filename");
		if (outputFilenameSpecified == null) {
			outputFilenameSpecified = "";
		}
                
                // SMTP PORT
                
                Integer smtpport = Integer.parseInt(commandLine.getOptionValue("smtpport"));
                Boolean smtpauth = Boolean.parseBoolean(commandLine.getOptionValue("smtpauth")); 
                
                OptionValues.SMTPType smtptype;
                String smtptypestring = commandLine.getOptionValue("smtpenc");
                if (smtptypestring != null){
                    smtptype = OptionValues.SMTPType.valueOf( smtptypestring.toUpperCase() );
                }else{
                    smtptype =  OptionValues.SMTPType.PLAIN;
                }
                
                // SMTP TLS
                // SMTP 
                
                
		// Iterate over reports, generating output for each
		for (String reportsDefinitionFileName : reportDefinitionFileNames) {
			String outputFilename = null;
			if ((reportDefinitionFileNames.size() == 1) && (!outputFilenameSpecified.equals(""))) {
				outputFilename = outputFilenameSpecified;
			} else {
				outputFilename = outputFilenameSpecified
						+ reportsDefinitionFileName.replaceAll("\\..*$", "");
				outputFilename = outputFilename.replaceAll("^.*\\/", "");
				outputFilename = outputFilename.replaceAll("^.*\\\\", "");
			}
			outputFilename = outputFilename.replaceAll("\\W", "").toLowerCase() + "."
					+ outputType.toString().toLowerCase();
			if (outputPath != null) {
				if (!outputPath.endsWith("\\") && !outputPath.endsWith("/")) {
					outputPath += java.io.File.separator;
				}
				outputFilename = outputPath + outputFilename;
			}
			System.out.println("Going to generate report " + outputFilename);
                        
                        runJasperReports.generateReport(reportsDefinitionFileName, jdbcClass, jdbcURL, databaseUsername, databasePassword, jdbcURL, parameters);
			outputFileNames.add(outputFilename);
		}
		String emailRecipientList = commandLine.getOptionValue("emailto");
		if (emailRecipientList != null) {
			Set<String> emailRecipients = new HashSet<String>(Arrays.asList(emailRecipientList
					.split(",")));
			String emailSender = commandLine.getOptionValue("emailfrom");
			String emailSubject = commandLine.getOptionValue("emailsubject");
			if (emailSubject == null) {
				emailSubject = "Report attached";
			}
			String emailHost = commandLine.getOptionValue("smtphost");
			if (emailHost == null) {
				emailHost = "localhost";
			}
			String emailUser = commandLine.getOptionValue("smtpuser");
			String emailPass = commandLine.getOptionValue("smtppass");
			System.out.println("Emailing reports to " + emailRecipients);			
                        runJasperReports.emailReport(emailHost, emailUser, emailPass, emailRecipients, emailSender, emailSubject, outputFileNames, smtpauth, smtptype, smtpport);
		} else {
			System.out.println("Email not generated (no recipients specified)");
		}
	}
}
