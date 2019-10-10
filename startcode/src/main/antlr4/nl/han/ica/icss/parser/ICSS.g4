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
styleRule: selector body | ifClause;
body: OPEN_BRACE declaration+ CLOSE_BRACE;

// Selector
selector: ID_IDENT #idSelector
        | CLASS_IDENT #classSelector
        | LOWER_IDENT #tagSelector
        ;

// Declaration
//declaration: propertyName COLON (expression | operation) SEMICOLON;
declaration: propertyName COLON expression SEMICOLON | ifClause;
propertyName: LOWER_IDENT;

// If clause
ifClause: IF BOX_BRACKET_OPEN expression BOX_BRACKET_CLOSE body;

// Operation

// Expression
expression: expression MUL expression #multiplyOperation
            | expression PLUS expression #addOperation
            | expression MIN expression #subtractOperation
            | variableReference #varRef
            | literal #lit
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