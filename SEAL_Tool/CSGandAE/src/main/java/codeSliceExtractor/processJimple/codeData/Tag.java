package codeSliceExtractor.processJimple.codeData;

/**
 * Types of instruction recognized by our parser
 */
public enum Tag {
    CAST,
    OBJECT,
    FUNCTION,
    IF,
    CONSTANT,
    OPERATION,
    ARRAY,
    PARAMETER,
    SOURCE,
    THROW,
    RETURN
}
