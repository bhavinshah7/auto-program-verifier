package com.verifier.checker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;

public class Z3SmtProcess {

	public void process() {
		String[] command = { "z3", "-in", "-smt2" };
		ProcessBuilder process_builder = new ProcessBuilder(command);
		Process process;
		try {
			process = process_builder.start();

			// Read out dir output
			InputStream is = process.getInputStream();
			InputStream iserr = process.getErrorStream();
			InputStreamReader isr = new InputStreamReader(is);
			InputStreamReader isrerr = new InputStreamReader(iserr);
			BufferedReader br = new BufferedReader(isr);
			BufferedReader brerr = new BufferedReader(isrerr);
			String line = new String();

			OutputStream os = process.getOutputStream();

			os.write(
					"(set-option :print-success false)(set-logic QF_UF)(declare-const p Bool)(assert (and p (not p)))(check-sat)(exit)"
							.getBytes());

			System.out.printf("Output of running %s is:\n", Arrays.toString(command));
			do {
				while ((line = br.readLine()) != null) {
					System.out.println(line);

				}
				while ((line = brerr.readLine()) != null) {
					System.out.println(line);

				}
				process.wait(1000);
			} while (process.isAlive());

			// Wait to get exit value
			try {
				int exitValue = process.waitFor();
				System.out.println("\n\nExit Value is " + exitValue);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
