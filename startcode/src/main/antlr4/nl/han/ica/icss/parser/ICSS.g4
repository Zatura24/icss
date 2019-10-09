grammar ICSS;

//--- LEXER: ---
// IF support:
IF: 'if';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;

//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';

//--- PARSER: ---
// Stylesheet
stylesheet: variableAssignment* styleRule* EOF;

// Variable
variableAssignment: variableReference ASSIGNMENT_OPERATOR expression SEMICOLON;
variableReference: CAPITAL_IDENT;

// Stylerule
styleRule: selector OPEN_BRACE declaration+ CLOSE_BRACE;

// Selector
selector: ID_IDENT #idSelector
        | CLASS_IDENT #classSelector
        | LOWER_IDENT #tagSelector
        ;

// Declaration
//declaration: propertyName COLON (expression | operation) SEMICOLON;
declaration: propertyName COLON expression SEMICOLON;
propertyName: LOWER_IDENT;

// Operation
//operation: expression
//        | operation MUL operation
//        | operation MIN operation
//        | operation PLUS operation
//        ;
//operator: MUL #multiplyOperation
//        | PLUS #addOperation
//        | MIN #subtractOperation
//        ;
//operation: expression operator (expression | operation);

// Expression
expression: variableReference
            | literal
            ;

// Literal
literal: bool           #boolLiteral
        | COLOR         #colorLiteral
        | PERCENTAGE    #percentageLiteral
        | PIXELSIZE     #pixelsizeLiteral
        | SCALAR        #scalarLiteral
        ;
bool: TRUE      #trueBool
    | FALSE     #falseBool
    ;