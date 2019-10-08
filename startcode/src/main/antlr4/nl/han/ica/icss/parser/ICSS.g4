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
LOWER_IDENT: [a-z0-9\-]+;
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
stylesheet: styleRule+ EOF;

// Style rule
styleRule: variableAssignment | selector OPEN_BRACE body CLOSE_BRACE;

// CSS styling block
selector: idSelector | classSelector | tagSelector;
body: declaration+;

// Selector types
idSelector: ID_IDENT;
classSelector: CLASS_IDENT;
tagSelector: LOWER_IDENT;

// Variable style
variableAssignment: variableReference ASSIGNMENT_OPERATOR expression SEMICOLON;
variableReference: CAPITAL_IDENT;

// Declaration style
//declaration: propertyName COLON (expression | operation) SEMICOLON;
declaration: propertyName COLON expression SEMICOLON;
propertyName: LOWER_IDENT;

// Operation style
//operation: operation MUL operation
//           | operation PLUS operation
//           | operation MIN operation
//           | expression;

// Expression
expression: variableReference
            | literal;

literal: bool
        | COLOR
        | PERCENTAGE
        | PIXELSIZE
        | SCALAR;
bool: TRUE | FALSE;