package br.ce.wcaquino.matchers;

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
	
	public static DataDiferencaDiasMatcher ehHojeComDiferencaDias(Integer dias) {
		return new DataDiferencaDiasMatcher(dias);
	}
	
}
