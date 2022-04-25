package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDAO dao;
	
	private SPCService spcService; 
	
	private EmailService emailService;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> listFilme)
			throws FilmeSemEstoqueException, LocadoraException {

		if (usuario == null) {
			throw new LocadoraException("Usuario vazio");
		}

		
		if (listFilme == null) {
				throw new LocadoraException("Filme vazio");
			}
		

			for (Filme list : listFilme) {
				if (list.getEstoque() == 0) {
					throw new FilmeSemEstoqueException();
				}
			}
			boolean negativado;
			try {
				negativado = spcService.possuiNegativacao(usuario);

			} catch (Exception e) {
				throw new LocadoraException("problemas com SPC, tente novamente");
			}
			if (negativado) {
				throw new LocadoraException("Usuário Negativado");
			}

		Filme filme = new Filme();
		Locacao locacao = new Locacao();
		locacao.setListFilme(listFilme);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date()); 
		Double valorTotal = 0.0;
		int n = listFilme.size();
		Double valorPre = 0.0;
		double valorTotal1 = 0.0;
		for (int i = 0; i <= n - 1; i++) {

			if (i >= 2) {
				
				switch (i) {

				case 2:
					valorPre = (listFilme.get(2).getPrecoLocacao()) * 0.75;
					break;
				case 3:
					valorPre = (listFilme.get(3).getPrecoLocacao()) * 0.50;
					break;
				case 4:
					valorPre = (listFilme.get(4).getPrecoLocacao()) * 0.25;
					break;
				case 5:
					valorPre = (listFilme.get(5).getPrecoLocacao()) * 0.00;
					break;

				}

				valorTotal1 += valorPre;

			} else {

				valorTotal += listFilme.get(i).getPrecoLocacao();
			}
		
		}
		locacao.setValor(valorTotal + valorTotal1);

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		dao.salvar(locacao);
	
		return locacao;
	}
	
	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		for(Locacao locacao : locacoes) {
			if(locacao.getDataRetorno().before(new Date())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
			
		}
	}
	
	public void prorrogarLocacao(Locacao locacao, int dias) {
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setListFilme(locacao.getListFilme());
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor()*dias);
		dao.salvar(novaLocacao);
		
	}
}