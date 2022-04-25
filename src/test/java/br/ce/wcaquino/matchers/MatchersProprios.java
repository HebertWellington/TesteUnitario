package br.ce.wcaquino.matchers;

import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;

import java.util.Calendar;
import java.util.Date;

public class MatchersProprios {
	
	
	public static DiaSemanaMatchers caiEm(Integer diaSemana) {
		return new DiaSemanaMatchers(diaSemana);
	}
	
	public static DiaSemanaMatchers caiNumaSegunda() {
		return new DiaSemanaMatchers(Calendar.MONDAY);
	}
	
	public static MesmaDataMatcher ehHoje() {
		return new MesmaDataMatcher(new Date());
	}
	
	public static MesmaDataMatcher ehHojeComDiferencaDias(int dias) {
		return new MesmaDataMatcher(obterDataComDiferencaDias(1));
	}
	
}
