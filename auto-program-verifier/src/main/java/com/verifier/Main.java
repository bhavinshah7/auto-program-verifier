package com.verifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import com.verifier.ast.Program;
import com.verifier.ast.Tree;
import com.verifier.graph.CntrlFlowGen;
import com.verifier.graph.Graph;
import com.verifier.lexer.JSymbol;
import com.verifier.lexer.Lexer;
import com.verifier.lexer.Token;
import com.verifier.parser.ASTPrinter;
import com.verifier.parser.ParseException;
import com.verifier.parser.Parser;

/**
 * @author Bhavin Shah, Anindo Saha
 *
 *         Simple test driver for the java lexer. Just runs it on some input
 *         files and produces debug output. Needs symbol class from parser.
 */
public class Main {

	private static String DOT = "C:/Program Files (x86)/Graphviz2.38/bin/dot.exe"; // Linux

	public static void main(String argv[]) {

		Main main = new Main();

		if (argv.length < 2) {
			System.err.println("USAGE: java verifier.Main <OPTION> <FILENAME>");
			System.exit(1);
		}

		switch (argv[0]) {
		case "--lex": {
			main.lex(argv);
		}
			break;

		case "--ast": {
			main.parse(argv);
		}
			break;

		case "-c": {
			main.genCfg(argv);
		}
			break;

		case "-h": {
			main.genHorn(argv);
		}
			break;

		case "-v": {
			main.verifyProg(argv);
		}
			break;

		default: {
			System.err.println("Invalid option " + argv[0]);
		}

		}

	}

	public void lex(String[] argv) {

		for (int i = 1; i < argv.length; i++) {

			try {

				if (!isFileValid(argv[i])) {
					continue;
				}

				Lexer lexer = new Lexer(new FileReader(argv[i]));
				PrintWriter writer = new PrintWriter(getFileName(argv[i]) + ".lexed", "UTF-8");

				JSymbol s;
				do {
					s = lexer.yylex();
					writer.println(s);
				} while (s.token != Token.EOF);

				writer.close();
			} catch (Exception e) {
				e.printStackTrace(System.out);
				System.exit(1);
			}
		}

	}

	public void parse(String argv[]) {
		ASTPrinter printer = new ASTPrinter();
		for (int i = 1; i < argv.length; i++) {
			try {

				if (!isFileValid(argv[i])) {
					continue;
				}

				Lexer lexer = new Lexer(new FileReader(argv[i]));
				Parser p = new Parser(lexer);
				Tree tree = p.parse();
				if (tree != null) {
					String sexpr = printer.visit((Program) tree);
					PrintWriter writer = new PrintWriter(getFileName(argv[i]) + ".ast", "UTF-8");
					writer.println(sexpr);
					writer.close();
				}

			} catch (ParseException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void genCfg(String argv[]) {
		CntrlFlowGen cfgGen = new CntrlFlowGen();
		for (int i = 1; i < argv.length; i++) {
			try {

				if (!isFileValid(argv[i])) {
					continue;
				}

				System.out.println("Parsing file [" + argv[i] + "]");

				Lexer lexer = new Lexer(new FileReader(argv[i]));
				Parser p = new Parser(lexer);
				Tree tree = p.parse();
				if (tree != null) {
					cfgGen.visit((Program) tree);
					Graph g = cfgGen.getG();

					String dot = getFileName(argv[i]) + ".gv";
					String png = getFileName(argv[i]) + ".png";
					PrintWriter writer = new PrintWriter(dot, "UTF-8");
					writer.println(g.getDotSrc());
					writer.close();
					System.out.println("Generated output file");

					Runtime rt = Runtime.getRuntime();
					String[] args = { DOT, "-Tpng", dot, "-o", png };
					Process proc = rt.exec(args);
					proc.waitFor();
				}

			} catch (ParseException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void genHorn(String argv[]) {
		CntrlFlowGen cfgGen = new CntrlFlowGen();
		for (int i = 1; i < argv.length; i++) {
			try {

				if (!isFileValid(argv[i])) {
					continue;
				}
				Lexer lexer = new Lexer(new FileReader(argv[i]));
				Parser p = new Parser(lexer);
				Tree tree = p.parse();
				if (tree != null) {
					cfgGen.visit((Program) tree);
					Graph g = cfgGen.getG();
					String horn = g.getHornClauses();
					System.out.println(horn);
				}

			} catch (ParseException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void verifyProg(String argv[]) {
		CntrlFlowGen cfgGen = new CntrlFlowGen();
		for (int i = 1; i < argv.length; i++) {
			try {

				if (!isFileValid(argv[i])) {
					continue;
				}
				Lexer lexer = new Lexer(new FileReader(argv[i]));
				Parser p = new Parser(lexer);
				Tree tree = p.parse();
				if (tree != null) {
					cfgGen.visit((Program) tree);
					Graph g = cfgGen.getG();
					String horn = g.getHornClauses();
					// System.out.println(horn);

					String smt2 = getFileName(argv[i]) + ".smt2";
					PrintWriter writer = new PrintWriter(smt2);
					writer.println(horn);
					writer.close();

					ProcessBuilder pb = new ProcessBuilder("java", "-jar", "lib/lazabs.jar", smt2);
					Process process = pb.start();

					InputStream in = process.getInputStream();
					InputStreamReader ins = new InputStreamReader(in);
					BufferedReader br = new BufferedReader(ins);

					BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

					String line;

					while ((line = br.readLine()) != null) {
						if ("sat".equals(line)) {
							System.out.println("correct");
							System.exit(0);
						}
					}

					while ((line = stdError.readLine()) != null) {
						System.out.println(line);
					}

					process.waitFor();
					process.destroy();
					System.out.println("incorrect");
				}

			} catch (ParseException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	private boolean isFileValid(String filename) {
		File f = new File(filename);

		if (!f.exists()) {
			System.err.println(filename + ": No such file!");
			return false;
		}

		String fileExtension = getFileExtension(f);
		if (!"imp".equals(fileExtension)) {
			System.err.println(filename + ": Invalid file extension!");
			return false;
		}

		return true;
	}

	private String getFileExtension(File file) {
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}

	private String getFileName(String filename) {
		try {
			return filename.substring(0, filename.lastIndexOf("."));
		} catch (Exception e) {
			return "";
		}
	}

}
