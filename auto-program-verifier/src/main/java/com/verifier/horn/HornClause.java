package com.verifier.horn;

import java.util.List;

import com.verifier.ast.Identifier;
import com.verifier.ast.Statement;

public class HornClause {

	public List<Identifier> head;
	public List<Identifier> body;
	public Statement stat;

	public List<Identifier> getHead() {
		return head;
	}

	public void setHead(List<Identifier> head) {
		this.head = head;
	}

	public List<Identifier> getBody() {
		return body;
	}

	public void setBody(List<Identifier> body) {
		this.body = body;
	}

	public Statement getStat() {
		return stat;
	}

	public void setStat(Statement stat) {
		this.stat = stat;
	}

}
