grammar first;

prog: stat* EOF;

stat
    : logexpr SEMI                                                 #logexpr_stat
    | TYPE ID ('=' expr)? SEMI                                  #var_decl
    | IF_kw '(' cond=logexpr ')' thenBlock=block ('else' elseBlock=block)? #if_stat
    | WHILE_kw '(' cond=logexpr ')' body=block                     #while_stat
    | PRINT_kw expr SEMI                                        #print_stat
    ;

block
    : stat                                                      #block_single
    | '{' stat* '}'                                             #block_real
    ;

logexpr
    : expr OR expr                                              #orExpr
    | expr AND expr                                             #andExpr
    | comexpr                                                   #comparexpr
    ;

comexpr
    : expr (EQ | NEQ) expr                                      #eqExpr
    | expr (LT | GT | LEQ | GEQ) expr                           #relExpr
    | expr                                                      #xexpr
    ;

expr
    : <assoc=right> ID '=' expr                                 #assignExpr
    | expr (ADD | SUB) expr                                     #addExpr
    | expr (MUL | DIV) expr                                     #mulExpr
    | NOT expr                                                  #notExpr
    | '(' expr ')'                                              #parensExpr
    | INT                                                       #intExpr
    | TRUE                                                      #trueExpr
    | FALSE                                                     #falseExpr
    | ID                                                        #idExpr
    ;

TYPE     : 'int' ;
IF_kw    : 'if' ;
WHILE_kw : 'while' ;
PRINT_kw : 'print' ;

OR   : 'or' ;
AND  : 'and' ;
NOT  : 'not' ;

EQ   : '==' ;
NEQ  : '!=' ;
LT   : '<' ;
GT   : '>' ;
LEQ  : '<=' ;
GEQ  : '>=' ;

MUL  : '*' ;
DIV  : '/' ;
ADD  : '+' ;
SUB  : '-' ;

TRUE  : 'true' ;
FALSE : 'false' ;

SEMI : ';' ;

NEWLINE : [\r\n]+ -> channel(HIDDEN);
WS      : [ \t]+  -> channel(HIDDEN);

INT : [0-9]+ ;
ID  : [a-zA-Z_][a-zA-Z0-9_]* ;

COMMENT      : '/*' .*? '*/' -> channel(HIDDEN);
LINE_COMMENT : '//' ~[\r\n]* -> channel(HIDDEN);