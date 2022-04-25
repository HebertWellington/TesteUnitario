package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZero;


public class CalculadoraTest {
	
	private Calculador calc = new Calculador();
	
	@Test
	public void deveSomarDoisValores() {
		
		//cenário
		int a = 5;
		int b = 3;
		
		//ação
		
		int resultado = calc.somar(a,b);
		
		//verificação
		
		Assert.assertEquals(8, resultado);
		
	}
	
	@Test
	public void deveSubtrairDoisValores() {
		
		//cenário
		int a = 8;
		int b = 5;
		
		//ação
		int resultado = calc.subtrair(a,b);
		
		//verificação
		
		Assert.assertEquals(3, resultado);
		
	}
	
	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZero {
		//cenario
		int a = 6;
		int b = 3;
		
		//ação
		
		int resultado = calc.dividir(a,b);
		
		//verificação
		
	Assert.assertEquals(2, resultado);
	}
	
	@Test(expected = NaoPodeDividirPorZero.class)
	public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZero {
		// cenario
		int a = 6;
		int b = 0;

		// ação

		int resultado = calc.dividir(a, b);

		// verificação

		Assert.assertEquals(2, resultado);

	}
}
