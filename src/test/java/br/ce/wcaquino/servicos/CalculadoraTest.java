package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Test;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZero;


public class CalculadoraTest {
	
	private Calculador calc = new Calculador();
	
	@Test
	public void deveSomarDoisValores() {
		
		//cen�rio
		int a = 5;
		int b = 3;
		
		//a��o
		
		int resultado = calc.somar(a,b);
		
		//verifica��o
		
		Assert.assertEquals(8, resultado);
		
	}
	
	@Test
	public void deveSubtrairDoisValores() {
		
		//cen�rio
		int a = 8;
		int b = 5;
		
		//a��o
		int resultado = calc.subtrair(a,b);
		
		//verifica��o
		
		Assert.assertEquals(3, resultado);
		
	}
	
	@Test
	public void deveDividirDoisValores() throws NaoPodeDividirPorZero {
		//cenario
		int a = 6;
		int b = 3;
		
		//a��o
		
		int resultado = calc.dividir(a,b);
		
		//verifica��o
		
	Assert.assertEquals(2, resultado);
	}
	
	@Test(expected = NaoPodeDividirPorZero.class)
	public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZero {
		// cenario
		int a = 6;
		int b = 0;

		// a��o

		int resultado = calc.dividir(a, b);

		// verifica��o

		Assert.assertEquals(2, resultado);

	}
}
