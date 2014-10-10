package developer.betfair.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

public class RGenerator implements genDataTypeListOf, GeneratorInterface {

	String api;

	/**
	 * Construct and generate the R version of
	 * 
	 * @param i
	 * @param outDir
	 * @throws IOException
	 */
	public RGenerator(Interface i, String outDir, String api)
			throws IOException {
		this.api = api;
		Map<String, String> simpleToPrimitiveTypeMap = new HashMap<String, String>();

		new File(outDir).mkdirs();

		FileOutputStream fbetfair = new FileOutputStream(String.format(
				"%s/%s.R", outDir, i.name));
		StringBuffer genBetfairOutput = new StringBuffer();

		genInterfaceComment(i, genBetfairOutput);

		// Exception is duplicated between interfaces and being very
		// simple may be better with other exceptions in a separate file
		// exception.R
		// ExceptionType e = i.exceptionType;
		// genExceptionType(genBetfairOutput,e);

		for (SimpleType s : i.simpleType) {
			genSimpleType(genBetfairOutput, simpleToPrimitiveTypeMap, s);
		}
		for (DataType d : i.dataType) {
			genDataType(genBetfairOutput, simpleToPrimitiveTypeMap, d);
		}
		for (Operation o : i.operation) {
			genOperation(genBetfairOutput, simpleToPrimitiveTypeMap, o);
		}
		fbetfair.getChannel().write(
				ByteBuffer.wrap(genBetfairOutput.toString().getBytes()));
		fbetfair.close();
	}

	public void genExceptionType(StringBuffer buff, ExceptionType e) {
		genExceptionTypeComment(buff, e);
		genExceptionTypeFunction(buff, e);
		buff.append("\n");
	}

	public void genExceptionTypeFunction(StringBuffer buff, ExceptionType e) {
		buff.append(String.format("%s<-function(", e.name));

		if (e.parameter != null) {
			for (Parameter p : e.parameter) {
				buff.append(String.format("%s,", p.name));
			}
			buff.deleteCharAt(buff.length() - 1);
		}

		buff.append(") {\n");

		StringBuffer ebuff = new StringBuffer();
		if (e.parameter != null) {
			ebuff.append(String.format("ex=paste0('%s ',", e.name));
			for (Parameter p : e.parameter) {
				ebuff.append(String.format("' %s=',%s,", p.name, p.name));
			}
			ebuff.deleteCharAt(ebuff.length() - 1);
			ebuff.append(")\n");
		}
		String etext = ebuff.toString();
		etext = WordUtils.wrap(etext, 70, "\n  ", false);
		buff.append("  " + etext);
		buff.append(String.format("  stop(ex)\n", e.name));
		buff.append("}\n\n");
	}

	public void genExceptionTypeComment(StringBuffer buff, ExceptionType e) {
		genBeginSeperator(buff);
		buff.append(String.format("# ExceptionType:  %s\n", e.name));
		String description = e.description.replaceAll("[\\t\\n]", " ");
		description = description.replaceAll("\\s+", " ");
		description = WordUtils.wrap(description, 70, "\n#            ", false);
		buff.append("#            " + description + "\n");
		if (e.parameter != null) {
			for (Parameter p : e.parameter) {
				String pdescription = p.description.replaceAll("[\\t\\n]", " ");
				pdescription = pdescription.replaceAll("\\s+", " ");
				pdescription = WordUtils.wrap(pdescription, 70,
						"\n#            ", false);
				buff.append(String.format("# Parameter: %s of type %s\n",
						p.name, p.type));
				buff.append(String.format("#            %s\n", pdescription));
				if (p.validValues != null && !p.validValues.value.isEmpty()) {
					for (Value v : p.validValues.value) {
						buff.append(String.format("# Value:     %s\n", v.name));
						if (v.description != null
								&& !v.description.replaceAll("\\s+", "")
										.equals("")) {
							String vdescription = v.description.replaceAll(
									"[\\t\\n]", " ");
							vdescription = vdescription.replaceAll("\\s+", " ");
							vdescription = WordUtils.wrap(vdescription, 70,
									"\n#            ", false);
							buff.append(String.format("#            %s\n",
									vdescription));
						}
					}
				}
			}
		}
		genEndSeperator(buff);
	}

	public void genOperation(StringBuffer buff,
			Map<String, String> simpleToPrimitiveTypeMap, Operation o) {
		genOperationComment(buff, simpleToPrimitiveTypeMap, o);
		genOperationFunction(buff, o);
		genOperationToRConversion(buff, o);
	}

	public void genOperationToRConversion(StringBuffer buff, Operation o) {
		StringBuffer funcbuff = new StringBuffer();

		funcbuff.append(String.format("r.%s<-function(", o.name));
		if (o.parameters != null) {
			Request r = o.parameters.request;
			if (r.parameter != null) {
				for (Parameter p : r.parameter) {
					if (p.mandatory != null && p.isMandatory()) {
						funcbuff.append(String.format("%s=%s(), ", p.name,
								convertPrimitiveRTypes(p.type)));
					} else {
						funcbuff.append(String.format("%s=NULL, ", p.name));
					}
				}
				funcbuff.deleteCharAt(funcbuff.length() - 2);
				funcbuff.append(", exchange=.BetfairEnv$exchange, appKey=.BetfairEnv$appKey, sessionToken=.BetfairEnv$sessionToken");
			} else {
				funcbuff.append("exchange=.BetfairEnv$exchange, appKey=.BetfairEnv$appKey, sessionToken=.BetfairEnv$sessionToken");
			}
		} else {
			funcbuff.append("exchange=.BetfairEnv$exchange, appKey=.BetfairEnv$appKey, sessionToken=.BetfairEnv$sessionToken");
		}
		funcbuff.append(") {\n");

		String functxt = funcbuff.toString();
		functxt = WordUtils.wrap(functxt, 80, "\n  ", false);
		buff.append(functxt);

		StringBuffer parambuff = new StringBuffer();
		parambuff.append(String.format("  ret<-json.%s(", o.name));
		if (o.parameters != null) {
			Request r = o.parameters.request;
			if (r.parameter != null) {
				for (Parameter p : r.parameter) {
					parambuff.append(String.format("%s=%s, ", p.name, p.name));
				}
				parambuff.deleteCharAt(parambuff.length() - 2);
				parambuff
						.append(", exchange=exchange, appKey=appKey, sessionToken=sessionToken");
			} else {
				parambuff
						.append("exchange=exchange, appKey=appKey, sessionToken=sessionToken");
			}
		} else {
			parambuff
					.append("exchange=exchange, appKey=appKey, sessionToken=sessionToken");
		}

		parambuff.append(")\n");
		String paramtxt = parambuff.toString();
		paramtxt = WordUtils.wrap(paramtxt, 80, "\n ", false);
		buff.append("  " + paramtxt);
		buff.append("  toR(ret)\n");
		buff.append("}\n\n");
	}

	public void genOperationFunction(StringBuffer buff, Operation o) {
		StringBuffer funcbuff = new StringBuffer();

		funcbuff.append(String.format("json.%s<-function(", o.name));
		if (o.parameters != null) {
			Request r = o.parameters.request;
			if (r.parameter != null) {
				for (Parameter p : r.parameter) {
					if (p.mandatory != null && p.isMandatory()) {
						funcbuff.append(String.format("%s=%s(), ", p.name,
								convertPrimitiveRTypes(p.type)));
					} else {
						funcbuff.append(String.format("%s=NULL, ", p.name));
					}
				}
				funcbuff.deleteCharAt(funcbuff.length() - 2);
				funcbuff.append(", exchange=.BetfairEnv$exchange, appKey=.BetfairEnv$appKey, sessionToken=.BetfairEnv$sessionToken");
			} else {
				funcbuff.append("exchange=.BetfairEnv$exchange, appKey=.BetfairEnv$appKey, sessionToken=.BetfairEnv$sessionToken");
			}
		} else {
			funcbuff.append("exchange=.BetfairEnv$exchange, appKey=.BetfairEnv$appKey, sessionToken=.BetfairEnv$sessionToken");
		}

		funcbuff.append(") {\n");

		String functxt = funcbuff.toString();
		functxt = WordUtils.wrap(functxt, 80, "\n  ", false);
		buff.append(functxt);

		StringBuffer parambuff = new StringBuffer();
		parambuff.append("  params = list(");
		if (o.parameters != null) {
			Request r = o.parameters.request;
			if (r.parameter != null) {
				for (Parameter p : r.parameter) {
					parambuff.append(String.format("%s=%s, ", p.name, p.name));
				}
				parambuff.deleteCharAt(parambuff.length() - 2);
			}
		}

		parambuff.append(")\n");
		String paramtxt = parambuff.toString();
		paramtxt = WordUtils.wrap(paramtxt, 80, "\n  ", false);
		buff.append("  " + paramtxt);

		buff.append(String
				.format("  run.operation('%s',params,exchange,appKey,sessionToken,api='%s')\n",
						o.name, api));
		buff.append("}\n\n");
	}

	public void genOperationComment(StringBuffer buff,
			Map<String, String> simpleToPrimitiveTypeMap, Operation o) {
		genBeginSeperator(buff);
		buff.append(String.format("#' @title %s\n", o.name));
		String description = o.description.replaceAll("[\\t\\n]", " ");
		description = description.replaceAll("\\s+", " ");
		description = description.replaceAll("^ ", "");
		description = WordUtils.wrap(description, 77, "\n#' ",
				false);
		if (description.trim().length()>0) {
			buff.append("#' @description " + description + "\n");
		}
		if (o.parameters != null) {
			Request r = o.parameters.request;
			if (r.parameter != null) {
				for (Parameter p : r.parameter) {
					String t = convertValidateRTypes(p.type);
					if ((simpleToPrimitiveTypeMap.containsKey(t))) {
						t = convertValidateRTypes(simpleToPrimitiveTypeMap
								.get(t));
					}
					String mandatory = "optional";
					if (p.mandatory != null && p.isMandatory()) {
						mandatory = "mandatory";
					}
					String pdescription = p.description.replaceAll("[\\t\\n]",
							" ");
					pdescription = pdescription.replaceAll("\\s+", " ");
					pdescription = pdescription.replaceAll("^ ", "");
					pdescription = pdescription.replaceAll("@", "");
					pdescription = WordUtils.wrap(pdescription, 70,
							"\n#'          ", false);
					buff.append(String.format(
							"#' @param   %s is %s of type %s.\n", p.name,
							mandatory, t));
					buff.append(String.format("#'          %s\n",
							pdescription));
				}
			}
			SimpleResponse s = o.parameters.simpleResponse;
			if (s.description != null) {
				String sdescription = s.description.replaceAll("[\\t\\n]"," ");
				sdescription = sdescription.replaceAll("\\s+", " ");
				sdescription = sdescription.replaceAll("^ ", "");
				if (sdescription.trim().length()>0) {
					sdescription = WordUtils.wrap(sdescription, 70,
						"\n#'          ", false);
					buff.append(String.format("#' @return  %s\n",sdescription));
				}
			}
		}
		genEndSeperator(buff);
	}

	public void genDataType(StringBuffer buff,
			Map<String, String> simpleToPrimitiveTypeMap, DataType d) {
		genDataTypeComment(buff, simpleToPrimitiveTypeMap, d);
		genDataTypeFunction(buff, simpleToPrimitiveTypeMap, d);
		genDataTypeSetOf(buff, d);
		genDataTypeListOf(buff, d);
	}

	public void genDataTypeListOf(StringBuffer buff, DataType d) {
		buff.append(String.format("ListOf%s<-function(...) {\n", d.name));
		buff.append(String.format("  lv<-list(...)\n"));
		buff.append(String.format(
				"  class(lv) <- append(class(lv),'ListOf%s')\n", d.name));
		buff.append(String.format("  return(lv)\n"));
		buff.append(String.format("}\n\n"));
	}

	public void genDataTypeSetOf(StringBuffer buff, DataType d) {
		buff.append(String.format("SetOf%s<-function(...) {\n", d.name));
		buff.append(String.format("  lv<-list(...)\n"));
		buff.append(String.format(
				"  class(lv) <- append(class(lv),'SetOf%s')\n", d.name));
		buff.append(String.format("  return(lv)\n"));
		buff.append(String.format("}\n\n"));
	}

	public void genDataTypeFunction(StringBuffer buff,
			Map<String, String> simpleToPrimitiveTypeMap, DataType d) {
		buff.append(String.format("%s<-function(", d.name));

		if (d.parameter != null) {
			for (Parameter p : d.parameter) {
				if (p.mandatory != null && p.isMandatory()) {
					buff.append(String.format("%s,", p.name));

				} else {
					buff.append(String.format("%s=NULL,", p.name));
				}
			}
			buff.deleteCharAt(buff.length() - 1);
		}

		buff.append(") {\n");

		if (d.parameter != null) {
			for (Parameter p : d.parameter) {
				String validator = "validateField";
				if (p.type.equals("dateTime")) {
					validator = "validateDateTime";
				}
				String t = convertValidateRTypes(p.type);
				if ((simpleToPrimitiveTypeMap.containsKey(t))) {
					t = convertValidateRTypes(simpleToPrimitiveTypeMap.get(t));
				}

				if (p.mandatory != null && p.isMandatory()) {
					buff.append(String.format("  %s(%s,'%s',TRUE,'%s')\n",
							validator, p.name, t, p.name));
				} else {
					buff.append(String.format("  %s(%s,'%s',FALSE,'%s')\n",
							validator, p.name, t, p.name));
				}
			}

			buff.append("\n  ret=list(");
			for (Parameter p : d.parameter) {
				buff.append(String.format("%s=%s,", p.name, p.name,
						convertPrimitiveRTypes(p.type)));
			}
			buff.deleteCharAt(buff.length() - 1);
			buff.append(")\n");
			buff.append("  ret[sapply(ret, is.null)] <- NULL\n");
		}

		buff.append(String.format(
				"  class(ret) <- append(class(ret),\"%s\")\n", d.name));

		buff.append("  ret\n");
		buff.append("}\n\n");
	}

	public void genDataTypeComment(StringBuffer buff,
			Map<String, String> simpleToPrimitiveTypeMap, DataType d) {
		genBeginSeperator(buff);
		buff.append(String.format("#' @title  %s\n", d.name));
		String description = d.description.replaceAll("[\\t\\n]", " ");
		description = description.replaceAll("\\s+", " ");
		description = description.replaceAll("^ ", "");
		description = WordUtils.wrap(description, 77, "\n#' ", false);
		if (description.trim().length()>0) {
			buff.append("#' @description " + description + "\n");
		}
		
		//buff.append("#' \n");
		if (d.parameter != null) {
			for (Parameter p : d.parameter) {

				String t = convertValidateRTypes(p.type);
				if ((simpleToPrimitiveTypeMap.containsKey(t))) {
					t = convertValidateRTypes(simpleToPrimitiveTypeMap.get(t));
				}
				String mandatory = "optional";
				if (p.mandatory != null && p.isMandatory()) {
					mandatory = "mandatory";
				}
				String pdescription = p.description.replaceAll("[\\t\\n]", " ");
				pdescription = pdescription.replaceAll("\\s+", " ");
				pdescription = pdescription.replaceAll("^ ", "");
				pdescription = WordUtils.wrap(pdescription, 70,
						"\n#'        ", false);
				buff.append(String.format("#' @param %s is %s of type %s.\n",
						p.name, mandatory, t));
				buff.append(String.format("#'        %s\n", pdescription));
			}
		}
		genEndSeperator(buff);
	}

	public void genSimpleType(StringBuffer buff,
			Map<String, String> simpleToPrimitiveTypeMap, SimpleType s) {
		genSimpleTypeComment(buff, s);
		genSimpleTypeFunction(buff, simpleToPrimitiveTypeMap, s);
		buff.append("\n");
	}

	public void genSimpleTypeFunction(StringBuffer buff,
			Map<String, String> simpleToPrimitiveTypeMap, SimpleType s) {
		if (s.validValues != null && !s.validValues.value.isEmpty()) {
			buff.append(String.format("%sValidValues=c(", s.name));

			for (Value v : s.validValues.value) {
				buff.append(String.format("\"%s\",", v.name));
			}
			buff.deleteCharAt(buff.length() - 1);
			buff.append(")\n");
			// buff.append(String.format("%s<-function(...) { ItemOf(list(...),'%s',%sValidValues) }\n",s.name,s.name,s.name));
			buff.append(String.format("%s<-function(values) {\n", s.name));
			buff.append(String.format(
					"  validateSet(values,'%s',%sValidValues)\n", s.name,
					s.name));
			buff.append(String
					.format("  class(values) <- append(class(values),\"%s\")\n",
							s.name));
			buff.append(String.format("  return(values)\n"));
			buff.append("}\n");
			genSimpleTypeSetOfV(buff, s);
			genSimpleTypeListOfV(buff, s);
		} else {
			buff.append(String.format("%s<-%s\n", s.name,
					convertPrimitiveRTypes(s.type)));
			genSimpleTypeSetOf(buff, s);
			genSimpleTypeListOf(buff, s);
			simpleToPrimitiveTypeMap
					.put(s.name, convertPrimitiveRTypes(s.type));
		}
	}

	public void genSimpleTypeComment(StringBuffer buff, SimpleType s) {		
		genBeginSeperator(buff);
		buff.append(String.format("#' @title %s\n", s.name));
		buff.append(String.format("#' @description %s\n", s.name));
		buff.append(String.format("#' @usage %s(values)\n", s.name));
		buff.append(String.format("#' @param values Any of following valid values\n"));
		//buff.append(String.format("#' \n"));
		if (s.validValues != null && !s.validValues.value.isEmpty()) {
			buff.append(String.format("#' @details {\n"));
			buff.append(String.format("#'   \\itemize{\n"));
			for (Value v : s.validValues.value) {
				buff.append(String.format("#'     \\item {     %s\n", v.name));
				if (v.description != null
						&& !v.description.replaceAll("\\s+", "").equals("")) {
					String description = v.description.replaceAll("[\\t\\n]",
							" ");
					description = description.replaceAll("\\s+", " ");
					description = description.replaceAll("^ ", "");
					description = description.replaceAll("@", "");
					description = WordUtils.wrap(description, 70,
							"\n#'            ", false);
					buff.append(String.format("#'            %s\n", description));
				}
				buff.append(String.format("#'     }\n"));
			}
			buff.append(String.format("#'   }\n"));
			buff.append(String.format("#' }\n"));
		}
		genEndSeperator(buff);
	}

	public void genSimpleTypeListOfV(StringBuffer buff, SimpleType s) {
		buff.append(String
				.format("ListOf%s<-function(...) { ListOfV(list(...),'%s',%sValidValues) }\n",
						s.name, s.name, s.name));
	}

	public void genSimpleTypeSetOfV(StringBuffer buff, SimpleType s) {
		buff.append(String
				.format("SetOf%s<-function(...) { SetOfV(list(...),'%s',%sValidValues) }\n",
						s.name, s.name, s.name));
	}

	public void genSimpleTypeListOf(StringBuffer buff, SimpleType s) {
		buff.append(String.format(
				"ListOf%s<-function(...) { ListOf(list(...),'%s') }\n", s.name,
				s.name, s.name));
	}

	public void genSimpleTypeSetOf(StringBuffer buff, SimpleType s) {
		buff.append(String.format(
				"SetOf%s<-function(...) { SetOf(list(...),'%s') }\n", s.name,
				s.name, s.name));
	}


	public void genInterfaceComment(Interface i, StringBuffer buff) {
		genBeginSeperator(buff);
		buff.append(String.format("#  Interface:   %s\n", i.name));
		buff.append(String.format("#  Description: %s\n", i.description));
		buff.append(String.format("#  Version:     %s\n", i.version));
		buff.append(String.format("#  Owner:       %s\n", i.owner));
		buff.append(String.format("#  Date:        %s\n", i.date));
		buff.append(String.format("#  Generated:   %s\n", DateFormat
				.getDateInstance().format(new Date())));
		genEndSeperator(buff);
		buff.append("\n");
	}

	@Override
	public void genBeginSeperator(StringBuffer buff) {
		buff.append(String.format("%80s\n", "").replaceAll(" ", "#"));
	}
	
	@Override
	public void genEndSeperator(StringBuffer buff) {
		buff.append(String.format("%80s\n", "").replaceAll(" ", "#"));
	}
	
	/**
	 * Convert generic xml definition types to R types
	 * @param type
	 * @return
	 */
	private String convertPrimitiveRTypes(String type) {
		if (type.startsWith("set")) {
			type = type.replace("set(", "SetOf");
			type = type.replace(")", "");
		} else if (type.startsWith("list")) {
			type = type.replace("list(", "ListOf");
			type = type.replace(")", "");
		}

		return type;
	}

	/**
	 * Convert generic xml definition types to R types
	 * @param type
	 * @return
	 */
	private String convertValidateRTypes(String type) {
		if (type.equals("double")) {
			type = "numeric";
		} else if (type.equals("i64") || type.equals("i32")) {
			type = "integer";
		} else if (type.equals("bool")) {
			type = "logical";
		} else if (type.equals("string")) {
			type = "character";
		} else if (type.startsWith("set(")) {
			type = type.replace("set(", "SetOf");
			type = type.replace(")", "");
		} else if (type.startsWith("list(")) {
			type = type.replace("list(", "ListOf");
			type = type.replace(")", "");
		}
		return type;
	}

}
