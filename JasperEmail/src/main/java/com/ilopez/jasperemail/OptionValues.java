/*
 *  Copyright 2014 Israel Lopez
 *  Copyright 2011 GT webMarque Ltd
 * 
 *  This file is part of RunJasperReports.
 *
 *  RunJasperReports is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  RunJasperReports is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with RunJasperReports.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.ilopez.jasperemail;

/**
 * These enumerations used when parsing command line
 */
public class OptionValues {

	public enum OutputType {
		PDF, HTML, TEXT, CSV, XLS, XLSX;
		
		public String toString() {
			return this.name().toLowerCase();
		}
	}
	
	public enum ParamType {
		STRING, BOOLEAN, DOUBLE, INTEGER;
		
		public String toString() {
			return this.name().toLowerCase();
		}
	}
        
        public enum SMTPType{
            PLAIN, SSL, TLS;
            public String toString() {
                return this.name().toLowerCase();
            }
        }
	
	
	
}
