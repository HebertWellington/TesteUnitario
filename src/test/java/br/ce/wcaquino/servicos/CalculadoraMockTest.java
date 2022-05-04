package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito.Then;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {
	
	
	@Mock
	private Calculador calMock;
	
	@Spy
	private Calculador calSpy;
	
	@Before
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void devoMostrarDiferencaEntreMockSpy() {
		Mockito.when(calMock.somar(1, 2)).thenReturn(5);
		Mockito.when(calSpy.somar(1, 2)).thenReturn(5);
		Mockito.doNothing().when(calSpy).imprime();

		System.out.println(calMock.somar(1, 2));
		System.out.println(calSpy.somar(1, 2));
		
		System.out.println("Mock");
		calMock.imprime();
		System.out.println("Spy");
		calSpy.imprime();

	}
	
	@Test
	public void test() {
		Calculador calc =Mockito.mock(Calculador.class);
		
		ArgumentCaptor<Integer> argCapt = ArgumentCaptor.forClass(Integer.class);
		
		Mockito.when(calc.somar(argCapt.capture(), argCapt.capture())).thenReturn(5);
		
		Assert.assertEquals(5, calc.somar(1, 1000));
		
		System.out.println(argCapt.getAllValues());
	}
}
