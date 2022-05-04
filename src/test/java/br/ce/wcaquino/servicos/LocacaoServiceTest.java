package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builder.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builder.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.builder.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDiferencaDias;
import static br.ce.wcaquino.utils.DataUtils.verificarDiaSemana;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.DataDiferencaDiasMatcher;
import br.ce.wcaquino.matchers.MatchersProprios;

public class LocacaoServiceTest {
	
	@InjectMocks
	private LocacaoService service;
	
	List<Filme> listFilme = new ArrayList<Filme>();
	
	@Mock
	private LocacaoDAO dao;
	
	@Mock
	private SPCService spc;
	
	@Mock
	private EmailService email;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = none();

	@Before
	public void testLocação() {
		 MockitoAnnotations.openMocks(this);		
	}

	@Test
	public void testeLocacao() throws Exception {
		assumeFalse(verificarDiaSemana(new Date(), Calendar.SATURDAY));

		// cenário
		Usuario usuario = umUsuario().agora();
		Filme filme = (umFilme().comValor(7.0)).agora();
		Filme filme1 = umFilme().comValor(5.0).agora();

		listFilme.add(filme);
		listFilme.add(filme1);

		// ação
		Locacao locacao = service.alugarFilme(usuario, listFilme);

		// verificação

		error.checkThat(locacao.getValor(), is(equalTo(12.0)));
		error.checkThat(locacao.getValor(), is(not(6.0)));
		//error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		//error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));

	}

	@Test(expected = Exception.class)
	public void testLocacao_filmeSemEstoque() throws Exception {

		// cenário
		Usuario usuario = umUsuario().agora();
		Filme filme = umFilme().semEstoque().agora();
		listFilme.add(filme);

		// ação
		service.alugarFilme(usuario, listFilme);

	}

	@Test
	public void testLocacao_usuarioVazio() throws FilmeSemEstoqueException {

		// cenário
		Filme filme = umFilme().agora();
		listFilme.add(filme);

		// ação

		try {
			service.alugarFilme(null, listFilme);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}
		System.out.println("Forma robusta");
	}

	@Test
	public void testLocacao_FilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
		// cenário
		Usuario usuario = umUsuario().agora();
		
		
		exception.expectMessage("Filme vazio");
		exception.expect(LocadoraException.class);

		// ação

		service.alugarFilme(usuario, null);
		System.out.println("Forma nova");

	}
		
	@Test
	public void naoDeveDevolverFilmeNoDomingo() throws FilmeSemEstoqueException, LocadoraException {
		
		assumeTrue(verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = umUsuario().agora();
		
		listFilme = asList(umFilme().agora());
		
		//ação
		Locacao locacao = service.alugarFilme(usuario, listFilme);

		//verificação
		assertThat(locacao.getDataRetorno(), caiNumaSegunda());
	}
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception{
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> listFilme = asList(umFilme().agora());
		
		when(spc.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
				
		//acao
		try {
			service.alugarFilme(usuario, listFilme);
		
		//verificacao
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuário Negativado"));
		}
		
		verify(spc).possuiNegativacao(usuario);
		
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		//cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuário em dia").agora();
		Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();

		List<Locacao> locacoes = asList(
				umLocacao().atrasado().comUsuario(usuario).agora(),
				umLocacao().comUsuario(usuario2).agora(),
				umLocacao().atrasado().comUsuario(usuario3).agora(),
				umLocacao().atrasado().comUsuario(usuario3).agora());

		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
		
		//verificacao
		verify(email, times(3)).notificarAtraso(Mockito.any(Usuario.class));
		verify(email).notificarAtraso(usuario);
		verify(email, never()).notificarAtraso(usuario2);
		verify(email, atLeastOnce()).notificarAtraso(usuario3);
		verifyNoMoreInteractions(email);

	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception{
		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> listFilme = Arrays.asList(umFilme().agora());
		
		Mockito.when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrofica"));
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("problemas com SPC, tente novamente");
		
		//acao
		service.alugarFilme(usuario, listFilme);
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario
		Locacao locacao = umLocacao().agora();
		
		//acao
		service.prorrogarLocacao(locacao, 3);
		
		//verificacao
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class);
		Mockito.verify(dao).salvar(argCapt.capture());
		Locacao locacaoRetornada = argCapt.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(30.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDias(3));

	}
}
