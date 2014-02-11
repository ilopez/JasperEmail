# JasperEmail


A command line tool to email Jasper Report output as attachments.  This project was originally forked by Israel Lopez from https://github.com/okohll/RunJasperReports.

# Downloads

**Latest**

[Single JAR w/o Dependencies](/latest.zip)

[Shaded JAR w/ Dependencies (JasperReports 5.2)](/latest_shaded.zip)


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

	
# Where to Get Help
Please use the "issues" page on github.


# Dependencies
* Java 7
* commons-cli 1.2
* commons-io 2.4
* jasperreports 5.2
* jasperreports-fonts 4.0
* javax.mail 1.5.1
* servlet-api 2.5

# Build Environment
* Windows 7
* NetBeans IDE 7.4


# Motivations
I originally was going to write an app like this, but I found RunJasperReports by okohll.  I figured in the spirit of opensource I thought that its probably best served to fork the project, and change the code to fit my needs.  I used GitHub fork, changed the code how I thought it should be built and kept the repository clean.  One of my goals was to support any JDBC datasources without having to add additional code.  This was added by jdbcclass and jdbcurl arguments.  I also updated the code to support TLS/SSL smtp servers (gmail).  I also believe that report generation arguments should be passed through the command prompt or embedded in the JasperReport design and respected, so duplicate code was removed to support the newer formats (XLSX) with less code.

# Contributions
Found through perusing file headers & comments.

* GT webMarque LTD
* Oliver Kohill
* Bal?zs B?r?ny (Non UTF-8 Encoded Author)

# License

The original license for RunJasperReports was GPL v3, and JasperEmail will continue to be released under GPL v3.
