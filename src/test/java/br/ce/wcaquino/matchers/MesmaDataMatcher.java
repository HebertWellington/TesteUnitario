package br.ce.wcaquino.matchers;

import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.ce.wcaquino.utils.DataUtils;

public class MesmaDataMatcher extends TypeSafeMatcher<Date> {

	private Date data2;
	
	public MesmaDataMatcher(Date data2) {
		this.data2 = data2;
	}

	public void describeTo(Description description) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.isMesmaData(data, data2);
	}

}
