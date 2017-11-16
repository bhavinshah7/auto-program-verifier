# auto-program-verifier

A program verification engine to verify a simple imperative language. 
It takes a program as textual input, generates a control-flow-graph and a set of Horn clauses for the program.
The progam is verified by consulting [Eldarica](https://github.com/uuverifiers/eldarica) Horn solver.
Output is printed on the screen as correct for a program with no bug and incorrect for a program that has bug. 

## Language BNF:
```
Program	::= Stmt ; <EOF> 
Stmt    ::= skip  
Id      ::= IExp  
            Stmt ; Stmt  
            if ( BExp ) Stmt else Stmt endif 
            while ( BExp ) Stmt endwhile 
            assert ( BExp )  
BExp    ::= IExp <= IExp 
            IExp == IExp 
            not BExp 
            BExp and BExp 
            BExp or BExp  
IExp    ::= Int
            Id 
            IExp + IExp 
            IExp - IExp  
```
