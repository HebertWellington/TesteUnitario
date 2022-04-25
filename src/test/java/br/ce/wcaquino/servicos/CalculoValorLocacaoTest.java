package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {
	
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private LocacaoDAO dao;
	
	@Mock
	private SPCService spc;
	
	@Parameter
	public List<Filme> listFilme;
	
	@Parameter(value=1)
	public Double valorLocacao;
	
	@Parameter(value=2)
	public String cenario;
	
	private static Filme filme1 = umFilme().agora(); 
	private static Filme filme2 = umFilme().agora(); 
	private static Filme filme3 = umFilme().agora(); 
	private static Filme filme4 = umFilme().agora(); 
	private static Filme filme5 = umFilme().agora(); 
	private static Filme filme6 = umFilme().agora(); 

	
	@Parameters(name= "{2}")
	public static Collection<Object[]> getParametros(){
		return Arrays.asList(new Object[][] {
			
			{Arrays.asList(filme1, filme2, filme3), 27.5, "3 filmes: 25%"},
			{Arrays.asList(filme1, filme2, filme3, filme4), 32.5, "4 filmes: 50%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 35.0, "5 filmes: 75%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 35.0, "6 filmes: 100%"}
			
		});
	}
	
	@Before
	public void testLocação() {
		MockitoAnnotations.initMocks(this);

	}
	
	@Test
	public void testDesconto75PctNoTerceiroFilme() throws FilmeSemEstoqueException, LocadoraException {
		
		//cenário
		
		Usuario usuario = new Usuario("Usuario 1");

		//ação
		
		Locacao locacao = service.alugarFilme(usuario, listFilme);
		
		//verificação
		
		Assert.assertThat(locacao.getValor(), is(valorLocacao));	
	}
	
}
