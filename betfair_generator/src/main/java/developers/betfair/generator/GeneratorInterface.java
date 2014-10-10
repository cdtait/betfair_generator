package developer.betfair.generator;

import java.util.Map;

public interface GeneratorInterface {
	
	/**
	 * 
	 * @param buff
	 * @param e
	 */
	public void genExceptionType(StringBuffer buff, ExceptionType e);

	public void genExceptionTypeFunction(StringBuffer buff, ExceptionType e);

	public void genExceptionTypeComment(StringBuffer buff, ExceptionType e);

	public void genOperation(StringBuffer buff,Map<String, String> simpleToPrimitiveTypeMap, Operation o);

	public void genOperationFunction(StringBuffer buff, Operation o);

	public void genOperationComment(StringBuffer buff,
			Map<String, String> simpleToPrimitiveTypeMap, Operation o);

	public void genDataType(StringBuffer buff,Map<String, String> simpleToPrimitiveTypeMap, DataType d) ;

	public void genDataTypeListOf(StringBuffer buff, DataType d);

	public void genDataTypeSetOf(StringBuffer buff, DataType d);

	public void genDataTypeFunction(StringBuffer buff,
			Map<String, String> simpleToPrimitiveTypeMap, DataType d);

	public void genDataTypeComment(StringBuffer buff,
			Map<String, String> simpleToPrimitiveTypeMap, DataType d);

	public void genSimpleType(StringBuffer buff,Map<String, String> simpleToPrimitiveTypeMap, SimpleType s);

	public void genSimpleTypeFunction(StringBuffer buff,Map<String, String> simpleToPrimitiveTypeMap, SimpleType s);

	public void genSimpleTypeComment(StringBuffer buff, SimpleType s);

	public void genSimpleTypeListOfV(StringBuffer buff, SimpleType s);

	public void genSimpleTypeSetOfV(StringBuffer buff, SimpleType s);
	
	public void genSimpleTypeListOf(StringBuffer buff, SimpleType s);

	public void genSimpleTypeSetOf(StringBuffer buff, SimpleType s);
	
	public void genInterfaceComment(Interface i,StringBuffer buff);

	public void genBeginSeperator(StringBuffer buff);
	
	public void genEndSeperator(StringBuffer buff);
	
}
