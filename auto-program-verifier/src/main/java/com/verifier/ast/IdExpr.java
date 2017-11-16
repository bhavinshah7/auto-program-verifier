package com.verifier.ast;

import com.verifier.lexer.JSymbol;

public class IdExpr extends IExp
{

    public String varID;

    public IdExpr(JSymbol jSymbol, String varID) {
        super(jSymbol);
        this.varID = varID;
    }

    public String getVarID() {
        return varID;
    }

    public void setVarID(String varID) {
        this.varID = varID;
    }

    @Override
    public <R> R accept(Visitor<R> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return varID;
    }
}
