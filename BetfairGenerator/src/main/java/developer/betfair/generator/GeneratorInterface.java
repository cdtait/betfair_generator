package developer.betfair.generator;

import java.util.Map;

/**
 *
 * @author obod
 */
public interface GeneratorInterface {

    /**
     * Generate Exception Type
     *
     * @param buff
     * @param e
     */
    public void genExceptionType(StringBuffer buff, ExceptionType e);

    /**
     * Generate Exception Type function.
     *
     * @param buff
     * @param e
     */
    public void genExceptionTypeFunction(StringBuffer buff, ExceptionType e);

    /**
     * Generate the Exception Type comment
     *
     * @param buff
     * @param e
     */
    public void genExceptionTypeComment(StringBuffer buff, ExceptionType e);

    /**
     * Generate the R operation from schema model
     *
     * @param buff
     * @param simpleToPrimitiveTypeMap map schema types to R types
     * @param o Operation model instance
     */
    public void genOperation(StringBuffer buff, Map<String, String> simpleToPrimitiveTypeMap, Operation o);

    /**
     * Generate the function to produce the JSON return object
     *
     * @param buff
     * @param o
     */
    public void genOperationFunction(StringBuffer buff, Operation o);

    /**
     * Generate the operation comment
     *
     * @param buff
     * @param simpleToPrimitiveTypeMap
     * @param o
     */
    public void genOperationComment(StringBuffer buff,
            Map<String, String> simpleToPrimitiveTypeMap, Operation o);

    /**
     * Generate the DataType functions
     *
     * @param buff
     * @param simpleToPrimitiveTypeMap
     * @param d
     */
    public void genDataType(StringBuffer buff, Map<String, String> simpleToPrimitiveTypeMap, DataType d);

    /**
     * Generate DataType list definition
     *
     * @param buff
     * @param d
     */
    public void genDataTypeListOf(StringBuffer buff, DataType d);

    /**
     * Generate DataType set definition
     *
     * @param buff
     * @param d
     */
    public void genDataTypeSetOf(StringBuffer buff, DataType d);

    /**
     * Generate DataType function
     *
     * @param buff
     * @param simpleToPrimitiveTypeMap
     * @param d
     */
    public void genDataTypeFunction(StringBuffer buff,
            Map<String, String> simpleToPrimitiveTypeMap, DataType d);

    /**
     * Generate DataType comment
     *
     * @param buff
     * @param simpleToPrimitiveTypeMap
     * @param d
     */
    public void genDataTypeComment(StringBuffer buff,
            Map<String, String> simpleToPrimitiveTypeMap, DataType d);

    /**
     * Generate the SimpleType
     *
     * @param buff
     * @param simpleToPrimitiveTypeMap
     * @param s
     */
    public void genSimpleType(StringBuffer buff, Map<String, String> simpleToPrimitiveTypeMap, SimpleType s);

    /**
     * Generate SimpleType function
     *
     * @param buff
     * @param simpleToPrimitiveTypeMap
     * @param s
     */
    public void genSimpleTypeFunction(StringBuffer buff, Map<String, String> simpleToPrimitiveTypeMap, SimpleType s);

    /**
     * Generate SimplaeType comment
     *
     * @param buff
     * @param s
     */
    public void genSimpleTypeComment(StringBuffer buff, SimpleType s);

    /**
     * Generate a validated SimpleType list definition
     *
     * @param buff
     * @param s
     */
    public void genSimpleTypeListOfV(StringBuffer buff, SimpleType s);

    /**
     * Generate a validated SimpleType set definition
     *
     * @param buff
     * @param s
     */
    public void genSimpleTypeSetOfV(StringBuffer buff, SimpleType s);

    /**
     * Generate a SimpleType list definition
     *
     * @param buff
     * @param s
     */
    public void genSimpleTypeListOf(StringBuffer buff, SimpleType s);

    /**
     * Generate a SimpleType set definition
     *
     * @param buff
     * @param s
     */
    public void genSimpleTypeSetOf(StringBuffer buff, SimpleType s);

    /**
     * Generate the interface comment
     *
     * @param i
     * @param buff
     */
    public void genInterfaceComment(Interface i, StringBuffer buff);

    /**
     * Generate the begin comment separator line
     *
     * @param buff
     */
    public void genBeginSeperator(StringBuffer buff);

    /**
     * Generate the end of comment separator line
     *
     * @param buff
     */
    public void genEndSeperator(StringBuffer buff);

}
