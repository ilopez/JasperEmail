# JasperEmail


A command line tool to email Jasper Report output as attachments.  This project was originally forked by Israel Lopez from https://github.com/okohll/RunJasperReports.

# Downloads

**Latest**

[Single JAR w/o Dependencies](http://jasperreports.ilopez.com/latest.zip)

[Shaded JAR w/ Dependencies (JasperReports 5.2)](http://jasperreports.ilopez.com/latest_shaded.zip)


# 

# Usage

	usage: java -jar JasperEmail.jar
	 -dbpass <password>             Database password
	 -dbuser <username>             Username to connect to databasewith
	 -emailfrom <emailaddress>      Sender email address
	 -emailsubject <emailsubject>   Subject line of email
	 -emailto <emailaddress>        Email address to send generated reports to
	 -filename <outputfilename>     Output filename (excluding filetype
									suffix)
	 -folder <foldername>           Folder to write generated reports to, with
									trailing separator (slash or backslash)
	 -jdbcclass <jdbcclass>         Provide the JDBC Database Class
	 -jdbcurl <jdbcurl>             Provide the JDBC Database URL
	 -output <outputtype>           Output type, one of: [pdf, html, text,
									csv, xls, xlsx]
	 -params <parameters>           Parameters, e.g.
									param1=boolean:true,param2=string:ABC,para
									m3=double:134.2,param4=integer:85
	 -reports <reportlist>          Comma separated list of JasperReport XML
									input files
	 -smtpauth <smtpauth>           Set SMTP Authentication
	 -smtphost <smtphost>           Address of email server
	 -smtppass <smtppass>           Password if email server requires
									authentication
	 -smtpport <smtpport>           Port for the SMTP server
									25/468/587/2525/2526
	 -smtptype <smtptype>           Define SMTP Type, one of: [plain, ssl,
									tls]
	 -smtpuser <smtpuser>           Username if email server requires
									authentication

	See http://github.com/ilopez/JasperEmail for further documentation



# Motivations


# Contributions
