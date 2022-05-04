package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZero;

public class Calculador {

	public int somar(int a, int b) {
		
		return a + b;
	}

	public int subtrair(int a, int b) {

		return a - b;
	}

	public int dividir(int a, int b) throws NaoPodeDividirPorZero {
		if(b == 0) {
			throw new NaoPodeDividirPorZero();
		}
		return a/b;
	}
	
	public void imprime() {
		System.out.println("Passei aqui");
	}

}
